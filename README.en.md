# Cloud Storage Java SDK

## Language

- [简体中文](README.md)
- [English](README.en.md)

## wcs-java-sdk

wcs-java-SDK is built based on the wcs cloud storage API specification and supports Java 8 and above versions (currently supports Java platform use, not suitable for Android platform).

- [Usage Guide](#usage-guide)
  - [Prepare Development Environment](#prepare-development-environment)
  - [Configuration Information](#configuration-information)
  - [Set Encryption Algorithm](#set-encryption-algorithm)
  - [File Upload](#file-upload)
    - [Normal Form Upload](#normal-form-upload)
    - [Slice Upload](#slice-upload)
  - [Resource Management](#resource-management)
    - [Delete File](#delete-file)
    - [Get File Information](#get-file-information)
    - [List Resources](#list-resources)
    - [Copy Resources](#copy-resources)
    - [Move Resources](#move-resources)
    - [Update Mirror Resources](#update-mirror-resources)
    - [Delete Resources by Prefix](#delete-resources-by-prefix)
  - [Audio and Video Operations](#audio-and-video-operations)
  - [Fetch Resources](#fetch-resources)
  - [Download Resources](#download-resources)
  - [Calculate File ETag](#calculate-file-etag)

### Usage Guide

#### Prepare Development Environment

- Add dependencies in Maven project ([Get maven latest version](https://mvnrepository.com/artifact/com.chinanetcenter.wcs.sdk/wcs-java-sdk))
  ```
   <dependency>
       <groupId>com.chinanetcenter.wcs.sdk</groupId>
       <artifactId>wcs-java-sdk</artifactId>
       <version>2.0.11</version>
   </dependency>
  ```
- Download using GitHub

#### Configuration Information

When users access wcs cloud storage, they need to use a pair of valid AK and SK for signature authentication, and fill in configuration information such as "upload domain" and "management domain" for file operations. The configuration information only needs to be initialized once in the entire application, as follows:

- Open a wcs cloud storage platform account
- Log in to the wcs cloud storage platform, view AK and SK under "Key Management" under "Security Management", and view upload and management domains under "Domain Query".

After obtaining AK and SK information, you can initialize the information as follows:

```
import com.chinanetcenter.api.util.Config;
//1. Initialize information
String ak = "your access key";
String sk = "your secrete key";
String PUT_URL = "your uploadDomain";
String GET_URL = "your downloadDomain";
String MGR_URL = "your mgrDomain";
int connection_timeout = 30000;
int socket_timeout = 60000;
Config.init(ak,sk,PUT_URL,GET_URL,MGR_URL,'logfilePath',connection_timeout,socket_timeout);
```

#### Set Encryption Algorithm

Added SM3 encryption algorithm support, you can choose between SHA1 and SM3 encryption algorithms.

**Configuration Method**

```
In the Config class, a new ENCRYPTION_TYPE configuration item has been added, using the enumeration type EncryptionType, which supports the following values:
- EncryptionType.SHA1 (default value): Use HMAC-SHA1 algorithm for signing
- EncryptionType.SM3: Use SM3 algorithm for signing
```

**Example Code**

```java
// Use SHA1 algorithm
Config.ENCRYPTION_TYPE = EncryptionType.SHA1;
// Use SM3 algorithm
Config.ENCRYPTION_TYPE = EncryptionType.SM3;
```

**HTTP Request Header**

The SDK will automatically add the `encryption-type` header information when sending HTTP requests:

- When using SHA1 algorithm, the header value is `SHA-1`
- When using SM3 algorithm, the header value is `SM3`
  The SDK will automatically add this header in each HTTP request method, no need for manual configuration.

**Example Demo**

Refer to the sample code in `src/main/java/com/chinanetcenter/api/demo/UploadDemo.java`, which demonstrates how to use different encryption algorithms for upload operations

#### Calculate Upload Credentials

1. For security reasons, mobile terminals such as JavaScript, Android, and iOS are not suitable for configuring sensitive information such as AK and SK to the front end to directly calculate tokens. The java-SDK can be used as the token server for these front-end SDKs.

**Example:**

```
bucketName = 'your bucket name';
fileKey = 'your key name';
PutPolicy putPolicy = new PutPolicy();		// Create PutPolicy object
putPolicy.setOverwrite(1);    			// Configure overwrite upload
putPolicy.setScope(bucketName + ":" + fileKey); // Configure scope to specify the space and file name for upload
putPolicy.setDeadline("timestamp");		// Configure token expiration time, format is millisecond-level expiration timestamp
String uploadToken = TokenUtil.getUploadToken(putPolicy);
```

#### File Upload

1. returnUrl and callbackUrl cannot be specified at the same time.
2. If the file size exceeds 20M, it is recommended to use slice upload
3. The upload domain name provided by cloud storage is a normal domain name. If you are sensitive to upload speed and have requirements, it is recommended to use wcs upload acceleration service.
4. SDK supports automatic file type identification (refer to demo uploadFileForAutoMimeType method)

File upload is divided into three modes according to different usage scenarios: normal upload, callback upload, and notification upload. The upload method can choose normal form upload or slice upload according to the file size.

1. Normal upload: After the user uploads the file, the upload return result is uniformly controlled by the cloud storage platform.
2. Callback upload: After the user uploads the file, the information returned to the client is customized. It is necessary to enable the callbackUrl parameter of the upload policy data, and the callbackBody parameter is optional (it is recommended to use this parameter).
3. Notification upload: While uploading files, users submit file processing instructions (including video transcoding, image watermarking, image scaling, etc.). It is necessary to enable the persistentOps parameter and persistentNotifyUrl parameter of the upload policy data.

##### Normal Form Upload

**Example:**

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
         * You can get uploadDomain, MgrDomain in user management interface - security management - domain query, need to add http://
         */
        Config.PUT_URL = "your uploadDomain";
        String bucketName = "your-bucket";
        String fileKey = "test.JPG";
        String fileKeyWithFolder = "folder/test.JPG";
        String srcFilePath = "D:\\testfile\\1m.JPG";
        UploadDemo demo = new UploadDemo();
	//Upload local file
	//Normal upload
        demo.uploadFile(bucketName, fileKey, srcFilePath);
	
	//Upload with callback, return information. Specify folder
        //demo.uploadReturnBody(bucketName, fileKeyWithFolder, srcFilePath);
	
	//Upload specified file type, server defaults to file suffix or file content
        //demo.uploadMimeType(bucketName, fileKey, srcFilePath);
	
	//Preprocess file after upload
        //demo.uploadPersistent(bucketName, fileKey, srcFilePath);
	
	//Auto identify file type
	//demo.uploadFileForAutoMimeType(bucketName, fileKey, srcFilePath);
	
	//Upload file stream
        //FileInputStream in = new FileInputStream(new File(srcFilePath));
        //demo.uploadFile(bucketName, fileKey, in);
        //demo.uploadFileForAutoMimeType(bucketName, fileKey, in);
    }

    /**
     * Upload file through local file path
     * Default overwrite upload
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
     * Upload file through file stream, the method will close InputStream
     * Default overwrite upload
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
     * After uploading, callback, return information, etc. can be specified through PutPolicy
     * callbackurl, callbackbody, returnurl similar to this method
     */
    public void uploadReturnBody(String bucketName,String fileKey,String srcFilePath){
        String returnBody = "key=$(key)&fname=$(fname)&fsize=$(fsize)&url=$(url)&hash=$(hash)&mimeType=$(mimeType)";
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setOverwrite(1); //Overwrite upload
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
     * Upload specified file type, specified file save time, server defaults to file suffix or file content
     * Specified mimeType, Content-type will be specified as this type when downloading
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
	    paramMap.put("deleteAfterDays", 365);
            HttpClientResult result = fileUploadManage.upload(paramMap,srcFilePath);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * Transcode the file after uploading
     * After successful upload, return persistentId response, you can use this id to query the transcoding status
     */
    public void uploadPersistent(String bucketName,String fileKey,String srcFilePath){
        PutPolicy putPolicy = new PutPolicy();
        String returnBody = "key=$(key)&persistentId=$(persistentId)&fsize=$(fsize)";
        putPolicy.setOverwrite(1);
        putPolicy.setDeadline(String.valueOf(DateUtil.nextDate(1, new Date()).getTime()));
        putPolicy.setScope(bucketName + ":" + fileKey);
        putPolicy.setPersistentOps("imageMogr2/jpg/crop/500x500/gravity/CENTER/lowpoly/1|saveas/ZnV5enRlc3Q4Mi0wMDE6ZG9fY3J5c3RhbGxpemVfZ3Jhdml0eV9jZW50ZXJfMTQ2NTkwMDg0Mi5qcGc="); // Set video transcoding operation
        putPolicy.setPersistentNotifyUrl("http://demo1/notifyUrl"); // Set callback interface after transcoding
        putPolicy.setReturnBody(returnBody);
        try {
            HttpClientResult result = fileUploadManage.upload(bucketName,fileKey,srcFilePath,putPolicy);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
	/**
	 * Upload file through local file path, will automatically identify file type
	 * Default overwrite upload
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
	 * Upload file through file stream, the method will close InputStream, will automatically identify file type
	 * Default overwrite upload
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

##### Slice Upload

**Example:**

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
         * You can get uploadDomain, MgrDomain in user management interface - security management - domain query, need to add http://
         */
        Config.PUT_URL = "your uploadDomain";
        String bucketName = "your-bucket";
        String fileKey = "java-sdk/com.toycloud.MeiYe.apk";

        String srcFilePath = "D:\\testfile\\test001\\com.toycloud.MeiYe.apk";
	
	/**
	 * Set the size of each block to 4M, if the network condition is good, you can set 8M, 12M, 16M and other 4M multiples to improve upload efficiency. The default value of this parameter is 4M
	 */
	BaseBlockUtil.BLOCK_SIZE = 4 * 1024 * 1024;
	
	/**
	 * Set each slice size to 4M to reduce upload requests. If the network environment is not good, it is not recommended to set this parameter or set it to a smaller value to avoid timeout. The default value of this parameter is 256KB
	 */
        BaseBlockUtil.CHUNK_SIZE = 4 * 1024 * 1024;
	
	/**
	 * Set block upload concurrency to speed up upload. If the network environment is not good, it is not recommended to set this parameter or set it to a smaller value to avoid timeout. The default value of this parameter is 5
	 */
	BaseBlockUtil.THREAD_NUN = 5；
	
	/**
	 * Set the number of retries for request timeout and server response exceptions. The default value of this parameter is 3
	 */
	BaseBlockUtil.TRIED_TIMES = 5；
        SliceUploadDemo demo = new SliceUploadDemo();
        demo.sliceUpload(bucketName,fileKey,srcFilePath);
		demo.sliceUploadForAutoMimeType(bucketName, fileKey, srcFilePath);
        /**  Second way, key is not written to scope, but specified from head for the same token can upload multiple files
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
	 * Slice upload, will automatically identify file type
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
             * This method will be called back after the file is uploaded successfully
             * Check whether the hash of the uploaded file is consistent with the hash of the local file, if not consistent, the local file may have been modified
             */
            @Override
            public void onSuccess(JsonNode obj) {
                File fileHash = new File(filePath);
                String eTagHash = WetagUtil.getEtagHash(fileHash.getParent(), fileHash.getName());// Calculate hash based on file content
                SliceUploadHttpResult result = new SliceUploadHttpResult(obj);
                if (eTagHash.equals(result.getHash())) {
                    System.out.println("Upload successful");
                } else {
                    System.out.println("hash not equal,eTagHash:" + eTagHash + " ,hash:" + result.getHash());
                }
            }

            @Override
            public void onSuccess(byte[] body) {
                System.out.println(new String(body));
            }

            // This method will be called back when file upload fails
            @Override
            public void onFailure(Exception ex) {
                if (ex instanceof WsClientException) {
                    WsClientException wsClientException = (WsClientException) ex;
                    System.out.println(wsClientException.code + ":" + wsClientException.getMessage());
                }else {
                    ex.printStackTrace();
                }
                System.out.println("Upload error," + ex.getMessage());
            }

            // Progress bar display, this method is called back every time a block is uploaded successfully
            @Override
            public void onProcess(long current, long total) {
                System.out.printf("%s\r", current * 100 / total + " %");
            }

            /**
             * Persistence, save progress information during breakpoint resume, and assign JSONObject to PutExtra when uploading next time
             * The sdk saves the information to the disk file by default, if needed, please save it to db yourself
             * When resuming upload next time, assign the value to the PutExtra parameter
             */
            @Override
            public void onPersist(JsonNode obj) {
                BaseBlockUtil.savePutExtra(bucketName, fileKey, obj);
            }
        };
    }
}
```

#### Resource Management

Process files stored on wcs cloud storage, including deletion, listing resources, etc.

##### Delete File

**Example:**

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
         * You can get uploadDomain, MgrDomain in user management interface - security management - domain query, need to add http://
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

##### Get File Information

Get information about a file, including file name, file size, file ETag information, etc.

**Example:**

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
         * You can get uploadDomain, MgrDomain in user management interface - security management - domain query, need to add http://
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

##### List Resources

List resources in the specified space

**Example:**

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
         * You can get uploadDomain, MgrDomain in user management interface - security management - domain query, need to add http://
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

##### Copy Resources

Copy the specified resource as a newly named resource. If there is a resource with the same name in the target space, it will not be overwritten.

**Example:**

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
         * You can get uploadDomain, MgrDomain in user management interface - security management - domain query, need to add http://
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

##### Move Resources

Move the specified resource from the source space to the target space, or rename the resource within the same space. If there is a resource with the same name in the target space, it will not be overwritten.

**Example:**

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
         * You can get uploadDomain, MgrDomain in user management interface - security management - domain query, need to add http://
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

##### Update Mirror Resources

For spaces with mirror storage set, it provides the function of fetching specified resources from the mirror source site and storing them in the space. If there is a resource with the same name in the space, it will be overwritten by the resource from the mirror source site.

**Example:**

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
         * You can get uploadDomain, MgrDomain in user management interface - security management - domain query, need to add http://
         */
        Config.MGR_URL = "your MgrDomain";
        String bucketName = "your-bucket";
        new PreFetchDemo().prefetch(bucketName);
    }

    public void prefetch(String bucketName) {
        OperationManager fileManageCommand = new OperationManager();
        String fileName1 = "testPreFetch1.png"; // File name
        String fileName2 = "testPreFetch2.png"; // File name
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

##### Delete Resources by Prefix

You can delete all resources with a common prefix by specifying a prefix, such as specifying prefix=html/, delete all files in the html directory under the root path. 【Note: This interface is an asynchronous deletion method】

```
List<FmgrParam> list = new ArrayList<FmgrParam>();
FmgrParam fmgrParam = new FmgrParam();
fmgrParam.setBucket(bucketName);
fmgrParam.setPrefix("aac");
String notifyURL = "http://demo1/notifyUrl";  //Notification address, not required
HttpClientResult result = fileManageCommand.fmgrDeletePrefix(list);  // Delete result not notified
HttpClientResult result = fileManageCommand.fmgrDeletePrefix(list, notifyURL); //Delete interface notify to specified address
```

#### Audio and Video Operations

Provide audio and video processing functions, including: transcoding and repackaging, audio and video splicing, decompression and other operations. For specific processing parameters, please refer to [Audio and Video Processing Ops Parameter Format](#document/API/Appendix/fopsParam#音视频处理)

**Example:**

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
         * You can get uploadDomain, MgrDomain in user management interface - security management - domain query, need to add http://
         */
        Config.MGR_URL = "your MgrDomain";
        String bucketName = "your-bucket";
        String fileKey = "java-sdk/10m2.mp4";
        //Set transcoding operation parameters
        String fops = "avthumb/mp4/s/640x360/vb/1.25m";
        //You can use saveas parameter to customize the name of the transcoded file,
        //or you can not specify it, it will be named by default and saved in the current space  base64 for targetBucket_Name:custom file key.
        String saveAsKey = EncodeUtils.urlsafeEncode(bucketName + ":1.256m.jpg");
        fops += "|saveas/" + saveAsKey;
        String notifyURL = "http://demo1/notifyUrl";  //Notification address, will callback this address after successful transcoding
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

#### Fetch Resources

Provide fetching resources from a specified URL and storing them in a specified space.

**Example:**

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
         * You can get uploadDomain, MgrDomain in user management interface - security management - domain query, need to add http://
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
            String notifyURL = "http://demo1/notifyUrl";  //Notification address, will callback this address after successful transcoding
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

#### Download Resources

Provide downloading resources from specified domain name and resource name.

```
/**
 * Download file
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

#### Calculate ETag

Calculate the etag value of the local file, which can be used to compare with the etag value of the file stored on cloud storage to verify whether the file is complete

```
File fileHash = new File(filePath);
String eTagHash = WetagUtil.getEtagHash(fileHash);// Calculate hash based on file content
```