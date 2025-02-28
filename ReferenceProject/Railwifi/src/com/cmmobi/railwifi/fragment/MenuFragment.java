package com.cmmobi.railwifi.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmmobi.railwifi.MainApplication;
import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.activity.LoginFromMainpageActivity;
import com.cmmobi.railwifi.activity.MainActivity;
import com.cmmobi.railwifi.activity.RegisterActivity;
import com.cmmobi.railwifi.activity.SettingActivity;
import com.cmmobi.railwifi.activity.UserMainPageAcitivity;
import com.cmmobi.railwifi.dialog.PromptDialog;
import com.cmmobi.railwifi.download.DownloadEvent;
import com.cmmobi.railwifi.event.FragmentEvent;
import com.cmmobi.railwifi.event.NetworkEvent;
import com.cmmobi.railwifi.event.ParallelEvent;
import com.cmmobi.railwifi.parallel.DiscScanTask;
import com.cmmobi.railwifi.parallel.ParallelManager;
import com.cmmobi.railwifi.utils.CmmobiClickAgentWrapper;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.cmmobi.railwifi.utils.StringUtils;
import com.cmmobi.railwifi.utils.ViewUtils;

import de.greenrobot.event.EventBus;


public class MenuFragment extends XFragment implements OnClickListener {
	private final String TAG = "MenuFragment";
	private DisplayMetrics dm = new DisplayMetrics();
	private TextView tvWelcome = null;
	private View rlAlliance = null;
	private View rlHomePage = null;
	private View rlCurSelected = null;
	private View rlRailService = null;
	private View rlGameWorld = null;
	private View rlCityIntro = null;
	private View rlAmusement = null;
	private View curSelectedTopLine = null;
	private View curSelectedBottomLine = null;
	private View contentView;
	private View line0,line1,line2,line3,line4,line5,line6;
	private int selectedIndex = 0;
	private ImageView ivPrompt = null;
	SharedPreferences mySharedPreferences= null;
	
