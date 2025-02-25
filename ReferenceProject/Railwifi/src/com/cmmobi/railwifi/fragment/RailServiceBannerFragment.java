package com.cmmobi.railwifi.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.activity.AlbumDetailActivity;
import com.cmmobi.railwifi.activity.JokeDetailActivity;
import com.cmmobi.railwifi.activity.MovieDetailActivity;
import com.cmmobi.railwifi.activity.MusicDetailActivity;
import com.cmmobi.railwifi.activity.MusicMainPageActivity;
import com.cmmobi.railwifi.activity.NewsDetailActivity;
import com.cmmobi.railwifi.activity.RailTravelDetailAcitivity;
import com.cmmobi.railwifi.music.MusicService;
import com.cmmobi.railwifi.network.GsonResponseObject;
import com.cmmobi.railwifi.network.GsonResponseObject.MusicElem;
import com.cmmobi.railwifi.network.GsonResponseObject.serviceBannerphotoElem;
import com.cmmobi.railwifi.network.Requester;
import com.cmmobi.railwifi.utils.CmmobiClickAgentWrapper;
import com.cmmobi.railwifi.utils.ConStant;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.cmmobi.railwifi.utils.ViewUtils;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;


public class RailServiceBannerFragment extends Fragment implements OnClickListener, Callback {
	private final String TAG = "PlayBillFragment";
	private static final String KEY_URL = "PlayBillFragment:imageUrl";
	private static final String KEY_NAME = "PlayBillFragment:name";
	private static final String KEY_ID = "PlayBillFragment:id";
	private static final String KEY_TYPE = "PlayBillFragment:type";
	private String imageUrl = "";
	private String content = "";
	private String object_id = "";
	private String type = "";
	MyImageLoader imageLoader = null;
	DisplayImageOptions imageLoaderOptions = null;
	ImageView imageView;
	TextView tvRailService;
	TextView tvAmusement;
	private int mDrawable = 0;
	private Handler handler = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		imageLoader = MyImageLoader.getInstance();

		imageLoaderOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisc(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.showImageForEmptyUri(mDrawable)
				.showImageOnFail(mDrawable)
				.showImageOnLoading(mDrawable)
//				.displayer(new SimpleBitmapDisplayer())
				// .displayer(new CircularBitmapDisplayer()) 圆形图片
				
				.displayer(new RoundedBitmapDisplayer(12))// 圆角图片
				.build();
		super.onCreate(savedInstanceState);
		
//		EventBus.getDefault().register(this);
		
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(KEY_URL)) {
				imageUrl = savedInstanceState.getString(KEY_URL);
			}
			if (savedInstanceState.containsKey(KEY_NAME)) {
				content = savedInstanceState.getString(KEY_NAME);
			}
			if (savedInstanceState.containsKey(KEY_ID)) {
				object_id = savedInstanceState.getString(KEY_ID);
			}
			if (savedInstanceState.containsKey(KEY_TYPE)) {
				type = savedInstanceState.getString(KEY_TYPE);
			}
        }
		
		handler = new Handler(this);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
