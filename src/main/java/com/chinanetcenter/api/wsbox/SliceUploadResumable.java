package com.chinanetcenter.api.wsbox;

import com.chinanetcenter.api.entity.PutPolicy;
import com.chinanetcenter.api.entity.SliceUploadHttpResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.sliceUpload.*;
import com.chinanetcenter.api.util.DateUtil;
import com.chinanetcenter.api.util.TokenUtil;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by fuyz on 2014/8/18.
 * 分片上传
 */
public class SliceUploadResumable {

    /**
     * 用户初始化ak和sk，sdk自己生成token
     */
    public void execUpload(String bucketName, String fileKey, String filePath, PutPolicy putPolicy, PutExtra putExtra, JSONObjectRet jsonObjectRet) {
        execUpload(bucketName,fileKey,filePath,putPolicy,putExtra,jsonObjectRet,null);
    }
    /**
     * 用户初始化ak和sk，sdk自己生成token
     */
    public void execUpload(String bucketName, String fileKey, String filePath, PutPolicy putPolicy, PutExtra putExtra, JSONObjectRet jsonObjectRet,Map<String,String> headMap) {
        RandomAccessFile file = null;
        ExecutorService pool = null;
        try {
            if (BaseBlockUtil.BLOCK_SIZE < 4 * BaseBlockUtil.MB || (BaseBlockUtil.BLOCK_SIZE % (4 * BaseBlockUtil.MB)) != 0) {
                jsonObjectRet.onFailure(new Exception("块大小应该为4M的整数倍!"));
                return;
            }
            file = new RandomAccessFile(filePath, "r");
            pool = Executors.newFixedThreadPool(BaseBlockUtil.THREAD_NUN);
            CompletionService<BlockObject> completionService = new ExecutorCompletionService<BlockObject>(pool);
            if (putExtra == null || putExtra.processes == null || putExtra.totalSize != file.length()) {
                putExtra = BaseBlockUtil.getPutExtra(bucketName, fileKey);
                if (putExtra == null){
                    putExtra = new PutExtra();
                    initPutExtra(bucketName, fileKey, file, putExtra, jsonObjectRet);
                }
            }
            putPolicy.setDeadline(String.valueOf(DateUtil.nextHours(1, new Date()).getTime()));
            Date dealEndDate = DateUtil.nextMinute(5, new Date());
            String token = TokenUtil.getUploadToken(putPolicy);
            Map<String,String> currHeadMap = new HashMap<String,String>();
            currHeadMap.put("Authorization", "UpToken " + token);
            currHeadMap.put("uploadBatch",putExtra.uploadBatch);
            if (headMap != null && headMap.size() > 0){
                for (Map.Entry<String, String> entry : headMap.entrySet()) {
                    currHeadMap.put(entry.getKey(), entry.getValue());
                }
            }

            int runnerThread = 0;
            for (BlockObject blockObject : putExtra.processes) {
                runnerThread++;
                while (runnerThread > (BaseBlockUtil.THREAD_NUN + 1)) {
                    completionService.take().get();
                    runnerThread--;
                }
                if (dealEndDate.before(new Date())) {
                    dealEndDate = DateUtil.nextMinute(5, new Date());
                    putPolicy.setDeadline(String.valueOf(DateUtil.nextHours(1, new Date()).getTime()));
                    token = TokenUtil.getUploadToken(putPolicy);
                    currHeadMap.put("Authorization", "UpToken " + token);
                }
                RandomAccessFile blockFile = new RandomAccessFile(filePath, "r");
                blockObject.setCommonParam(blockFile, bucketName, fileKey);
                BlockUpload task = new BlockUpload(blockObject, jsonObjectRet, putExtra, currHeadMap);
                completionService.submit(task);
            }

            for (int i = 0; i < runnerThread; i++) {
                completionService.take().get();
            }
            pool.shutdown();
            putPolicy.setDeadline(String.valueOf(DateUtil.nextHours(1, new Date()).getTime()));
            token = TokenUtil.getUploadToken(putPolicy);
            currHeadMap.put("Authorization", "UpToken " + token);
            BaseBlockUtil util = new BaseBlockUtil(null, jsonObjectRet, putExtra, currHeadMap);

            SliceUploadHttpResult result = util.mkFile(currHeadMap, fileKey, putExtra, 0);
            if (result.getStatus() == 200) {
                jsonObjectRet.onSuccess(result.toJSON());
            } else {
                if (result.getStatus() == 412) {
                    System.out.println(fileKey + " 此文件块有缺失，文件重新上传");
                    putPolicy.setOverwrite(1);
                }
                putExtra.processes = null;
                jsonObjectRet.onFailure(new WsClientException(result.getStatus(), result.getResponse()));
            }
            BaseBlockUtil.clearPutExtra(bucketName,fileKey);
        } catch (Exception e) {
            jsonObjectRet.onFailure(e);
        } finally {
            if (pool != null) {
                pool.shutdownNow();
            }
            if (file != null) {
                try {
                    int reTime = 0;
                    while (pool != null && !pool.isTerminated() && reTime < 50) {
                        reTime++;
                        Thread.sleep(1000);
                    }
                    file.close();
                } catch (Exception e) {
                    System.out.println("file close error");
                }
            }
        }
    }

