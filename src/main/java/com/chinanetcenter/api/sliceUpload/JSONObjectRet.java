package com.chinanetcenter.api.sliceUpload;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by Administrator on 2014/8/18.
 */
public abstract class JSONObjectRet {
    public JSONObjectRet() {
    }

    public abstract void onSuccess(JsonNode obj);


    public abstract void onSuccess(byte[] body);

    public abstract void onFailure(Exception ex);

    public abstract void onProcess(long current, long total);

    public abstract void onPersist(JsonNode obj);
}
