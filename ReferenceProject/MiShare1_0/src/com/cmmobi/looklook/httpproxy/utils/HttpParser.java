package com.cmmobi.looklook.httpproxy.utils;

import java.util.ArrayList;
import java.util.List;

import com.cmmobi.looklook.httpproxy.utils.ProxyConfig.ProxyRequest;
import com.cmmobi.looklook.httpproxy.utils.ProxyConfig.ProxyResponse;

import android.util.Log;

/**
 * Http报文处理类
 * @author Ray
 *
 */
public class HttpParser {
	final static public String TAG = "HttpParser";
	final static private String RANGE_PARAMS="Range: bytes=";
	final static private String RANGE_PARAMS_0="Range: bytes=0-";
	final static private String CONTENT_RANGE_PARAMS="Content-Range: bytes ";
	
	private static final  int HEADER_BUFFER_LENGTH_MAX = 1024 * 10;
	private byte[] headerBuffer = new byte[HEADER_BUFFER_LENGTH_MAX];
	private int headerBufferLength=0;
	
	/** 链接带的端口 */
	private int remotePort=-1;
	/** 远程服务器地址 */
	private String remoteHost;
	/** 代理服务器使用的端口 */
	private int localPort;
	/** 本地服务器地址 */
	private String localHost;
	
	public HttpParser(String rHost,int rPort,String lHost,int lPort){
		remoteHost=rHost;
		remotePort =rPort;
		localHost=lHost;
		localPort=lPort;
	}
	
	public void clearHttpBody(){
		headerBuffer = new byte[HEADER_BUFFER_LENGTH_MAX];
		headerBufferLength=0;
	}
	
	/**
	 * 获取Request报文
	 * @param source
	 * @param length
	 * @return
	 */
	public byte[] getRequestBody(byte[] source,int length){
		List<byte[]> httpRequest=getHttpBody(ProxyConfig.HTTP_REQUEST_BEGIN, 
				ProxyConfig.HTTP_BODY_END, 
				source,
				length);
		if(httpRequest.size()>0){
			return httpRequest.get(0);
		}
		return null;
	}
	
	/**
	 * Request报文解析转换ProxyRequest
	 * @param bodyBytes
	 * @return
	 */
	public ProxyRequest getProxyRequest(byte[] bodyBytes){
		ProxyRequest result=new ProxyRequest();
		//获取Body
		result._body=new String(bodyBytes);
		
		// 把request中的本地ip改为远程ip
		result._body = result._body.replace(localHost, remoteHost);
		// 把代理服务器端口改为原URL端口
		if (remotePort ==-1)
			result._body = result._body.replace(":" + localPort, "");
		else
			result._body = result._body.replace(":" + localPort, ":"+ remotePort);
		//不带Range则添加补上，方便后面处理
		if(result._body.contains(RANGE_PARAMS)==false)
			result._body = result._body.replace(ProxyConfig.HTTP_BODY_END,
					"\r\n"+RANGE_PARAMS_0+ProxyConfig.HTTP_BODY_END);
		Log.i(TAG, result._body);

		//获取Range的位置
		String rangePosition=ProxyUtils.getSubString(result._body,RANGE_PARAMS,"-");
		Log.i(TAG,"------->rangePosition:"+rangePosition);
		result._rangePosition = Long.valueOf(rangePosition);
		
		return result;
	}
	
	/**
	 * 获取ProxyResponse
	 * @param source
	 * @param length
	 */
	public ProxyResponse getProxyResponse(byte[] source,int length){
		List<byte[]> httpResponse=getHttpBody(ProxyConfig.HTTP_RESPONSE_BEGIN, 
				ProxyConfig.HTTP_BODY_END, 
				source,
				length);
		
		if (httpResponse.size() == 0)
			return null;
		
		ProxyResponse result=new ProxyResponse();
		
		//获取Response正文
		result._body=httpResponse.get(0);
		String text = new String(result._body);
		
		Log.i(TAG + "<---", text);
		//获取二进制数据
		if(httpResponse.size()==2) {
			result._other = httpResponse.get(1);
			Log.i(TAG,"result._other size:"+result._other.length);
		}
		//样例：Content-Range: bytes 2267097-257405191/257405192
		try {
			// 获取起始位置
			String currentPosition = ProxyUtils.getSubString(text,CONTENT_RANGE_PARAMS, "-");
			result._currentPosition = Integer.valueOf(currentPosition);

			// 获取最终位置
			String startStr = CONTENT_RANGE_PARAMS + currentPosition + "-";
			String duration = ProxyUtils.getSubString(text, startStr, "/");
			result._duration = Integer.valueOf(duration);
		} catch (Exception ex) {
			Log.e(TAG, ProxyUtils.getExceptionMessage(ex));
		}
		return result;
	}
	
	/**
	 * 替换Request报文中的Range位置,"Range: bytes=0-" -> "Range: bytes=XXX-"
	 * @param requestStr
	 * @param position
	 * @return
	 */
	public String modifyRequestRange(String requestStr,int position){
		String str=ProxyUtils.getSubString(requestStr, RANGE_PARAMS, "-");
		str=str+"-";
		String result = requestStr.replaceAll(str, position+"-");
		return result;
	}
	
	private List<byte[]> getHttpBody(String beginStr,String endStr,byte[] source,int length){
		if((headerBufferLength+length)>=headerBuffer.length){
			clearHttpBody();
		}
		
		System.arraycopy(source, 0, headerBuffer, headerBufferLength, length);
		headerBufferLength+=length;
		
		List<byte[]> result = new ArrayList<byte[]>();
		String responseStr = new String(headerBuffer);
		if (responseStr.contains(beginStr)
				&& responseStr.contains(endStr)) {
			
			int startIndex=responseStr.indexOf(beginStr, 0);
			int endIndex = responseStr.indexOf(endStr, startIndex);
			endIndex+=endStr.length();
			
			byte[] header=new byte[endIndex-startIndex];
			System.arraycopy(headerBuffer, startIndex, header, 0, header.length);
			result.add(header);
			
			if (headerBufferLength > header.length) {//还有数据
				byte[] other = new byte[headerBufferLength - header.length];
				System.arraycopy(headerBuffer, header.length, other, 0,other.length);
				result.add(other);
			}
			clearHttpBody();
		}
		
		return result;
	}
	
}
