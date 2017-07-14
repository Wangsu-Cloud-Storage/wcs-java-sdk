package com.chinanetcenter.api.wsbox;

import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.http.HttpClientUtil;
import com.chinanetcenter.api.util.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fuyz on 2016/6/27.
 * 直播的文件管理
 */
public class WsliveFileManage {

    /**
     *根据流名获取文件列表
     */
    public static HttpClientResult fileList(String bucketName, String channelname, String startTime, String endTime,String start, String limit) throws WsClientException {
        String url = "/wslive/list?" + "channelname=" + channelname + "&bucket=" + bucketName + "&startTime=" + startTime +"&endTime=" + endTime + "&start="+ start + "&limit=" + limit;
        Map<String, String> headMap = new HashMap<String, String>();
        String listToken = TokenUtil.getFileListToken(url);
        url = Config.MGR_URL + url;
        headMap.put("Authorization", listToken);
        return HttpClientUtil.httpGet(url, headMap);
    }

    /**
     *设置文件的过期时间
     * 超过过期时间自动删除
     */
    public static HttpClientResult setDeadline(String bucketName, String key, int deadline) throws WsClientException {
        String body = "bucket=" + EncodeUtils.urlsafeEncode(bucketName);
        body += "&key=" + EncodeUtils.urlsafeEncode(key) + "&deadline=" + deadline;
        String url = Config.MGR_URL + "/wslive/setdeadline";
        String value = EncodeUtils.urlsafeEncode(EncryptUtil.sha1Hex(("/wslive/setdeadline" + "\n" + body).getBytes(), Config.SK));
        String Authorization = Config.AK + ":" + value;
        Map<String, String> headMap = new HashMap<String, String>();
        headMap.put("Authorization", Authorization);
        return HttpClientUtil.httpPostStringEntity(url, headMap, body);
    }
}
