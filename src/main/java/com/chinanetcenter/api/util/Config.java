package com.chinanetcenter.api.util;

import com.chinanetcenter.api.emum.EncryptionType;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 配置类， 配置用户的ak和sk信息<br>
 *
 * @author zouhao
 * @version 1.0
 * @since 2014/03/02
 */
public class Config {
    public final static String VERSION_NO = "wcs-java-sdk-2.0.11";
    /**
     * 具体AK SK信息请从网宿云存储Web应用中(账号管理-密钥管理)处获取
     */
    public static String AK = "your-ak";
    public static String SK = "your-sk";
    /**
     * 可在用户管理界面-安全管理-域名查询获取uploadDomain,MgrDomain
     */
    public static String PUT_URL = "your uploadDomain";
    public static String MGR_URL = "your MgrDomain";
    /**
     * 下载GET_URL使用绑定域名
     */
    public static String GET_URL = "your downloadDomain";
    public static String LOCAL_IP = "127.0.0.1";
    public static String LOG_FILE_PATH = "";

    /**
     * 请求链接超时时间，单位毫秒
     */
    public static int CONNECTION_TIME_OUT = 30000;
    /**
     * 数据传输超时时间，单位毫秒
     */
    public static int SOCKET_TIME_OUT = 60000;
    /**
     * 请求超时次数
     */
    public static int REQUEST_RETRY_TIMES = 3;
    /**
     * 限速 单位kb/s 为0时表示不限速
     */
    public static int TRAFFIC_LIMIT = 0;

    /**
     * 加密算法类型，默认使用SHA1算法
     * 支持的值：SHA1、SM3
     */
    public static EncryptionType ENCRYPTION_TYPE = EncryptionType.SHA1;

    /**
     * 禁止外部直接生成实例<br>
     */
    private Config() {
        try {
            LOCAL_IP = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            LOCAL_IP = "127.0.0.1";
        }
    }

    /**
     * 初始化构造密钥<br>
     *
     * @param ak 空间的AK信息
     * @param sk 空间的SK信息
     */
    public static void init(String ak, String sk) {
        AK = ak;
        SK = sk;
    }

    /**
     * 初始化构造密钥<br>
     *
     * @param ak 空间的AK信息
     * @param sk 空间的SK信息
     * @param logFilePath http请求日志的路径
     */
    public static void init(String ak, String sk, String logFilePath) {
        AK = ak;
        SK = sk;
        LOG_FILE_PATH = logFilePath;
    }

    /**
     * 初始化构造密钥<br>
     *
     * @param ak 空间的AK信息
     * @param sk 空间的SK信息
     */
    public static void init(String ak, String sk, String putUrl, String getUrl) {
        AK = ak;
        SK = sk;
        PUT_URL = putUrl;
        GET_URL = getUrl;
    }

    /**
     * 初始化构造密钥<br>
     *
     * @param ak 空间的AK信息
     * @param sk 空间的SK信息
     */
    public static void init(String ak, String sk, String putUrl, String getUrl, String mgrUrl) {
        AK = ak;
        SK = sk;
        PUT_URL = putUrl;
        GET_URL = getUrl;
        MGR_URL = mgrUrl;
    }

    /**
     * 初始化构造密钥<br>
     *
     * @param ak          空间的AK信息
     * @param sk          空间的SK信息
     * @param logFilePath http请求日志的路径
     */
    public static void init(String ak, String sk, String putUrl, String getUrl, String mgrUrl, String logFilePath) {
        AK = ak;
        SK = sk;
        PUT_URL = putUrl;
        GET_URL = getUrl;
        MGR_URL = mgrUrl;
        LOG_FILE_PATH = logFilePath;
    }

    /**
     *
     * @param ak          空间的AK信息
     * @param sk          空间的SK信息
     * @param logFilePath http请求日志的路径
     * @param connectionTimeOut 请求超时时间
     * @param socketTimeOut 数据传输超时时间
     */
    public static void init(String ak, String sk, String putUrl, String getUrl, String mgrUrl, String logFilePath,
                            int connectionTimeOut, int socketTimeOut) {
        AK = ak;
        SK = sk;
        PUT_URL = putUrl;
        GET_URL = getUrl;
        MGR_URL = mgrUrl;
        LOG_FILE_PATH = logFilePath;
        CONNECTION_TIME_OUT = connectionTimeOut;
        SOCKET_TIME_OUT = socketTimeOut;
    }

    /**
     *
     * @param ak          空间的AK信息
     * @param sk          空间的SK信息
     * @param logFilePath http请求日志的路径
     * @param connectionTimeOut 请求超时时间
     * @param socketTimeOut 数据传输超时时间
     * @param requestRetryTimes 重试次数
     */
    public static void init(String ak, String sk, String putUrl, String getUrl, String mgrUrl, String logFilePath,
                            int connectionTimeOut, int socketTimeOut, int requestRetryTimes) {
        AK = ak;
        SK = sk;
        PUT_URL = putUrl;
        GET_URL = getUrl;
        MGR_URL = mgrUrl;
        LOG_FILE_PATH = logFilePath;
        CONNECTION_TIME_OUT = connectionTimeOut;
        SOCKET_TIME_OUT = socketTimeOut;
        REQUEST_RETRY_TIMES = requestRetryTimes;
    }

    /**
     *
     * @param ak          空间的AK信息
     * @param sk          空间的SK信息
     * @param logFilePath http请求日志的路径
     * @param connectionTimeOut 请求超时时间
     * @param socketTimeOut 数据传输超时时间
     * @param requestRetryTimes 重试次数
     * @param trafficLimit 限速
     */
    public static void init(String ak, String sk, String putUrl, String getUrl, String mgrUrl, String logFilePath,
                            int connectionTimeOut, int socketTimeOut, int requestRetryTimes, int trafficLimit) {
        AK = ak;
        SK = sk;
        PUT_URL = putUrl;
        GET_URL = getUrl;
        MGR_URL = mgrUrl;
        LOG_FILE_PATH = logFilePath;
        CONNECTION_TIME_OUT = connectionTimeOut;
        SOCKET_TIME_OUT = socketTimeOut;
        REQUEST_RETRY_TIMES = requestRetryTimes;
        TRAFFIC_LIMIT = trafficLimit;
    }
}
