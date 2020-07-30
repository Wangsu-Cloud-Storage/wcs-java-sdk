package com.chinanetcenter.api.sliceUpload;

import com.chinanetcenter.api.entity.PutPolicy;
import com.chinanetcenter.api.util.TokenUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.*;

/**
 * Created by fuyz on 2014/8/18.
 */
public class PutExtra {
    public Map<String, String> params;
    public Map<String, String> xParams;
    public BlockObject[] processes;
    public String mimeType;
    public long totalSize;
    public String uploadBatch;

    public ArrayList<BlockObject> streamProcesses;

    public PutExtra() {
    }

    public PutExtra(String bucketName, String fileKey, String filePath, PutPolicy putPolicy, long fileSize) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("bucketName", bucketName);
        params.put("fileKey", fileKey);
        params.put("filePath", filePath);
        params.put("putPolicy", putPolicy.toString());
        String token = TokenUtil.getUploadToken(putPolicy);
        params.put("token", token);
        String uploadBatch = "JAVA-SDK-" + UUID.randomUUID();
        this.uploadBatch = uploadBatch;
        this.totalSize = fileSize;
        this.params = params;
    }

    public PutExtra(JsonNode obj) {
        mimeType = obj.path("mimeType").asText() == null ? "" : obj.path("mimeType").asText();
        uploadBatch = obj.path("uploadBatch").asText() == null ? "" : obj.path("uploadBatch").asText();
        totalSize = obj.path("totalSize") == null ? 0 : obj.path("totalSize").asLong();
        JsonNode procs = obj.path("processes");
        processes = new BlockObject[procs.size()];
        for (int i = 0; i < procs.size(); i++) {
            processes[i] = new BlockObject(procs.get(i));
        }
        params = new HashMap<String, String>();
        xParams = new HashMap<String, String>();
        JsonNode paramsJson = obj.path("params");
        JsonNode xParamsJson = obj.path("xParams");
        for (Iterator<?> iter = paramsJson.fieldNames(); iter.hasNext(); ) {
            String key = (String) iter.next();
            params.put(key, paramsJson.path(key).asText());
        }
        if (xParamsJson != null && xParamsJson.fieldNames() != null) {
            for (Iterator<?> iter = xParamsJson.fieldNames(); iter.hasNext(); ) {
                String key = (String) iter.next();
                xParams.put(key, xParamsJson.path(key).asText());
            }
        }
    }

    public boolean isFinishAll() {
        if (totalSize <= 0) return false;
        long currentSize = 0;
        for (BlockObject pr : processes) {
            currentSize += pr.getOffset();
        }
        return currentSize >= totalSize;
    }

    public JsonNode toJSON() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = JsonNodeFactory.instance.objectNode();
        ArrayNode process = JsonNodeFactory.instance.arrayNode();
        for (BlockObject p : processes) {
            process.add(p.toJSON());
        }
        json.put("processes", process);
        json.put("mimeType", mimeType);
        json.put("uploadBatch", uploadBatch);
        json.put("totalSize", totalSize);
        if (params != null) json.put("params", objectMapper.readTree(objectMapper.writeValueAsString(params)));
        if (xParams != null) json.put("xParams", objectMapper.readTree(objectMapper.writeValueAsString(xParams)));
        return json;
    }
}
