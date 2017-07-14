package com.chinanetcenter.api.exception;

import com.chinanetcenter.api.entity.HttpClientResult;

import java.io.IOException;


public class WsClientException extends IOException {
  public int code;
  public HttpClientResult response;

  public WsClientException(int code,String message) {
    super(message);
    this.code = code;
  }

  public WsClientException(HttpClientResult response) {
    super(response == null ? "" : response.getResponse());
    this.response = response;
    this.code = response == null ? -1 : response.getStatus();
  }

  public WsClientException(Exception e) {
    super(e);
    this.response = null;
  }

  public String url() {
    return response.getUrl();
  }

  public int code() {
    return response == null ? -1 : response.getStatus();
  }
}