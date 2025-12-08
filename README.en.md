# Cloud Storage Java SDK

## 语言 / Language
- [简体中文](README.md)
- [English](README.en.md)

## wcs-java-sdk

### Overview
wcs-java-sdk is the official Java SDK provided by Wangsu Cloud Storage, which encapsulates the RESTful API of Wangsu Cloud Storage. It provides simple and reliable programming interfaces for developers to access Wangsu Cloud Storage services quickly.

### Features
- File upload (simple upload, multipart upload)
- File download
- File management (delete, copy, move, get file information)
- Resource listing
- Audio and video processing
- Image processing
- Mirror resource update

### Environmental Requirements
- JDK 1.6 or above
- Maven 3.0 or above (for dependency management)

### Installation

#### Using Maven
Add the following dependency to your project's pom.xml file:

```xml
<dependency>
    <groupId>com.chinanetcenter.api</groupId>
    <artifactId>wcs-java-sdk</artifactId>
    <version>2.0.11</version>
</dependency>
```

#### Manual Installation
1. Download the wcs-java-sdk JAR file and its dependencies
2. Add the downloaded JAR files to your project's classpath

### Quick Start

#### Preparation
- Register a Wangsu Cloud Storage account and obtain the Access Key (AK) and Secret Key (SK)
- Create a storage space (Bucket)
- Obtain the upload domain, management domain, and download domain from the console

#### Configuration Information
```java
import com.chinanetcenter.api.util.Config;

public class Demo {
    public static void main(String[] args) {
        // Set AK and SK
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        
        // Set upload domain and management domain
        Config.PUT_URL = "your uploadDomain";
        Config.MGR_URL = "your MgrDomain";
    }
}
```

#### Set Encryption Algorithm
```java
// Use MD5 encryption (default)
Config.setAuth(AuthEnum.MD5);

// Use HMAC-SHA1 encryption
Config.setAuth(AuthEnum.SHA1);
```

### File Upload

#### Simple Upload
Simple upload is suitable for small files (recommended for files less than 100MB).