//		EventBus.getDefault().unregister(this);
		Log.d("=AAA=","RailServiceBannerFragment onDestroy in");
		ViewUtils.releasePicture(imageView);
		super.onDestroy();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,  Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.item_view_pager, null);
		view.setPadding(DisplayUtil.getSize(getActivity(), 2),0, 
				DisplayUtil.getSize(getActivity(), 2), 0);
		imageView = (ImageView)view.findViewById(R.id.iv_playbill);
		tvRailService = (TextView) view.findViewById(R.id.tv_playbill_name);
		tvRailService.setTypeface(Typeface.MONOSPACE, Typeface.ITALIC);
		tvRailService.setTextSize(DisplayUtil.textGetSizeSp(getActivity(), 33));
		
		tvAmusement = (TextView) view.findViewById(R.id.tv_media_name);
		tvAmusement.setTypeface(Typeface.MONOSPACE, Typeface.ITALIC);
		tvAmusement.setTextSize(DisplayUtil.textGetSizeSp(getActivity(), 33));
		ViewUtils.setMarginTop(tvAmusement, 36);
		ViewUtils.setHeight(tvAmusement, 100);
		switch (mDrawable) {
		case R.drawable.pic_entertainment_default:
			tvRailService.setVisibility(View.GONE);
			tvAmusement.setVisibility(View.VISIBLE);
			tvAmusement.setText(content);
			break;
		case R.drawable.pic_index_default:
			tvAmusement.setVisibility(View.GONE);
			tvRailService.setVisibility(View.VISIBLE);
			tvRailService.setText(content);
			break;
		}
		Log.d(TAG,"imageUrl = " + imageUrl + " content = " + content);
		imageLoader.displayImage(imageUrl, imageView, imageLoaderOptions);
		
		view.setOnClickListener(this);
		return view;
	}
	
	public static RailServiceBannerFragment newInstance(serviceBannerphotoElem elem,int drawable) {
		RailServiceBannerFragment fragment = new RailServiceBannerFragment();
		/*fragment.imageUrl = url;
		fragment.name = name;*/
//		fragment.photoElem = elem;
		fragment.imageUrl = elem.img_path;
		fragment.content = elem.content;
		fragment.object_id = elem.object_id;
		fragment.type = elem.type;
		fragment.mDrawable = drawable;
        return fragment;
    }
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putString(KEY_URL, imageUrl);
		outState.putString(KEY_NAME, content);
		outState.putString(KEY_ID, object_id);
		outState.putString(KEY_TYPE, type);
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		Log.d("=AAA=","RailServiceBanner hidden = " + hidden);
		super.onHiddenChanged(hidden);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(mDrawable){
		case R.drawable.pic_entertainment_default:
			CmmobiClickAgentWrapper.onEvent(getActivity(), "av_fun", "5");
			break;
		case R.drawable.pic_index_default:
			CmmobiClickAgentWrapper.onEvent(getActivity(), "t_service", "7");
			break;
		}
		
		switch(v.getId()) {
		case R.id.rl_item_view_pager:			
			if ("1".equals(type)) {
				Intent newsIntent = new Intent(getActivity(), NewsDetailActivity.class);
				newsIntent.putExtra(ConStant.INTENT_MEDIA_ID, object_id);
				startActivity(newsIntent);
			} else if ("2".equals(type)) {
				Intent movieIntent = new Intent(getActivity(),MovieDetailActivity.class);
				movieIntent.putExtra(ConStant.INTENT_MEDIA_ID, object_id);
				startActivity(movieIntent);
			} else if ("3".equals(type)) {
				Requester.requestMusicDetail(handler, object_id);
			} else if ("4".equals(type)) {
				
			} else if ("5".equals(type)) {
				Intent jokIntent = new Intent(getActivity(),JokeDetailActivity.class);
				jokIntent.putExtra(ConStant.INTENT_MEDIA_ID, object_id);
				startActivity(jokIntent);
			} else if ("6".equals(type)){
				Intent travelIntent = new Intent(getActivity(), RailTravelDetailAcitivity.class);
				travelIntent.putExtra("lineid", object_id);
				startActivity(travelIntent);
			} else if ("7".equals(type)) {
				Intent recommIntent = new Intent(getActivity(),AlbumDetailActivity.class);
				recommIntent.putExtra("mediaid", object_id);
				startActivity(recommIntent);
			}
			break;
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
		case Requester.RESPONSE_TYPE_MUSIC_DETAILS:
			GsonResponseObject.musicDetailResp r45 = (GsonResponseObject.musicDetailResp) msg.obj;
			if(r45!=null && "0".equals(r45.status)){
				
				List<MusicElem> pathArray = new ArrayList<GsonResponseObject.MusicElem>();
				MusicElem me = new MusicElem();
				me.content = r45.content;
				me.img_path = r45.img_path;
				me.media_id = r45.media_id;
				me.name = r45.name;
				me.src_path = r45.src_path;
				me.tag = r45.tag;
				pathArray.add(me);
				MusicService.getInstance().setPlayArray(pathArray);
				Intent intent = new Intent(getActivity(), MusicDetailActivity.class);
				intent.putExtra(ConStant.INTENT_MEDIA_ID, me.media_id);
				getActivity().startActivityForResult(intent, MusicMainPageActivity.BACK_FROM_DETAIL);
			}
			break;
		}
		return false;
	}
}
