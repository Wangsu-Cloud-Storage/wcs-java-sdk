package com.chinanetcenter.api.util;

import com.chinanetcenter.api.entity.PutPolicy;

import java.util.HashMap;
import java.util.Map;

/**
 * 上传策略工具类
 */
public class PutPolicyUtil {

    /**
     * 解析输入的参数<br>
     * 例如命令java -jar wcs-demo-1.0-SNAPSHOT-jar-with-dependencies.jar put testbucket0001 panda002.mp4 D:\Project\WCSdocs\对外接口资料\test.mp4 persistentOps/avthumb/flv/vb/1.25m;vframe/jpg/offset/0<br>
     * 其中参数persistentOps/avthumb/flv/vb/1.25m;vframe/jpg/offset/0 会被解析为persistentOps/和avthumb/flv/vb/1.25m;vframe/jpg/offset/0两部分<br>
     * 最终PutPolicy对象中的persistentOps属性会设置为avthumb/flv/vb/1.25m;vframe/jpg/offset/0<br>
     * PutPolicy其他参数的解析类似上面这个例子<br>
     * @param args
     * @return PutPolicy对象
     */
    public static PutPolicy setArgs(String[] args) {
        PutPolicy putPolicy = new PutPolicy();
        String[] tmpstr;
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("size")) {
                tmpstr = args[i].split("size=");
                putPolicy.setFsizeLimit(Long.parseLong(tmpstr[1]));
            } else if (args[i].startsWith("overwrite")) {
                tmpstr = args[i].split("overwrite=");
                putPolicy.setOverwrite(Integer.parseInt(tmpstr[1]));
            } else if (args[i].startsWith("returnUrl")) {
                tmpstr = args[i].split("returnUrl=");
                putPolicy.setReturnUrl(tmpstr[1]);
            } else if (args[i].startsWith("e")) {//按秒计算
                tmpstr = args[i].split("e=");
                putPolicy.setDeadline(tmpstr[1]);
            } else if (args[i].startsWith("returnBody")) {
                tmpstr = args[i].split("returnBody=");
                putPolicy.setReturnBody(tmpstr[1]);
            } else if (args[i].startsWith("callbackUrl")) {
                tmpstr = args[i].split("callbackUrl=");
                putPolicy.setCallbackUrl(tmpstr[1]);
            } else if (args[i].startsWith("callbackBody")) {
                tmpstr = args[i].split("callbackBody=");
                putPolicy.setCallbackBody(tmpstr[1]);
            } else if (args[i].startsWith("persistentOps")) {
                tmpstr = args[i].split("persistentOps=");
                putPolicy.setPersistentOps(tmpstr[1]);
            } else if (args[i].startsWith("persistentNotifyUrl")) {
                tmpstr = args[i].split("persistentNotifyUrl=");
                putPolicy.setPersistentNotifyUrl(tmpstr[1]);
            }
        }
        return putPolicy;
    }

    public static Map<String,String> setXArgs(String[] args){
        Map<String,String> params = new HashMap<String, String>();
        String[] tmpstr;
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("x:")) {
                tmpstr = args[i].split("=");
                params.put(tmpstr[0],tmpstr[1]);
            }
        }
        return params;
    }

}
