package com.chinanetcenter.api.demo;

import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.wsbox.OperationManager;

/**
 * 下载文件
 */
public class DownloadDemo {
    public static void main(String[] args) {
        String downloadDomain = "your download domain";
        String fileKey = "file name";
        String filePath = "local path";
        OperationManager fileManageCommand = new OperationManager();
        try {
            HttpClientResult result = fileManageCommand.download(downloadDomain, fileKey, filePath, null);
            System.out.println(result.getStatus());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
}
