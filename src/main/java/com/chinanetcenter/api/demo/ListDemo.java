package com.chinanetcenter.api.demo;

import com.chinanetcenter.api.entity.FileListObject;
import com.chinanetcenter.api.entity.FileMessageObject;
import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.util.JsonMapper;
import com.chinanetcenter.api.util.StringUtil;
import com.chinanetcenter.api.wsbox.OperationManager;

import java.io.IOException;

/**
 * Created by fuyz on 2016/9/1.
 * stat文件信息
 */
public class ListDemo {
    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * 可在用户管理界面-安全管理-域名查询获取uploadDomain,MgrDomain
         */
        Config.MGR_URL = "your MgrDomain";
        String bucketName = "your-bucket";
        try {
            ListDemo demo = new ListDemo();
            demo.listFile(bucketName);
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }

    public void listFile(String bucket) throws WsClientException {
        OperationManager fileManageCommand = new OperationManager();
        int querySize = 10;
        String prex = "";
        HttpClientResult result = fileManageCommand.fileList(bucket, String.valueOf(querySize), prex, "", "");
//        System.out.println(result.getStatus() + ":" + result.getInnerResponse());
        while (result != null && result.getStatus() == 200 && StringUtil.isNotEmpty(result.getResponse()) && !"{}".equals(result.getResponse())) {
            JsonMapper objectMapper = new JsonMapper();
            try {
                FileListObject fileListObject = objectMapper.fromJson(result.getResponse(), FileListObject.class);
                for (String folder : fileListObject.getCommonPrefixes()) {
                    System.out.println("folder:" + folder);
                }
                for (FileMessageObject object : fileListObject.getItems()) {
                    System.out.print("key:" + object.getKey() + "\t");
                    System.out.print("putTime:" + object.getPutTime() + "\t");
                    System.out.print("hash:" + object.getHash() + "\t");
                    System.out.print("fsize:" + object.getFsize() + "\t");
                    System.out.print("mimeType:" + object.getMimeType() + "\t");
                    System.out.print("expirationDate:" + object.getExpirationDate() + "\t");
                    System.out.println();
                }
                if (fileListObject.getItems().size() < querySize) {
                    break;
                }
                result = fileManageCommand.fileList(bucket, String.valueOf(querySize), prex, "", fileListObject.getMarker());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
