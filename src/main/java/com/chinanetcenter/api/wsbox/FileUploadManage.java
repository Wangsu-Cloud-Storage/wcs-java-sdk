package com.chinanetcenter.api.wsbox;


import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.entity.PutPolicy;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.http.HttpClientUtil;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.util.DateUtil;
import com.chinanetcenter.api.util.MimetypesUtil;
import com.chinanetcenter.api.util.TokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 上传文件
 *
 * @author zouhao
 * @version 1.0
 * @since 2014/02/14
 */
public class FileUploadManage {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadManage.class);

    public HttpClientResult upload(String bucketName, String fileKey, String srcFile) throws WsClientException {
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setScope(bucketName + ":" + fileKey);
        putPolicy.setOverwrite(1);
        putPolicy.setDeadline(String.valueOf(DateUtil.nextHours(1, new Date()).getTime()));
        return upload(bucketName, fileKey, srcFile, putPolicy);
    }

    public HttpClientResult uploadForAutoMimeType(String bucketName, String fileKey, String srcFile) throws WsClientException {
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setScope(bucketName + ":" + fileKey);
        putPolicy.setOverwrite(1);
        putPolicy.setDeadline(String.valueOf(DateUtil.nextHours(1, new Date()).getTime()));
        return uploadForAutoMimeType(bucketName, fileKey, srcFile, putPolicy);
    }

    public HttpClientResult upload(String bucketName, String fileKey, InputStream in) throws WsClientException {
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setScope(bucketName + ":" + fileKey);
        putPolicy.setOverwrite(1);
        putPolicy.setDeadline(String.valueOf(DateUtil.nextHours(1, new Date()).getTime()));
        String fileName = fileKey;
        if (fileName.contains("/")) {
            fileName = StringUtils.substringAfterLast(fileName, "/");
        }
        return upload(bucketName, fileKey, fileName, in, putPolicy);
    }

    public HttpClientResult uploadForAutoMimeType(String bucketName, String fileKey, InputStream in) throws WsClientException {
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setScope(bucketName + ":" + fileKey);
        putPolicy.setOverwrite(1);
        putPolicy.setDeadline(String.valueOf(DateUtil.nextHours(1, new Date()).getTime()));
        String fileName = fileKey;
        if (fileName.contains("/")) {
            fileName = StringUtils.substringAfterLast(fileName, "/");
        }
        return uploadForAutoMimeType(bucketName, fileKey, fileName, in, putPolicy);
    }

    /**
     * 上传文件到指定的空间，并且可以设定上传策略
     *
     * @param bucketName 上传文件要保存到所在空间的空间名
     * @param fileKey    上传文件在空间中保存的文件名
     * @param srcFile    上传文件本地路径
     * @param putPolicy  上传策略  上传策略数据是资源上传时附带的一组配置设定。
     *                   通过这组配置信息，用户可以自定义上传具体要求。它将上传什么资源，上传到哪个空间，
     *                   上传结果是回调通知还是使用重定向跳转，是否需要设置反馈信息的内容，以及授权上传的截止时间等。
     *                   具体属性说明请参考PutPolicy实体
     */
    public HttpClientResult upload(String bucketName, String fileKey, String srcFile, PutPolicy putPolicy) throws WsClientException {
        if (putPolicy.getDeadline() == null) {
            putPolicy.setDeadline(String.valueOf(DateUtil.nextHours(1, new Date()).getTime()));
        }
        if (StringUtils.isEmpty(fileKey)) {
            putPolicy.setScope(bucketName);
        } else {
            putPolicy.setScope(bucketName + ":" + fileKey);
        }
        String uploadToken = TokenUtil.getUploadToken(putPolicy);
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("token", uploadToken);
        return upload(paramMap,srcFile);
    }

    /**
     * 上传文件到指定的空间，会自动识别文件类型，并且可以设定上传策略
     *
     * @param bucketName 上传文件要保存到所在空间的空间名
     * @param fileKey    上传文件在空间中保存的文件名
     * @param srcFile    上传文件本地路径
     * @param putPolicy  上传策略  上传策略数据是资源上传时附带的一组配置设定。
     *                   通过这组配置信息，用户可以自定义上传具体要求。它将上传什么资源，上传到哪个空间，
     *                   上传结果是回调通知还是使用重定向跳转，是否需要设置反馈信息的内容，以及授权上传的截止时间等。
     *                   具体属性说明请参考PutPolicy实体
     */
    public HttpClientResult uploadForAutoMimeType(String bucketName, String fileKey, String srcFile, PutPolicy putPolicy) throws WsClientException {
        if (putPolicy.getDeadline() == null) {
            putPolicy.setDeadline(String.valueOf(DateUtil.nextHours(1, new Date()).getTime()));
        }
        if (StringUtils.isEmpty(fileKey)) {
            putPolicy.setScope(bucketName);
        } else {
            putPolicy.setScope(bucketName + ":" + fileKey);
        }
        String uploadToken = TokenUtil.getUploadToken(putPolicy);
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("token", uploadToken);
        return uploadForAutoMimeType(paramMap, srcFile);
    }

    /**
     * 上传文件到指定的空间，并且可以设定上传策略
     *
     * @param bucketName  上传文件要保存到所在空间的空间名
     * @param fileKey     上传文件在空间中保存的文件名
     * @param inputStream 文件流
     * @param putPolicy   上传策略  上传策略数据是资源上传时附带的一组配置设定。
     *                    通过这组配置信息，用户可以自定义上传具体要求。它将上传什么资源，上传到哪个空间，
     *                    上传结果是回调通知还是使用重定向跳转，是否需要设置反馈信息的内容，以及授权上传的截止时间等。
     *                    具体属性说明请参考PutPolicy实体
     */
    public HttpClientResult upload(String bucketName, String fileKey, String fileName, InputStream inputStream, PutPolicy putPolicy) throws WsClientException {
        if (putPolicy.getDeadline() == null) {
            putPolicy.setDeadline(String.valueOf(DateUtil.nextHours(1, new Date()).getTime()));
        }
        if (fileKey == null || fileKey.equals("")) {
            putPolicy.setScope(bucketName);
        } else {
            putPolicy.setScope(bucketName + ":" + fileKey);
        }
        String uploadToken = TokenUtil.getUploadToken(putPolicy);
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("token", uploadToken);
        return upload(paramMap,fileName,inputStream);
    }

    /**
     * 上传文件到指定的空间，会自动识别文件类型，并且可以设定上传策略
     *
     * @param bucketName  上传文件要保存到所在空间的空间名
     * @param fileKey     上传文件在空间中保存的文件名
     * @param inputStream 文件流
     * @param putPolicy   上传策略  上传策略数据是资源上传时附带的一组配置设定。
     *                    通过这组配置信息，用户可以自定义上传具体要求。它将上传什么资源，上传到哪个空间，
     *                    上传结果是回调通知还是使用重定向跳转，是否需要设置反馈信息的内容，以及授权上传的截止时间等。
     *                    具体属性说明请参考PutPolicy实体
     */
    public HttpClientResult uploadForAutoMimeType(String bucketName, String fileKey, String fileName, InputStream inputStream, PutPolicy putPolicy) throws WsClientException {
        if (putPolicy.getDeadline() == null) {
            putPolicy.setDeadline(String.valueOf(DateUtil.nextHours(1, new Date()).getTime()));
        }
        if (fileKey == null || fileKey.equals("")) {
            putPolicy.setScope(bucketName);
        } else {
            putPolicy.setScope(bucketName + ":" + fileKey);
        }
        String uploadToken = TokenUtil.getUploadToken(putPolicy);
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("token", uploadToken);
        return uploadForAutoMimeType(paramMap, fileName, inputStream);
    }

    public HttpClientResult upload(Map<String, String> paramMap, String fileName, InputStream inputStream) throws WsClientException {
        String url = Config.PUT_URL + "/file/upload";
        return HttpClientUtil.httpPost(url, null, paramMap, fileName, inputStream);
    }

    public HttpClientResult uploadForAutoMimeType(Map<String, String> paramMap, String fileName, InputStream inputStream) throws WsClientException {
        String url = Config.PUT_URL + "/file/upload";
        String mimeType = paramMap.get("mimeType");
        if (StringUtils.isEmpty(mimeType)) {
            try {
                ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
                byte[] buff = new byte[1024];
                int rc = 0;
                while ((rc = inputStream.read(buff, 0, 1024)) > 0) {
                    swapStream.write(buff, 0, rc);
                }
                byte[] datas = swapStream.toByteArray();
                String contentType = MimetypesUtil.getContentType(datas, fileName);
                paramMap.put("mimeType", contentType);
                inputStream = new ByteArrayInputStream(datas);
            } catch (IOException e) {
                logger.error("read from inputStream error", e);
            }
        }
        return HttpClientUtil.httpPost(url, null, paramMap, fileName, inputStream);
    }

    public HttpClientResult upload(Map<String, String> paramMap, String srcFile) throws WsClientException {
        String url = Config.PUT_URL + "/file/upload";
        File file = new File(srcFile);
        return HttpClientUtil.httpPost(url, paramMap, null, file);
    }

    public HttpClientResult uploadForAutoMimeType(Map<String, String> paramMap, String srcFile) throws WsClientException {
        String url = Config.PUT_URL + "/file/upload";
        File file = new File(srcFile);
        String mimeType = paramMap.get("mimeType");
        if (StringUtils.isEmpty(mimeType)) {
            String contentType = MimetypesUtil.getContentType(file, srcFile);
            paramMap.put("mimeType", contentType);
        }
        return HttpClientUtil.httpPost(url, paramMap,null, file);
    }

    public HttpClientResult multiUpload(List<File> fileList, PutPolicy putPolicy, Map<String, String> xParams) throws WsClientException {
        String url = Config.PUT_URL + "/multifile/upload";
        if (putPolicy.getDeadline() == null) {
            putPolicy.setDeadline(String.valueOf(DateUtil.nextHours(1, new Date()).getTime()));
        }
        String uploadToken = TokenUtil.getUploadToken(putPolicy);
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("token", uploadToken);
        if (xParams != null && xParams.size() > 0) {
            for (Map.Entry<String, String> entry : xParams.entrySet()) {
                paramMap.put(entry.getKey(), entry.getValue());
            }
        }
        return HttpClientUtil.httpMultiPost(url, null, paramMap, fileList);
    }
}
