package com.cmmobi.railwifi.activity;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.adapter.RecommendLineGridViewAdapter;
import com.cmmobi.railwifi.dao.TravelOrderInfo;
import com.cmmobi.railwifi.dialog.PromptDialog;
import com.cmmobi.railwifi.network.GsonRequestObject;
import com.cmmobi.railwifi.network.GsonResponseObject;
import com.cmmobi.railwifi.network.Requester;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.cmmobi.railwifi.view.NoScrollGridView;
import com.google.gson.Gson;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class RailTravelOrderPayResultActivity extends TitleRootActivity {

	private ImageView ivIcon;
	private TextView tvTicketLeft;
	private TextView tvName;
	private TextView tvMoney;
	private TextView tvAddress;
	private TextView tvAddressDetail;
	private TextView tvTravel;
	private TextView tvTravelDetail;
	private TextView tvPhone;
	private TextView tvPhoneDetail;
	private ImageView ivSuccess;
	private TextView tvRecommend;
	private NoScrollGridView gvImgs;
	private ImageView ivLine;
	private TextView ivLineB;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitleText("支付成功");
		hideRightButton();
		ivIcon = (ImageView)findViewById(R.id.iv_icon);
		RelativeLayout.LayoutParams pm = (RelativeLayout.LayoutParams) ivIcon.getLayoutParams();
		pm.leftMargin = DisplayUtil.getSize(this, 24);
		pm.rightMargin = DisplayUtil.getSize(this, 24);
		pm.bottomMargin = DisplayUtil.getSize(this, 18);
		pm.topMargin = DisplayUtil.getSize(this, 21);
		ivIcon.setLayoutParams(pm);
		tvTicketLeft = (TextView) findViewById(R.id.tv_ticket_left);
		tvTicketLeft.setTypeface(Typeface.MONOSPACE,Typeface.ITALIC);
		tvTicketLeft.setTextSize(DisplayUtil.textGetSizeSp(this, 27));
		tvName = (TextView) findViewById(R.id.tv_name);
		tvName.setTextSize(DisplayUtil.textGetSizeSp(this, 33));
		tvName.setTypeface(Typeface.MONOSPACE,Typeface.ITALIC);
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tvName.getLayoutParams();
		lp.height = DisplayUtil.getSize(this, 60);
		lp.leftMargin = DisplayUtil.getSize(this, 24);
		tvName.setLayoutParams(lp);
		ivLine = (ImageView) findViewById(R.id.iv_line);
		ivLine.setPadding(DisplayUtil.getSize(this, 12), 0, DisplayUtil.getSize(this, 12), 0);
		tvMoney = (TextView)findViewById(R.id.tv_money);
		tvMoney.setTextSize(DisplayUtil.textGetSizeSp(this, 36));
		tvMoney.setPadding(DisplayUtil.getSize(this, 24), DisplayUtil.getSize(this, 35), DisplayUtil.getSize(this, 12), DisplayUtil.getSize(this, 35));
				
		ivLineB = (TextView) findViewById(R.id.iv_lineb);
		lp= (RelativeLayout.LayoutParams) ivLineB.getLayoutParams();
		lp.bottomMargin = DisplayUtil.getSize(this, 21);
		ivLineB.setLayoutParams(lp);
		
		ivSuccess = (ImageView) findViewById(R.id.iv_success);
		ivSuccess.setPadding(DisplayUtil.getSize(this, 24), 0, DisplayUtil.getSize(this, 24), 0);
		lp= (RelativeLayout.LayoutParams) ivSuccess.getLayoutParams();
		lp.height = DisplayUtil.getSize(this, 106);
		ivSuccess.setLayoutParams(lp);
		
		tvAddress = (TextView) findViewById(R.id.tv_address);
		tvAddress.setTextSize(DisplayUtil.textGetSizeSp(this, 30));
		tvAddress.setPadding(DisplayUtil.getSize(this, 15), DisplayUtil.getSize(this, 15), 0, DisplayUtil.getSize(this, 12));
		tvAddress.setTypeface(Typeface.DEFAULT_BOLD);
		
		tvAddressDetail = (TextView) findViewById(R.id.tv_addressdetail);
		tvAddressDetail.setTextSize(DisplayUtil.textGetSizeSp(this, 27));
		tvAddressDetail.setPadding(DisplayUtil.getSize(this, 15), 0, 0, 0);

		tvTravel = (TextView) findViewById(R.id.tv_travel);
		tvTravel.setTextSize(DisplayUtil.textGetSizeSp(this, 30));
		tvTravel.setPadding(DisplayUtil.getSize(this, 15), DisplayUtil.getSize(this, 45), 0, DisplayUtil.getSize(this, 12));
		tvTravel.setTypeface(Typeface.DEFAULT_BOLD);
		
		tvTravelDetail = (TextView) findViewById(R.id.tv_traveldetail);
		tvTravelDetail.setTextSize(DisplayUtil.textGetSizeSp(this, 27));
		tvTravelDetail.setPadding(DisplayUtil.getSize(this, 15), 0, 0, 0);

		tvPhone = (TextView) findViewById(R.id.tv_phone);
		tvPhone.setTextSize(DisplayUtil.textGetSizeSp(this, 30));
		tvPhone.setPadding(DisplayUtil.getSize(this, 15), DisplayUtil.getSize(this, 45), 0, DisplayUtil.getSize(this, 12));
		tvPhone.setTypeface(Typeface.DEFAULT_BOLD);
		
		tvPhoneDetail = (TextView) findViewById(R.id.tv_phonedetail);
		tvPhoneDetail.setTextSize(DisplayUtil.textGetSizeSp(this, 27));
		tvPhoneDetail.setPadding(DisplayUtil.getSize(this, 15), 0, 0, DisplayUtil.getSize(this, 15));

		tvRecommend = (TextView) findViewById(R.id.tv_recommend);
		tvRecommend.setTextSize(DisplayUtil.textGetSizeSp(this, 27));
		LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) tvRecommend.getLayoutParams();
		llp.topMargin = DisplayUtil.getSize(this, 12);
		llp.bottomMargin = llp.bottomMargin;
		llp.height = DisplayUtil.getSize(this, 52);
		llp.width = DisplayUtil.getSize(this, 220);
		tvRecommend.setLayoutParams(llp);
		
		gvImgs = (NoScrollGridView) findViewById(R.id.gv_imgs);

		String orderinfoString = getIntent().getStringExtra("travelOrderInfo");
		if(!TextUtils.isEmpty(orderinfoString)){
			TravelOrderInfo orderInfo = new Gson().fromJson(orderinfoString, TravelOrderInfo.class);
			tvName.setText(orderInfo.getFullname() + " ");
			tvMoney.setText(Html.fromHtml("您已成功支付："+ "<font color=\"#f65c00\">"+ orderInfo.getTotal_price() + "元" + "</font>"));
			tvTicketLeft.setText("我们在列车上期待您的光临 ");
			Requester.requestTravelPayShow(handler, orderInfo.getLine_id());
			
		}
		
		findViewById(R.id.sv_scrollview).scrollTo(0, 0);
				
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester.RESPONSE_TYPE_TRAVEL_PAYOK_SHOW:
			GsonResponseObject.travelPayShowResp payShowResp = (GsonResponseObject.travelPayShowResp)msg.obj;
			if(null!=payShowResp && "0".equals(payShowResp.status)){
				findViewById(R.id.rl_content).setVisibility(View.VISIBLE);
				findViewById(R.id.rl_empty).setVisibility(View.GONE);
				tvAddressDetail.setText(payShowResp.sign_address);
				tvTravelDetail.setText(payShowResp.hotline);
				tvPhoneDetail.setText(payShowResp.complaints_hotline);
				RecommendLineGridViewAdapter adapter = new RecommendLineGridViewAdapter(this, payShowResp.recommendlist);
				gvImgs.setAdapter(adapter);
				gvImgs.setVerticalSpacing(DisplayUtil.getSize(this, 12));
				gvImgs.setHorizontalSpacing(DisplayUtil.getSize(this, 12));
			    gvImgs.setPadding(DisplayUtil.getSize(this, 12), DisplayUtil.getSize(this, 12), DisplayUtil.getSize(this, 12), DisplayUtil.getSize(this, 12));
			}else{
				findViewById(R.id.rl_content).setVisibility(View.GONE);
				findViewById(R.id.rl_empty).setVisibility(View.VISIBLE);
			}
			break;

		default:
			break;
		}
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_railtravel_order_pay_result;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		default:
			break;
		}
	}
}
