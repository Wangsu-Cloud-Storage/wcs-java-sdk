package com.chinanetcenter.api.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fuyz on 2016/9/2.
 * Fmgr参数对象
 */
public class FmgrParam {
    private String fetchURL;
    private String bucket;
    private String fileKey;
    private String prefix;
    private String md5;
    private String resource;
    private String output;
    private String deletets;//指定是否进行关联删除ts文件。  0 不进行关联删除  1 删除关联的ts文件
    private int deadline = -1;//文件保存期限。超过保存天数文件自动删除,单位：天。例如：1、2、3…… 注：0表示尽快删除，-1表示取消过期时间，永久保存
    /**
     * 用于存放后期扩展的参数，key value得根据文档中心的格式填
     */
    private Map<String, String> paramMap = new HashMap<String, String>();

    public String getFetchURL() {
        return fetchURL;
    }

    public void setFetchURL(String fetchURL) {
        this.fetchURL = fetchURL;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getDeletets() {
        return deletets;
    }

    public void setDeletets(String deletets) {
        this.deletets = deletets;
    }

    public int getDeadline() {
        return deadline;
    }

    public void setDeadline(int deadline) {
        this.deadline = deadline;
    }

    public Map<String, String> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, String> paramMap) {
        this.paramMap = paramMap;
    }

    public void putExtParams(String key, String value) {
        this.paramMap.put(key, value);
    }
}
