package com.chinanetcenter.api.wsbox;

import com.chinanetcenter.api.entity.FmgrParam;
import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.http.HttpClientUtil;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.util.EncodeUtils;
import com.chinanetcenter.api.util.EncryptUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fuyz on 2016/9/2.
 * fgmr操作请求
 */
public class FmgrFileManage {

    public HttpClientResult fmgrStatus(String persistentId) throws WsClientException {
        String url = Config.MGR_URL + "/fmgr/status?persistentId=" + persistentId;
        return HttpClientUtil.httpGet(url,null);
    }

    public HttpClientResult fmgrFetch(List<FmgrParam> fmgrList,String notifyUrl,String force,String separate) throws WsClientException {
        String url = Config.MGR_URL + "/fmgr/fetch";
        StringBuffer bodySB = new StringBuffer("");
        StringBuffer fopsSB = new StringBuffer("");
        for (FmgrParam fmgrParam : fmgrList){
            fopsSB.append("fetchURL/").append(EncodeUtils.urlsafeEncode(fmgrParam.getFetchURL()));
            fopsSB.append("/bucket/").append(EncodeUtils.urlsafeEncode(fmgrParam.getBucket()));
            if (StringUtils.isNotEmpty(fmgrParam.getFileKey())){
                fopsSB.append("/key/").append(EncodeUtils.urlsafeEncode(fmgrParam.getFileKey()));
            }
            if (StringUtils.isNotEmpty(fmgrParam.getPrefix())){
                fopsSB.append("/prefix/").append(EncodeUtils.urlsafeEncode(fmgrParam.getPrefix()));
            }
            if (StringUtils.isNotEmpty(fmgrParam.getMd5())){
                fopsSB.append("/md5/").append(EncodeUtils.urlsafeEncode(fmgrParam.getMd5()));
            }
            fopsSB.append(";");
        }
        fopsSB = fopsSB.deleteCharAt(fopsSB.length() - 1);
        bodySB.append("fops=").append(fopsSB.toString());
        if (StringUtils.isNotEmpty(notifyUrl)){
            bodySB.append("&notifyURL=").append(notifyUrl);
        }
        if (StringUtils.isNotEmpty(force)){
            bodySB.append("&force=").append(force);
        }
        if (StringUtils.isNotEmpty(separate)){
            bodySB.append("&separate=").append(separate);
        }
        String value = EncodeUtils.urlsafeEncode(EncryptUtil.sha1Hex(("/fmgr/fetch" + "\n" + bodySB.toString()).getBytes(), Config.SK));
        String authorization = Config.AK + ":" + value;
        Map<String, String> headMap = new HashMap<String, String>();
        headMap.put("Authorization", authorization);
        return HttpClientUtil.httpPostStringEntity(url,headMap,bodySB.toString());
    }

    public HttpClientResult fetchCopy(List<FmgrParam> fmgrList,String notifyUrl,String force,String separate) throws WsClientException {
        String url = Config.MGR_URL + "/fmgr/copy";
        StringBuffer bodySB = new StringBuffer("");
        StringBuffer fopsSB = new StringBuffer("");
        for (FmgrParam fmgrParam : fmgrList){
            fopsSB.append("resource/").append(EncodeUtils.urlsafeEncode(fmgrParam.getResource()));
            fopsSB.append("/bucket/").append(EncodeUtils.urlsafeEncode(fmgrParam.getBucket()));
            if (StringUtils.isNotEmpty(fmgrParam.getFileKey())){
                fopsSB.append("/key/").append(EncodeUtils.urlsafeEncode(fmgrParam.getFileKey()));
            }
            if (StringUtils.isNotEmpty(fmgrParam.getPrefix())){
                fopsSB.append("/prefix/").append(EncodeUtils.urlsafeEncode(fmgrParam.getPrefix()));
            }
            fopsSB.append(";");
        }
        fopsSB = fopsSB.deleteCharAt(fopsSB.length() - 1);
        bodySB.append("fops=").append(fopsSB.toString());
        if (StringUtils.isNotEmpty(notifyUrl)){
            bodySB.append("&notifyURL=").append(notifyUrl);
        }
        if (StringUtils.isNotEmpty(force)){
            bodySB.append("&force=").append(force);
        }
        if (StringUtils.isNotEmpty(separate)){
            bodySB.append("&separate=").append(separate);
        }
        String value = EncodeUtils.urlsafeEncode(EncryptUtil.sha1Hex(("/fmgr/copy" + "\n" + bodySB.toString()).getBytes(), Config.SK));
        String authorization = Config.AK + ":" + value;
        Map<String, String> headMap = new HashMap<String, String>();
        headMap.put("Authorization", authorization);
        return HttpClientUtil.httpPostStringEntity(url,headMap,bodySB.toString());
    }

