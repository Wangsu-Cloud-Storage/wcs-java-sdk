package com.chinanetcenter.api.wsbox;

import com.chinanetcenter.api.entity.Avinfo;
import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.util.EncodeUtils;
import com.chinanetcenter.api.http.HttpClientUtil;
import com.chinanetcenter.api.util.JsonMapper;

/**
 * Created by fuyz on 2015/7/29.
 * 获取文件的avinfo
 */
public class FileAvInfo {

    public static Avinfo getFileAvinfo(String fileKey) throws WsClientException {
        StringBuilder url = new StringBuilder(Config.GET_URL);
        String encodeKey = EncodeUtils.escapeFileKey(fileKey);//进行编码
        url.append("/").append(encodeKey);
        url.append("?op=avinfo");
        HttpClientResult result = HttpClientUtil.httpGet(url.toString(), null);
        if (result.getStatus() == 200){
            JsonMapper jsonMapper = new JsonMapper();
            return jsonMapper.fromJson(result.getResponse(),Avinfo.class);
        }else{
            System.out.println("get avinfo error,status:" + result.getStatus() + ",message:" + result.getResponse());
            return null;
        }
    }
}
