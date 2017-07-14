package com.chinanetcenter.api.entity;

/**
 * Created by lidl on 15-3-20.
 */
public class ProgressListener {

    /**
     * @param bytesWritten 已经上传或者下载的进度
     * @param totalSize    文件总大小
     */
    public void onProgress(long bytesWritten, long totalSize) {
        System.out.println(bytesWritten + " ," + totalSize);
    }

}
