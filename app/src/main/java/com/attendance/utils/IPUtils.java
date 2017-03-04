package com.attendance.utils;

import android.content.Context;

//服务器地址管理类
public class IPUtils {
	
	//	<string name="service_URL_Login">http://192.168.0.152/staffLogin.do</string><!-- 登录 -->
	public static String getServiceURLLogin() {
		SharedFileUtil sharedFileUtil = new SharedFileUtil();
		String serverIp = sharedFileUtil.getString("serverIp");
		return String.format("http://%s/staffLogin.do", serverIp);
	}
	
	
	// <string name="service_URL_bottleInfo">http://192.168.0.152/webApi</string>
	public static String getServiceURLBottleInfo() {
		SharedFileUtil sharedFileUtil = new SharedFileUtil();
		String serverIp = sharedFileUtil.getString("serverIp");
		return String.format("http://%s/webApi", serverIp);
	}

	// <string name="service_URL_upload">http://192.168.0.152/upload.do</string>
	public static String getServiceURLUpload() {
		SharedFileUtil sharedFileUtil = new SharedFileUtil();
		String serverIp = sharedFileUtil.getString("serverIp");
		return String.format("http://%s/upload.do", serverIp);
	}
	public static String getServiceURLValidateCode() {
		SharedFileUtil sharedFileUtil = new SharedFileUtil();
		String serverIp = sharedFileUtil.getString("serverIp");
		return String.format("http://%s/webApi/getValidateCode", serverIp);
	}

	//<string name="service_URL_getImage">http://192.168.0.152/ImageUpload/</string>
	public static String getServiceURLGetImage() {
		SharedFileUtil sharedFileUtil = new SharedFileUtil();
		String serverIp = sharedFileUtil.getString("serverIp");
		return String.format("http://%s/ImageUpload", serverIp);
	}

	//<string name="service_URL_handover">http://192.168.0.152/webApi</string>
	public static String getServiceURLHandover() {
		SharedFileUtil sharedFileUtil = new SharedFileUtil();
		String serverIp = sharedFileUtil.getString("serverIp");
		return String.format("http://%s/webApi", serverIp);
	}

	//<string name="service_URL_supply">http://192.168.0.152/supplyManage.do</string>
	public static String getServiceURLSupply() {
		SharedFileUtil sharedFileUtil = new SharedFileUtil();
		String serverIp = sharedFileUtil.getString("serverIp");
		return String.format("http://%s/supplyManage.do", serverIp);
	}

	//<string name="Service_URL_transport">http://192.168.0.152/transportManage.do</string>
	public static String getServiceURLTransport() {
		SharedFileUtil sharedFileUtil = new SharedFileUtil();
		String serverIp = sharedFileUtil.getString("serverIp");
		return String.format("http://%s/transportManage.do", serverIp);
	}

	//<string name="service_URL_recovery">http://192.168.0.152/bottleRecycle.do</string>
	public static String getServiceURLRecovery() {
		SharedFileUtil sharedFileUtil = new SharedFileUtil();
		String serverIp = sharedFileUtil.getString("serverIp");
		return String.format("http://%s/bottleRecycle.do", serverIp);
	}

	//<string name="service_URL_getLatestversion">http://192.168.0.152/softwareUpgradeManager.do</string><!-- 版本验证 -->
	public static String getServiceURLGetLatestversion() {
		SharedFileUtil sharedFileUtil = new SharedFileUtil();
		String serverIp = sharedFileUtil.getString("serverIp");
		return String.format("http://%s/softwareUpgradeManager.do", serverIp);
	}

	// <string name="service_URL_download_newversion">http://192.168.0.152/ImageUpload/SoftwareUpdate</string><!-- 新版本下载 -->
	public static String getServiceURLDownloadNewversion() {
		SharedFileUtil sharedFileUtil = new SharedFileUtil();
		String serverIp = sharedFileUtil.getString("serverIp");
		return String.format("http://%s/ImageUpload/SoftwareUpdate", serverIp);
	}
	
	//192.168.0.99:8080/bottleErrorRecordList.do 
	public static String getBottleErrorRecordURL(){
		SharedFileUtil sharedFileUtil = new SharedFileUtil();
		String serverIp = sharedFileUtil.getString("serverIp");
		return String.format("http://%s/webApi/bottleErrorRecordList", serverIp);
	}
}
