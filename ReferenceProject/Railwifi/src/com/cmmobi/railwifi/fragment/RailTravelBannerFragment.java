package com.cmmobi.railwifi.fragment;

import java.util.HashMap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.activity.RailTravelDetailAcitivity;
import com.cmmobi.railwifi.network.GsonResponseObject.LineC;
import com.cmmobi.railwifi.network.GsonResponseObject.serviceBannerphotoElem;
import com.cmmobi.railwifi.utils.CmmobiClickAgentWrapper;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.cmmobi.railwifi.utils.ViewUtils;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;


public class RailTravelBannerFragment extends Fragment {
	private final String TAG = "PlayBillFragment";
	private static final String KEY_URL = "TravelLineImg:imageUrl";
	private static final String KEY_ID = "TravelLineId:id";
	private String imageUrl = "";
	private String lineId = "";
	MyImageLoader imageLoader = null;
	DisplayImageOptions imageLoaderOptions = null;
	ImageView imageView;
	TextView textView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		imageLoader = MyImageLoader.getInstance();

		imageLoaderOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(false)
				.cacheOnDisc(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.showImageOnLoading(R.drawable.tourism_pic_bg_default)
				.showImageOnFail(R.drawable.tourism_pic_bg_default)
				.showImageForEmptyUri(R.drawable.tourism_pic_bg_default)
				.displayer(new RoundedBitmapDisplayer(12))// 圆角图片
				.build();
		super.onCreate(savedInstanceState);
		
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(KEY_URL)) {
				imageUrl = savedInstanceState.getString(KEY_URL);
			}
			if (savedInstanceState.containsKey(KEY_ID)) {
				lineId = savedInstanceState.getString(KEY_ID);
			}
        }
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,  Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View pageView = inflater.inflate(R.layout.travel_view_pager_item, null);
		imageView = (ImageView)pageView.findViewById(R.id.iv_travel);
		imageView.setMaxHeight(DisplayUtil.getSize(getActivity(), 335));
		imageView.setMaxWidth(DisplayUtil.getScreenWidth(getActivity()) -  DisplayUtil.getSize(getActivity(), 12) *2 - DisplayUtil.dip2px(getActivity(), 1)*2);
		imageView.setPadding(DisplayUtil.dip2px(getActivity(), 1), 0, DisplayUtil.dip2px(getActivity(), 1), 0);
		imageLoader.displayImage(imageUrl, imageView, imageLoaderOptions);
		imageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CmmobiClickAgentWrapper.onEvent(getActivity(), "t_travel", "label", "2", "label2", lineId);
				Intent intent = new Intent(getActivity(), RailTravelDetailAcitivity.class);
				intent.putExtra("lineid", lineId);
				getActivity().startActivity(intent);
			}
		});
		return pageView;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		ViewUtils.releasePicture(imageView);
		super.onDestroy();
	}
	
	public static RailTravelBannerFragment newInstance(LineC elem) {
		RailTravelBannerFragment fragment = new RailTravelBannerFragment();
		fragment.imageUrl = elem.img_path;
		fragment.lineId = elem.line_id;
        return fragment;
    }
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putString(KEY_URL, imageUrl);
		outState.putString(KEY_ID, lineId);
	}
}
