package com.cmmobi.looklook.v31.httpproxybak;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URL;

import cn.zipper.framwork.io.network.ZNetworkStateDetector;
import cn.zipper.framwork.utils.MD5Util;

import com.cmmobi.looklook.v31.httpproxybak.Config.ProxyRequest;
import com.cmmobi.looklook.v31.httpproxybak.Config.ProxyResponse;
import com.cmmobi.looklook.v31.httpproxybak.Config.ProxyResponseWrapper;
import com.cmmobi.looklook.v31.httpproxybak.ProxyTempFile.tmpBlock;

import android.util.Log;

/**
 * Author:Ray
 */

public class HttpProxy3 {

	private static final String TAG = "HttpProxy";
	private static HttpProxy3 httpproxy;
	
	/**避免某些MediaPlayer不播放尾部就结束*/
	private static final int SIZE = 1024*1024;
	/**缓存所需的大小*/
	private int mBufferSize;
	/**缓存缓存文件的最大数量*/
	private int mBufferFileMaximum;
	/** 链接远程服务器端口 */
	private int remotePort=-1;
	/** 远程服务器地址 */
	private String remoteHost;
	/** 代理服务器使用的端口 */
	private int localPort;
	/** 本地服务器地址 */
	private String localHost;
	/**TCP Server，接收Media Player连接*/
	private ServerSocket localServer = null;
	/**服务器的Address*/
	private SocketAddress serverAddress;
	/**Response对象*/
	private ProxyResponse proxyResponse=null;
	/**缓存文件夹*/
	private String mBufferDirPath=null;
	/**视频id，缓存文件以ID命名*/
	private String mId,mUrl;
	/**有效的媒体文件链接(重定向之后)*/
	private String mMediaUrl;
	/**缓存文件路径*/
	private String mMediaFilePath;
	/**缓存是否可用*/
	private boolean mEnable = false;
	
	private Proxy proxy=null;
	
	private ProxyTempFile tmpfile = null;
	//获取单例
	public static HttpProxy3 getInstance() {
		if(httpproxy == null) {
			Log.d(TAG, "Http Proxy Building");
			httpproxy = new HttpProxy3();
		}
		return httpproxy;
	}
	
	private HttpProxy3() {
		try {
			//初始化代理服务器
			mBufferDirPath = Config.CACHE_FILE_PATH; 
			localHost = Config.LOCAL_IP_ADDRESS;
//			mBufferSize=size;
//			mBufferFileMaximum = maximum;
			localServer = new ServerSocket(0, 1,InetAddress.getByName(localHost));
			localPort =localServer.getLocalPort();//有ServerSocket自动分配端口
			//启动代理服务器
			new Thread() {
				public void run() {
					startProxy();
				}
			}.start();
			
			mEnable = true;
		} catch (Exception e) {
			mEnable = false;
		}
	}
	
