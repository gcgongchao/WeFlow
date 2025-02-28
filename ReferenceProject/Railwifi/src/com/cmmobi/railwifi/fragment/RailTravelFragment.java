package com.cmmobi.railwifi.fragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.PopupWindow.OnDismissListener;
import cn.trinea.android.common.util.ListUtils;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.activity.RailTravelDetailAcitivity;
import com.cmmobi.railwifi.activity.RailTravelOrderHistoryAcitivity;
import com.cmmobi.railwifi.activity.MainActivity.BannerEvent;
import com.cmmobi.railwifi.adapter.MoreAdapter;
import com.cmmobi.railwifi.adapter.RailTravelBannerAdapter;
import com.cmmobi.railwifi.adapter.RailTravelListAdapter;
import com.cmmobi.railwifi.dialog.PromptDialog;
import com.cmmobi.railwifi.network.GsonResponseObject;
import com.cmmobi.railwifi.network.GsonResponseObject.LineC;
import com.cmmobi.railwifi.network.GsonResponseObject.LineInfo;
import com.cmmobi.railwifi.network.GsonResponseObject.travelLineListResp;
import com.cmmobi.railwifi.network.Requester;
import com.cmmobi.railwifi.utils.CmmobiClickAgentWrapper;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.cmmobi.railwifi.view.MyViewPager;
import com.cmmobi.railwifi.view.autoscrollviewpager.AutoScrollViewPager;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.viewpagerindicator.CirclePageIndicator;

import de.greenrobot.event.EventBus;

/**
 * @author youtian
 * @email youtian@cmmobi.com
 * @date  2014-11-15
 */
public class RailTravelFragment extends TitleRootFragment implements OnRefreshListener2<ListView>{

	private String TAG = "RailTravelAcitivity";
	private PullToRefreshListView xlv_travelData;
	private ListView lv_travelData;
	private RailTravelListAdapter travelListAdapter;
	private static final int REFRESH_COMPLETE = 0xffabab;
	
	private boolean isHasNextPage = false;
	
	private LinearLayout ll_travelLayout;
	private PopupWindow mMorePopupWindow;
	
	private int currentPage = 1;
	private String currentCity = "全部";
	private String[] allCities;
	private MyViewPager viewPager;
	private GsonResponseObject.travelLineListResp lineListResp;

	private View titleView;
	
	@Override
	public int subContentViewId() {
		return R.layout.activity_railtravel;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		initViews(view);
		
		
		return view;
	}
	
