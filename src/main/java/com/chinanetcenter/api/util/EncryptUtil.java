package com.chinanetcenter.api.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.chinanetcenter.api.emum.EncryptionType;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;

/**
 * 签名工具类
 */
public class EncryptUtil {

    /**
     * 根据配置的加密类型选择加密算法
     * @param data 待加密数据
     * @param key 密钥
     * @return 加密后的十六进制字符串
     */
    public static String encrypt(byte[] data, String key) {
        if (EncryptionType.SM3.equals(Config.ENCRYPTION_TYPE)) {
            return sm3Hex(data, key);
        } else {
            return sha1Hex(data, key);
        }
    }

    public static String sha1Hex(byte[] data, String key) {
        byte[] keyBytes = key.getBytes();
        SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");
        Mac mac;
        StringBuilder sb = new StringBuilder();
        try {
            mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data);

            for (byte b : rawHmac) {
                sb.append(byteToHexString(b));
            }
        } catch (Exception e) {

        }
        return sb.toString();
    }

    /**
     * 使用SM3算法进行加密
     * @param data 待加密数据
     * @param key 密钥
     * @return 加密后的十六进制字符串
     */
    public static String sm3Hex(byte[] data, String key) {
        byte[] keyBytes = key.getBytes();
        StringBuilder sb = new StringBuilder();
        try {
            SM3Digest digest = new SM3Digest();
            HMac hMac = new HMac(digest);
            KeyParameter keyParameter = new KeyParameter(keyBytes);
            hMac.init(keyParameter);
            hMac.update(data, 0, data.length);
            byte[] result = new byte[hMac.getMacSize()];
            hMac.doFinal(result, 0);
            
            for (byte b : result) {
                sb.append(byteToHexString(b));
            }
        } catch (Exception e) {
            // 处理异常情况
        }
        return sb.toString();
    }

    private static String byteToHexString(byte ib) {
        char[] Digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] ob = new char[2];
        ob[0] = Digit[(ib >>> 4) & 0X0f];
        ob[1] = Digit[ib & 0X0F];
        String s = new String(ob);
        return s;
    }

}
