package com.chinanetcenter.api.http;

import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.entity.JsonValidator;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.util.EncodeUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.CharsetUtils;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class HttpClientUtil {

    public static HttpClientResult httpPost(String url, Map<String, String> params) throws WsClientException {
        return httpPost(url, params, null, null);
    }

    public static HttpClientResult httpPost(String url, Map<String, String> params, Map<String, String> headMap) throws WsClientException {
        return httpPost(url, params, headMap, null);
    }

    public static CloseableHttpClient getHttpClient() {
        CloseableHttpClient httpClient = null;
        try {
            HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {
                @Override
                public boolean retryRequest(IOException exception,
                                            int executionCount, HttpContext context) {
                    System.out.println("request fail retryRequest false");
                    return false;
                }
            };
            // 5秒超时
            RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(30000)
                    .setSocketTimeout(30000).setConnectTimeout(30000).setRedirectsEnabled(false)
                    .build();

            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
            cm.setMaxTotal(50);// 连接池最大并发连接数
            cm.setDefaultMaxPerRoute(30);// 单路由最大并发数
            SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(30000).build();
            cm.setDefaultSocketConfig(socketConfig);

            httpClient = HttpClients.custom().setConnectionManager(cm).setDefaultRequestConfig(requestConfig).setRetryHandler(myRetryHandler).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return httpClient;
    }

    /**
     * \
     * 配置了超时时间
     *
     * @param url
     * @return
     */
    public static CloseableHttpClient createHttpClient(String url) {
        CloseableHttpClient httpClient = null;
        try {
            HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {
                @Override
                public boolean retryRequest(IOException exception,
                                            int executionCount, HttpContext context) {
                    System.out.println("request fail retryRequest false");
                    return false;
                }
            };
            // 5秒超时
            RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(30000)
                    .setSocketTimeout(30000).setConnectTimeout(30000).setRedirectsEnabled(false)
                    .build();
            SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(30000).build();
            if(StringUtils.startsWith(url, "https://")){
                SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                    //信任所有
                    public boolean isTrusted(X509Certificate[] chain,
                                             String authType) throws CertificateException {
                        return true;
                    }
                }).build();
                SSLConnectionSocketFactory sslSf = new SSLConnectionSocketFactory(sslContext,SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                return HttpClients.custom().setDefaultSocketConfig(socketConfig).setDefaultRequestConfig(requestConfig).setRetryHandler(myRetryHandler).setSSLSocketFactory(sslSf).build();
            }else {
                PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
                cm.setMaxTotal(50);// 连接池最大并发连接数
                cm.setDefaultMaxPerRoute(30);// 单路由最大并发数
                cm.setDefaultSocketConfig(socketConfig);
                httpClient = HttpClients.custom().setConnectionManager(cm).setDefaultRequestConfig(requestConfig).setRetryHandler(myRetryHandler).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return httpClient;
    }

    public static HttpClientResult httpPost(String url, Map<String, String> params, Map<String, String> headMap, File file) throws WsClientException {
        String response = "";
        HttpPost httpPost = null;
        CloseableHttpResponse ht = null;
        InputStream is = null;
        BufferedReader br = null;
        try {
            httpPost = new HttpPost(url);
            List<NameValuePair> paramsList = new ArrayList<NameValuePair>();

            if (file != null) {
                MultipartEntityBuilder mEntityBuilder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                mEntityBuilder.setCharset(Charset.forName("UTF-8"));

                FileBody fileBody = new FileBody(file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
                mEntityBuilder.addPart("file", fileBody);
                mEntityBuilder.addTextBody("desc", file.getName());

                if (params != null && params.size() > 0) {
                    for(Map.Entry<String, String> entry:params.entrySet()){
                        mEntityBuilder.addTextBody(entry.getKey(), entry.getValue(), ContentType.create("text/plain", Charset.forName("UTF-8")));
                    }
                }
                httpPost.setEntity(mEntityBuilder.build());
            } else if (params != null && params.size() > 0) {
                for(Map.Entry<String, String> entry:params.entrySet()){
                    paramsList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                HttpEntity he = null;
                try {
                    he = new UrlEncodedFormEntity(paramsList, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                httpPost.setEntity(he);
            }

            if (headMap != null && headMap.size() > 0) {
                for(Map.Entry<String, String> entry:headMap.entrySet()){
                    httpPost.setHeader(entry.getKey(),entry.getValue());
                }
            }
            if (!httpPost.containsHeader("User-Agent"))
                httpPost.addHeader("User-Agent", Config.VERSION_NO);
//            CloseableHttpClient hc = getHttpClient();
            CloseableHttpClient hc = createHttpClient(url);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();//设置请求和传输超时时间
            httpPost.setConfig(requestConfig);
            ht = hc.execute(httpPost);

            HttpEntity het = ht.getEntity();
            is = het.getContent();
            br = new BufferedReader(new InputStreamReader(is, "utf8"));
            String readLine;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                sb.append(readLine);
            }
            response += sb.toString();
            int status = ht.getStatusLine().getStatusCode();
            if (status == 200) {
                if (!new JsonValidator().validate(response)) {
                    response = EncodeUtils.urlsafeDecodeString(response);
                }
            }
            return new HttpClientResult(url, status, response);
        } catch (IOException e) {
            e.printStackTrace();
            throw new WsClientException(e);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(br);
            IOUtils.closeQuietly(ht);
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
        }
    }

    public static HttpClientResult httpGet(String url, Map<String, String> headMap) throws WsClientException {
        HttpGet httpGet = null;
        CloseableHttpResponse ht = null;
        try {
            httpGet = new HttpGet(url);

            if (headMap != null && headMap.size() > 0) {
                for(Map.Entry<String, String> entry:headMap.entrySet()){
                    httpGet.setHeader(entry.getKey(), entry.getValue());
                }
            }
            if (!httpGet.containsHeader("User-Agent"))
                httpGet.addHeader("User-Agent", Config.VERSION_NO);

//            CloseableHttpClient hc = getHttpClient();
            CloseableHttpClient hc = createHttpClient(url);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();//设置请求和传输超时时间
            httpGet.setConfig(requestConfig);
            ht = hc.execute(httpGet);

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
            int status = ht.getStatusLine().getStatusCode();
            return new HttpClientResult(url, status, sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new WsClientException(e);
        } finally {
            if (httpGet != null) {
                httpGet.releaseConnection();
            }
            IOUtils.closeQuietly(ht);
        }
    }

    public static HttpClientResult httpPost(String url, Map<String, String> headMap, Map<String, String> params, String fileName, InputStream inputStream) throws WsClientException {
        String response = "";
        HttpPost httpPost = null;
        CloseableHttpResponse ht = null;
        CloseableHttpClient hc = null;
        InputStream is = null;
        BufferedReader br = null;
        try {
            httpPost = new HttpPost(url);
            List<NameValuePair> paramsList = new ArrayList<NameValuePair>();

            if (inputStream != null) {
                MultipartEntityBuilder mEntityBuilder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                mEntityBuilder.setCharset(Charset.forName("UTF-8"));
                InputStreamBody fileBody = new InputStreamBody(inputStream, fileName);
                mEntityBuilder.addPart("file", fileBody);
                mEntityBuilder.addTextBody("desc", fileName);

                if (params != null && params.size() > 0) {
                    for(Map.Entry<String, String> entry:params.entrySet()){
                        mEntityBuilder.addTextBody(entry.getKey(), entry.getValue(), ContentType.create("text/plain", Charset.forName("UTF-8")));
                    }
                }

                httpPost.setEntity(mEntityBuilder.build());
            } else if (params != null && params.size() > 0) {
                for(Map.Entry<String, String> entry:params.entrySet()){
                    paramsList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                HttpEntity he = null;
                try {
                    he = new UrlEncodedFormEntity(paramsList, "utf-8");
                } catch (UnsupportedEncodingException ignored) {

                }
                httpPost.setEntity(he);
            }

            if (headMap != null && headMap.size() > 0) {
                for(Map.Entry<String, String> entry:headMap.entrySet()){
                    httpPost.setHeader(entry.getKey(),entry.getValue());
                }
            }
            if (!httpPost.containsHeader("User-Agent"))
                httpPost.addHeader("User-Agent", Config.VERSION_NO);
//            hc = getHttpClient();
            hc = createHttpClient(url);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();//设置请求和传输超时时间
            httpPost.setConfig(requestConfig);
            ht = hc.execute(httpPost);

            HttpEntity het = ht.getEntity();
            is = het.getContent();
            br = new BufferedReader(new InputStreamReader(is, "utf8"));
            String readLine;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                sb.append(readLine);
            }
            response += sb.toString();

            int status = ht.getStatusLine().getStatusCode();
            if (status == 200) {
                if (!new JsonValidator().validate(response)) {
                    response = EncodeUtils.urlsafeDecodeString(response);
                }
            }
            return new HttpClientResult(url, status, response);
        } catch (IOException e) {
            e.printStackTrace();
            throw new WsClientException(e);
        } finally {
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(br);
            IOUtils.closeQuietly(ht);
            IOUtils.closeQuietly(hc);
        }
    }

    public static HttpClientResult httpMultiPost(String url, Map<String, String> headMap, Map<String, String> params, List<File> fileList) throws WsClientException {
        HttpPost httpPost = null;
        CloseableHttpResponse ht = null;
        InputStream is = null;
        BufferedReader br = null;
        try {
            httpPost = new HttpPost(url);
            List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
            MultipartEntityBuilder mEntityBuilder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            mEntityBuilder.setCharset(Charset.forName("utf-8"));
            if (fileList != null && fileList.size() > 0) {
                for (int i = 0; i < fileList.size(); i++) {
                    File file = fileList.get(i);
                    FileBody fileBody = new FileBody(file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
                    mEntityBuilder.addPart("file", fileBody);
                }
                if (params != null && params.size() > 0) {
                    for(Map.Entry<String, String> entry:params.entrySet()){
                        mEntityBuilder.addTextBody(entry.getKey(), entry.getValue(), ContentType.create("text/plain", Charset.forName("UTF-8")));
                    }
                }
                httpPost.setEntity(mEntityBuilder.setCharset(CharsetUtils.get("UTF-8")).build());
            } else if (params != null && params.size() > 0) {
                for(Map.Entry<String, String> entry:params.entrySet()){
                    paramsList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                HttpEntity he = new UrlEncodedFormEntity(paramsList, "utf-8");
                httpPost.setEntity(he);
            }

            if (headMap != null && headMap.size() > 0) {
                for(Map.Entry<String, String> entry:headMap.entrySet()){
                    httpPost.setHeader(entry.getKey(),entry.getValue());
                }
            }
            if (!httpPost.containsHeader("User-Agent"))
                httpPost.addHeader("User-Agent", Config.VERSION_NO);
//            CloseableHttpClient hc = getHttpClient();
            CloseableHttpClient hc = createHttpClient(url);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(120000).setConnectTimeout(120000).build();//设置请求和传输超时时间
            httpPost.setConfig(requestConfig);
            ht = hc.execute(httpPost);
            HttpEntity het = ht.getEntity();
            is = het.getContent();
            br = new BufferedReader(new InputStreamReader(is, "utf8"));
            String readLine;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                sb.append(readLine);
            }
            int status = ht.getStatusLine().getStatusCode();
            return new HttpClientResult(url, status, sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new WsClientException(e);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(br);
            IOUtils.closeQuietly(ht);
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
        }
    }

    public static HttpClientResult httpPostStringEntity(String url, Map<String, String> headMap, String body) throws WsClientException {
        HttpPost httpPost = null;
        CloseableHttpResponse ht = null;
        InputStream is = null;
        BufferedReader br = null;
        try {
            httpPost = new HttpPost(url);

            StringEntity s = new StringEntity(body, "utf-8");
            httpPost.setEntity(s);

            if (headMap != null && headMap.size() > 0) {
                for(Map.Entry<String, String> entry:headMap.entrySet()){
                    httpPost.setHeader(entry.getKey(), entry.getValue());
                }
            }
            if (!httpPost.containsHeader("User-Agent"))
                httpPost.addHeader("User-Agent", Config.VERSION_NO);
//            CloseableHttpClient hc = getHttpClient();
            CloseableHttpClient hc = createHttpClient(url);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();//设置请求和传输超时时间
            httpPost.setConfig(requestConfig);
            ht = hc.execute(httpPost);

            HttpEntity het = ht.getEntity();
            is = het.getContent();
            br = new BufferedReader(new InputStreamReader(is, "utf8"));
            String readLine;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                sb.append(readLine);
            }
            int status = ht.getStatusLine().getStatusCode();
            return new HttpClientResult(url, status, sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new WsClientException(e);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(br);
            IOUtils.closeQuietly(ht);
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
        }
    }

    public static HttpClientResult httpGet(String url, String filePath, Map<String, String> requestHeaders) throws WsClientException {
        HttpGet httpGet = null;
        CloseableHttpResponse ht = null;
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            httpGet = new HttpGet(url);
            if (requestHeaders != null && requestHeaders.size() > 0) {
                for (Map.Entry<String, String> entry : requestHeaders.entrySet()) {
                    httpGet.setHeader(entry.getKey(), entry.getValue());
                }
            }

            if (!httpGet.containsHeader("User-Agent")) {
                httpGet.addHeader("User-Agent", Config.VERSION_NO);
            }

            CloseableHttpClient hc = createHttpClient(url);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();//设置请求和传输超时时间
            httpGet.setConfig(requestConfig);
            ht = hc.execute(httpGet);
            int status = ht.getStatusLine().getStatusCode();
            HttpEntity het = ht.getEntity();
            is = het.getContent();

            if (status == 200) {
                int size;
                byte[] buf = new byte[1024 * 1024];
                File tempFile = new File(filePath);
                File parentFile = tempFile.getParentFile();
                if (null != parentFile && !parentFile.exists()) {
                    parentFile.mkdirs();
                }
                fos = new FileOutputStream(filePath);
                while ((size = is.read(buf)) != -1) {
                    fos.write(buf, 0, size);
                }
                return new HttpClientResult(status);
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String readLine;
                StringBuilder sb = new StringBuilder();
                while ((readLine = br.readLine()) != null) {
                    sb.append(readLine);
                }
                br.close();
                return new HttpClientResult(url, status, sb.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new WsClientException(e);
        } finally {
            if (httpGet != null) {
                httpGet.releaseConnection();
            }
            IOUtils.closeQuietly(ht);
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(fos);
        }
    }

    public static HttpClientResult httpGetContentStream(String url, Map<String, String> requestHeaders) throws WsClientException {
        HttpGet httpGet = null;
        CloseableHttpResponse ht = null;
        try {
            httpGet = new HttpGet(url);
            if (requestHeaders != null && requestHeaders.size() > 0) {
                for (Map.Entry<String, String> entry : requestHeaders.entrySet()) {
                    httpGet.setHeader(entry.getKey(), entry.getValue());
                }
            }

            if (!httpGet.containsHeader("User-Agent")) {
                httpGet.addHeader("User-Agent", Config.VERSION_NO);
            }

            CloseableHttpClient hc = createHttpClient(url);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();//设置请求和传输超时时间
            httpGet.setConfig(requestConfig);
            ht = hc.execute(httpGet);
            int status = ht.getStatusLine().getStatusCode();
            HttpEntity het = ht.getEntity();

            if (status == 200) {
                HttpClientResult httpClientResult = new HttpClientResult(status);
                httpClientResult.setContent(het.getContent());
                return httpClientResult;
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(het.getContent()));
                String readLine;
                StringBuilder sb = new StringBuilder();
                while ((readLine = br.readLine()) != null) {
                    sb.append(readLine);
                }
                br.close();
                return new HttpClientResult(url, status, sb.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new WsClientException(e);
        }
    }

}
