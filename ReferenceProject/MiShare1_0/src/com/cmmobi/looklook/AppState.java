package com.cmmobi.looklook;

import com.cmmobi.looklook.common.gson.GsonResponse3;


public final class AppState {
	
	private static GsonResponse3.uaResponse uaResponse; //UA响应;
	private static GsonResponse3.registerResponse registerResponse; //注册响应;
	private static GsonResponse3.loginResponse loginResponse; //登录响应;
	
	
	
	
	public static GsonResponse3.registerResponse getRegisterResponse() {
		return registerResponse;
	}

	public static void setRegisterResponse(Object registerResponse) {
		AppState.registerResponse = (GsonResponse3.registerResponse) registerResponse;
	}
	
	public static GsonResponse3.loginResponse getLoginResponse() {
		return loginResponse;
	}

	public static void setLoginResponse(Object loginResponse) {
		AppState.loginResponse = (GsonResponse3.loginResponse) loginResponse;
	}

	public static GsonResponse3.uaResponse getUaResponse() {
		return uaResponse;
	}

	public static void setUaResponse(Object uaResponse) {
		AppState.uaResponse = (GsonResponse3.uaResponse) uaResponse;
	}

	
	/**
	 * 禁止构造实例;
	 */
	private AppState() {
	}
}
