package com.chinanetcenter.api.demo;

import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.entity.PutPolicy;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.util.DateUtil;
import com.chinanetcenter.api.util.TokenUtil;
import com.chinanetcenter.api.wsbox.FileUploadManage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fuyz on 2016/8/30.
 * 上传demo
 */
public class UploadDemo {
    FileUploadManage fileUploadManage = new FileUploadManage();

    public static void main(String[] args) throws FileNotFoundException {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * 可在用户管理界面-安全管理-域名查询获取uploadDomain,MgrDomain
         */
        Config.PUT_URL = "your uploadDomain";
        String bucketName = "your-bucket";
        String fileKey = "test.JPG";
        String fileKeyMp4 = "folder/test.JPG";
        String srcFilePath = "D:\\testfile\\1m.JPG";
        UploadDemo demo = new UploadDemo();
        demo.uploadFile(bucketName, fileKey, srcFilePath);
        FileInputStream in = new FileInputStream(new File(srcFilePath));
        demo.uploadFile(bucketName, fileKey, in);
        demo.uploadReturnBody(bucketName, fileKeyMp4, srcFilePath);
        demo.uploadMimeType(bucketName, fileKey, srcFilePath);
        demo.uploadPersistent(bucketName, fileKey, srcFilePath);
    }

    /**
     * 通过本地的文件路径上传文件
     * 默认覆盖上传
     */
    public void uploadFile(String bucketName,String fileKey,String srcFilePath){
        try {
            HttpClientResult result = fileUploadManage.upload(bucketName,fileKey,srcFilePath);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过文件流上传文件，方法里会关闭InputStream
     * 默认覆盖上传
     */
    public void uploadFile(String bucketName,String fileKey,InputStream in){
        try {
            HttpClientResult result = fileUploadManage.upload(bucketName,fileKey,in);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传后需要回调、返回信息等，可通过PutPolicy指定上传策略
     * callbackurl、callbackbody、returnurl 类似这个方法
     */
    public void uploadReturnBody(String bucketName,String fileKey,String srcFilePath){
        String returnBody = "key=$(key)&fname=$(fname)&fsize=$(fsize)&url=$(url)&hash=$(hash)&mimeType=$(mimeType)";
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setOverwrite(1); //覆盖上传
        putPolicy.setDeadline(String.valueOf(DateUtil.nextDate(1,new Date()).getTime()));
        putPolicy.setReturnBody(returnBody);
        putPolicy.setScope(bucketName + ":" + fileKey);
        try {
            HttpClientResult result = fileUploadManage.upload(bucketName,fileKey,srcFilePath,putPolicy);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传指定文件类型，服务端默认按照文件后缀或者文件内容
     * 指定了mimeType，在下载的时候Content-type会指定该类型
     */
    public void uploadMimeType(String bucketName,String fileKey,String srcFilePath){
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setOverwrite(1);
        putPolicy.setDeadline(String.valueOf(DateUtil.nextDate(1, new Date()).getTime()));
        putPolicy.setScope(bucketName + ":" + fileKey);
        try {
            String uploadToken = TokenUtil.getUploadToken(putPolicy);
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("token", uploadToken);
            paramMap.put("mimeType", "application/UQ");
            HttpClientResult result = fileUploadManage.upload(paramMap,srcFilePath);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传文件后对该文件做转码
     * 上传成功后返回persistentId应答，可以通过这个id去查询转码情况
     */
    public void uploadPersistent(String bucketName,String fileKey,String srcFilePath){
        PutPolicy putPolicy = new PutPolicy();
        String returnBody = "key=$(key)&persistentId=$(persistentId)&fsize=$(fsize)";
        putPolicy.setOverwrite(1);
        putPolicy.setDeadline(String.valueOf(DateUtil.nextDate(1, new Date()).getTime()));
        putPolicy.setScope(bucketName + ":" + fileKey);
        putPolicy.setPersistentOps("imageMogr2/jpg/crop/500x500/gravity/CENTER/lowpoly/1|saveas/ZnV5enRlc3Q4Mi0wMDE6ZG9fY3J5c3RhbGxpemVfZ3Jhdml0eV9jZW50ZXJfMTQ2NTkwMDg0Mi5qcGc="); // 设置视频转码操作
        putPolicy.setPersistentNotifyUrl("http://demo1/notifyUrl"); // 设置转码后回调的接口
        putPolicy.setReturnBody(returnBody);
        try {
            HttpClientResult result = fileUploadManage.upload(bucketName,fileKey,srcFilePath,putPolicy);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
}
