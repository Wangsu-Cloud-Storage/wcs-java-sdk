package com.chinanetcenter.api.demo;

import com.chinanetcenter.api.entity.FmgrParam;
import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.wsbox.FmgrFileManage;

import java.util.ArrayList;
import java.util.List;

/**
 * 用途描述：高级资源管理-复制资源
 * Created by chenql on 2018/4/3.
 */
public class FmgrCopyDemo {
    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * 可在用户管理界面-安全管理-域名查询获取uploadDomain,MgrDomain
         */
        Config.MGR_URL = "your MgrDomain";
        String bucketName = "your-bucket";
        FmgrFileManage fileManageCommand = new FmgrFileManage();
        try {
            List<FmgrParam> list = new ArrayList<FmgrParam>();
            FmgrParam fmgrParam = new FmgrParam();
            fmgrParam.setBucket(bucketName);
            fmgrParam.setResource("mybucket:aa.jpg");
            fmgrParam.setFileKey("copy/a.jpg");
            list.add(fmgrParam);
            FmgrParam fmgrParam2 = new FmgrParam();
            fmgrParam2.setBucket(bucketName);
            fmgrParam2.setResource("mybucket:bb.jpg");
            fmgrParam2.setFileKey("copy/b.jpg");
            list.add(fmgrParam2);
            String notifyURL = "http://demo1/notifyUrl";  //通知地址，转码成功后会回调此地址
            String separate = "1";
            HttpClientResult result = fileManageCommand.fmgrCopy(list, notifyURL, null, separate);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
}