##### Normal Form Upload
**Example:**
```java
import com.chinanetcenter.api.entity.PutPolicy;
import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.util.DateUtil;
import com.chinanetcenter.api.wsbox.FileUploadManage;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UploadDemo {

    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * You can get uploadDomain and MgrDomain from the user management interface - Security Management - Domain Name Query, need to add http://
         */
        Config.PUT_URL = "your uploadDomain";
        String bucketName = "your-bucket";
        String fileKey = "java-sdk/testfile.jpg";
        String srcFilePath = "D:\\testfile\\test.jpg";
        UploadDemo demo = new UploadDemo();
        demo.upload(bucketName, fileKey, srcFilePath);
        demo.uploadWithToken(bucketName, fileKey, srcFilePath);
        demo.uploadReturnBody(bucketName, fileKey, srcFilePath);
        demo.uploadMimeType(bucketName, fileKey, srcFilePath);
        demo.uploadPersistent(bucketName, fileKey, srcFilePath);
    }

    /**
     * Normal upload
     */
    public void upload(String bucketName, String fileKey, String srcFilePath) {
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setOverwrite(1); // Overwrite upload
        putPolicy.setDeadline(String.valueOf(DateUtil.nextDate(1, new Date()).getTime()));
        putPolicy.setScope(bucketName + ":" + fileKey);
        try {
            FileUploadManage fileUploadManage = new FileUploadManage();
            HttpClientResult result = fileUploadManage.upload(bucketName, fileKey, srcFilePath, putPolicy);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * Upload with specified token
     */
    public void uploadWithToken(String bucketName, String fileKey, String srcFilePath) {
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setOverwrite(1);
        putPolicy.setDeadline(String.valueOf(DateUtil.nextDate(1, new Date()).getTime()));
        putPolicy.setScope(bucketName + ":" + fileKey);
        try {
            String uploadToken = TokenUtil.getUploadToken(putPolicy);
            FileUploadManage fileUploadManage = new FileUploadManage();
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("token", uploadToken);
            HttpClientResult result = fileUploadManage.upload(paramMap, srcFilePath);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * Upload with return body, the server will return the specified content according to returnBody
     */
    public void uploadReturnBody(String bucketName, String fileKey, String srcFilePath) {
        String returnBody = "key=$(key)&fname=$(fname)&fsize=$(fsize)&url=$(url)&hash=$(hash)&mimeType=$(mimeType)";
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setOverwrite(1); // Overwrite upload
        putPolicy.setDeadline(String.valueOf(DateUtil.nextDate(1, new Date()).getTime()));
        putPolicy.setReturnBody(returnBody);
        putPolicy.setScope(bucketName + ":" + fileKey);
        try {
            HttpClientResult result = fileUploadManage.upload(bucketName, fileKey, srcFilePath, putPolicy);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * Upload with specified file type and file retention time. The server will default to file suffix or file content.
     * If mimeType is specified, the Content-type will be set to this type when downloading.
     */
    public void uploadMimeType(String bucketName, String fileKey, String srcFilePath) {
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setOverwrite(1);
        putPolicy.setDeadline(String.valueOf(DateUtil.nextDate(1, new Date()).getTime()));
        putPolicy.setScope(bucketName + ":" + fileKey);
        try {
            String uploadToken = TokenUtil.getUploadToken(putPolicy);
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("token", uploadToken);
            paramMap.put("mimeType", "application/UQ");
            paramMap.put("deleteAfterDays", "365");
            HttpClientResult result = fileUploadManage.upload(paramMap, srcFilePath);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * Upload files and transcode them after upload.
     * Returns persistentId after successful upload, which can be used to query transcoding status.
     */
    public void uploadPersistent(String bucketName, String fileKey, String srcFilePath) {
        PutPolicy putPolicy = new PutPolicy();
        String returnBody = "key=$(key)&persistentId=$(persistentId)&fsize=$(fsize)";
        putPolicy.setOverwrite(1);
        putPolicy.setDeadline(String.valueOf(DateUtil.nextDate(1, new Date()).getTime()));
        putPolicy.setScope(bucketName + ":" + fileKey);
        putPolicy.setPersistentOps("imageMogr2/jpg/crop/500x500/gravity/CENTER/lowpoly/1|saveas/ZnV5enRlc3Q4Mi0wMDE6ZG9fY3J5c3RhbGxpemVfZ3Jhdml0eV9jZW50ZXJfMTQ2NTkwMDg0Mi5qcGc="); // Set video transcoding operations
        putPolicy.setPersistentNotifyUrl("http://demo1/notifyUrl"); // Set callback interface after transcoding
        putPolicy.setReturnBody(returnBody);
        try {
            HttpClientResult result = fileUploadManage.upload(bucketName, fileKey, srcFilePath, putPolicy);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * Upload files by local file path, will automatically identify file type
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
     * Upload files by input stream, the method will close InputStream, will automatically identify file type
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

##### Multipart Upload
**Example:**
```java
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
         * You can get uploadDomain and MgrDomain from the user management interface - Security Management - Domain Name Query, need to add http://
         */
        Config.PUT_URL = "your uploadDomain";
        String bucketName = "your-bucket";
        String fileKey = "java-sdk/com.toycloud.MeiYe.apk";

        String srcFilePath = "D:\\testfile\\test001\\com.toycloud.MeiYe.apk";

        /**
         * Set the size of each block to 4M. If network conditions are good, you can set 8M, 12M, 16M, etc. (multiples of 4M) to improve upload efficiency. The default value is 4M.
         */
        BaseBlockUtil.BLOCK_SIZE = 4 * 1024 * 1024;

        /**
         * Set the size of each slice to 4M to reduce upload requests. If network conditions are poor, it is not recommended to set this parameter or set it to a smaller value to avoid timeout. The default value is 256KB.
         */
        BaseBlockUtil.CHUNK_SIZE = 4 * 1024 * 1024;

        /**
         * Set the number of concurrent block uploads to speed up upload. If network conditions are poor, it is not recommended to set this parameter or set it to a smaller value to avoid timeout. The default value is 5.
         */
        BaseBlockUtil.THREAD_NUN = 5;

        /**
         * Set the number of retries for request timeout and server response exceptions. The default value is 3.
         */
        BaseBlockUtil.TRIED_TIMES = 5;
        SliceUploadDemo demo = new SliceUploadDemo();
        demo.sliceUpload(bucketName, fileKey, srcFilePath);
        demo.sliceUploadForAutoMimeType(bucketName, fileKey, srcFilePath);
        /**  Second way, key is not written to scope, but specified from head. Used for uploading multiple files with the same token.
        String fileKey2 = "java-sdk/com.toycloud.MeiYe2.apktest";
        String mimeType = "application/vnd.android.package-archive";
        demo.sliceUpload(bucketName, fileKey2, srcFilePath, mimeType);
         */
    }

    public void sliceUpload(final String bucketName, final String fileKey, final String filePath) {
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setScope(bucketName + ":" + fileKey);
        putPolicy.setOverwrite(1);
        putPolicy.setDeadline(String.valueOf(DateUtil.nextDate(1, new Date()).getTime()));
        JSONObjectRet jsonObjectRet = getJSONObjectRet(bucketName, fileKey, filePath);
        SliceUploadResumable sliceUploadResumable = new SliceUploadResumable();
        sliceUploadResumable.execUpload(bucketName, fileKey, filePath, putPolicy, null, jsonObjectRet);
    }

    public void sliceUpload(final String bucketName, final String fileKey, final String filePath, String mimeType) {
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setScope(bucketName);
        putPolicy.setOverwrite(1);
        putPolicy.setDeadline(String.valueOf(DateUtil.nextDate(1, new Date()).getTime()));
        JSONObjectRet jsonObjectRet = getJSONObjectRet(bucketName, fileKey, filePath);
        SliceUploadResumable sliceUploadResumable = new SliceUploadResumable();
        Map<String, String> headMap = new HashMap<String, String>();
        headMap.put("mimeType", mimeType);
        headMap.put("key", EncodeUtils.urlsafeEncode(fileKey));
        sliceUploadResumable.execUpload(bucketName, fileKey, filePath, putPolicy, null, jsonObjectRet, headMap);
    }

    /**
     * Multipart upload, will automatically identify file type
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

    public JSONObjectRet getJSONObjectRet(final String bucketName, final String fileKey, final String filePath) {
        return new JSONObjectRet() {
            /**
             * This method will be called back after successful file upload
             * Check if the hash of the uploaded file is consistent with the hash of the local file. If not, the local file may have been modified.
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
                } else {
                    ex.printStackTrace();
                }
                System.out.println("Upload error, " + ex.getMessage());
            }

            // Progress bar display, this method will be called back after each block is uploaded successfully
            @Override
            public void onProcess(long current, long total) {
                System.out.printf("%s\r", current * 100 / total + " %");
            }

            /**
             * Persistence, save progress information during resumable upload. Assign JSONObject to PutExtra when uploading again.
             * The SDK saves information to disk files by default. If needed, please save it to the database yourself.
             * Assign the value to the PutExtra parameter when resuming upload next time.
             */
            @Override
            public void onPersist(JsonNode obj) {
                BaseBlockUtil.savePutExtra(bucketName, fileKey, obj);
            }
        };
    }
}
```

### Resource Management
Process files stored on Wangsu Cloud Storage, including deletion, listing resources, etc.

##### Delete File

**Example:**
```java
import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.wsbox.OperationManager;

