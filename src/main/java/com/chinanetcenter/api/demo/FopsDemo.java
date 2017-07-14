package com.chinanetcenter.api.demo;

import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.util.EncodeUtils;
import com.chinanetcenter.api.wsbox.OperationManager;

/**
 * Created by fuyz on 2016/9/1.
 * 转码请求
 */
public class FopsDemo {
    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * 可在用户管理界面-安全管理-域名查询获取uploadDomain,MgrDomain
         */
        Config.MGR_URL = "your MgrDomain";
        String bucketName = "your-bucket";
        String fileKey = "java-sdk/10m2.mp4";
        //设置转码操作参数
        String fops = "avthumb/mp4/s/640x360/vb/1.25m";
        //可以对转码后的文件进行使用saveas参数自定义命名，
        //也可以不指定,会默认命名并保存在当前空间 对 目标Bucket_Name:自定义文件key 做base64。
        String saveAsKey = EncodeUtils.urlsafeEncode(bucketName + ":1.256m.jpg");
        fops += "|saveas/" + saveAsKey;
        String notifyURL = "http://demo1/notifyUrl";  //通知地址，转码成功后会回调此地址
        String force = "1";
        String separate = "1";
        FopsDemo demo = new FopsDemo();
        demo.fileTrans(bucketName,fileKey,fops,notifyURL,force,separate);

    }

    public void fileTrans(String bucketName, String fileKey, String fops, String notifyURL, String force,String separate) {
        OperationManager fileManageCommand = new OperationManager();
        try {
            HttpClientResult result = fileManageCommand.fops(bucketName, fileKey, fops, notifyURL,force,separate);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
}
