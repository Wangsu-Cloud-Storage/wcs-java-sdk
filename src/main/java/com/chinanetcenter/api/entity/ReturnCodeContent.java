package com.chinanetcenter.api.entity;

import java.util.Map;

/**
 * @version 1.0
 * @since 2014/02/14
 */
public class ReturnCodeContent {

    private int code;
    private boolean result;
    private String error;
    private String message;
    private String name;
    private int fsize;
    private String mimeType;
    private long putTime;
    private String width;
    private String height;
    private String hash;
    private String accessKey;
    private String secretKey;
    private Map<String, String> responseMap;
    private String infos;
    private String imageProgressResult;
    private String size;
    private String colorModel;
    private String exif;
    private String returnbody;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFsize() {
        return fsize;
    }

    public void setFsize(int fsize) {
        this.fsize = fsize;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getPutTime() {
        return putTime;
    }

    public void setPutTime(long putTime) {
        this.putTime = putTime;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Map<String, String> getResponseMap() {
        return responseMap;
    }

    public void setResponseMap(Map<String, String> responseMap) {
        this.responseMap = responseMap;
    }

    public String getInfos() {
        return infos;
    }

    public void setInfos(String infos) {
        this.infos = infos;
    }

    public String getImageProgressResult() {
        return imageProgressResult;
    }

    public void setImageProgressResult(String imageProgressResult) {
        this.imageProgressResult = imageProgressResult;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColorModel() {
        return colorModel;
    }

    public void setColorModel(String colorModel) {
        this.colorModel = colorModel;
    }

    public String getExif() {
        return exif;
    }

    public void setExif(String exif) {
        this.exif = exif;
    }

    public String getReturnbody() {
        return returnbody;
    }

    public void setReturnbody(String returnbody) {
        this.returnbody = returnbody;
    }

    @Override
    public String toString() {
        return "ReturnCodeContent{" +
                "code=" + code +
                ", result=" + result +
                ", error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", name='" + name + '\'' +
                ", fsize=" + fsize +
                ", mimeType='" + mimeType + '\'' +
                ", putTime=" + putTime +
                ", width='" + width + '\'' +
                ", height='" + height + '\'' +
                ", hash='" + hash + '\'' +
                ", accessKey='" + accessKey + '\'' +
                ", secretKey='" + secretKey + '\'' +
                ", responseMap=" + responseMap +
                ", infos='" + infos + '\'' +
                ", imageProgressResult='" + imageProgressResult + '\'' +
                ", size='" + size + '\'' +
                ", colorModel='" + colorModel + '\'' +
                ", exif='" + exif + '\'' +
                ", returnbody='" + returnbody + '\'' +
                '}';
    }
}
