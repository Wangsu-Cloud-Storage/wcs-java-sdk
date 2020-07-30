package com.chinanetcenter.api.sliceUpload;

import com.chinanetcenter.api.entity.SliceUploadHttpResult;
import com.chinanetcenter.api.http.HttpClientUtil;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.util.EncodeUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.zip.CRC32;

/**
 * Created by fuyz on 2014/8/5.
 * 分片上传公用方法
 */
public class BaseBlockUtil {
    public static int KB = 1024;
    public static int MB = 1024 * KB;
    public static int BLOCK_SIZE = 4 * MB;
    public static int CHUNK_SIZE = 256 * KB;
    public static int TRIED_TIMES = 3;
    public static int THREAD_NUN = 5;
    public static boolean isPersist = true;
    public static String properties_file_path = "";
    private static Logger logger = Logger.getLogger(BaseBlockUtil.class);
    public BlockObject blockObject;
    public JSONObjectRet jsonObjectRet;
    public PutExtra putExtra;
    protected Map<String, String> headMap;
    protected int REQUEST_TIMEOUT = 60 * 1000;  //设置请求超时60秒钟
    protected int SO_TIMEOUT = 300 * 1000;       //设置等待数据超时时间5分钟

    public BaseBlockUtil(BlockObject blockObject, JSONObjectRet jsonObjectRet, PutExtra putExtra, Map<String, String> headMap) {
        this.blockObject = blockObject;
        this.jsonObjectRet = jsonObjectRet;
        this.putExtra = putExtra;
        this.headMap = headMap;
    }

