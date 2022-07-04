package com.chinanetcenter.api.util;

import org.apache.http.entity.AbstractHttpEntity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author luosh
 * @since 2022/6/10 9:31
 */
public class BandwidthLimiterByteArrayHttpEntity extends AbstractHttpEntity {
    private boolean consumed;
    private long length;
    private ByteArrayInputStream blockBuffer;

    public BandwidthLimiterByteArrayHttpEntity(boolean consumed, long length, ByteArrayInputStream blockBuffer) {
        this.consumed = consumed;
        this.length = length;
        this.blockBuffer = blockBuffer;
    }

    @Override
    public boolean isRepeatable() {
        return true;
    }

    @Override
    public long getContentLength() {
        return length;
    }

    @Override
    public InputStream getContent() throws IOException, IllegalStateException {
        return null;
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        consumed = false;
        UploadLimiter uploadLimiter = new UploadLimiter(outputStream, Config.TRAFFIC_LIMIT > 0, new BandwidthLimiter(Config.TRAFFIC_LIMIT));
        try {
            byte[] b = new byte[1024 * 4];
            long len;
            int totalLength = 0;
            while (true) {
                if ((length - totalLength) < b.length) {
                    int lastLength = (int) (length - totalLength);
                    b = new byte[lastLength];
                }
                len = blockBuffer.read(b);
                if (len == -1 || totalLength >= length) {
                    break;
                }
                uploadLimiter.write(b, 0, (int) len);
                totalLength += len;
            }
            uploadLimiter.flush();
        } finally {
            outputStream.close();
            consumed = true;
        }
    }

    @Override
    public boolean isStreaming() {
        return !consumed;
    }

}