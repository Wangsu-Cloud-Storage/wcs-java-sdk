package com.chinanetcenter.api.entity;

import com.chinanetcenter.api.util.JsonMapper;

import java.util.List;

/**
 * Created by fuyz on 2015/7/29.
 */
public class Avinfo {

    private Format format;
    private List<Stream> streams;

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public List<Stream> getStreams() {
        return streams;
    }

    public void setStreams(List<Stream> streams) {
        this.streams = streams;
    }

    public String toJson(){
        JsonMapper jsonMapper = new JsonMapper();
        return jsonMapper.toJson(this);
    }
}
