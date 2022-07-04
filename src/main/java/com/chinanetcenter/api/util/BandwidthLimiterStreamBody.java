package com.chinanetcenter.api.util;

import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.content.InputStreamBody;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Administrator on 2014/10/29.
 */
public class BandwidthLimiterStreamBody extends InputStreamBody {

    private final String filename;
    private final String charset;
    private final InputStream inputStream;

    /**
     * @since 4.1
     */
    public BandwidthLimiterStreamBody(
            final String filename,
            final String charset,
            final InputStream inputStream) {
        super(inputStream, filename);
        this.filename = filename;
        this.charset = charset;
        this.inputStream = inputStream;
    }

    public BandwidthLimiterStreamBody(InputStream inputStream, String fileName) {
        this(fileName, null, inputStream);
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public void writeTo(final OutputStream out) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        InputStream in = inputStream;
        UploadLimiter uploadLimiter = new UploadLimiter(out, Config.TRAFFIC_LIMIT > 0, new BandwidthLimiter(Config.TRAFFIC_LIMIT));
        try {
            byte[] tmp = new byte[8192];
            int l;
            while ((l = in.read(tmp)) != -1) {
                uploadLimiter.write(tmp, 0, l);
            }
            uploadLimiter.flush();
        } finally {
            in.close();
        }
    }

    @Override
    public String getTransferEncoding() {
        return MIME.ENC_BINARY;
    }

    @Override
    public String getCharset() {
        return charset;
    }

    @Override
    public String getFilename() {
        return filename;
    }
}