    public HttpClientResult fetchMove(List<FmgrParam> fmgrList,String notifyUrl,String force,String separate) throws WsClientException {
        String url = Config.MGR_URL + "/fmgr/move";
        StringBuffer bodySB = new StringBuffer("");
        StringBuffer fopsSB = new StringBuffer("");
        for (FmgrParam fmgrParam : fmgrList){
            fopsSB.append("resource/").append(EncodeUtils.urlsafeEncode(fmgrParam.getResource()));
            fopsSB.append("/bucket/").append(EncodeUtils.urlsafeEncode(fmgrParam.getBucket()));
            if (StringUtils.isNotEmpty(fmgrParam.getFileKey())){
                fopsSB.append("/key/").append(EncodeUtils.urlsafeEncode(fmgrParam.getFileKey()));
            }
            if (StringUtils.isNotEmpty(fmgrParam.getPrefix())){
                fopsSB.append("/prefix/").append(EncodeUtils.urlsafeEncode(fmgrParam.getPrefix()));
            }
            fopsSB.append(";");
        }
        fopsSB = fopsSB.deleteCharAt(fopsSB.length() - 1);
        bodySB.append("fops=").append(fopsSB.toString());
        if (StringUtils.isNotEmpty(notifyUrl)){
            bodySB.append("&notifyURL=").append(notifyUrl);
        }
        if (StringUtils.isNotEmpty(force)){
            bodySB.append("&force=").append(force);
        }
        if (StringUtils.isNotEmpty(separate)){
            bodySB.append("&separate=").append(separate);
        }
        String value = EncodeUtils.urlsafeEncode(EncryptUtil.sha1Hex(("/fmgr/move" + "\n" + bodySB.toString()).getBytes(), Config.SK));
        String authorization = Config.AK + ":" + value;
        Map<String, String> headMap = new HashMap<String, String>();
        headMap.put("Authorization", authorization);
        return HttpClientUtil.httpPostStringEntity(url,headMap,bodySB.toString());
    }

    public HttpClientResult fetchDelete(List<FmgrParam> fmgrList,String notifyUrl,String force,String separate) throws WsClientException {
        String url = Config.MGR_URL + "/fmgr/delete";
        StringBuffer bodySB = new StringBuffer("");
        StringBuffer fopsSB = new StringBuffer("");
        for (FmgrParam fmgrParam : fmgrList){
            fopsSB.append("bucket/").append(EncodeUtils.urlsafeEncode(fmgrParam.getBucket()));
            if (StringUtils.isNotEmpty(fmgrParam.getFileKey())){
                fopsSB.append("/key/").append(EncodeUtils.urlsafeEncode(fmgrParam.getFileKey()));
            }
            fopsSB.append(";");
        }
        fopsSB = fopsSB.deleteCharAt(fopsSB.length() - 1);
        bodySB.append("fops=").append(fopsSB.toString());
        if (StringUtils.isNotEmpty(notifyUrl)){
            bodySB.append("&notifyURL=").append(notifyUrl);
        }
        if (StringUtils.isNotEmpty(force)){
            bodySB.append("&force=").append(force);
        }
        if (StringUtils.isNotEmpty(separate)){
            bodySB.append("&separate=").append(separate);
        }
        String value = EncodeUtils.urlsafeEncode(EncryptUtil.sha1Hex(("/fmgr/delete" + "\n" + bodySB.toString()).getBytes(), Config.SK));
        String authorization = Config.AK + ":" + value;
        Map<String, String> headMap = new HashMap<String, String>();
        headMap.put("Authorization", authorization);
        return HttpClientUtil.httpPostStringEntity(url,headMap,bodySB.toString());
    }

    public HttpClientResult fetchDeletePrefix(List<FmgrParam> fmgrList,String notifyUrl,String force,String separate) throws WsClientException {
        String url = Config.MGR_URL + "/fmgr/deletePrefix";
        StringBuffer bodySB = new StringBuffer("");
        StringBuffer fopsSB = new StringBuffer("");
        for (FmgrParam fmgrParam : fmgrList){
            fopsSB.append("bucket/").append(EncodeUtils.urlsafeEncode(fmgrParam.getBucket()));
            fopsSB.append("/prefix/").append(EncodeUtils.urlsafeEncode(fmgrParam.getPrefix()));
            if (StringUtils.isNotEmpty(fmgrParam.getOutput())){
                fopsSB.append("/output/").append(EncodeUtils.urlsafeEncode(fmgrParam.getOutput()));
            }
            fopsSB.append(";");
        }
        fopsSB = fopsSB.deleteCharAt(fopsSB.length() - 1);
        bodySB.append("fops=").append(fopsSB.toString());
        if (StringUtils.isNotEmpty(notifyUrl)){
            bodySB.append("&notifyURL=").append(notifyUrl);
        }
        if (StringUtils.isNotEmpty(force)){
            bodySB.append("&force=").append(force);
        }
        if (StringUtils.isNotEmpty(separate)){
            bodySB.append("&separate=").append(separate);
        }
        String value = EncodeUtils.urlsafeEncode(EncryptUtil.sha1Hex(("/fmgr/deletePrefix" + "\n" + bodySB.toString()).getBytes(), Config.SK));
        String authorization = Config.AK + ":" + value;
        Map<String, String> headMap = new HashMap<String, String>();
        headMap.put("Authorization", authorization);
        return HttpClientUtil.httpPostStringEntity(url,headMap,bodySB.toString());
    }
}
