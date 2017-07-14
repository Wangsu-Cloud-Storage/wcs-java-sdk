package com.chinanetcenter.api.entity;

/**
 * Created by lidl on 15-3-20.
 */


public class ProgressNotifier {

    private ProgressListener progressListener;

    private long mTotal = 0L;

    private long mWritten;

    public ProgressNotifier(long total, ProgressListener progressListener) {
        this.progressListener = progressListener;
        this.mTotal = total;
    }
}