    public static void savePutExtra(String bucketName, String fileName, JsonNode obj) {
        if (!isPersist) {
            return;
        }
        File configFile = getPropertiesFile(bucketName, fileName);

        synchronized (configFile) {
            FileOutputStream fileOutputStream = null;
            try {
                if (!configFile.getParentFile().exists()) {
                    configFile.getParentFile().mkdirs();
                }
                if (!configFile.exists()) {
                    configFile.createNewFile();
                }
                fileOutputStream = new FileOutputStream(configFile);
                ObjectMapper objectMapper = new ObjectMapper();
                fileOutputStream.write(objectMapper.writeValueAsString(obj).getBytes());
                fileOutputStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }
    }

    public static PutExtra getPutExtra(String bucketName, String fileName) {
        if (!isPersist) {
            return null;
        }
        File configFile = getPropertiesFile(bucketName, fileName);
        if (!configFile.exists()) return null;
        FileReader reader;
        int fileLen = (int) configFile.length();
        char[] chars = new char[fileLen];
        try {
            reader = new FileReader(configFile);
            reader.read(chars);
            String txt = String.valueOf(chars);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode obj = objectMapper.readTree(txt);
            return new PutExtra(obj);
        } catch (Exception e) {
            return null;
        }
    }

    public static void clearPutExtra(String bucketName, String fileName) {
        if (!isPersist) {
            return;
        }
        File configFile = getPropertiesFile(bucketName, fileName);
        if (!configFile.exists()) return;
        configFile.delete();
    }

    public static File getPropertiesFile(String bucketName, String fileName) {
        String key = EncodeUtils.urlsafeEncode(fileName);
        String filePath = properties_file_path;
        if (StringUtils.isEmpty(filePath)) {
            filePath = System.getProperty("java.io.tmpdir");
        }
        if (!filePath.endsWith("/")) {
            filePath += "/";
        }
        return new File(filePath + bucketName + File.separator + key + "_sliceConfig.properties");
    }

    protected String getBPutUrl() {
        return Config.PUT_URL + "/bput/" + blockObject.getLastCtx() + "/" + blockObject.getStart();
    }

    protected String getMkBlkUrl() {
        return Config.PUT_URL + "/mkblk/" + blockObject.getBlockLen() + "/" + blockObject.getBlockIdx();
    }

    public SliceUploadHttpResult mkFile(Map<String, String> headMap, String key, PutExtra putExtra, int time) {
        String url;
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse ht = null;
        HttpPost httpPost = null;
        try {
            StringBuilder ctx = new StringBuilder();
            if (putExtra.streamProcesses != null && putExtra.streamProcesses.size() > 0) {
                for (BlockObject blockObject : putExtra.streamProcesses) {
                    ctx.append(",").append(blockObject.getLastCtx());
                }
            } else {
                for (BlockObject blockObject : putExtra.processes) {
                    ctx.append(",").append(blockObject.getLastCtx());
                }
            }

            url = buildMkFileUrl(putExtra.totalSize, key, putExtra.xParams);
            httpClient = HttpClientUtil.createHttpClient(url);
            httpPost = new HttpPost(url);
            if (!httpPost.containsHeader("User-Agent")) {
                httpPost.addHeader("User-Agent", Config.VERSION_NO);
            }
            if (headMap != null && headMap.size() > 0) {
                for (Map.Entry<String, String> entry : headMap.entrySet()) {
                    httpPost.setHeader(entry.getKey(), entry.getValue());
                }
            }

            httpPost.setEntity(new StringEntity(ctx.substring(1)));
            ht = httpClient.execute(httpPost);

            HttpEntity het = ht.getEntity();
            InputStream is = het.getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf8"));
            String readLine;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                sb.append(readLine);
            }
            is.close();
            br.close();

            SliceUploadHttpResult ret = new SliceUploadHttpResult(ht.getStatusLine().getStatusCode(), sb.toString());
            // 401 上传数据块校验出错 ； 412 服务器块拼接文件出错，需要重新上传文件;  500服务端失败;  579 回调失败，不再重新makefile;
            if ((ret.status == 401 || ret.status == 412 || (ret.status / 100 == 5 && ret.status != 579))
                    && time < BaseBlockUtil.TRIED_TIMES) {
                return mkFile(headMap, key, putExtra, time + 1);
            }
            return ret;
        } catch (Exception e) {
            if (e instanceof ClientProtocolException) {
                String message = "make file ClientProtocolException, key:" + key + " ,ClientProtocolException error,Message:" + e.getMessage();
                logger.error("make file ClientProtocolException error," + message);
            }
            // 连接异常等重新makefile
            if (time < BaseBlockUtil.TRIED_TIMES) {
                return mkFile(headMap, key, putExtra, time + 1);
            }
            throw new RuntimeException(e);
        } finally {
            try {
                if (httpPost != null) {
                    httpPost.releaseConnection();
                }
                if (ht != null) {
                    ht.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    public String buildMkFileUrl(long length, String key, Map<String, String> params) {
        StringBuilder url = new StringBuilder();
        url.append(Config.PUT_URL).append("/mkfile/").append(length);
        if (null != key) {
            url.append("/key/").append(new String(EncodeUtils.urlsafeEncodeBytes(key.getBytes())));
        }
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, String> a : params.entrySet()) {
                url.append("/").append(a.getKey()).append("/").append(new String(EncodeUtils.urlsafeEncodeBytes(a.getValue().getBytes())));
            }
        }
        return url.toString();
    }

    public SliceUploadHttpResult handleResult(CloseableHttpResponse response) {
        try {
            StatusLine status = response.getStatusLine();
            int statusCode = status.getStatusCode();
            String responseBody = EntityUtils.toString(
                    response.getEntity(), "utf-8");
            return new SliceUploadHttpResult(statusCode, responseBody);
        } catch (Exception e) {
            return new SliceUploadHttpResult(500, "can not load response.");
        }
    }

    public HttpPost buildUpPost(String url) {
        HttpPost post = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SO_TIMEOUT).setConnectTimeout(REQUEST_TIMEOUT).build();//设置请求和传输超时时间
        post.setConfig(requestConfig);
        return post;
    }

    public HttpEntity buildHttpEntity(final RandomAccessFile file, final long offset, final int len) {
        AbstractHttpEntity entity = new AbstractHttpEntity() {
            private boolean consumed = false;
            private long length = len;

            @Override
            public boolean isRepeatable() {
                return true;
            }

            @Override
            public long getContentLength() {
                return length;
            }

            @Override
            public InputStream getContent() throws IOException,
                    IllegalStateException {
                return null;
            }

            @Override
            public void writeTo(OutputStream os) throws IOException {
                consumed = false;
                try {
                    byte[] b = new byte[1024 * 4];
                    long len;
                    int totalLength = 0;
                    file.seek(offset);
                    while (true) {
                        if ((length - totalLength) < b.length) {
                            int lastLength = (int) (length - totalLength);
                            b = new byte[lastLength];
                        }
                        len = file.read(b);
                        if (len == -1 || totalLength >= length) {
                            break;
                        }
                        os.write(b, 0, (int) len);
                        totalLength += len;
                    }
                    os.flush();
                } finally {
                    os.close();
                    consumed = true;
                }
            }

            @Override
            public boolean isStreaming() {
                return !consumed;
            }
        };
        entity.setContentType("application/octet-stream");
        return entity;
    }

    protected long buildCrc32(int len) {
        return crc32(getByteData(len));
    }

    protected String getFileMD5String(int len) {
        byte[] data = getByteData(len);
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data);
            return toHex(md.digest());
        } catch (IllegalStateException e) {
            return null;
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    private String toHex(byte buffer[]) {
        StringBuilder sb = new StringBuilder();
        String s;
        for (byte aBuffer : buffer) {
            s = Integer.toHexString((int) aBuffer & 0xff);
            if (s.length() < 2) {
                sb.append('0');
            }
            sb.append(s);
        }
        return sb.toString();
    }

    private byte[] getByteData(int len) {
        byte[] data = new byte[len];
        try {
            blockObject.file.seek(blockObject.getOffset() + blockObject.getStart());
            blockObject.file.read(data);
            return data;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public long crc32(byte[] data) {
        CRC32 crc32 = new CRC32();
        crc32.update(data);
        return crc32.getValue();
    }

    protected String getMD5String(byte[] data, int offset, int length) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data, offset, length);
            return toHex(md.digest());
        } catch (IllegalStateException e) {
            return null;
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public long crc32(byte[] data, int offset, int length) {
        CRC32 crc32 = new CRC32();
        crc32.update(data, offset, length);
        return crc32.getValue();
    }

    public HttpEntity buildHttpEntity(final ByteArrayInputStream blockBuffer, final long offset, final int len) {
        AbstractHttpEntity entity = new AbstractHttpEntity() {
            private boolean consumed = false;
            private long length = len;

            @Override
            public boolean isRepeatable() {
                return true;
            }

            @Override
            public long getContentLength() {
                return length;
            }

            @Override
            public InputStream getContent() throws IOException,
                    IllegalStateException {
                return null;
            }

            @Override
            public void writeTo(OutputStream os) throws IOException {
                consumed = false;
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
                        os.write(b, 0, (int) len);
                        totalLength += len;
                    }
                    os.flush();
                } finally {
                    os.close();
                    consumed = true;
                }
            }

            @Override
            public boolean isStreaming() {
                return !consumed;
            }
        };
        entity.setContentType("application/octet-stream");
        return entity;
    }
}