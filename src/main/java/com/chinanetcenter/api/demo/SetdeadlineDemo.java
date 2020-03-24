package com.chinanetcenter.api.demo;

import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.wsbox.WsliveFileManage;

/**
 * 用途描述：资源管理-设置文件保存期限
 * Created by chenql on 2018/4/3.
 */
public class SetdeadlineDemo {

    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * 可在用户管理界面-安全管理-域名查询获取uploadDomain,MgrDomain
         */
        Config.MGR_URL = "your MgrDomain";
        String bucketName = "your-bucket";
        String fileKey = "java-sdk/testfile.jpg";
        int deadline = 30;//代表30天后过期
        WsliveFileManage manageCommand = new WsliveFileManage();
        try {
            HttpClientResult result = manageCommand.setDeadline(bucketName, fileKey, deadline);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
}
