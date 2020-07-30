package com.chinanetcenter.api.demo;

import com.chinanetcenter.api.entity.PutPolicy;
import com.chinanetcenter.api.entity.SliceUploadHttpResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.sliceUpload.BaseBlockUtil;
import com.chinanetcenter.api.sliceUpload.JSONObjectRet;
import com.chinanetcenter.api.util.*;
import com.chinanetcenter.api.wsbox.SliceUploadResumable;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fuyz on 2016/8/31.
 * 分片上传
 */
public class SliceUploadDemo {

    public static void main(String[] args) throws FileNotFoundException {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * 可在用户管理界面-安全管理-域名查询获取uploadDomain,MgrDomain
         */
        Config.PUT_URL = "your uploadDomain";
        String bucketName = "your-bucket";
        String fileKey = "java-sdk/com.toycloud.MeiYe.apk";

        String srcFilePath = "D:\\testfile\\test001\\com.toycloud.MeiYe.apk";
        BaseBlockUtil.CHUNK_SIZE = 4 * 1024 * 1024;  //每一片为4M，默认256k，减少上传请求
        SliceUploadDemo demo = new SliceUploadDemo();
        demo.sliceUpload(bucketName,fileKey,srcFilePath);
        /**  第二种方式，key不写到scope里，而是从head指定 用于同一个token可以上传多个文件
        String fileKey2 = "java-sdk/com.toycloud.MeiYe2.apktest";
        String mimeType = "application/vnd.android.package-archive";
        demo.sliceUpload(bucketName,fileKey2,srcFilePath,mimeType);
         */
    }

    public void sliceUpload(final String bucketName, final String fileKey, final String filePath) {
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setScope(bucketName + ":" + fileKey);
        putPolicy.setOverwrite(1);
        putPolicy.setDeadline(String.valueOf(DateUtil.nextDate(1, new Date()).getTime()));
        JSONObjectRet jsonObjectRet = getJSONObjectRet(bucketName,fileKey,filePath);
        SliceUploadResumable sliceUploadResumable = new SliceUploadResumable();
        sliceUploadResumable.execUpload(bucketName, fileKey, filePath, putPolicy, null, jsonObjectRet);
    }

    public void sliceUpload(final String bucketName, final String fileKey, final String filePath,String mimeType) {
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setScope(bucketName);
        putPolicy.setOverwrite(1);
        putPolicy.setDeadline(String.valueOf(DateUtil.nextDate(1, new Date()).getTime()));
        JSONObjectRet jsonObjectRet = getJSONObjectRet(bucketName,fileKey,filePath);
        SliceUploadResumable sliceUploadResumable = new SliceUploadResumable();
        Map<String,String> headMap = new HashMap<String, String>();
        headMap.put("mimeType",mimeType);
        headMap.put("key", EncodeUtils.urlsafeEncode(fileKey));
        sliceUploadResumable.execUpload(bucketName, fileKey, filePath, putPolicy, null, jsonObjectRet,headMap);
    }

    public JSONObjectRet getJSONObjectRet(final String bucketName,final String fileKey,final String filePath){
        return new JSONObjectRet() {
            /**
             * 文件上传成功后会回调此方法
             * 校验下上传文件的hash和本地文件的hash是否一致，不一致可能本地文件被修改过
             */
            @Override
            public void onSuccess(JsonNode obj) {
                File fileHash = new File(filePath);
                String eTagHash = WetagUtil.getEtagHash(fileHash.getParent(), fileHash.getName());// 根据文件内容计算hash
                SliceUploadHttpResult result = new SliceUploadHttpResult(obj);
                if (eTagHash.equals(result.getHash())) {
                    System.out.println("上传成功");
                } else {
                    System.out.println("hash not equal,eTagHash:" + eTagHash + " ,hash:" + result.getHash());
                }
            }

            @Override
            public void onSuccess(byte[] body) {
                System.out.println(new String(body));
            }

            // 文件上传失败回调此方法
            @Override
            public void onFailure(Exception ex) {
                if (ex instanceof WsClientException) {
                    WsClientException wsClientException = (WsClientException) ex;
                    System.out.println(wsClientException.code + ":" + wsClientException.getMessage());
                }else {
                    ex.printStackTrace();
                }
                System.out.println("上传出错，" + ex.getMessage());
            }

            // 进度条展示，每上传成功一个块回调此方法
            @Override
            public void onProcess(long current, long total) {
                System.out.printf("%s\r", current * 100 / total + " %");
            }

            /**
             * 持久化，断点续传时把进度信息保存，下次再上传时把JSONObject赋值到PutExtra
             * sdk默认把信息保存到磁盘文件，如果有需要请自己保存到db
             * 下次再续传的时候把值赋值到PutExtra参数里
             */
            @Override
            public void onPersist(JsonNode obj) {
                BaseBlockUtil.savePutExtra(bucketName, fileKey, obj);
            }
        };
    }

    public void sliceUpload(final String bucketName, final String fileKey, InputStream inputStream) {
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setScope(bucketName + ":" + fileKey);
        putPolicy.setOverwrite(1);
        putPolicy.setDeadline(String.valueOf(DateUtil.nextDate(1, new Date()).getTime()));
        JSONObjectRet jsonObjectRet = new JSONObjectRet() {
            @Override
            public void onSuccess(JsonNode obj) {
                System.out.println("上传成功");
            }

            @Override
            public void onSuccess(byte[] body) {
            }

            @Override
            public void onFailure(Exception ex) {
                if (ex instanceof WsClientException) {
                    WsClientException wsClientException = (WsClientException) ex;
                    System.out.println(wsClientException.code + ":" + wsClientException.getMessage());
                } else {
                    ex.printStackTrace();
                }
                System.out.println("上传出错，" + ex.getMessage());
            }

            @Override
            public void onProcess(long current, long total) {
            }

            @Override
            public void onPersist(JsonNode obj) {
            }
        };
        SliceUploadResumable sliceUploadResumable = new SliceUploadResumable();
        sliceUploadResumable.execUpload(bucketName, fileKey, inputStream, putPolicy, jsonObjectRet);
    }
}
