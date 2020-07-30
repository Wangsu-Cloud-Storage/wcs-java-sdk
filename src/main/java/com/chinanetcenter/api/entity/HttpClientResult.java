package com.chinanetcenter.api.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;

/**
 * HTTP返回结果<br>
 * status  HTTP状态码，比如200,500,404
 * response  返回应答信息
 * innerResponse 回调服务器返回的应答信息,json字符串
 * responseObject 回调服务器返回的Json
 * @version 1.0
 * @since 2014/02/14
 */
public class HttpClientResult {
    public String url;

    /**
     * HTTP状态码
     */
    public int status;

    /**
     * 提示信息
     */
    public String response;

    public JsonNode responseObject;

    private String innerResponse;

    private InputStream content;

    public HttpClientResult() {
    }

    public HttpClientResult(int status) {
        this.status = status;
    }

    public HttpClientResult(int status, String response) {
        this.status = status;
        this.innerResponse = response;
        this.response = response;
        try {
            if (new JsonValidator().validate(response)) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode innerJsonObject;
                innerJsonObject = objectMapper.readTree(innerResponse);
                JsonNode jsonObject = objectMapper.readTree(innerJsonObject.path("response").asText());
                if (jsonObject != null) {
                    this.response = jsonObject.toString();
                    this.responseObject = jsonObject;
                }
            }
        } catch (Exception e) {
            this.response = innerResponse;
        }
    }

    public HttpClientResult(String url,int status, String response) {
        this(status,response);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getInnerResponse() {
        return innerResponse;
    }

    public void setInnerResponse(String innerResponse) {
        this.innerResponse = innerResponse;
    }

    public JsonNode getResponseObject() {
        return responseObject;
    }

    public void setResponseObject(JsonNode responseObject) {
        this.responseObject = responseObject;
    }

    public InputStream getContent() {
        return content;
    }

    public void setContent(InputStream content) {
        this.content = content;
    }

    @Override
    /**
     *
     */
    public String toString() {
        return "HttpClientResult{" +
                "url=" + url +
                ",status=" + status +
                ", response='" + response +
                '}';
    }
}
