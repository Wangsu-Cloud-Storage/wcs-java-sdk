package com.chinanetcenter.api.entity;

import java.util.List;

/**
 * Created by fuyz on 2015/5/26.
 */
public class FileListObject {
    private String marker;
    private List<String> commonPrefixes;
    private List<FileMessageObject> items;

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public List<String> getCommonPrefixes() {
        return commonPrefixes;
    }

    public void setCommonPrefixes(List<String> commonPrefixes) {
        this.commonPrefixes = commonPrefixes;
    }

    public List<FileMessageObject> getItems() {
        return items;
    }

    public void setItems(List<FileMessageObject> items) {
        this.items = items;
    }
}
