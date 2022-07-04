package com.chinanetcenter.api.util;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.content.AbstractContentBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BandwidthLimiterFileBody extends AbstractContentBody {

    private final File file;
    private final String filename;
    private final String charset;

    public BandwidthLimiterFileBody(final File file,
                                    final String filename,
                                    final ContentType contentType,
                                    final String charset) {
        super(contentType);
        if (file == null) {
            throw new IllegalArgumentException("File may not be null");
        }
        this.file = file;
        if (filename != null) {
            this.filename = filename;
        } else {
            this.filename = file.getName();
        }
        this.charset = charset;
    }

    public BandwidthLimiterFileBody(final File file,
                                    final ContentType contentType,
                                    final String charset) {
        this(file, null, contentType, charset);
    }

    public BandwidthLimiterFileBody(final File file, final ContentType contentType) {
        this(file, contentType, null);
    }

    public BandwidthLimiterFileBody(final File file) {
        this(file, ContentType.APPLICATION_OCTET_STREAM);
    }

    public InputStream getInputStream() throws IOException {
        return new FileInputStream(this.file);
    }

    @Override
    public void writeTo(final OutputStream out) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        InputStream in = new FileInputStream(this.file);
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
    public long getContentLength() {
        return this.file.length();
    }

    @Override
    public String getFilename() {
        return filename;
    }

    public File getFile() {
        return this.file;
    }

}