	// ============== PUBLIC function ==============
	/**
	 * 获得本地地址,生成本地缓存文件
	 *  1. 未下载完 http://127.0.0.1/xxx.mp4
	 *  2. 已下载完 /mnt/sdcard/xxx.mp4
	 */
	public String prepare(String origUrl) {
		mUrl = origUrl;
		//已经缓存完成
		if(false) {
			return "";
		}
		//代理服务器不可用
		if(!getEnable()) {
			Log.e(TAG, "Proxy Server Down!");
			return mUrl;
		}
		
		try {
			String ret = startCache();
			if(ret != null) {
				if(!ret.equals("OK")) {
					return ret;
				}
			} else {
				Log.e(TAG, "Something Error Occured When Cache Initialized.");
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (ZNetworkStateDetector.isAvailable()) {
			// 排除HTTP特殊,如重定向
			Log.e(TAG, "排除HTTP特殊,如重定向");
			mMediaUrl = Utils.getRedirectUrl(mUrl);
		} else {
			Log.e(TAG, "直接使用原始url");
			mMediaUrl = origUrl;
		}
		Log.d("Http mMediaUrl",mMediaUrl);
		// ----获取对应本地代理服务器的链接----//
		String localUrl="";
		URI originalURI = URI.create(mMediaUrl);
		remoteHost = originalURI.getHost();
		if (originalURI.getPort() != -1) {// URL带Port
			serverAddress = new InetSocketAddress(remoteHost, originalURI.getPort());// 使用默认端口
			remotePort = originalURI.getPort();// 保存端口，中转时替换
			localUrl = mMediaUrl.replace(remoteHost + ":" + originalURI.getPort(), localHost + ":" + localPort);
		} else {// URL不带Port
			serverAddress = new InetSocketAddress(remoteHost, Config.HTTP_PORT);// 使用80端口
			remotePort = -1;
			localUrl = mMediaUrl.replace(remoteHost, localHost + ":" + localPort);
		}
		
		return localUrl;
	}
	
	public long getTotalSize() {
		return mBufferSize;
	}
	// PRIVATE function
	
	public int getTargetSize(String remoteurl) throws Exception {
		URL url = new URL(remoteurl);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		return urlConnection.getContentLength();
	}
	
	/**
	 * 开始下载线程，缓存至文件
	 * @throws Exception
	 */
	private String startCache() throws Exception {
		//代理服务器不可用
		if(!getEnable() || mUrl == null || mUrl.equals("")) {
			Log.e(TAG, "代理服务器不可用或mUrl空:[" + mUrl + "]");
			return null;
		}

		String filekey = MD5Util.getMD5String(mUrl);
		Log.d(TAG, "filekey = " + filekey);
		String fileName = Utils.getValidFileName(filekey);
		
		mMediaFilePath = mBufferDirPath + "/" + fileName + ".tmp";
		String comfile = mBufferDirPath + "/" + fileName + ".mp4";
		//判断文件是否存在，忽略已经缓冲过的文件
		File comFile = new File(comfile);
		
		if (comFile.exists()) {
			Log.i(TAG, "Use Local File[" + comfile + "]");
			return comfile;
		}
		
		if (ZNetworkStateDetector.isAvailable()) {
			mBufferSize = getTargetSize(mUrl);
			Log.i(TAG, "mBufferSize:" + mBufferSize);
			if(mBufferSize <= 0) {
				return null;
			}
			//TODO:
//			createTmpFile(mMediaFilePath, mBufferSize);
			tmpfile = new ProxyTempFile(mMediaFilePath, mBufferSize);
			
			return "OK";
		} else {
			return null;
		}
	}
	
	/**
	 * 代理服务器是否可用
	 * @return
	 */
	private boolean getEnable() {
		// 判断外部存储器是否可用
		File dir = new File(mBufferDirPath);
		mEnable = dir.exists();
		if (!mEnable)
			return mEnable;
		// 获取可用空间大小
		long freeSize = Utils.getAvailaleSize(mBufferDirPath);
		mEnable = (freeSize > mBufferSize);

		return mEnable;
	}
	
	private void startProxy2() {
		while (true) {
			// --------------------------------------
			// 监听MediaPlayer的请求，MediaPlayer->代理服务器
			// --------------------------------------
			Log.i(TAG, "......ready to start...........");
			try {
				Socket s = localServer.accept();
				if(proxy!=null){
					Log.i(TAG, "......closeSockets A...........");
					proxy.closeSockets();
				}
				Log.i(TAG, "......started...........");
				proxy = new Proxy(s);
				
				new Thread(){
					public void run(){
						Log.i(TAG, "......ready to start...........");
						try {
							Socket s = localServer.accept();
							Log.i(TAG, "......closeSockets B...........");
							proxy.closeSockets();
							Log.i(TAG, "......started...........");
							proxy = new Proxy(s);
							proxy.run();
						} catch (IOException e) {
							Log.e(TAG, e.toString());
							Log.e(TAG, Utils.getExceptionMessage(e));
						}
						
					}
				}.start();
				proxy.run();
			} catch (IOException e) {
				Log.e(TAG, e.toString());
				Log.e(TAG, Utils.getExceptionMessage(e));
			}
		}
	}
	
	private void startProxy() {
		while (true) {
			// --------------------------------------
			// 监听MediaPlayer的请求，MediaPlayer->代理服务器
			// --------------------------------------
			Log.i(TAG, "......ready to start...........");
			try {
				Socket s = localServer.accept();
				if(proxy!=null){
					Log.i(TAG, "......closeSockets A...........");
					proxy.closeSockets();
				}
				Log.i(TAG, "......started...........");
				proxy = new Proxy(s);
				proxy.run();
			} catch (IOException e) {
				Log.e(TAG, e.toString());
				Log.e(TAG, Utils.getExceptionMessage(e));
			}
		}
	}
	
	/** 真实代理类 **/
	private class Proxy {
		/** 收发Media Player请求的Socket */
		private Socket sckPlayer = null;
		/** 收发Media Server请求的Socket */
		private Socket sckServer = null;
		
		public Proxy(Socket sckPlayer){
			this.sckPlayer=sckPlayer;
		}
		
		/**
		 * 关闭现有的链接
		 */
		public void closeSockets(){
			try {// 开始新的request之前关闭过去的Socket
				if (sckPlayer != null){
					sckPlayer.close();
					sckPlayer=null;
				}
				
				if (sckServer != null){
					sckServer.close();
					sckServer=null;
				}
			} catch (IOException e1) {}
		}
		
		public void run() {
			HttpParser httpParser = null;
			HttpGetProxyUtils utils = null;
			int bytes_read;

			byte[] local_request = new byte[1024];
			byte[] remote_reply = new byte[1024];

			boolean sentResponseHeader = false;

			try {
				Log.i(TAG, "<----------------------------------->");
//				stopDownload();

				httpParser = new HttpParser(remoteHost, remotePort, localHost,
						localPort);

				ProxyRequest request = null;
				while ((bytes_read = sckPlayer.getInputStream().read(
						local_request)) != -1) {
					byte[] buffer = httpParser.getRequestBody(local_request,
							bytes_read);
					if (buffer != null) {
						request = httpParser.getProxyRequest(buffer);
						break;
					}
				}

				utils = new HttpGetProxyUtils(sckPlayer, serverAddress);
				boolean isExists = new File(mMediaFilePath).exists();
				if (request != null) {// MediaPlayer的request有效
					
					if(tmpfile == null) {
						tmpfile = new ProxyTempFile(mMediaFilePath, mBufferSize);
					}
					tmpBlock tb = tmpfile.isInsideRange((int) request._rangePosition);
					if(tb != null) {// 需要发送缓存到MediaPlayer
						Log.i(TAG, "----------------->A 需要发送缓存到MediaPlayer");
						Log.i(TAG, "request : " + request._rangePosition + "; cache : [" + tb.range + " - " + (tb.range + tb.size) + "]");
						if (!sentResponseHeader) {
							proxyResponse = utils.fadeResponseHeader(mBufferSize, request._rangePosition, httpParser);
							utils.sendToMP(proxyResponse._body);
							sentResponseHeader = true;
						}
						isExists = false;
						int sentBufferSize = 0;
						sentBufferSize = utils.sendPrebufferToMP3(mMediaFilePath, request._rangePosition, (tb.range + tb.size - request._rangePosition));
						if (sentBufferSize > 0) {// 成功发送缓存，重新发送请求到服务器
							// 修改Range后的Request发送给服务器
							int newRange = (int) (sentBufferSize + request._rangePosition);
							String newRequestStr = httpParser.modifyRequestRange(request._body, newRange);
							request._rangePosition = newRange;
							Log.i(TAG, newRequestStr);
							try {
								if (sckServer != null)
									sckServer.close();
							} catch (IOException ex) {
							}
							sckServer = utils.sentToServer(newRequestStr);
							// 把服务器的Response的Header去掉
							ProxyResponseWrapper wrapper = null;
							Log.d(TAG, "......SEND HEADER OTHER DATA A(" + request._rangePosition + ")...........");
							wrapper = utils.removeResponseHeader(sckServer, httpParser, tmpfile, request._rangePosition);
							if (wrapper != null) {
								proxyResponse = wrapper.pr;
								request._rangePosition = wrapper._offset;
							}
						} else {
							Log.i(TAG, "......play over...........");
							Log.i(TAG, "......closeSockets E...........");
							closeSockets();
							return;
						}
					} else {
						sckServer = utils.sentToServer(request._body);// 发送MediaPlayer的request
					}
					
				} else {// MediaPlayer的request无效
					Log.i(TAG, "......closeSockets C...........");
					closeSockets();
					return;
				}
				long offset = request._rangePosition;
				// ------------------------------------------------------
				// 把网络服务器的反馈发到MediaPlayer，网络服务器->代理服务器->MediaPlayer
				// ------------------------------------------------------
				while (sckServer != null && ((bytes_read = sckServer.getInputStream().read(remote_reply)) != -1)) {
					if (sentResponseHeader) {
						try {// 拖动进度条时，容易在此异常，断开重连
//							Log.d(TAG, "......SEND CONTENT DATA (" + offset + ")...........");
							offset += utils.sendToMP(remote_reply, bytes_read, tmpfile, offset, false);
						} catch (Exception e) {
							Log.e(TAG, e.toString());
							Log.e(TAG, Utils.getExceptionMessage(e));
							break;// 发送异常直接退出while
						}

						if (proxyResponse == null)
							continue;// 没Response Header则退出本次循环

						// 已完成读取
						if (proxyResponse._currentPosition > proxyResponse._duration - SIZE) {
							Log.i(TAG, "....ready....over....");
							proxyResponse._currentPosition = -1;
						} else if (proxyResponse._currentPosition != -1) {// 没完成读取
							proxyResponse._currentPosition += bytes_read;
						}

						continue;// 退出本次while
					}
					proxyResponse = httpParser.getProxyResponse(remote_reply, bytes_read);
					if (proxyResponse == null)
						continue;// 没Response Header则退出本次循环

					sentResponseHeader = true;
					// send http header to mediaplayer
					Log.d(TAG, "......SEND HEADER (" + offset + ")...........");
					offset += utils.sendToMP(proxyResponse._body, tmpfile, offset, true);

					tmpBlock tb2 = tmpfile.isInsideRange((int) request._rangePosition);
					
					if (isExists && tb2 != null) {// 需要发送缓存到MediaPlayer
						Log.i(TAG, "----------------->B 需要发送缓存到MediaPlayer");
						isExists = false;
						int sentBufferSize = 0;
						sentBufferSize = utils.sendPrebufferToMP(mMediaFilePath, request._rangePosition);
						if (sentBufferSize > 0) {// 成功发送缓存，重新发送请求到服务器
							// 修改Range后的Request发送给服务器
							int newRange = (int) (sentBufferSize + request._rangePosition);
							String newRequestStr = httpParser.modifyRequestRange(request._body, newRange);
							Log.i(TAG, newRequestStr);
							try {
								if (sckServer != null)
									sckServer.close();
							} catch (IOException ex) {
							}
							sckServer = utils.sentToServer(newRequestStr);
							// 把服务器的Response的Header去掉
							ProxyResponseWrapper wrapper = null;
							Log.d(TAG, "......SEND HEADER OTHER DATA B(" + offset + ")...........");
							wrapper = utils.removeResponseHeader(sckServer, httpParser, tmpfile, offset);
							if (wrapper != null) {
								proxyResponse = wrapper.pr;
								offset += wrapper._offset;
							}
//							proxyResponse = utils.removeResponseHeader(
//									sckServer, httpParser);
							continue;
						}
					}

					// 发送剩余数据
					if (proxyResponse._other != null) {
						Log.d(TAG, "......SEND HEADER OTHER DATA (" + offset + ")...........");
						offset += utils.sendToMP(proxyResponse._other, tmpfile, offset, false);
					}
				}
				Log.i(TAG, "......closeSockets D...........");
				// 关闭 2个SOCKET
				closeSockets();
			} catch (Exception e) {
				Log.e(TAG, e.toString());
				Log.e(TAG, Utils.getExceptionMessage(e));
			}
		}
	}
	
}