	private ImageButton ibSign;
	private ImageButton ibLogin;
	private ImageButton ibRegister;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_menu, null);
		if (view != null) {
			initViews(view);
		}
		contentView = view;
		return view;
	}
	
	private void initViews(View view) {
		
		ibLogin = (ImageButton) view.findViewById(R.id.btn_login);
		ibSign = (ImageButton) view.findViewById(R.id.btn_sign);
		ibRegister = (ImageButton) view.findViewById(R.id.btn_register);
		
		rlAlliance = view.findViewById(R.id.rl_alliance);
		rlHomePage = view.findViewById(R.id.rl_home_page);
		rlRailService = view.findViewById(R.id.rl_rail_service);
		rlCityIntro = view.findViewById(R.id.rl_city_introduction);
		rlGameWorld = view.findViewById(R.id.rl_game_world);
		rlAmusement = view.findViewById(R.id.rl_media_amusement);
		rlHomePage.setOnClickListener(this);
		rlRailService.setOnClickListener(this);
		rlAmusement.setOnClickListener(this);
		view.findViewById(R.id.rl_city_life).setOnClickListener(this);
		rlGameWorld.setOnClickListener(this);
		rlAlliance.setOnClickListener(this);
		rlCityIntro.setOnClickListener(this);
		view.findViewById(R.id.ll_setting).setOnClickListener(this);
		view.findViewById(R.id.iv_head).setOnClickListener(this);
		view.findViewById(R.id.rl_user_info).setOnClickListener(this);
		view.findViewById(R.id.btn_sign).setOnClickListener(this);
		view.findViewById(R.id.btn_login).setOnClickListener(this);
		view.findViewById(R.id.btn_register).setOnClickListener(this);
		
		line0 = view.findViewById(R.id.view_line_0);
		line1 = view.findViewById(R.id.view_line_1);
		line2 = view.findViewById(R.id.view_line_2);
		line3 = view.findViewById(R.id.view_line_3);
		line4 = view.findViewById(R.id.view_line_4);
		line5 = view.findViewById(R.id.view_line_5);
		line6 = view.findViewById(R.id.view_line_6);
		
		rlCurSelected = rlHomePage;
		
		if (StringUtils.isEmpty(MainActivity.train_num)) {
			hideRailService();
		} else {
			showRailService();
		}
		
		initHightLine();
		
	/*	dm = getResources().getDisplayMetrics();
		final float defaultPercent = 910.0f/(1280 - 50);
		final float defaultMidPos = (577.0f) * dm.heightPixels / 1280;
		int messageTop = (int) (defaultMidPos * (1 - defaultPercent));
		ViewUtils.setMarginTopPixel(view.findViewById(R.id.rl_message), messageTop);
		*/
		
		ViewUtils.setMarginTop(view.findViewById(R.id.rl_message), 186);
		ViewUtils.setHeight(view.findViewById(R.id.rl_message), 60);
		
		TextView tvHomePage = (TextView) view.findViewById(R.id.tv_home_page);
		TextView tvRailService = (TextView) view.findViewById(R.id.tv_rail_service);
		TextView tvMediaAmusement = (TextView) view.findViewById(R.id.tv_media_amusement);
		TextView tvCityLife = (TextView) view.findViewById(R.id.tv_city_life);
		TextView tvGameWorld = (TextView) view.findViewById(R.id.tv_game_world);
		TextView tvPopularApp = (TextView) view.findViewById(R.id.tv_alliance);
		TextView tvCityIntroduction = (TextView) view.findViewById(R.id.tv_city_introduction);
		TextView tvAlliance = (TextView) view.findViewById(R.id.tv_alliance);
//		TextView tvSetting = (TextView) view.findViewById(R.id.tv_setting);
		
		ivPrompt = (ImageView) view.findViewById(R.id.iv_tips);
		ViewUtils.setSize(ivPrompt, 16, 16);
		
		tvRailService.setTypeface(Typeface.MONOSPACE, Typeface.ITALIC);
		tvMediaAmusement.setTypeface(Typeface.MONOSPACE, Typeface.ITALIC);
		tvCityLife.setTypeface(Typeface.MONOSPACE, Typeface.ITALIC);
		tvGameWorld.setTypeface(Typeface.MONOSPACE, Typeface.ITALIC);
		tvPopularApp.setTypeface(Typeface.MONOSPACE, Typeface.ITALIC);
		tvCityIntroduction.setTypeface(Typeface.MONOSPACE, Typeface.ITALIC);
		tvAlliance.setTypeface(Typeface.MONOSPACE, Typeface.ITALIC);
//		tvSetting.setTypeface(Typeface.MONOSPACE, Typeface.ITALIC);
		
		tvHomePage.setTextSize(DisplayUtil.textGetSizeSp(getActivity(), 36));
		tvRailService.setTextSize(DisplayUtil.textGetSizeSp(getActivity(), 36));
		tvMediaAmusement.setTextSize(DisplayUtil.textGetSizeSp(getActivity(), 36));
		tvCityLife.setTextSize(DisplayUtil.textGetSizeSp(getActivity(), 36));
		tvGameWorld.setTextSize(DisplayUtil.textGetSizeSp(getActivity(), 36));
		tvPopularApp.setTextSize(DisplayUtil.textGetSizeSp(getActivity(), 36));
		tvCityIntroduction.setTextSize(DisplayUtil.textGetSizeSp(getActivity(), 36));
		tvAlliance.setTextSize(DisplayUtil.textGetSizeSp(getActivity(), 36));
//		tvSetting.setTextSize(DisplayUtil.textGetSizeSp(getActivity(), 36));
		
		tvWelcome = (TextView) view.findViewById(R.id.tv_welcome);
		
		ViewUtils.setMarginTop(tvWelcome, 54);
		
//		tvWelcom.setTextSize(DisplayUtil.textGetSizeSp(getActivity(), 42));
		Log.d(TAG,"textSize = " + DisplayUtil.textGetSizeSp(getActivity(), 42));
		
		TextView tvIdText = (TextView) view.findViewById(R.id.tv_userid);
		tvIdText.setTextSize(DisplayUtil.textGetSizeSp(getActivity(), 27));
		ViewUtils.setMarginTop(tvIdText, 16);
		
		ViewUtils.setMarginLeft(view.findViewById(R.id.rl_user_info), 22);
		ViewUtils.setMarginLeft(view.findViewById(R.id.iv_message), 188);
		ViewUtils.setMarginLeft(view.findViewById(R.id.rl_head), 24);
		ViewUtils.setMarginTop(view.findViewById(R.id.rl_head), 30);
		ViewUtils.setMarginLeft(view.findViewById(R.id.btn_sign), 30);
		ViewUtils.setMarginTop(view.findViewById(R.id.btn_sign), 10);
		ViewUtils.setMarginLeft(view.findViewById(R.id.btn_login), 30);
		ViewUtils.setMarginTop(view.findViewById(R.id.btn_login), 10);
		ViewUtils.setMarginLeft(view.findViewById(R.id.btn_register), 20);
		ViewUtils.setMarginTop(view.findViewById(R.id.btn_register), 10);
		ViewUtils.setMarginTop(view.findViewById(R.id.sv_index), 0);
		ViewUtils.setSize(view.findViewById(R.id.rl_head), 193, 193);
		ViewUtils.setSize(view.findViewById(R.id.iv_head), 169, 169);
		
		
		ViewUtils.setMarginLeft(view.findViewById(R.id.tv_msg), 12);
		ViewUtils.setMarginLeft(view.findViewById(R.id.ll_setting), 56);
		ViewUtils.setMarginBottom(view.findViewById(R.id.ll_setting), 50);
		
		TextView tvMessage = (TextView) view.findViewById(R.id.tv_msg);
		tvMessage.setTextSize(DisplayUtil.textGetSizeSp(getActivity(), 30));
		ViewUtils.setMarginLeft(tvMessage, 12);
		
		ViewUtils.setMarginLeft(view.findViewById(R.id.iv_rail_service), 50);
		ViewUtils.setMarginLeft(view.findViewById(R.id.iv_media_amusement), 50);
		ViewUtils.setMarginLeft(view.findViewById(R.id.iv_city_life), 50);
		ViewUtils.setMarginLeft(view.findViewById(R.id.iv_game_world), 50);
		ViewUtils.setMarginLeft(view.findViewById(R.id.iv_alliance), 50);
		ViewUtils.setMarginLeft(view.findViewById(R.id.iv_city_introduction), 50);
		ViewUtils.setMarginLeft(view.findViewById(R.id.iv_home_page), 50);
		
		ViewUtils.setMarginLeft(view.findViewById(R.id.tv_rail_service), 32);
		ViewUtils.setMarginLeft(view.findViewById(R.id.tv_media_amusement), 32);
		ViewUtils.setMarginLeft(view.findViewById(R.id.tv_city_life), 32);
		ViewUtils.setMarginLeft(view.findViewById(R.id.tv_game_world), 32);
		ViewUtils.setMarginLeft(view.findViewById(R.id.tv_alliance), 32);
		ViewUtils.setMarginLeft(view.findViewById(R.id.tv_city_introduction), 32);
		
		ViewUtils.setSize(view.findViewById(R.id.iv_home_page), 79, 110);
		ViewUtils.setSize(view.findViewById(R.id.iv_rail_service), 79, 110);
		ViewUtils.setSize(view.findViewById(R.id.iv_alliance), 79, 110);
		ViewUtils.setSize(view.findViewById(R.id.iv_media_amusement), 79, 110);
		ViewUtils.setSize(view.findViewById(R.id.iv_city_introduction), 79, 110);
		ViewUtils.setSize(view.findViewById(R.id.iv_game_world), 79, 110);
		
		ViewUtils.setHeight(rlAlliance, 110);
		ViewUtils.setHeight(rlAmusement, 110);
		ViewUtils.setHeight(rlCityIntro, 110);
		ViewUtils.setHeight(rlGameWorld, 110);
		ViewUtils.setHeight(rlHomePage, 110);
		ViewUtils.setHeight(rlRailService, 110);
		
	}
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if (mySharedPreferences.getBoolean("has_new", false)) {
			ivPrompt.setVisibility(View.VISIBLE);
        } else {
        	ivPrompt.setVisibility(View.GONE);
        }

	}
	
	private void initHightLine() {
		if (curSelectedTopLine != null) {
			curSelectedTopLine.setBackgroundColor(0xffffffff);
		}
		
		if (curSelectedBottomLine != null) {
			curSelectedBottomLine.setBackgroundColor(0xffffffff);
		}
		
		Log.d("=AAA=","initHightLine selectedIndex = " + selectedIndex);
		switch (selectedIndex) {
		case 0:
		case 1:
			rlCurSelected = rlHomePage;
			curSelectedTopLine = line0;
			curSelectedBottomLine = line1;
			break;
		case 2:
			rlCurSelected = rlRailService;
			curSelectedTopLine = line1;
			curSelectedBottomLine = line2;
			break;
		case 3:
			rlCurSelected = rlAlliance;
			curSelectedTopLine = line2;
			curSelectedBottomLine = line3;
			break;
		case 4:
			rlCurSelected = rlAmusement;
			curSelectedTopLine = line3;
			curSelectedBottomLine = line4;
			break;
		case 5:
			rlCurSelected = rlGameWorld;
			curSelectedTopLine = line4;
			curSelectedBottomLine = line5;
			break;
		case 6:
			rlCurSelected = rlCityIntro;
			curSelectedTopLine = line5;
			curSelectedBottomLine = line6;
			break;
		}
		
		rlCurSelected.setBackgroundResource(R.drawable.drawer_list_bg_1);
		if (selectedIndex == 2 && rlRailService.getVisibility() == View.GONE) {
			
		} else {//仅仅当列车服务不隐藏时，高亮中间线
			curSelectedTopLine.setBackgroundColor(0xffe5e5dc);
			curSelectedBottomLine.setBackgroundColor(0xffe5e5dc);
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mySharedPreferences= mySharedPreferences = MainApplication.getAppInstance().getSharedPreferences("has_new_download", 
				Activity.MODE_PRIVATE);
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
	
	public void setNickName(String nickName) {
		if (nickName == null) {
			nickName = "游客";
		}
		if (tvWelcome != null) { 
			tvWelcome.setText("欢迎您，"+ nickName);
		}
	}
	
	public void setLoginStatus(Boolean isLogin, Boolean isSign){
		if(!isLogin){
			ibLogin.setVisibility(View.VISIBLE);
			ibRegister.setVisibility(View.VISIBLE);
			ibSign.setVisibility(View.GONE);
		}else{
			ibLogin.setVisibility(View.GONE);
			ibRegister.setVisibility(View.GONE);
			ibSign.setVisibility(View.VISIBLE);
			if(isSign){
				ibSign.setEnabled(false);
			}else{
				ibSign.setEnabled(true);
			}
		}
	}
	
	public void setSex(String sex){
		if("0".equals(sex)){
			tvWelcome.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_sex_male, 0, 0, 0);
		}else if("1".equals(sex)){
			tvWelcome.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_sex_female, 0, 0, 0);
		}else{
			tvWelcome.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_sex_neutral, 0, 0, 0);
		}
	}
	
	private void selectedIndex(View v,View lineTop,View lineBottom) {
		if (rlCurSelected != v) {
			Log.d("=AAA=","selectedIndex in");
			rlCurSelected.setBackgroundColor(0);
			rlCurSelected = v;
			rlCurSelected.setBackgroundResource(R.drawable.drawer_list_bg_1);
			
			curSelectedTopLine.setBackgroundColor(0xffffffff);
			curSelectedBottomLine.setBackgroundColor(0xffffffff);
			
			curSelectedTopLine = lineTop;
			curSelectedBottomLine = lineBottom;
			
			curSelectedTopLine.setBackgroundColor(0xffe5e5dc);
			curSelectedBottomLine.setBackgroundColor(0xffe5e5dc);
		}
	}
	
	
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.rl_home_page:
			CmmobiClickAgentWrapper.onEvent(getActivity(), "home_nav", "7");
			EventBus.getDefault().post(FragmentEvent.HOMEPAGE);
			selectedIndex = 1;
			selectedIndex(v,line0,line1);
			break;
		case R.id.rl_rail_service:
			CmmobiClickAgentWrapper.onEvent(getActivity(), "home_nav", "1");
			selectedIndex = 2;
			EventBus.getDefault().post(FragmentEvent.RAILSERVICE);
			selectedIndex(v,line1,line2);
			break;
		case R.id.rl_media_amusement:
			selectedIndex = 4;
			CmmobiClickAgentWrapper.onEvent(getActivity(), "home_nav", "2");
			EventBus.getDefault().post(FragmentEvent.MEDIAAMUSEMENT);
			selectedIndex(v,line3,line4);
			break;
		case R.id.rl_city_life:
