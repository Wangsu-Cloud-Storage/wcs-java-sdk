package com.chinanetcenter.api.demo;

import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.wsbox.FmgrFileManage;

/**
 * 用途描述：高级资源管理-任务查询
 * Created by chenql on 2018/4/3.
 */
public class FmgrStatusDemo {
    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * 可在用户管理界面-安全管理-域名查询获取uploadDomain,MgrDomain
         */
        Config.MGR_URL = "your MgrDomain";
        String persistentId = "your-persistentId";
        FmgrFileManage fileManageCommand = new FmgrFileManage();
        try {
            HttpClientResult result = fileManageCommand.fmgrStatus(persistentId);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
}
