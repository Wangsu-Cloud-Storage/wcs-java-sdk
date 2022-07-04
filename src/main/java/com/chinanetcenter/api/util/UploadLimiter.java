package com.chinanetcenter.api.util;


import java.io.IOException;
import java.io.OutputStream;

public class UploadLimiter extends OutputStream {
    private OutputStream os;
    private boolean isBandwidthLimiter = true;
    private BandwidthLimiter bandwidthLimiter = null;

    public UploadLimiter(OutputStream os, boolean isBandwidthLimiter, BandwidthLimiter bandwidthLimiter) {
        this.os = os;
        this.isBandwidthLimiter = isBandwidthLimiter;
        this.bandwidthLimiter = bandwidthLimiter;
    }

    public UploadLimiter(OutputStream os, boolean isBandwidthLimiter) {
        this.os = os;
        this.isBandwidthLimiter = isBandwidthLimiter;
    }

    public UploadLimiter(OutputStream os, BandwidthLimiter bandwidthLimiter) {
        this.os = os;
        this.bandwidthLimiter = bandwidthLimiter;
    }

    @Override
    public void write(int b) throws IOException {
        if (isBandwidthLimiter) {
            bandwidthLimiter.limitNextBytes();
        }
        this.os.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (isBandwidthLimiter) {
            bandwidthLimiter.limitNextBytes(len);
        }
        this.os.write(b, off, len);
    }
}