    public void initPutExtra(String bucketName, String fileKey, RandomAccessFile file, PutExtra putExtra, JSONObjectRet ret) {
        try {
            if (putExtra == null){
                putExtra = new PutExtra();
            }
            int blockCount = (int) ((file.length() - 1) / BaseBlockUtil.BLOCK_SIZE) + 1;
            putExtra.processes = new BlockObject[blockCount];
            putExtra.totalSize = file.length();
            for (int i = 0; i < blockCount; i++) {
                int blockLength = (int) Math.min(file.length() - (long) BaseBlockUtil.BLOCK_SIZE * i, BaseBlockUtil.BLOCK_SIZE);
                BlockObject blockObject = new BlockObject(file, bucketName, fileKey, i, blockLength);
                putExtra.processes[i] = blockObject;
            }
            putExtra.uploadBatch = "JAVA-SDK-" + UUID.randomUUID();
        } catch (IOException e) {
            ret.onFailure(e);
        }
    }

    public void execUpload(String bucketName, String fileKey, InputStream inputStream, PutPolicy putPolicy, JSONObjectRet jsonObjectRet) {
        execUpload(bucketName, fileKey, inputStream, putPolicy, jsonObjectRet, null);
    }

    public void execUpload(String bucketName, String fileKey, InputStream inputStream, PutPolicy putPolicy, JSONObjectRet jsonObjectRet, Map<String, String> headMap) {
        ExecutorService pool = null;
        try {
            if (BaseBlockUtil.BLOCK_SIZE < 4 * BaseBlockUtil.MB || (BaseBlockUtil.BLOCK_SIZE % (4 * BaseBlockUtil.MB)) != 0) {
                jsonObjectRet.onFailure(new Exception("块大小应该为4M的整数倍!"));
                return;
            }

            PutExtra putExtra = new PutExtra();
            putExtra.streamProcesses = new ArrayList<BlockObject>();
            putExtra.uploadBatch = "JAVA-SDK-" + UUID.randomUUID();

            putPolicy.setDeadline(String.valueOf(DateUtil.nextHours(1, new Date()).getTime()));
            Date dealEndDate = DateUtil.nextMinute(5, new Date());
            String token = TokenUtil.getUploadToken(putPolicy);
            Map<String, String> currHeadMap = new HashMap<String, String>();
            currHeadMap.put("Authorization", "UpToken " + token);
            currHeadMap.put("uploadBatch", putExtra.uploadBatch);
            if (headMap != null && headMap.size() > 0) {
                for (Map.Entry<String, String> entry : headMap.entrySet()) {
                    currHeadMap.put(entry.getKey(), entry.getValue());
                }
            }

            pool = Executors.newFixedThreadPool(BaseBlockUtil.THREAD_NUN);
            CompletionService<BlockObject> completionService = new ExecutorCompletionService<BlockObject>(pool);
            int runnerThread = 0;

            long size = 0;
            boolean eof = false;
            int blockIdx = 0;
            while (!eof) {
                int bufferIndex = 0;
                int blockSize = 0;
                int len = 0;
                byte[] blockBuffer = new byte[BaseBlockUtil.BLOCK_SIZE];

                while (len != -1 && bufferIndex != blockBuffer.length) {
                    blockSize = blockBuffer.length - bufferIndex;
                    len = inputStream.read(blockBuffer, bufferIndex, blockSize);
                    if (len != -1) {
                        bufferIndex += len;
                    } else {
                        eof = true;
                    }
                }

                if (bufferIndex > 0) {
                    if (dealEndDate.before(new Date())) {
                        dealEndDate = DateUtil.nextMinute(5, new Date());
                        putPolicy.setDeadline(String.valueOf(DateUtil.nextHours(1, new Date()).getTime()));
                        token = TokenUtil.getUploadToken(putPolicy);
                        currHeadMap.put("Authorization", "UpToken " + token);
                    }

                    BlockObject blockObject = new BlockObject(blockBuffer, bucketName, fileKey, blockIdx, bufferIndex);
                    putExtra.streamProcesses.add(blockObject);
                    BlockUpload task = new BlockUpload(blockObject, jsonObjectRet, putExtra, currHeadMap);
                    completionService.submit(task);

                    size += bufferIndex;
                    blockIdx++;
                    runnerThread++;
                }

                while (runnerThread > BaseBlockUtil.THREAD_NUN) {
                    completionService.take().get();
                    runnerThread--;
                }
            }

            for (int i = 0; i < runnerThread; i++) {
                completionService.take().get();
            }
            pool.shutdown();

            putPolicy.setDeadline(String.valueOf(DateUtil.nextHours(1, new Date()).getTime()));
            token = TokenUtil.getUploadToken(putPolicy);
            currHeadMap.put("Authorization", "UpToken " + token);
            putExtra.totalSize = size;
            BaseBlockUtil util = new BaseBlockUtil(null, jsonObjectRet, putExtra, currHeadMap);
            SliceUploadHttpResult result = util.mkFile(currHeadMap, fileKey, putExtra, 0);
            if (result.getStatus() == 200) {
                jsonObjectRet.onSuccess(result.toJSON());
            } else {
                if (result.getStatus() == 412) {
                    System.out.println(fileKey + " 此文件块有缺失，文件重新上传");
                    putPolicy.setOverwrite(1);
                }
                putExtra.streamProcesses = null;
                jsonObjectRet.onFailure(new WsClientException(result.getStatus(), result.getResponse()));
            }
        } catch (Exception e) {
            jsonObjectRet.onFailure(e);
        } finally {
            if (pool != null) {
                pool.shutdownNow();
            }
            IOUtils.closeQuietly(inputStream);
        }
    }
}