public class DeleteDemo {
    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * You can get uploadDomain and MgrDomain from the user management interface - Security Management - Domain Name Query, need to add http://
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
Get the information description of a file, including file name, file size, ETag information, etc.

**Example:**
```java
import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.wsbox.OperationManager;

public class StatDemo {
    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * You can get uploadDomain and MgrDomain from the user management interface - Security Management - Domain Name Query, need to add http://
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
List resources in the specified bucket.

**Example:**
```java
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
         * You can get uploadDomain and MgrDomain from the user management interface - Security Management - Domain Name Query, need to add http://
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

##### Copy Resource
Copy the specified resource to a newly named resource. If a resource with the same name exists in the target bucket, it will not be overwritten.

**Example:**
```java
import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.wsbox.OperationManager;

public class CopyDemo {
    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * You can get uploadDomain and MgrDomain from the user management interface - Security Management - Domain Name Query, need to add http://
         */
        Config.MGR_URL = "your MgrDomain";
        String bucketName = "your-bucket";
        String fileKey = "java-sdk/testfile.jpg";
        String newBucketName = "your-bucket";
        String newFileKey = "java-sdk/testfile2.jpg";
        OperationManager fileManageCommand = new OperationManager();
        try {
            HttpClientResult result = fileManageCommand.copy(bucketName, fileKey, newBucketName, newFileKey);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
}
```

##### Move Resource
Move the specified resource from the source bucket to the target bucket, or rename the resource within the same bucket. If a resource with the same name exists in the target bucket, it will not be overwritten.

**Example:**
```java
import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.wsbox.OperationManager;

public class MoveDemo {
    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * You can get uploadDomain and MgrDomain from the user management interface - Security Management - Domain Name Query, need to add http://
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

##### Update Mirror Resource
For buckets with mirror storage set up, it provides the function to fetch specified resources from the mirror source site and store them in the bucket. If a resource with the same name already exists in the bucket, it will be overwritten by the resource from the mirror source site.

**Example:**
```java
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
         * You can get uploadDomain and MgrDomain from the user management interface - Security Management - Domain Name Query, need to add http://
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
You can delete all resources with a common prefix by specifying a prefix, such as specifying prefix=html/ to delete all files in the html directory under the root path. 【Note: This interface is an asynchronous deletion method】

```java
List<FmgrParam> list = new ArrayList<FmgrParam>();
FmgrParam fmgrParam = new FmgrParam();
fmgrParam.setBucket(bucketName);
fmgrParam.setPrefix("aac");
String notifyURL = "http://demo1/notifyUrl";  // Notification address, optional
HttpClientResult result = fileManageCommand.fmgrDeletePrefix(list);  // Deletion result is not notified
HttpClientResult result = fileManageCommand.fmgrDeletePrefix(list, notifyURL); // Deletion interface notifies the specified address
```

### Audio and Video Operations
Provide audio and video processing functions, including: transcoding and repackaging, audio and video splicing, decompression, etc. For specific processing parameters, please refer to [Audio and Video Processing Ops Parameter Format](#document/API/Appendix/fopsParam#audio-video-processing)

**Example:**
```java
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
         * You can get uploadDomain and MgrDomain from the user management interface - Security Management - Domain Name Query, need to add http://
         */
        Config.MGR_URL = "your MgrDomain";
        String bucketName = "your-bucket";
        String fileKey = "java-sdk/10m2.mp4";
        // Set transcoding operation parameters
        String fops = "avthumb/mp4/s/640x360/vb/1.25m";
        // You can use the saveas parameter to customize the name of the transcoded file,
        // or you can not specify it, it will be named by default and saved in the current bucket. Base64 encode the targetBucket_Name:custom file key.
        String saveAsKey = EncodeUtils.urlsafeEncode(bucketName + ":1.256m.jpg");
        fops += "|saveas/" + saveAsKey;
        String notifyURL = "http://demo1/notifyUrl";  // Notification address, will callback this address after successful transcoding
        String force = "1";
        String separate = "1";
        FopsDemo demo = new FopsDemo();
        demo.fileTrans(bucketName, fileKey, fops, notifyURL, force, separate);

    }

    public void fileTrans(String bucketName, String fileKey, String fops, String notifyURL, String force, String separate) {
        OperationManager fileManageCommand = new OperationManager();
        try {
            HttpClientResult result = fileManageCommand.fops(bucketName, fileKey, fops, notifyURL, force, separate);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
}
```

### Fetch Resource
Provide the function to fetch resources from the specified URL and store them in the specified bucket.

**Example:**
```java
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
         * You can get uploadDomain and MgrDomain from the user management interface - Security Management - Domain Name Query, need to add http://
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
            String notifyURL = "http://demo1/notifyUrl";  // Notification address, will callback this address after successful transcoding
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

### Download Resource
Provide the function to download resources from the specified domain name and resource name.

```java
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

### Calculate ETag
Calculate the ETag value of the local file, which can be used to compare with the ETag value of the file stored in cloud storage to verify whether the file is complete.

```java
File fileHash = new File(filePath);
String eTagHash = WetagUtil.getEtagHash(fileHash);// Calculate hash based on file content
```
