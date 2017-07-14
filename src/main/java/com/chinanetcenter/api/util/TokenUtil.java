package com.chinanetcenter.api.util;

import com.chinanetcenter.api.entity.PutPolicy;

/**
 * Created by zouhao on 14-5-16.
 */
public class TokenUtil {


    public static void main(String[] args) {
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setScope("viptest:moteltest001.mp4");
        Long time = DateUtil.parseDate("2050-01-01 12:00:00", DateUtil.COMMON_PATTERN).getTime();
        putPolicy.setDeadline(String.valueOf(time));
        String uploadToken = TokenUtil.getUploadToken(putPolicy);
        System.out.println(uploadToken);
    }

    /**
     * 获取上传的token
     *
     * @param putPolicy
     * @return
     */
    public static String getUploadToken(PutPolicy putPolicy) {
        JsonMapper jsonMapper = JsonMapper.nonEmptyMapper();
        String putPolicyStr = jsonMapper.toJson(putPolicy);
        String encodePutPolicy = EncodeUtils.urlsafeEncode(putPolicyStr);
        String singSk = EncryptUtil.sha1Hex(encodePutPolicy.getBytes(), Config.SK);//签名
        String skValue = EncodeUtils.urlsafeEncode(singSk);//Base64编码
        String uploadToken = Config.AK + ":" + skValue + ":" + encodePutPolicy;
        return uploadToken;
    }


    /**
     * 获取删除的token
     *
     * @param bucketName 空间名称
     * @param fileName   文件名称
     * @return
     */
    public static String getDeleteToken(String bucketName, String fileName) {
        String encodedEntryURI = EncodeUtils.urlsafeEncodeString((bucketName + ":" + fileName).getBytes());
        String encodeDeletePath = "/delete/" + encodedEntryURI + "\n";
        String signSk = EncryptUtil.sha1Hex(encodeDeletePath.getBytes(), Config.SK);//签名
        String encodedSign = EncodeUtils.urlsafeEncode(signSk);//Base64编码
        String deleteToken = Config.AK + ":" + encodedSign;
        return deleteToken;
    }

    /**
     * 获取前缀模糊删除的token
     *
     * @param bucketName 空间名称
     * @param fileName   文件名称
     * @return
     */
    public static String getDeletePrefixToken(String bucketName, String fileName) {
        String encodedEntryURI = EncodeUtils.urlsafeEncodeString((bucketName + ":" + fileName).getBytes());
        String encodeDeletePath = "/deletePrefix/" + encodedEntryURI + "\n";
        String signSk = EncryptUtil.sha1Hex(encodeDeletePath.getBytes(), Config.SK);//签名
        String encodedSign = EncodeUtils.urlsafeEncode(signSk);//Base64编码
        String deleteToken = Config.AK + ":" + encodedSign;
        return deleteToken;
    }

    /**
     * 获取文件信息的token
     *
     * @param bucketName 空间名称
     * @param fileName 文件名称
     * @return
     */
    public static String getStatToken(String bucketName, String fileName) {
        String encodedEntryURI = EncodeUtils.urlsafeEncodeString((bucketName + ":" + fileName).getBytes());
        String encodeDeletePath = "/stat/" + encodedEntryURI + "\n";
        String signSk = EncryptUtil.sha1Hex(encodeDeletePath.getBytes(), Config.SK);//签名
        String encodedSign = EncodeUtils.urlsafeEncode(signSk);//Base64编码
        String deleteToken = Config.AK + ":" + encodedSign;
        return deleteToken;
    }

    public static String getFileListToken(String listUrl) {
        listUrl += "\n";
        String encodeDownloadUrl = EncryptUtil.sha1Hex(listUrl.getBytes(), Config.SK);//签名
        String skValues = EncodeUtils.urlsafeEncode(encodeDownloadUrl);//Base64编码
        String listToken = Config.AK + ":" + skValues;
        return listToken;
    }

}
