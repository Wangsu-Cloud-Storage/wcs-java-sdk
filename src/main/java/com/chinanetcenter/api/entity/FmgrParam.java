package com.chinanetcenter.api.entity;

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
}
