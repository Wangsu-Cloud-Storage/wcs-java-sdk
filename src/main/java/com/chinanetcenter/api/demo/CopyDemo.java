package com.chinanetcenter.api.demo;

import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.wsbox.OperationManager;

/**
 * Created by fuyz on 2016/9/1.
 * stat文件信息
 */
public class CopyDemo {
    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * 可在用户管理界面-安全管理-域名查询获取uploadDomain,MgrDomain
         */
        Config.MGR_URL = "your MgrDomain";
        String bucketName = "your-bucket";
        String fileKey = "java-sdk/testfile.jpg";
        String newBucketName = "your-bucket";
        String newFileKey = "java-sdk/testfile2.jpg";
        OperationManager fileManageCommand = new OperationManager();
        try {
            HttpClientResult result = fileManageCommand.copy(bucketName, fileKey,newBucketName,newFileKey);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
}
