package com.chinanetcenter.api.util;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;


/**
 * 获取文件类型工具类，根据文件后缀名判断
 * Created by xiexb on 2014/6/12.
 * <p>
 * 增加jmimemagic判断没有后缀的文件类型
 * Modified by xiexb on 2014/9/13.
 */
public class MimetypesUtil {
    public static final String m3u8Type = "application/vnd.apple.mpegurl";
    public static final String defaultType = "application/octet-stream";
    private static final Logger logger = LoggerFactory.getLogger(MimetypesUtil.class);
    public static long fileSizeLimit = 52428800;//小于五十兆的文件，没有文件后缀，通过jmimemagic读取文件内容判断文件类型
    private static MimetypesFileTypeMap mimetypesFileTypeMap;

    static {
        loadResource();
        initFileSizeLimit();
    }

    private static void initFileSizeLimit() {
        if (Config.MIMETYPE_FILESIZELIMIT > 0) {
            fileSizeLimit = Config.MIMETYPE_FILESIZELIMIT;
        }
    }

    private static void loadResource() {
        InputStream fis = null;
        try {
            fis = MimetypesUtil.class.getResourceAsStream("/wcs.mime.types");
            mimetypesFileTypeMap = new MimetypesFileTypeMap(fis);
        } catch (Exception e) {
            logger.error("wcs.mime.types配置文件不存在", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                    fis = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据指定的paramString获取mimetype
     *
     * @param paramString
     * @return
     */
    public static String getContentType(String paramString) {
        paramString = paramString.toLowerCase();
        return mimetypesFileTypeMap.getContentType(paramString);
    }

    /**
     * 没有指定format的时候，是取mimeType，源文件类型，则根据文件名获取
     *
     * @param format
     * @param mimeType
     * @param fileName
     * @return
     */
    public static String getContentType(String format, String mimeType, String fileName) {
        //没有指定format的时候，是跟源文件的一样
        String returnMimeType = mimeType;
        if (StringUtils.isNotBlank(format)) {
            returnMimeType = getContentType("temp." + format);
            logger.info("format is [" + format + "] and contentType is [" + returnMimeType + "]");
        }
        if (StringUtils.isBlank(mimeType)) {
            //如果没有指定format且找不到源文件类型，则根据文件名获取
            returnMimeType = MimetypesUtil.getContentType(fileName);
        }
        return returnMimeType;
    }

    /**
     * 由于用户可能上传一个没有后缀的的文件，当该文件小于fileSizeLimit时，通过jmimemagic判断文件类型
     * 根据指定的file文件和paramString获取mimetype
     *
     * @param file
     * @param paramString
     * @return
     */
    public static String getContentType(File file, String paramString) {
        String contentType = null;
        int i = -1;
        if (null != paramString) {
            i = paramString.lastIndexOf(".");
            if (i > 0) {
                contentType = getContentType(paramString);
                if (defaultType.equals(contentType)) {
                    i = -1;
                }
            }

        }
        if (i < 0) {//没有文件后缀的
            if (file.exists() && file.length() < fileSizeLimit) {//文件大小
                Magic parser = new Magic();
                try {
                    MagicMatch match = parser.getMagicMatch(file, false);
                    contentType = match.getMimeType();
                } catch (Exception e) {
                }
            }
        }
        if (StringUtils.isBlank(contentType) || StringUtils.equals(contentType, "???")) {
            contentType = getContentType(paramString);
        }
        return contentType;
    }


    /**
     * 由于用户可能上传一个流，通过jmimemagic判断文件类型
     * 根据指定的file文件和paramString获取mimetype
     *
     * @param inputStream
     * @param paramString
     * @return
     */
    public static String getContentType(InputStream inputStream, String paramString) {
        String contentType = null;
        int i = -1;
        if (null != paramString) {
            i = paramString.lastIndexOf(".");
            if (i > 0) {
                contentType = getContentType(paramString);
                if (defaultType.equals(contentType)) {
                    i = -1;
                }
            }

        }
        if (i < 0) {//没有文件后缀的
            try {
                if (null != inputStream && inputStream.available() < fileSizeLimit) {
                    ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
                    byte[] buff = new byte[1024];
                    int rc = 0;
                    while ((rc = inputStream.read(buff, 0, 1024)) > 0) {
                        swapStream.write(buff, 0, rc);
                    }
                    byte[] datas = swapStream.toByteArray();
                    Magic parser = new Magic();
                    MagicMatch match = parser.getMagicMatch(datas, false);
                    contentType = match.getMimeType();
                    inputStream = new ByteArrayInputStream(datas);
                }
            } catch (Exception e) {
                logger.error("getContentType failed");
            }

        }
        if (StringUtils.isBlank(contentType) || StringUtils.equals(contentType, "???")) {
            contentType = getContentType(paramString);
        }
        return contentType;
    }

    /**
     * 由于用户可能上传一个byte数据，通过jmimemagic判断文件类型
     * 根据指定的file文件和paramString获取mimetype
     *
     * @param datas
     * @param paramString
     * @return
     */
    public static String getContentType(byte[] datas, String paramString) {
        String contentType = null;
        int i = -1;
        if (null != paramString) {
            i = paramString.lastIndexOf(".");
            if (i > 0) {
                contentType = getContentType(paramString);
                if (defaultType.equals(contentType)) {
                    i = -1;
                }
            }

        }
        if (i < 0) {//没有文件后缀的
            try {
                if (null != datas && datas.length < fileSizeLimit) {
                    Magic parser = new Magic();
                    MagicMatch match = parser.getMagicMatch(datas, false);
                    contentType = match.getMimeType();
                }
            } catch (Exception e) {
                logger.error("getContentType failed");
            }

        }
        if (StringUtils.isBlank(contentType) || StringUtils.equals(contentType, "???")) {
            contentType = getContentType(paramString);
        }
        return contentType;
    }

}

