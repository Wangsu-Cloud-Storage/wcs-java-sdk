package com.chinanetcenter.api.entity;

import com.chinanetcenter.api.util.JsonMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by Administrator on 2014/8/5.
 */
public class SliceUploadHttpResult extends HttpClientResult {
    public String ctx;
    public String checksum;
    public int offset;
    public long crc32;
    public String hash;
    public JsonNode responseJson;


    public SliceUploadHttpResult() {

    }

    public SliceUploadHttpResult(JsonNode obj) {
        this.ctx = obj.path("ctx").asText() == null ? "" : obj.path("ctx").asText();
        this.hash = obj.path("hash").asText() == null ? "" : obj.path("hash").asText();
        this.crc32 = obj.path("crc32").asLong(0);
        this.checksum = obj.path("checksum").asText() == null ? "" : obj.path("checksum").asText();
        this.offset = obj.path("offset").asInt(0);
    }

    public SliceUploadHttpResult(int status, String response) {
        this.status = status;
        this.response = response;
        if (this.response != null && this.response.trim().startsWith("{")) {
            jsonToObject();
        }
    }

    public boolean isOk() {
        return this.status / 100 == 2;
    }

    public void jsonToObject() {
        JsonMapper jsonMapper = JsonMapper.nonEmptyMapper();
        SliceUploadHttpResult result = jsonMapper.fromJson(this.response,
                SliceUploadHttpResult.class);
        if (result != null) {
            this.ctx = result.getCtx();
            this.checksum = result.getChecksum();
            this.offset = result.getOffset();
            this.crc32 = result.getCrc32();
            this.hash = result.getHash();
        }
    }

    public String getCtx() {
        return ctx;
    }

    public void setCtx(String ctx) {
        this.ctx = ctx;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public long getCrc32() {
        return crc32;
    }

    public void setCrc32(long crc32) {
        this.crc32 = crc32;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public JsonNode getResponseJson() {
        return responseJson;
    }

    public void setResponseJson(JsonNode responseJson) {
        this.responseJson = responseJson;
    }

    public JsonNode toJSON() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(this.response);
    }
}
