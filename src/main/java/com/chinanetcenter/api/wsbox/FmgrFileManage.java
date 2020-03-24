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

    /**
     * Fmgr任务查询
     * https://wcs.chinanetcenter.com/document/API/Fmgr/status
     */
    public HttpClientResult fmgrStatus(String persistentId) throws WsClientException {
        String url = Config.MGR_URL + "/fmgr/status?persistentId=" + persistentId;
        return HttpClientUtil.httpGet(url,null);
    }

    /**
     * 抓取资源
     * https://wcs.chinanetcenter.com/document/API/Fmgr/fetch
     */
    public HttpClientResult fmgrFetch(List<FmgrParam> fmgrList,String notifyUrl,String force,String separate) throws WsClientException {
        String url = Config.MGR_URL + "/fmgr/fetch";
        StringBuilder bodySB = new StringBuilder("");
        StringBuilder fopsSB = new StringBuilder("");
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
            if (fmgrParam.getParamMap() != null && fmgrParam.getParamMap().size() > 0) {
                for (Map.Entry<String, String> entry : fmgrParam.getParamMap().entrySet()) {
                    fopsSB.append("/").append(entry.getKey()).append("/").append(entry.getValue());
                }
            }
            fopsSB.append(";");
        }
        fopsSB = fopsSB.deleteCharAt(fopsSB.length() - 1);
        bodySB.append("fops=").append(fopsSB.toString());
        if (StringUtils.isNotEmpty(notifyUrl)){
            bodySB.append("&notifyURL=").append(EncodeUtils.urlsafeEncode(notifyUrl));
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

    public HttpClientResult fetchCopy(List<FmgrParam> fmgrList, String notifyUrl, String force, String separate) throws WsClientException {
        return fmgrCopy(fmgrList, notifyUrl, force, separate);
    }

    /**
     * 复制资源
     * https://wcs.chinanetcenter.com/document/API/Fmgr/copy
     */
    public HttpClientResult fmgrCopy(List<FmgrParam> fmgrList, String notifyUrl, String force, String separate) throws WsClientException {
        String url = Config.MGR_URL + "/fmgr/copy";
        StringBuilder bodySB = new StringBuilder("");
        StringBuilder fopsSB = new StringBuilder("");
        for (FmgrParam fmgrParam : fmgrList){
            fopsSB.append("resource/").append(EncodeUtils.urlsafeEncode(fmgrParam.getResource()));
            fopsSB.append("/bucket/").append(EncodeUtils.urlsafeEncode(fmgrParam.getBucket()));
            if (StringUtils.isNotEmpty(fmgrParam.getFileKey())){
                fopsSB.append("/key/").append(EncodeUtils.urlsafeEncode(fmgrParam.getFileKey()));
            }
            if (StringUtils.isNotEmpty(fmgrParam.getPrefix())){
                fopsSB.append("/prefix/").append(EncodeUtils.urlsafeEncode(fmgrParam.getPrefix()));
            }
            if (fmgrParam.getParamMap() != null && fmgrParam.getParamMap().size() > 0) {
                for (Map.Entry<String, String> entry : fmgrParam.getParamMap().entrySet()) {
                    fopsSB.append("/").append(entry.getKey()).append("/").append(entry.getValue());
                }
            }
            fopsSB.append(";");
        }
        fopsSB = fopsSB.deleteCharAt(fopsSB.length() - 1);
        bodySB.append("fops=").append(fopsSB.toString());
        if (StringUtils.isNotEmpty(notifyUrl)){
            bodySB.append("&notifyURL=").append(EncodeUtils.urlsafeEncode(notifyUrl));
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

    public HttpClientResult fetchMove(List<FmgrParam> fmgrList, String notifyUrl, String force, String separate) throws WsClientException {
        return fmgrMove(fmgrList, notifyUrl, force, separate);
    }

    /**
     * 移动资源
     * https://wcs.chinanetcenter.com/document/API/Fmgr/move
     */
    public HttpClientResult fmgrMove(List<FmgrParam> fmgrList, String notifyUrl, String force, String separate) throws WsClientException {
        String url = Config.MGR_URL + "/fmgr/move";
        StringBuilder bodySB = new StringBuilder("");
        StringBuilder fopsSB = new StringBuilder("");
        for (FmgrParam fmgrParam : fmgrList){
            fopsSB.append("resource/").append(EncodeUtils.urlsafeEncode(fmgrParam.getResource()));
            fopsSB.append("/bucket/").append(EncodeUtils.urlsafeEncode(fmgrParam.getBucket()));
            if (StringUtils.isNotEmpty(fmgrParam.getFileKey())){
                fopsSB.append("/key/").append(EncodeUtils.urlsafeEncode(fmgrParam.getFileKey()));
            }
            if (StringUtils.isNotEmpty(fmgrParam.getPrefix())){
                fopsSB.append("/prefix/").append(EncodeUtils.urlsafeEncode(fmgrParam.getPrefix()));
            }
            if (fmgrParam.getParamMap() != null && fmgrParam.getParamMap().size() > 0) {
                for (Map.Entry<String, String> entry : fmgrParam.getParamMap().entrySet()) {
                    fopsSB.append("/").append(entry.getKey()).append("/").append(entry.getValue());
                }
            }
            fopsSB.append(";");
        }
        fopsSB = fopsSB.deleteCharAt(fopsSB.length() - 1);
        bodySB.append("fops=").append(fopsSB.toString());
        if (StringUtils.isNotEmpty(notifyUrl)){
            bodySB.append("&notifyURL=").append(EncodeUtils.urlsafeEncode(notifyUrl));
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

    public HttpClientResult fetchDelete(List<FmgrParam> fmgrList, String notifyUrl, String force, String separate) throws WsClientException {
        return fmgrDelete(fmgrList, notifyUrl, force, separate);
    }

    /**
     * 删除资源
     * https://wcs.chinanetcenter.com/document/API/Fmgr/delete
     */
    public HttpClientResult fmgrDelete(List<FmgrParam> fmgrList, String notifyUrl, String force, String separate) throws WsClientException {
        String url = Config.MGR_URL + "/fmgr/delete";
        StringBuilder bodySB = new StringBuilder("");
        StringBuilder fopsSB = new StringBuilder("");
        for (FmgrParam fmgrParam : fmgrList){
            fopsSB.append("bucket/").append(EncodeUtils.urlsafeEncode(fmgrParam.getBucket()));
            if (StringUtils.isNotEmpty(fmgrParam.getFileKey())){
                fopsSB.append("/key/").append(EncodeUtils.urlsafeEncode(fmgrParam.getFileKey()));
            }
            if (fmgrParam.getParamMap() != null && fmgrParam.getParamMap().size() > 0) {
                for (Map.Entry<String, String> entry : fmgrParam.getParamMap().entrySet()) {
                    fopsSB.append("/").append(entry.getKey()).append("/").append(entry.getValue());
                }
            }
            fopsSB.append(";");
        }
        fopsSB = fopsSB.deleteCharAt(fopsSB.length() - 1);
        bodySB.append("fops=").append(fopsSB.toString());
        if (StringUtils.isNotEmpty(notifyUrl)){
            bodySB.append("&notifyURL=").append(EncodeUtils.urlsafeEncode(notifyUrl));
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

    public HttpClientResult fetchDeletePrefix(List<FmgrParam> fmgrList, String notifyUrl, String force, String separate) throws WsClientException {
        return fmgrDeletePrefix(fmgrList, notifyUrl, force, separate);
    }

    /**
     * 按前缀删除资源
     * https://wcs.chinanetcenter.com/document/API/Fmgr/deletePrefix
     */
    public HttpClientResult fmgrDeletePrefix(List<FmgrParam> fmgrList, String notifyUrl, String force, String separate) throws WsClientException {
        String url = Config.MGR_URL + "/fmgr/deletePrefix";
        StringBuilder bodySB = new StringBuilder("");
        StringBuilder fopsSB = new StringBuilder("");
        for (FmgrParam fmgrParam : fmgrList){
            fopsSB.append("bucket/").append(EncodeUtils.urlsafeEncode(fmgrParam.getBucket()));
            fopsSB.append("/prefix/").append(EncodeUtils.urlsafeEncode(fmgrParam.getPrefix()));
            if (StringUtils.isNotEmpty(fmgrParam.getOutput())){
                fopsSB.append("/output/").append(EncodeUtils.urlsafeEncode(fmgrParam.getOutput()));
            }
            if (fmgrParam.getParamMap() != null && fmgrParam.getParamMap().size() > 0) {
                for (Map.Entry<String, String> entry : fmgrParam.getParamMap().entrySet()) {
                    fopsSB.append("/").append(entry.getKey()).append("/").append(entry.getValue());
                }
            }
            fopsSB.append(";");
        }
        fopsSB = fopsSB.deleteCharAt(fopsSB.length() - 1);
        bodySB.append("fops=").append(fopsSB.toString());
        if (StringUtils.isNotEmpty(notifyUrl)){
            bodySB.append("&notifyURL=").append(EncodeUtils.urlsafeEncode(notifyUrl));
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

    /**
     * 删除m3u8文件
     * https://wcs.chinanetcenter.com/document/API/Fmgr/deletem3u8
     */
    public HttpClientResult fmgrDeleteM3U8(List<FmgrParam> fmgrList, String notifyUrl, String separate) throws WsClientException {
        String url = Config.MGR_URL + "/fmgr/deletem3u8";
        StringBuilder bodySB = new StringBuilder("");
        StringBuilder fopsSB = new StringBuilder("");
        for (FmgrParam fmgrParam : fmgrList) {
            fopsSB.append("bucket/").append(EncodeUtils.urlsafeEncode(fmgrParam.getBucket()));
            fopsSB.append("/key/").append(EncodeUtils.urlsafeEncode(fmgrParam.getFileKey()));
            if (StringUtils.isNotEmpty(fmgrParam.getDeletets())) {
                fopsSB.append("/deletets/").append(fmgrParam.getDeletets());
            }
            if (fmgrParam.getParamMap() != null && fmgrParam.getParamMap().size() > 0) {
                for (Map.Entry<String, String> entry : fmgrParam.getParamMap().entrySet()) {
                    fopsSB.append("/").append(entry.getKey()).append("/").append(entry.getValue());
                }
            }
            fopsSB.append(";");
        }
        fopsSB = fopsSB.deleteCharAt(fopsSB.length() - 1);
        bodySB.append("fops=").append(fopsSB.toString());
        if (StringUtils.isNotEmpty(notifyUrl)) {
            bodySB.append("&notifyURL=").append(EncodeUtils.urlsafeEncode(notifyUrl));
        }
        if (StringUtils.isNotEmpty(separate)) {
            bodySB.append("&separate=").append(separate);
        }
        String value = EncodeUtils.urlsafeEncode(EncryptUtil.sha1Hex(("/fmgr/deletem3u8" + "\n" + bodySB.toString()).getBytes(), Config.SK));
        String authorization = Config.AK + ":" + value;
        Map<String, String> headMap = new HashMap<String, String>();
        headMap.put("Authorization", authorization);
        return HttpClientUtil.httpPostStringEntity(url, headMap, bodySB.toString());
    }

    /**
     * 批量修改文件保存期限
     * https://wcs.chinanetcenter.com/document/API/Fmgr/setdeadline
     */
    public HttpClientResult fmgrSetdeadline(List<FmgrParam> fmgrList, String notifyUrl) throws WsClientException {
        String url = Config.MGR_URL + "/fmgr/setdeadline";
        StringBuilder bodySB = new StringBuilder("");
        StringBuilder fopsSB = new StringBuilder("");
        for (FmgrParam fmgrParam : fmgrList) {
            fopsSB.append("bucket/").append(EncodeUtils.urlsafeEncode(fmgrParam.getBucket()));
            fopsSB.append("/prefix/").append(EncodeUtils.urlsafeEncode(fmgrParam.getPrefix()));
            fopsSB.append("/deadline/").append(fmgrParam.getDeadline());
            if (fmgrParam.getParamMap() != null && fmgrParam.getParamMap().size() > 0) {
                for (Map.Entry<String, String> entry : fmgrParam.getParamMap().entrySet()) {
                    fopsSB.append("/").append(entry.getKey()).append("/").append(entry.getValue());
                }
            }
            fopsSB.append(";");
        }
        fopsSB = fopsSB.deleteCharAt(fopsSB.length() - 1);
        bodySB.append("fops=").append(fopsSB.toString());
        if (StringUtils.isNotEmpty(notifyUrl)) {
            bodySB.append("&notifyURL=").append(EncodeUtils.urlsafeEncode(notifyUrl));
        }
        String value = EncodeUtils.urlsafeEncode(EncryptUtil.sha1Hex(("/fmgr/setdeadline" + "\n" + bodySB.toString()).getBytes(), Config.SK));
        String authorization = Config.AK + ":" + value;
        Map<String, String> headMap = new HashMap<String, String>();
        headMap.put("Authorization", authorization);
        return HttpClientUtil.httpPostStringEntity(url, headMap, bodySB.toString());
    }
}
