package com.chinanetcenter.api.emum;

/**
 * 加密算法类型的枚举类
 */
public enum EncryptionType {
    /**
     * SHA1算法
     */
    SHA1("SHA1", "SHA-1"),
    
    /**
     * SM3国密算法
     */
    SM3("SM3", "SM3");

    private final String code;
    private final String headerValue;

    EncryptionType(String code, String headerValue) {
        this.code = code;
        this.headerValue = headerValue;
    }

    /**
     * 获取加密算法代码
     * @return 加密算法代码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取HTTP请求头中使用的值
     * @return HTTP请求头中的值
     */
    public String getHeaderValue() {
        return headerValue;
    }

    /**
     * 通过代码获取枚举类型
     * @param code 代码
     * @return 枚举类型
     */
    public static EncryptionType fromCode(String code) {
        for (EncryptionType type : EncryptionType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return SHA1; // 默认返回SHA1
    }
} 