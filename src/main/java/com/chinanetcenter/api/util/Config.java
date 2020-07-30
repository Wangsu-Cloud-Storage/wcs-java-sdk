package com.chinanetcenter.api.util;

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
    public final static String VERSION_NO = "wcs-java-sdk-2.0.7";
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
}