//			CmmobiClickAgentWrapper.onEvent(getActivity(), "home_nav", "1");
			EventBus.getDefault().post(FragmentEvent.CITYLIFT);
			
			break;
		case R.id.rl_game_world:
			CmmobiClickAgentWrapper.onEvent(getActivity(), "home_nav", "3");
			selectedIndex = 5;
			EventBus.getDefault().post(FragmentEvent.GAMEWORLD);
			selectedIndex(v,line4,line5);
			break;
		case R.id.rl_alliance:
			//铁路旅游
			CmmobiClickAgentWrapper.onEvent(getActivity(), "home_nav", "8");
			selectedIndex = 3;
			EventBus.getDefault().post(FragmentEvent.RAILTRAVEL);
			selectedIndex(v,line2,line3);
			break;
		case R.id.rl_city_introduction:
			CmmobiClickAgentWrapper.onEvent(getActivity(), "home_nav", "4");
			selectedIndex = 6;
			EventBus.getDefault().post(FragmentEvent.CITYINTRODUCTION);
			selectedIndex(v,line5,line6);
			break;
		case R.id.ll_setting:
			CmmobiClickAgentWrapper.onEvent(getActivity(), "home_nav", "5");
			Intent setting_intent = new Intent(getActivity(), SettingActivity.class);
			startActivity(setting_intent);
			break;
		case R.id.iv_head:
		case R.id.rl_user_info:
			CmmobiClickAgentWrapper.onEvent(getActivity(), "home_nav", "6");
			Intent user_intent = new Intent(getActivity(), UserMainPageAcitivity.class);
			startActivity(user_intent);
			break;
		case R.id.btn_sign:
			//签到
			break;
		case R.id.btn_login:
			Intent loginIntent = new Intent(getActivity(), LoginFromMainpageActivity.class);
			startActivity(loginIntent);
			break;
		case R.id.btn_register:
			Intent registerIntent = new Intent(getActivity(), RegisterActivity.class);
			startActivity(registerIntent);
			break;
		
		}
	}
	
	public void change2Alliance() {
		selectedIndex = 3;
		selectedIndex(rlAlliance, line2, line3);
	}
	
	public void change2HomePage() {
		selectedIndex = 1;
		selectedIndex(rlHomePage, line0, line1);
	}
	
	public void change2RailService() {
		selectedIndex = 2;
		Log.d("=AAA=","change2RailService in");
		selectedIndex(rlRailService, line1,line2);
	}
	
	public void change2Amusement() {
		selectedIndex = 4;
		selectedIndex(rlAmusement, line3,line4);
	}
	
	public void change2GameWorld() {
		selectedIndex = 5;
		selectedIndex(rlGameWorld, line4,line5);
	}
	
	public void change2CityIntro() {
		selectedIndex = 6;
		selectedIndex(rlCityIntro, line5,line6);
	}
	
	public void hideRailService() {
		rlRailService.setVisibility(View.GONE);
		if (contentView != null) {
			contentView.findViewById(R.id.view_line_2).setVisibility(View.GONE);
			line2 = contentView.findViewById(R.id.view_line_1);
		}
		
		initHightLine();
	}
	
	public void showRailService() {
		rlRailService.setVisibility(View.VISIBLE);
		if (contentView != null) {
			contentView.findViewById(R.id.view_line_2).setVisibility(View.VISIBLE);
			line2 = contentView.findViewById(R.id.view_line_2);
		}
		
		initHightLine();
	}
	
	public void onEvent(NetworkEvent event) {
		switch(event) {
		case NET_RAILWIFI:
			Log.d("=AAA=","MenuFragment NET_RAILWIFI ");
			showRailService();
			break;
		case NET_OTHERS:
			Log.d("=AAA=","MenuFragment NET_OTHERS ");
			hideRailService();
			break;
		}
	}
	
	public void onEventMainThread(DownloadEvent _event) {
		if (_event == DownloadEvent.STATUS_CHANGED && ((DownloadEvent) _event).getType() == -1) {
			if (mySharedPreferences.getBoolean("has_new", false)) {
				ivPrompt.setVisibility(View.VISIBLE);
	        }
		}
	}
}
