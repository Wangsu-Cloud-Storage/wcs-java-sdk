package com.chinanetcenter.api.sliceUpload;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.ByteArrayInputStream;
import java.io.RandomAccessFile;

/**
 * Created by fuyz on 2014/8/5.
 * 切片上传块对象属性
 */
public class BlockObject {

    public RandomAccessFile file;
    private String bucketName;
    private String fileKey;
    private int blockIdx;
    private long start;
    private long offset;
    private int blockLen;
    private String lastCtx;
    private String token;
    private long successLength;
    private byte[] data;
    private ByteArrayInputStream blockBuffer;


    public BlockObject(RandomAccessFile file, String bucketName, String fileKey, int blockIdx, int blockLen) {
        this.file = file;
//        this.putPolicy = putPolicy;
        this.bucketName = bucketName;
        this.fileKey = fileKey;
        this.blockIdx = blockIdx;
        this.start = 0;
        this.offset = (long) BaseBlockUtil.BLOCK_SIZE * blockIdx;
        this.blockLen = blockLen;
    }

    public BlockObject(JsonNode obj) {
        parse(obj);
    }

    public BlockObject(byte[] data, String bucketName, String fileKey, int blockIdx, int blockLen) {
        this.data = data;
        this.bucketName = bucketName;
        this.fileKey = fileKey;
        this.blockIdx = blockIdx;
        this.blockLen = blockLen;
        this.blockBuffer = new ByteArrayInputStream(this.data);
    }

    public void setCommonParam(RandomAccessFile file, String bucketName, String fileKey) {
        this.file = file;
        this.bucketName = bucketName;
        this.fileKey = fileKey;
    }

    public BlockObject parse(JsonNode obj) {
        blockIdx = obj.path("blockIdx").asInt(0);
        start = obj.path("start").asLong(0);
        offset = obj.path("offset").asLong(0);
        blockLen = obj.path("blockLen").asInt(0);
        lastCtx = (obj.path("ctx").asText() == null ? "" : obj.path("ctx").asText());
        return this;
    }

    public void addSuccessLength(long len) {
        successLength += len;
        start += len;
    }

    public JsonNode toJSON() {
        ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put("blockIdx", blockIdx);
        json.put("start", start);
        json.put("offset", offset);
        json.put("blockLen", blockLen);
        json.put("ctx", lastCtx);
        return json;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public int getBlockIdx() {
        return blockIdx;
    }

    public void setBlockIdx(int blockIdx) {
        this.blockIdx = blockIdx;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public int getBlockLen() {
        return blockLen;
    }

    public void setBlockLen(int blockLen) {
        this.blockLen = blockLen;
    }

    public String getLastCtx() {
        return lastCtx;
    }

    public void setLastCtx(String lastCtx) {
        this.lastCtx = lastCtx;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getSuccessLength() {
        return successLength;
    }

    public void setSuccessLength(long successLength) {
        this.successLength = successLength;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public ByteArrayInputStream getBlockBuffer() {
        return blockBuffer;
    }

    public void setBlockBuffer(ByteArrayInputStream blockBuffer) {
        this.blockBuffer = blockBuffer;
    }
}
