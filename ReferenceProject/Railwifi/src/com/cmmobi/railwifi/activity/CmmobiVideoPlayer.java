package com.cmmobi.railwifi.activity;

import nativeInterface.MobilePlayInterface;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.cmmobi.railwifi.Config;
import com.cmmobi.railwifi.dao.PlayHistory;
import com.cmmobi.railwifi.dialog.PromptDialog;
import com.cmmobi.railwifi.event.DialogEvent;
import com.cmmobi.railwifi.network.Requester;
import com.cmmobi.railwifi.sql.HistoryManager;
import com.cmmobi.railwifi.utils.CmmobiClickAgentWrapper;
import com.cmmobi.railwifi.utils.DateUtils;
import com.google.gson.Gson;
import com.simope.yzvideo.base.MobilePlayActivity;

import de.greenrobot.event.EventBus;


public class CmmobiVideoPlayer extends MobilePlayActivity implements Callback, MobilePlayInterface{

	private PlayHistory playhistory;
	private Gson gson = new Gson();
	private Handler handler;
	
	private String name = null;
	private String videoUrl = null;
	private long playedtime = 0;
	private long totaltime = 0;
	
	@Override
	public void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		EventBus.getDefault().register(this);
		handler = new Handler(this);
		Intent intent = getIntent();
		String movietype = intent.getStringExtra(VideoPlayerActivity.KEY_MOVIE_TYPE);
				
		String play_history_str = intent.getStringExtra(VideoPlayerActivity.KEY_PLAYHISTORY);
	    if(!TextUtils.isEmpty(play_history_str)){
	    	playhistory = gson.fromJson(play_history_str, PlayHistory.class);
	    	String percent = playhistory.getPercent();
	    	if (!TextUtils.isEmpty(percent)&& !"0".equals(percent)&&VideoPlayerActivity.isNumeric(percent)) {
				playedtime = Long.parseLong(percent);
	    	}
	    	String total = playhistory.getTotaltime();
	    	if (!TextUtils.isEmpty(total)&& !"0".equals(total)&&VideoPlayerActivity.isNumeric(total)) {
	    		totaltime = Long.parseLong(total);
	    	}
	    	name = playhistory.getName();
	    	
	    	if(!TextUtils.isEmpty(playhistory.getMedia_id())){
	    		Requester.requestMoviePlay(handler, playhistory.getMedia_id(), movietype);
	    		videoUrl = Config.SERVER_RIA_URL + Requester.RIA_INTERFACE_MOVIE_PLAY + "?requestapp={\"media_id\":" + playhistory.getMedia_id() + ",\"movie_type\":" + movietype + "}&" + playhistory.getMedia_id() + ".smo";
	    	}/*else{
	    		PromptDialog.Dialog(this, false, "播放地址错误", "对不起，播放地址错误", "稍候再试", null, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						finish();
					}
				}, null);
	    	}*/
	    }else{
	    	name = intent.getStringExtra(VideoPlayerActivity.KEY_NAME);
	    	String pathkey = intent.getStringExtra(VideoPlayerActivity.KEY_PATH);
	    	String mediaidkey = intent.getStringExtra(VideoPlayerActivity.KEY_MEDIA_ID);
	    	if(!TextUtils.isEmpty(mediaidkey)){
	    		Requester.requestMoviePlay(handler, mediaidkey, movietype);
	    		videoUrl = Config.SERVER_RIA_URL + Requester.RIA_INTERFACE_MOVIE_PLAY + "?requestapp={\"media_id\":" + mediaidkey + ",\"movie_type\":" + movietype + "}&"  + mediaidkey + ".smo";
	    	}
	    }	
	    	   
	    if(!TextUtils.isEmpty(videoUrl) && !TextUtils.isEmpty(movietype)){
	    	 setVideoInfo(name, videoUrl, playedtime, totaltime);
		}else{
			Toast.makeText(this, "播放路径错误, 请稍后刷新列表再试", Toast.LENGTH_LONG).show();
			finish();
    	}
	    setCallFunc(this);
	    super.onCreate(bundle);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		CmmobiClickAgentWrapper.onResume(this);
	}
	

	@Override
	protected void onStop(){
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		CmmobiClickAgentWrapper.onPause(this);
		if(playhistory!=null){
			playhistory.setLocation(DateUtils.getFormatTime((int) getLocationLong()));
			playhistory.setPercent(getLocationLong()+"");
			playhistory.setTotaltime(getTotalTimeLong() + "");
			HistoryManager.getInstance().putPlayHistoryItem(playhistory);
		}
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		PromptDialog.dismissDialog();
		PromptDialog.dimissProgressDialog();
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void onEventMainThread(DialogEvent event) {
		TitleRootActivity.ProcessDialogEvent(this, event, this.getClass().getName());
	}

	@Override
	public void onStartADEvent(String url, String type) {
		// TODO Auto-generated method stub
	/*	String typeDoc = type;
		if("3".equals(type)){
			typeDoc = "2";
		}else if("4".equals(type)){
			typeDoc = "3";
		}
		System.out.println("==============call back =====" + url + ", " + typeDoc);
		*/
		CmmobiClickAgentWrapper.onEvent(this, "v_ad", "label", url, "label2", type);
	}
	
}
