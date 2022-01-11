## wcs-java-sdk

java SDK基于网宿云存储API规范构建,支持1.6及以上版本（目前支持java平台使用，不适合android平台）。

 - [使用指南](#使用指南)
   - [准备开发环境](#准备开发环境)
   - [配置信息](#配置信息)
   - [文件上传](#文件上传)
     - [普通表单上传](#普通表单上传)
     - [分片上传](#分片上传)
   - [资源管理](#资源管理)
     - [删除文件](#删除文件)
     - [获取文件信息](#获取文件信息)
     - [列举资源](#列举资源)
     - [复制资源](#复制资源)
     - [移动资源](#移动资源)
     - [更新镜像资源](#更新镜像资源)
   - [音视频操作](#音视频操作)
   - [抓取资源](#抓取资源)
   - [下载资源](#下载资源)

### 使用指南
#### 准备开发环境
 - 在Maven项目中加入依赖项

        <dependency>
            <groupId>com.chinanetcenter.wcs.sdk</groupId>
            <artifactId>wcs-java-sdk</artifactId>
            <version>2.0.x</version>
        </dependency>

 - 使用GitHub下载


#### 配置信息
用户接入网宿云存储时，需要使用一对有效的AK和SK进行签名认证，并填写“上传域名”和“管理域名”等配置信息进行文件操作。配置信息只需要在整个应用程序中初始化一次即可，具体操作如下：

 - 开通网宿云存储平台账户
 - 登录网宿云存储平台，在“安全管理”下的“密钥管理”查看AK和SK，“域名查询”查看上传、管理域名。

在获取到AK和SK等信息之后，您可以按照如下方式进行信息初始化：

    import com.chinanetcenter.api.util.Config;
    //1.初始化信息
    String ak = "your access key";
    String sk = "your secrete key";
    String PUT_URL = "your uploadDomain";
    String GET_URL = "your downloadDomain";
    String MGR_URL = "your mgrDomain";
    Config.init(ak,sk,PUT_URL,GET_URL,MGR_URL);

#### 文件上传
1. returnUrl和callbackUrl不能同时指定。
2. 若文件大小超过20M，建议使用分片上传
3. 云存储提供的上传域名为普通域名，若对上传速度较为敏感，有要求的客户建议采用网宿上传加速服务。
4. SDK支持自动识别文件类型（参考demo uploadFileForAutoMimeType方法）

文件上传根据使用场景的不同分为三种模式：普通上传，回调上传，通知上传。上传方式可根据文件大小选择普通的表单上传或者分片上传。
1. 普通上传：用户在上传文件后，上传返回结果由云存储平台统一控制。
2. 回调上传：用户上传文件后，对返回给客户端的信息进行自定义。需要启用上传策略数据的callbackUrl参数,而callbackBody参数可选（建议使用该参数）。
3. 通知上传：用户在上传文件的同时，提交文件处理指令（包括视频转码，图片水印，图片缩放等操作）。需要启用上传策略数据的persistentOps参数和persistentNotifyUrl参数。


##### 普通表单上传
**范例：**
```
import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.entity.PutPolicy;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.util.DateUtil;
import com.chinanetcenter.api.util.TokenUtil;
import com.chinanetcenter.api.wsbox.FileUploadManage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UploadDemo {
    FileUploadManage fileUploadManage = new FileUploadManage();

    public static void main(String[] args) throws FileNotFoundException {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * 可在用户管理界面-安全管理-域名查询获取uploadDomain,MgrDomain,需要添加http://
         */
        Config.PUT_URL = "your uploadDomain";
        String bucketName = "your-bucket";
        String fileKey = "test.JPG";
        String fileKeyWithFolder = "folder/test.JPG";
        String srcFilePath = "D:\\testfile\\1m.JPG";
        UploadDemo demo = new UploadDemo();
	//上传本地文件
	//普通上传
        demo.uploadFile(bucketName, fileKey, srcFilePath);
	
	//上传后需要回调、返回信息。指定文件夹
        //demo.uploadReturnBody(bucketName, fileKeyWithFolder, srcFilePath);
	
	//上传指定文件类型，服务端默认按照文件后缀或者文件内容
        //demo.uploadMimeType(bucketName, fileKey, srcFilePath);
	
	//上传文件后对文件做预处理
        //demo.uploadPersistent(bucketName, fileKey, srcFilePath);
	
	//自动识别文件类型
	//demo.uploadFileForAutoMimeType(bucketName, fileKey, srcFilePath);
	
	//上传文件流
        //FileInputStream in = new FileInputStream(new File(srcFilePath));
        //demo.uploadFile(bucketName, fileKey, in);
        //demo.uploadFileForAutoMimeType(bucketName, fileKey, in);
    }

    /**
     * 通过本地的文件路径上传文件
     * 默认覆盖上传
     */
    public void uploadFile(String bucketName,String fileKey,String srcFilePath){
        try {
            HttpClientResult result = fileUploadManage.upload(bucketName,fileKey,srcFilePath);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过文件流上传文件，方法里会关闭InputStream
     * 默认覆盖上传
     */
    public void uploadFile(String bucketName,String fileKey,InputStream in){
        try {
            HttpClientResult result = fileUploadManage.upload(bucketName,fileKey,in);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传后需要回调、返回信息等，可通过PutPolicy指定上传策略
     * callbackurl、callbackbody、returnurl 类似这个方法
     */
    public void uploadReturnBody(String bucketName,String fileKey,String srcFilePath){
        String returnBody = "key=$(key)&fname=$(fname)&fsize=$(fsize)&url=$(url)&hash=$(hash)&mimeType=$(mimeType)";
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setOverwrite(1); //覆盖上传
        putPolicy.setDeadline(String.valueOf(DateUtil.nextDate(1,new Date()).getTime()));
        putPolicy.setReturnBody(returnBody);
        putPolicy.setScope(bucketName + ":" + fileKey);
        try {
            HttpClientResult result = fileUploadManage.upload(bucketName,fileKey,srcFilePath,putPolicy);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传指定文件类型、指定文件保存时间，服务端默认按照文件后缀或者文件内容
     * 指定了mimeType，在下载的时候Content-type会指定该类型
     */
    public void uploadMimeType(String bucketName,String fileKey,String srcFilePath){
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setOverwrite(1);
        putPolicy.setDeadline(String.valueOf(DateUtil.nextDate(1, new Date()).getTime()));
        putPolicy.setScope(bucketName + ":" + fileKey);
        try {
            String uploadToken = TokenUtil.getUploadToken(putPolicy);
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("token", uploadToken);
            paramMap.put("mimeType", "application/UQ");
	    paramMap.put("deadline", 365);
            HttpClientResult result = fileUploadManage.upload(paramMap,srcFilePath);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传文件后对该文件做转码
     * 上传成功后返回persistentId应答，可以通过这个id去查询转码情况
     */
    public void uploadPersistent(String bucketName,String fileKey,String srcFilePath){
        PutPolicy putPolicy = new PutPolicy();
        String returnBody = "key=$(key)&persistentId=$(persistentId)&fsize=$(fsize)";
        putPolicy.setOverwrite(1);
        putPolicy.setDeadline(String.valueOf(DateUtil.nextDate(1, new Date()).getTime()));
        putPolicy.setScope(bucketName + ":" + fileKey);
        putPolicy.setPersistentOps("imageMogr2/jpg/crop/500x500/gravity/CENTER/lowpoly/1|saveas/ZnV5enRlc3Q4Mi0wMDE6ZG9fY3J5c3RhbGxpemVfZ3Jhdml0eV9jZW50ZXJfMTQ2NTkwMDg0Mi5qcGc="); // 设置视频转码操作
        putPolicy.setPersistentNotifyUrl("http://demo1/notifyUrl"); // 设置转码后回调的接口
        putPolicy.setReturnBody(returnBody);
        try {
            HttpClientResult result = fileUploadManage.upload(bucketName,fileKey,srcFilePath,putPolicy);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
	/**
	 * 通过本地的文件路径上传文件,会自动识别文件类型
	 * 默认覆盖上传
	 */
	public void uploadFileForAutoMimeType(String bucketName, String fileKey, String srcFilePath) {
		try {
			HttpClientResult result = fileUploadManage.uploadForAutoMimeType(bucketName, fileKey, srcFilePath);
			System.out.println(result.getStatus() + ":" + result.getResponse());
		} catch (WsClientException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通过文件流上传文件，方法里会关闭InputStream，会自动识别文件类型
	 * 默认覆盖上传
	 */
	public void uploadFileForAutoMimeType(String bucketName, String fileKey, InputStream in) {
		try {
			HttpClientResult result = fileUploadManage.uploadForAutoMimeType(bucketName, fileKey, in);
			System.out.println(result.getStatus() + ":" + result.getResponse());
		} catch (WsClientException e) {
			e.printStackTrace();
		}
	}
}
```

##### 分片上传
**范例：**
```
import com.chinanetcenter.api.entity.PutPolicy;
import com.chinanetcenter.api.entity.SliceUploadHttpResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.sliceUpload.BaseBlockUtil;
import com.chinanetcenter.api.sliceUpload.JSONObjectRet;
import com.chinanetcenter.api.util.*;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.util.DateUtil;
import com.chinanetcenter.api.util.EncodeUtils;
import com.chinanetcenter.api.util.WetagUtil;
import com.chinanetcenter.api.wsbox.SliceUploadResumable;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SliceUploadDemo {

    public static void main(String[] args) throws FileNotFoundException {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * 可在用户管理界面-安全管理-域名查询获取uploadDomain,MgrDomain,需要添加http://
         */
        Config.PUT_URL = "your uploadDomain";
        String bucketName = "your-bucket";
        String fileKey = "java-sdk/com.toycloud.MeiYe.apk";

        String srcFilePath = "D:\\testfile\\test001\\com.toycloud.MeiYe.apk";
	
	/**
	 * 设置每一片大小为4M，减少上传请求。如果网络环境不好不建议设置改参数或者设置成较小的值，避免超时的情况。该参数默认值为256KB
	 */
        BaseBlockUtil.CHUNK_SIZE = 4 * 1024 * 1024;
	
	/**
	 * 设置块上传并发数，加快上传速度。如果网络环境不好不建议设置改参数或者设置成较小的值，避免超时的情况。该参数默认值为1
	 */
	BaseBlockUtil.THREAD_NUN = 5；
        SliceUploadDemo demo = new SliceUploadDemo();
        demo.sliceUpload(bucketName,fileKey,srcFilePath);
		demo.sliceUploadForAutoMimeType(bucketName, fileKey, srcFilePath);
        /**  第二种方式，key不写到scope里，而是从head指定 用于同一个token可以上传多个文件
        String fileKey2 = "java-sdk/com.toycloud.MeiYe2.apktest";
        String mimeType = "application/vnd.android.package-archive";
        demo.sliceUpload(bucketName,fileKey2,srcFilePath,mimeType);
         */
    }

    public void sliceUpload(final String bucketName, final String fileKey, final String filePath) {
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setScope(bucketName + ":" + fileKey);
        putPolicy.setOverwrite(1);
        putPolicy.setDeadline(String.valueOf(DateUtil.nextDate(1, new Date()).getTime()));
        JSONObjectRet jsonObjectRet = getJSONObjectRet(bucketName,fileKey,filePath);
        SliceUploadResumable sliceUploadResumable = new SliceUploadResumable();
        sliceUploadResumable.execUpload(bucketName, fileKey, filePath, putPolicy, null, jsonObjectRet);
    }

    public void sliceUpload(final String bucketName, final String fileKey, final String filePath,String mimeType) {
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setScope(bucketName);
        putPolicy.setOverwrite(1);
        putPolicy.setDeadline(String.valueOf(DateUtil.nextDate(1, new Date()).getTime()));
        JSONObjectRet jsonObjectRet = getJSONObjectRet(bucketName,fileKey,filePath);
        SliceUploadResumable sliceUploadResumable = new SliceUploadResumable();
        Map<String,String> headMap = new HashMap<String, String>();
        headMap.put("mimeType",mimeType);
        headMap.put("key", EncodeUtils.urlsafeEncode(fileKey));
        sliceUploadResumable.execUpload(bucketName, fileKey, filePath, putPolicy, null, jsonObjectRet,headMap);
    }
	/**
	 * 分片上传，会自动识别文件类型
	 *
	 * @param bucketName
	 * @param fileKey
	 * @param filePath
	 */
	public void sliceUploadForAutoMimeType(final String bucketName, final String fileKey, final String filePath) {
		PutPolicy putPolicy = new PutPolicy();
		putPolicy.setScope(bucketName + ":" + fileKey);
		putPolicy.setOverwrite(1);
		putPolicy.setDeadline(String.valueOf(DateUtil.nextDate(1, new Date()).getTime()));
		JSONObjectRet jsonObjectRet = getJSONObjectRet(bucketName, fileKey, filePath);
		SliceUploadResumable sliceUploadResumable = new SliceUploadResumable();
		sliceUploadResumable.execUploadForAutoMimeType(bucketName, fileKey, filePath, putPolicy, null, jsonObjectRet);
	}

    public JSONObjectRet getJSONObjectRet(final String bucketName,final String fileKey,final String filePath){
        return new JSONObjectRet() {
            /**
             * 文件上传成功后会回调此方法
             * 校验下上传文件的hash和本地文件的hash是否一致，不一致可能本地文件被修改过
             */
            @Override
            public void onSuccess(JsonNode obj) {
                File fileHash = new File(filePath);
                String eTagHash = WetagUtil.getEtagHash(fileHash.getParent(), fileHash.getName());// 根据文件内容计算hash
                SliceUploadHttpResult result = new SliceUploadHttpResult(obj);
                if (eTagHash.equals(result.getHash())) {
                    System.out.println("上传成功");
                } else {
                    System.out.println("hash not equal,eTagHash:" + eTagHash + " ,hash:" + result.getHash());
                }
            }

            @Override
            public void onSuccess(byte[] body) {
                System.out.println(new String(body));
            }

            // 文件上传失败回调此方法
            @Override
            public void onFailure(Exception ex) {
                if (ex instanceof WsClientException) {
                    WsClientException wsClientException = (WsClientException) ex;
                    System.out.println(wsClientException.code + ":" + wsClientException.getMessage());
                }else {
                    ex.printStackTrace();
                }
                System.out.println("上传出错，" + ex.getMessage());
            }

            // 进度条展示，每上传成功一个块回调此方法
            @Override
            public void onProcess(long current, long total) {
                System.out.printf("%s\r", current * 100 / total + " %");
            }

            /**
             * 持久化，断点续传时把进度信息保存，下次再上传时把JSONObject赋值到PutExtra
             * sdk默认把信息保存到磁盘文件，如果有需要请自己保存到db
             * 下次再续传的时候把值赋值到PutExtra参数里
             */
            @Override
            public void onPersist(JsonNode obj) {
                BaseBlockUtil.savePutExtra(bucketName, fileKey, obj);
            }
        };
    }
}
```

#### 资源管理
对存储在网宿云存储上的文件进行处理，包括删除、列举资源等。

##### 删除文件

**范例：**
```
import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.wsbox.OperationManager;

public class DeleteDemo {
    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * 可在用户管理界面-安全管理-域名查询获取uploadDomain,MgrDomain,需要添加http://
         */
        Config.MGR_URL = "your MgrDomain";
        String bucketName = "your-bucket";
        String fileKey = "java-sdk/testfile.jpg";
        OperationManager fileManageCommand = new OperationManager();
        try {
            HttpClientResult result = fileManageCommand.delete(bucketName, fileKey);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
}
```

##### 获取文件信息
获取一个文件的信息描述，包括文件名，文件大小，文件的ETag信息等

**范例：**
```
import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.wsbox.OperationManager;

public class StatDemo {
    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * 可在用户管理界面-安全管理-域名查询获取uploadDomain,MgrDomain,需要添加http://
         */
        Config.MGR_URL = "your MgrDomain";
        String bucketName = "your-bucket";
        String fileKey = "java-sdk/testfile.jpg";
        OperationManager fileManageCommand = new OperationManager();
        try {
            HttpClientResult result = fileManageCommand.stat(bucketName, fileKey);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
}
```

##### 列举资源
列举指定空间内的资源

**范例：**
```
import com.chinanetcenter.api.entity.FileListObject;
import com.chinanetcenter.api.entity.FileMessageObject;
import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.util.StringUtil;
import com.chinanetcenter.api.wsbox.OperationManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class ListDemo {
    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * 可在用户管理界面-安全管理-域名查询获取uploadDomain,MgrDomain,需要添加http://
         */
        Config.MGR_URL = "your MgrDomain";
        String bucketName = "your-bucket";
        try {
            ListDemo demo = new ListDemo();
            demo.listFile(bucketName);
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }

    public void listFile(String bucket) throws WsClientException {
        OperationManager fileManageCommand = new OperationManager();
        int querySize = 10;
        String prex = "";
        HttpClientResult result = fileManageCommand.fileList(bucket, String.valueOf(querySize), prex, "", "");
        while (result != null && result.getStatus() == 200 && StringUtil.isNotEmpty(result.getResponse()) && !"{}".equals(result.getResponse())) {
            JsonMapper objectMapper = new JsonMapper();
            try {
                FileListObject fileListObject = objectMapper.readValue(result.getResponse(), FileListObject.class);
                for (String folder : fileListObject.getCommonPrefixes()) {
                    System.out.println("folder:" + folder);
                }
                for (FileMessageObject object : fileListObject.getItems()) {
                    System.out.print("key:" + object.getKey() + "\t");
                    System.out.print("putTime:" + object.getPutTime() + "\t");
                    System.out.print("hash:" + object.getHash() + "\t");
                    System.out.print("fsize:" + object.getFsize() + "\t");
                    System.out.print("mimeType:" + object.getMimeType() + "\t");
                    System.out.println();
                }
                if (fileListObject.getItems().size() < querySize) {
                    break;
                }
                result = fileManageCommand.fileList(bucket, String.valueOf(querySize), prex, "", fileListObject.getMarker());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
```
##### 复制资源
将指定资源复制为新命名的资源。如果目标空间存在同名资源，不会覆盖。

**范例：**
```
import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.wsbox.OperationManager;

public class CopyDemo {
    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * 可在用户管理界面-安全管理-域名查询获取uploadDomain,MgrDomain,需要添加http://
         */
        Config.MGR_URL = "your MgrDomain";
        String bucketName = "your-bucket";
        String fileKey = "java-sdk/testfile.jpg";
        String newBucketName = "your-bucket";
        String newFileKey = "java-sdk/testfile2.jpg";
        OperationManager fileManageCommand = new OperationManager();
        try {
            HttpClientResult result = fileManageCommand.copy(bucketName, fileKey,newBucketName,newFileKey);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
}
```

##### 移动资源
将源空间的指定资源移动到目标空间，或在同一空间内对资源重命名。如果目标空间存在同名资源，不会覆盖。

**范例：**
```
import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.wsbox.OperationManager;

public class MoveDemo {
    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * 可在用户管理界面-安全管理-域名查询获取uploadDomain,MgrDomain,需要添加http://
         */
        Config.MGR_URL = "your MgrDomain";
        String bucketName = "your-bucket";
        String fileKey = "java-sdk/testfile2.jpg";
        String newBucketName = "your-bucket";
        String newFileKey = "java-sdk/testfile3.jpg";
        OperationManager fileManageCommand = new OperationManager();
        try {
            HttpClientResult result = fileManageCommand.move(bucketName, fileKey, newBucketName, newFileKey);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
}
```

##### 更新镜像资源
对于设置了镜像存储的空间，提供从镜像源站抓取指定资源并存储到该空间中的功能。 如果该空间中已存在同名资源，则会被镜像源站的资源覆盖。

**范例：**
```
import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.wsbox.OperationManager;

import java.util.ArrayList;

public class PreFetchDemo {
    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * 可在用户管理界面-安全管理-域名查询获取uploadDomain,MgrDomain,需要添加http://
         */
        Config.MGR_URL = "your MgrDomain";
        String bucketName = "your-bucket";
        new PreFetchDemo().prefetch(bucketName);
    }

    public void prefetch(String bucketName) {
        OperationManager fileManageCommand = new OperationManager();
        String fileName1 = "testPreFetch1.png"; // 文件名称
        String fileName2 = "testPreFetch2.png"; // 文件名称
        ArrayList<String> fileKeys = new ArrayList<String>();
        fileKeys.add(fileName1);
        fileKeys.add(fileName2);

        try {
            HttpClientResult result = fileManageCommand.prefetch(bucketName, fileKeys);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
}
```
#### 音视频操作
提供音视频处理功能，包括：转码转封装、音视频拼接等操作。具体处理参数详见[音视频处理Ops参数格式](#document/API/Appendix/fopsParam#音视频处理)

**范例：**
```
import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.util.EncodeUtils;
import com.chinanetcenter.api.wsbox.OperationManager;

public class FopsDemo {
    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * 可在用户管理界面-安全管理-域名查询获取uploadDomain,MgrDomain,需要添加http://
         */
        Config.MGR_URL = "your MgrDomain";
        String bucketName = "your-bucket";
        String fileKey = "java-sdk/10m2.mp4";
        //设置转码操作参数
        String fops = "avthumb/mp4/s/640x360/vb/1.25m";
        //可以对转码后的文件进行使用saveas参数自定义命名，
        //也可以不指定,会默认命名并保存在当前空间 对 目标Bucket_Name:自定义文件key 做base64。
        String saveAsKey = EncodeUtils.urlsafeEncode(bucketName + ":1.256m.jpg");
        fops += "|saveas/" + saveAsKey;
        String notifyURL = "http://demo1/notifyUrl";  //通知地址，转码成功后会回调此地址
        String force = "1";
        String separate = "1";
        FopsDemo demo = new FopsDemo();
        demo.fileTrans(bucketName,fileKey,fops,notifyURL,force,separate);

    }

    public void fileTrans(String bucketName, String fileKey, String fops, String notifyURL, String force,String separate) {
        OperationManager fileManageCommand = new OperationManager();
        try {
            HttpClientResult result = fileManageCommand.fops(bucketName, fileKey, fops, notifyURL,force,separate);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
}
```
#### 抓取资源
提供从指定URL抓取资源，并存储到指定空间。

**范例：**
```
import com.chinanetcenter.api.entity.FmgrParam;
import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.wsbox.FmgrFileManage;

import java.util.ArrayList;
import java.util.List;

public class FmgrFetchDemo {
    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * 可在用户管理界面-安全管理-域名查询获取uploadDomain,MgrDomain,需要添加http://
         */
        Config.MGR_URL = "your MgrDomain";
        String bucketName = "your-bucket";
        FmgrFileManage fileManageCommand = new FmgrFileManage();
        try {
            List<FmgrParam> list = new ArrayList<FmgrParam>();
            FmgrParam fmgrParam = new FmgrParam();
            fmgrParam.setBucket(bucketName);
            fmgrParam.setFetchURL("https://wcs.chinanetcenter.com/indexNew/image/pic1.jpg");
            fmgrParam.setFileKey("indexNew/image/pic1.jpg");
	    fmgrParam.putExtParams("fetchTS", "0");
            list.add(fmgrParam);
            FmgrParam fmgrParam2 = new FmgrParam();
            fmgrParam2.setBucket(bucketName);
            fmgrParam2.setFetchURL("https://wcs.chinanetcenter.com/indexNew/image/pic2.m3u8");
            fmgrParam2.setFileKey("indexNew/image/pic2.m3u8");
	    fmgrParam.putExtParams("fetchTS", "0");
            list.add(fmgrParam2);
            String notifyURL = "http://demo1/notifyUrl";  //通知地址，转码成功后会回调此地址
            String force = "1";
            String separate = "1";
            HttpClientResult result = fileManageCommand.fmgrFetch(list, notifyURL, force, separate);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
}
```

#### 下载资源
提供从指定域名和资源名下载资源。
```
/**
 * 下载文件
 */
public class DownloadDemo {
    public static void main(String[] args) {
        String downloadDomain = "your download domain";
        String fileKey = "file name";
        String filePath = "local path";
        OperationManager fileManageCommand = new OperationManager();
        try {
            HttpClientResult result = fileManageCommand.download(downloadDomain, fileKey, filePath, null);
            System.out.println(result.getStatus());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
}
```