	private void initViews(View view){
		currentCity = "全部";
		currentPage = 1;
				
		EventBus.getDefault().register(this);
		setTitleTextAndDrawable(currentCity, R.drawable.top_close);
		setLeftButtonBackground(R.drawable.btn_navigation);
		setRightButtonBackgroundSquare(R.drawable.btn_top_collect_selector);
		ll_travelLayout = (LinearLayout) view.findViewById(R.id.ll_travel);
		xlv_travelData = (PullToRefreshListView) view.findViewById(R.id.xlv_travel_data);
		xlv_travelData.setShowIndicator(false);
		lv_travelData = xlv_travelData.getRefreshableView();
		xlv_travelData.setOnRefreshListener(this);
		xlv_travelData.setPullToRefreshEnabled(false);

		LinearLayout.LayoutParams pm = (LinearLayout.LayoutParams) xlv_travelData.getLayoutParams();
		pm.bottomMargin = DisplayUtil.getSize(getActivity(), 12);
		xlv_travelData.setLayoutParams(pm);
		travelListAdapter = new RailTravelListAdapter(getActivity());

		
		//xlv_travelData.setAdapter(travelListAdapter);
		
		lv_travelData.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), RailTravelDetailAcitivity.class);
				intent.putExtra("lineid", travelListAdapter.getItem(arg2-2).line_id);
				startActivity(intent);
			}
		});
		
		lv_travelData.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
								
				return false;
			}
		});
		
	
		titleView = getActivity().getLayoutInflater().inflate(R.layout.activity_railtravel_top_pics, null);
		viewPager = (MyViewPager)titleView.findViewById(R.id.vp_pager_pics);
		viewPager.setPadding( DisplayUtil.getSize(getActivity(), 12)-DisplayUtil.dip2px(getActivity(), 1), DisplayUtil.getSize(getActivity(), 12),  DisplayUtil.getSize(getActivity(), 12) -DisplayUtil.dip2px(getActivity(), 1), 0);
		RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) viewPager.getLayoutParams();
		lParams.height = DisplayUtil.getSize(getActivity(), 335);
		viewPager.setLayoutParams(lParams);
		getSlidingMenu().addIgnoredView(viewPager);
		
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Requester.requestTravelLineList(handler, currentCity, currentPage);
			}
		}, 250);
	}
	
	MoreAdapter adapter;
	
	public void initPopupwindow(final ArrayList<String> citys) {
		if(getActivity() == null) return;
		View view = getActivity().getLayoutInflater().inflate(R.layout.more_popup_window,
				null);
		ListView mlvSelector = (ListView) view.findViewById(R.id.lv_selector);
		adapter = new MoreAdapter(getActivity(), citys);
		mlvSelector.setAdapter(adapter);
		mlvSelector.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
				if (arg2.getKeyCode() == KeyEvent.KEYCODE_MENU
						&& arg2.getAction() == KeyEvent.ACTION_UP
						&& mMorePopupWindow.isShowing()) {
					mMorePopupWindow.dismiss();
				}
				return false;
			}
		});
		mlvSelector.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				currentCity = adapter.getItem(arg2);
				mMorePopupWindow.dismiss();
			}
		});
		
		mMorePopupWindow = new PopupWindow(view, DisplayUtil.getSize(getActivity(), 338), LayoutParams.WRAP_CONTENT, true);
		mMorePopupWindow.setBackgroundDrawable(getResources()
				.getDrawable(R.drawable.pop_window_bg));
		mMorePopupWindow.setOutsideTouchable(true);
		mMorePopupWindow.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				setTitleTextAndDrawable(currentCity, R.drawable.top_close);
				if(lineListResp != null && "0".equals(lineListResp.status) && lineListResp.linelist != null){
					if("全部".equals(currentCity)){
						travelListAdapter.setData(lineListResp.linelist);
					}else{
						List<LineInfo> list = new ArrayList<GsonResponseObject.LineInfo>();
						for(int i=0; i<lineListResp.linelist.length; i++){
							if(currentCity.equals(lineListResp.linelist[i].startaddress)){
								list.add(lineListResp.linelist[i]);
							}
						}
						travelListAdapter.setData(list);
					}
				}
				viewPager.startAutoScroll();
				CmmobiClickAgentWrapper.onEvent(getActivity(), "t_tra_sort", currentCity);
			}
		});
	}

	ArrayList<String> getPopupWindowData(){
		ArrayList<String> cities = new ArrayList<String>();
		if(allCities == null) return cities;
		for(int i=0; i<allCities.length;i++){
			if(currentCity.equals(allCities[i])){
				continue;
			}else{
				cities.add(allCities[i]);
			}
		}
		return cities;
	}
	
	void updatePopupWindow(ArrayList<String> data){
		adapter.setData(data);
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	public void onEvent(BannerEvent event) {
		switch(event) {
		case BANNERSTART:
			if (viewPager != null) {
				viewPager.startAutoScroll();
			}
			break;
		case BANNERSTOP:
			if (viewPager != null) {
				viewPager.stopAutoScroll();
			}
			break;
		}
	}
	
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		handler.sendEmptyMessage(REFRESH_COMPLETE);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		
		handler.sendEmptyMessage(REFRESH_COMPLETE);
		
		if(isHasNextPage){
			
			
		}else{
			
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btn_title_left:
			getSlidingMenu().toggle();
			break;
		case R.id.tv_title:
		case R.id.iv_title:
			if (mMorePopupWindow == null) {
				return;
			}
			if (mMorePopupWindow.isShowing()) {
				mMorePopupWindow.dismiss();
			} else {
				updatePopupWindow(getPopupWindowData());
				mMorePopupWindow.showAtLocation(ll_travelLayout, Gravity.CENTER_HORIZONTAL|Gravity.TOP, 0, DisplayUtil.getSize(getActivity(), 96+24));
				viewPager.stopAutoScroll();
				//mMorePopupWindow.showAsDropDown(getTvTitle(), -DisplayUtil.getSize(this, 338/2), -3);
				setTitleTextAndDrawable(currentCity, R.drawable.top_open);
				CmmobiClickAgentWrapper.onEvent(getActivity(), "t_travel", "label", "1", "label2", "");
			}
			break;
		case R.id.btn_title_right:
			CmmobiClickAgentWrapper.onEvent(getActivity(), "t_travel", "label", "3", "label2", "");
			Intent intent = new Intent(getActivity(), RailTravelOrderHistoryAcitivity.class);
		    startActivity(intent);
			break;
			default:{
				
			}	
		}
	}	
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester.RESPONSE_TYPE_TRAVEL_LINELIST:

			if(getActivity() == null) break;
			travelLineListResp t_lineListResp = (GsonResponseObject.travelLineListResp) msg.obj;
			if(t_lineListResp != null && "0".equals(t_lineListResp.status)){
				if(!"全部".equals(t_lineListResp.current_city)){
					break;
				}
				lineListResp = (GsonResponseObject.travelLineListResp) msg.obj;
				getActivity().findViewById(R.id.rl_empty).setVisibility(View.GONE);
				getActivity().findViewById(R.id.xlv_travel_data).setVisibility(View.VISIBLE);				
				CirclePageIndicator indicator = (CirclePageIndicator) titleView.findViewById(R.id.indicator);
				ArrayList<LineC> list = new ArrayList<GsonResponseObject.LineC>();
				Collections.addAll(list, lineListResp.piclist);
				
				if(list.size()>0){
					RailTravelBannerAdapter adapter = new RailTravelBannerAdapter(getChildFragmentManager());
					adapter.setList(list);
					viewPager.setAdapter(adapter);
					viewPager.setInterval(3000);
				    viewPager.startAutoScroll();
				    viewPager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
				    if(0<ListUtils.getSize(list)){
				    	viewPager.setCurrentItem(Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % ListUtils.getSize(list));
				    }
					indicator.setViewPager(viewPager);
					indicator.notifyDataSetChanged();
				}
				lv_travelData.addHeaderView(titleView);
				travelListAdapter.setData(lineListResp.linelist);
				if(lv_travelData.getAdapter() == null){
					lv_travelData.setAdapter(travelListAdapter);
				}
				allCities = lineListResp.name;
				initPopupwindow(getPopupWindowData());
			}else{
				getActivity().findViewById(R.id.rl_empty).setVisibility(View.VISIBLE);
				getActivity().findViewById(R.id.xlv_travel_data).setVisibility(View.GONE);
			}	
			break;
		case REFRESH_COMPLETE:
			xlv_travelData.onRefreshComplete();
			break;
		default:
			break;
		}
		return false;
	}
	
	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		try {
	        Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
	        childFragmentManager.setAccessible(true);
	        childFragmentManager.set(this, null);
	    } catch (NoSuchFieldException e) {
	        throw new RuntimeException(e);
	    } catch (IllegalAccessException e) {
	        throw new RuntimeException(e);
	    }
	}

}
