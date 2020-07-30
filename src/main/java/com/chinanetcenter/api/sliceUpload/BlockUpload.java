package com.chinanetcenter.api.sliceUpload;

import com.chinanetcenter.api.entity.SliceUploadHttpResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.http.HttpClientUtil;
import com.chinanetcenter.api.util.Config;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by fuyz on 2014/8/5.
 * 块上传
 */
public class BlockUpload extends BaseBlockUtil implements Callable {

    private static Logger logger = Logger.getLogger(BlockUpload.class);

    public BlockUpload(BlockObject blockObject, JSONObjectRet jsonObjectRet, PutExtra putExtra1, Map<String,String> headMap) {
        super(blockObject, jsonObjectRet, putExtra1, headMap);
    }


    /**
     * 块上传
     *
     * @return block ctx
     * @throws Exception
     */
    @Override
    public BlockObject call() throws Exception {
        int retry = 0;
        boolean isOut = false;
        while (!isOut) {
            try {
                isOut = true;
                buildBlockUpload();
            } catch (WsClientException e) {
                if (e.code == 412 && retry < 3) {
                    isOut = false;
                    blockObject.setStart(0);
                    blockObject.setSuccessLength(0);
                    retry++;
                } else {
                    throw e;
                }
            } finally {
                if (isOut) {
                    if (blockObject.getData() == null) {
                        blockObject.file.close();
                    }
                }
            }
        }
        return blockObject;
    }

    public void buildBlockUpload() throws Exception {
        if (blockObject.getStart() >= blockObject.getBlockLen()) {
            return;
        }
        blockObject.setStart(0);
        blockObject.setSuccessLength(0);
        if (blockObject.getStart() == 0) {
            int chunkLength = Math.min(blockObject.getBlockLen(), CHUNK_SIZE);
            buildMkBlk(chunkLength, 0);
        }

        if (blockObject.getBlockLen() > CHUNK_SIZE) {
            int count = (int) ((blockObject.getBlockLen() - blockObject.getStart() + CHUNK_SIZE - 1) / CHUNK_SIZE);
            for (int i = 0; i < count; i++) {
                int len = (int) Math.min(blockObject.getBlockLen() - blockObject.getStart(), CHUNK_SIZE);
                buildBPut(len, 0);
            }
        }
    }

    private SliceUploadHttpResult buildMkBlk(int len, int time) throws Exception {
        String url = getMkBlkUrl();
        return upload(url, len, time);
    }

    private SliceUploadHttpResult buildBPut(int len, int time) throws Exception {
        String url = getBPutUrl();
        return upload(url, len, time);
    }

    private SliceUploadHttpResult upload(String url, int len, int time) throws Exception {
        HttpPost post = null;
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = null;
        try {
            httpClient = HttpClientUtil.createHttpClient(url);
            post = buildUpPost(url);
            if (headMap != null && headMap.size() > 0) {
                for (Map.Entry<String, String> entry : headMap.entrySet()) {
                    post.setHeader(entry.getKey(), entry.getValue());
                }
            }
            if(!post.containsHeader("User-Agent")){
                post.addHeader("User-Agent", Config.VERSION_NO);
            }

            if (blockObject.getData() != null) {
                long start = blockObject.getStart();
                HttpEntity httpEntity = buildHttpEntity(blockObject.getBlockBuffer(), start, len);
                post.setEntity(httpEntity);
            } else {
                long start = blockObject.getOffset() + blockObject.getStart();
                post.setEntity(buildHttpEntity(blockObject.file, start, len));
            }
            response = httpClient.execute(post);

            SliceUploadHttpResult ret = handleResult(response);
            return checkAndRetryUpload(url, len, time, ret);
        } catch (WsClientException e) {
            String message = "url:" + url + ",blockIdx:" + blockObject.getBlockIdx() + " ,HttpClientException error,Message:" + e.getMessage();
            throw new WsClientException(e.code, message);
        } catch (ClientProtocolException cpe) {
            String message = "url:" + url + ",blockIdx:" + blockObject.getBlockIdx() + " ,ClientProtocolException error,Message:" + cpe.getMessage();
            logger.error(message);
            throw new ClientProtocolException(message);
        } finally {
            if (post != null) {
                post.releaseConnection();
            }
            if (response != null) {
                response.close();
            }
            if (httpClient != null) {
                httpClient.close();
            }
        }
    }

    private SliceUploadHttpResult checkAndRetryUpload(String url, int len, int time, SliceUploadHttpResult result) throws Exception {
        if (!result.isOk()) {
            if ((result.getStatus() == 406 || result.getStatus() == 701 || result.getStatus() / 100 == 5) && time < TRIED_TIMES) {
                return upload(url, len, time + 1);
            } else {
                throw new WsClientException(result.getStatus(), url + "connect result error, stauts :" + result.getStatus() + " reason:" + (result.response == null ? "" : result.response));
            }
        } else {
            if (blockObject.getData() != null) {
                //流式上传
                long crc32 = crc32(blockObject.getData(), (int) blockObject.getStart(), len);
                String checkSum = getMD5String(blockObject.getData(), (int) blockObject.getStart(), len);
                // 上传的数据 CRC32 或md5校验不一致。
                if (result.getCrc32() != crc32 || !result.getChecksum().equals(checkSum)) {
                    if (time < TRIED_TIMES) {
                        return upload(url, len, time + 1);
                    } else {
                        System.out.println("result.getCrc32():" + result.getCrc32() + ",crc32:" + crc32);
                        throw new HttpException("406 inner block's crc32 do not match." + (result.response == null ? "" : result.response));
                    }
                } else {
                    //修改successLength和start的值
                    blockObject.addSuccessLength(len);
                    blockObject.setLastCtx(result.getCtx());
                    return result;
                }
            } else {
                long crc32 = buildCrc32(len);
                String checkSum = getFileMD5String(len);
                // 上传的数据 CRC32 或md5校验不一致。
                if (result.getCrc32() != crc32 || !result.getChecksum().equals(checkSum)) {
                    if (time < TRIED_TIMES) {
                        return upload(url, len, time + 1);
                    } else {
                        System.out.println("result.getCrc32():" + result.getCrc32() + ",crc32:" + crc32);
                        throw new HttpException("406 inner block's crc32 do not match." + (result.response == null ? "" : result.response));
                    }
                } else {
                    blockObject.addSuccessLength(len);
                    blockObject.setLastCtx(result.getCtx());
                    jsonObjectRet.onPersist(putExtra.toJSON());
                    long current = 0;
                    for (BlockObject blockObject1 : putExtra.processes) {
                        current += blockObject1.getStart();
                    }
                    jsonObjectRet.onProcess(current, putExtra.totalSize);
                    return result;
                }
            }
        }
    }
}
