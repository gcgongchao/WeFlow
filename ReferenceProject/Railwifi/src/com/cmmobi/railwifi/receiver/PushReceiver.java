package com.cmmobi.railwifi.receiver;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.cmmobi.push.RichItem;
import com.cmmobi.railwifi.dialog.DialogUtils;
import com.example.listener.recevier.CmmobiPushRecevier;

/**
 * 自定义接收器
 */
public class PushReceiver extends CmmobiPushRecevier{
	public static final String CONTENT_TYPE = "CONTENT_TYPE";
	public static final String OBJECT_ID = "OBJECT_ID";
	
	//alumb(专辑), movie(电影), music（音乐）， joke（逗你玩），shop（订餐购物）, news(铁路资讯), travel(铁旅联盟)
	public static final String ALUMB = "alumb";
	public static final String MOVIE = "movie";
	public static final String MUSIC = "music";
	public static final String JOKE = "joke";
	public static final String SHOP = "shop";
	public static final String NEWS = "news";
	public static final String TRAVEL = "travel";
	public static final String HELP = "help";
	
	
	private static final String TAG = "PushReceiver";

	/**
     * 接收透传消息的函数。
     * 
     * @param context  	上下文
     * @param appId    	产品id
     * @param userId   	用户id
     * @param msgId    	消息id
     * @param title     标题
     * @param content   消息内容
     * @param items	 	富媒体数据
     * @param timeStamp 时间戳
     */
	public void onMessage(Context context, String appId, String userId,
			String msgId, int notifyId, boolean prompt, String title, String content,
			List<RichItem> items, String timeStamp, Map<String, String> dict) {
		// TODO Auto-generated method stub
		if (prompt) {
			// notification通知
			String content_type = dict.get(CONTENT_TYPE);
			String object_id = dict.get(OBJECT_ID);
			
			Log.v(TAG, "onMessage(Notify) msgId:" + msgId + ", notifyId:" +notifyId+ ", title:" + title + ", content:" + content);
			
			if(content_type!=null && content_type.equals(HELP)){
				DialogUtils.SendCallHelpDialog(context, title, content, msgId, notifyId);
			}else{
				DialogUtils.SendJumpDialog(context, title, content, msgId, notifyId, content_type, object_id);
			}

		} else {
			// 透传消息
			Log.v(TAG, "onMessage(Msg) msgId:" + msgId + ", not prompt, title:" + title + ", content:" + content);
		}

	}

	
	/**
     * 接收通知点击的函数。注：推送通知被用户点击前，应用无法通过接口获取通知的内容。
     * 
     * @param context	上下文
     * @param appId		产品id
     * @param userId	用户id
     * @param msgId		消息id
     * @param title		推送的通知的标题
     * @param content	推送的通知的内容
     * @param items		富媒体数据
     * @param timeStamp	时间戳
     */
	public void onNotificationClicked(final Context context, String appId, String userId, final String msgId, final String title, final String content,
			List<RichItem> items, String timeStamp, Map<String, String> dict) {
		// TODO Auto-generated method stub
		final String content_type = dict.get(CONTENT_TYPE);
		final String object_id = dict.get(OBJECT_ID);
		Log.v(TAG, "onNotificationClicked - msgId:" +msgId + ", title:" + title + ", content:" + content);
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(content_type!=null && content_type.equals(HELP)){
					DialogUtils.SendCallHelpDialog(context, title, content, msgId, -1);
				}else{
					DialogUtils.SendJumpDialog(context, title, content, msgId, -1, content_type, object_id);
				}
			}
		}, 5);
		

	}


	@Override
	public void onMessage(Context arg0, String arg1, String arg2, String arg3,
			int arg4, boolean arg5, String arg6, String arg7,
			List<RichItem> arg8, String arg9) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onNotificationClicked(Context arg0, String arg1, String arg2,
			String arg3, String arg4, String arg5, List<RichItem> arg6,
			String arg7) {
		// TODO Auto-generated method stub
		
	}
	
}

