/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.plugin.lyStorage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;

import net.shopxx.entity.PluginConfig;
import net.shopxx.plugin.StoragePlugin;

/**
 * Plugin - 阿里云存储
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Component("lyStoragePlugin")
public class LyStoragePlugin extends StoragePlugin {

	@Override
	public String getName() {
		return "绿叶存储";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public String getAuthor() {
		return "SHOP++";
	}

	@Override
	public String getSiteUrl() {
		return "http://www.shopxx.net";
	}

	@Override
	public String getInstallUrl() {
		return "ly_storage/install";
	}

	@Override
	public String getUninstallUrl() {
		return "ly_storage/uninstall";
	}

	@Override
	public String getSettingUrl() {
		return "ly_storage/setting";
	}

	@Override
	public void upload(String path, File file, String contentType) {
		PluginConfig pluginConfig = getPluginConfig();
		if (pluginConfig != null) {
			String endpoint = pluginConfig.getAttribute("endpoint");
			String accessId = pluginConfig.getAttribute("accessId");
			String accessKey = pluginConfig.getAttribute("accessKey");
			String bucketName = pluginConfig.getAttribute("bucketName");
			InputStream inputStream = null;
			try {
				inputStream = new BufferedInputStream(new FileInputStream(file));
				OSSClient ossClient = new OSSClient(endpoint, accessId, accessKey);
				ObjectMetadata objectMetadata = new ObjectMetadata();
				objectMetadata.setContentType(contentType);
				objectMetadata.setContentLength(file.length());
				ossClient.putObject(bucketName, StringUtils.removeStart(path, "/"), inputStream, objectMetadata);
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e.getMessage(), e);
			} finally {
				IOUtils.closeQuietly(inputStream);
			}
		}
	}
	
	@Override
	public  String upload(File tempFile, String contentType) {
	    String respStr = null;  
        CloseableHttpClient httpclient = HttpClients.createDefault();  
        try {  
            HttpPost httppost = new HttpPost("https://file.szgreenleaf.com/servlet/FileUploadServlet2.do");  
            FileBody binFileBody = new FileBody(tempFile);  
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder  .create();  
            multipartEntityBuilder.addPart("myfile", binFileBody);  
            HttpEntity reqEntity = multipartEntityBuilder.build();  
            httppost.setEntity(reqEntity);  
  
            CloseableHttpResponse response = httpclient.execute(httppost);  
            try {  
                HttpEntity resEntity = response.getEntity();  
                respStr = getRespString(resEntity);  
                Result res = JSON.parseObject(respStr,Result.class);
                respStr =  res.getReturnpath();
            } finally {  
                response.close();  
            }  
        }  catch (Exception e) {
            e.printStackTrace();
        } finally {  
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }  
        }  
        return respStr;  
    }
	
	
    
    /** 
     * 将返回结果转化为String 
     *  
     * @param entity 
     * @return 
     * @throws Exception 
     */  
    private String getRespString(HttpEntity entity) throws Exception {  
        if (entity == null) {  
            return null;  
        }  
        InputStream is = entity.getContent();  
        StringBuffer strBuf = new StringBuffer();  
        byte[] buffer = new byte[4096];  
        int r = 0;  
        while ((r = is.read(buffer)) > 0) {  
            strBuf.append(new String(buffer, 0, r, "UTF-8"));  
        }  
        return strBuf.toString();  
    }  
  

	@Override
	public String getUrl(String path) {
		PluginConfig pluginConfig = getPluginConfig();
		if (pluginConfig != null) {
			String urlPrefix = pluginConfig.getAttribute("urlPrefix");
			return urlPrefix +"/"+ path;
		}
		return null;
	}
	
	public static class Result {
	    String returnpath;
	    String isSuccess;
	    String msgKey;
        public String getReturnpath() {
            return returnpath;
        }
        public void setReturnpath(String returnpath) {
            this.returnpath = returnpath;
        }
        public String getIsSuccess() {
            return isSuccess;
        }
        public void setIsSuccess(String isSuccess) {
            this.isSuccess = isSuccess;
        }
        public String getMsgKey() {
            return msgKey;
        }
        public void setMsgKey(String msgKey) {
            this.msgKey = msgKey;
        }
	    
	}

}