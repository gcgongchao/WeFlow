package com.cmmobi.railwifi.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.network.GsonResponseObject;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.cmmobi.railwifi.utils.StringUtils;
import com.cmmobi.railwifi.utils.ViewUtils;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class RailNewsAdapter extends BaseAdapter /*implements OnGlobalLayoutListener*/ {

	private static final String TAG = "RailNewsAdapter";
	private Context context;
	private LayoutInflater inflater;
	private List<GsonResponseObject.newsElem> list;// = new ArrayList<GsonResponseObject.mediaElem>();
	private int size = 0;
	
	//使用开源的webimageloader
	public static DisplayImageOptions options;
	protected MyImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;
//	private int fontNum = 15;
//	private boolean inited = false;
	ViewHolder holder = null;
	
	public RailNewsAdapter(final Context context) {
		this.context = context;
		DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
		this.size = dm.widthPixels/5;
		inflater = LayoutInflater.from(context);
		
		animateFirstListener = new AnimateFirstDisplayListener();
		imageLoader = MyImageLoader.getInstance();
		
		options = new DisplayImageOptions.Builder()
		.cacheOnDisk(true)
		.showImageOnLoading(R.drawable.pic_list_default)
		.showImageForEmptyUri(R.drawable.pic_list_default)
		.showImageOnFail(R.drawable.pic_list_default)
		.cacheInMemory(true)
//		.displayer(new SimpleBitmapDisplayer())
//		.displayer(new CircularBitmapDisplayer()) //圆形图片
		.displayer(new RoundedBitmapDisplayer(5))// 圆角图片
		.build();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public GsonResponseObject.newsElem getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.list_railnews_item, null);
			holder = new ViewHolder();
			
			holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
//			holder.tv_title.getViewTreeObserver().addOnGlobalLayoutListener(this);
			holder.iv_img = (ImageView) convertView.findViewById(R.id.im_news);
			holder.tv_subheading =  (TextView) convertView.findViewById(R.id.tv_subheading);
			
			ViewUtils.setSize(holder.iv_img, 116, 116);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
//		Log.v(TAG, StringUtils.getStringInfo(list.get(position).title) + ":" + list.get(position).title);
//		Log.v(TAG, StringUtils.getStringInfo(StringUtils.ToSBC(list.get(position).title)) + ":" + StringUtils.ToSBC(list.get(position).title));

//		holder.tv_title.setText(StringUtils.ToEllipsize(list.get(position).title, fontNum) );
		holder.tv_title.setText(list.get(position).title);
		holder.tv_subheading.setText(list.get(position).subheading);
		Log.v(TAG, "displayImage : position:" + position + ", url:" +  list.get(position).img_path);
		imageLoader.displayImage(list.get(position).img_path, holder.iv_img, options, animateFirstListener);

		return convertView;
	}
	
	public void setData(List<GsonResponseObject.newsElem> data){
		this.list = data;
//		if(data != null && data.size()>0){
//			this.list.clear();
//			this.list.addAll(data);
//		}else{
//			this.list.clear();
//		}
	}
	
	public boolean isEmpty()
	{
		return list.isEmpty();
	}
	
	
	
	public class ViewHolder {
		TextView tv_title;
		TextView tv_subheading;
		ImageView iv_img;
	}



//	@Override
//	public void onGlobalLayout() {
//		// TODO Auto-generated method stub
//		Log.v(TAG, "getMeasuredWidth:" + holder.tv_title.getMeasuredWidth() + ", getWidth:" + holder.tv_title.getWidth() + ", getTextSize:" + holder.tv_title.getTextSize());
//		if(holder.tv_title.getMeasuredWidth()>0){
//			fontNum = (int) (holder.tv_title.getMeasuredWidth() / holder.tv_title.getTextSize());
//		
//			if(!inited){
//				notifyDataSetInvalidated();
//				inited = true;
//			}
//
//		}
//	
//		
//	}

	
}