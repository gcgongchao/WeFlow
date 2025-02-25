package com.cmmobi.railwifi.activity;

import java.text.DecimalFormat;
import java.util.HashMap;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.common.tools.Info;
import com.cmmobi.railwifi.Config;
import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.alipay.PayInfo;
import com.cmmobi.railwifi.alipay.Result;
import com.cmmobi.railwifi.dao.DaoMaster;
import com.cmmobi.railwifi.dao.DaoMaster.DevOpenHelper;
import com.cmmobi.railwifi.dao.FavDao.Properties;
import com.cmmobi.railwifi.dao.DaoSession;
import com.cmmobi.railwifi.dao.TravelOrderInfo;
import com.cmmobi.railwifi.dao.TravelOrderInfoDao;
import com.cmmobi.railwifi.dialog.PromptDialog;
import com.cmmobi.railwifi.network.GsonResponseObject;
import com.cmmobi.railwifi.network.Requester;
import com.cmmobi.railwifi.utils.CmmobiClickAgentWrapper;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.cmmobi.railwifi.utils.RailTravelInfo;
import com.google.gson.Gson;

public class RailTravelOrderDetailActivity extends TitleRootActivity {

	private final static String TAG = "RailTravelOrderDetailActivity";
	private ImageView ivIcon;
	private TextView tvTicketLeft;
	private TextView tvName;
	private TextView tvMoney;
	private TextView tvVisa;
	private TextView tvVisaDetail;
	private ImageButton btnPay;
	private ImageButton btnPayLater;
	private TextView tvNotice;
	private ImageView ivLine;
	private String price;
	private GsonResponseObject.travelLineInfoResp lineInfoResp;
	private RailTravelInfo travelInfo;
	private String lineid;
	private TravelOrderInfo orderInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitleText("订单详情");
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
		tvTicketLeft.setTextSize(DisplayUtil.textGetSizeSp(this, 30));
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
				
		tvNotice = (TextView) findViewById(R.id.tv_notice);
		tvNotice.setTextSize(DisplayUtil.textGetSizeSp(this, 27));
		tvVisa = (TextView) findViewById(R.id.tv_visa);
		tvVisa.setTextSize(DisplayUtil.textGetSizeSp(this, 33));
		tvVisa.setPadding(DisplayUtil.getSize(this, 24), DisplayUtil.getSize(this, 12), 0, 0);
		tvVisaDetail = (TextView) findViewById(R.id.tv_visa_detail);
		tvVisaDetail.setTextSize(DisplayUtil.textGetSizeSp(this, 27));
		tvVisaDetail.setPadding(DisplayUtil.getSize(this, 24), 0, DisplayUtil.getSize(this, 24), 0);
		tvVisaDetail.setLineSpacing(0, (float)1.5);
		
		btnPay = (ImageButton) findViewById(R.id.btn_pay);
		btnPay.setPadding(DisplayUtil.getSize(this, 21), DisplayUtil.getSize(this, 21), DisplayUtil.getSize(this, 21), DisplayUtil.getSize(this, 21));
		btnPayLater = (ImageButton) findViewById(R.id.btn_pay_later);
		btnPayLater.setPadding(DisplayUtil.getSize(this, 21), DisplayUtil.getSize(this, 21), DisplayUtil.getSize(this, 21), DisplayUtil.getSize(this, 21));
		btnPay.setOnClickListener(this);
		btnPayLater.setOnClickListener(this);

		String lineinfo = getIntent().getStringExtra("lineinfo");
		if(!TextUtils.isEmpty(lineinfo)){
			lineInfoResp = new Gson().fromJson(lineinfo, GsonResponseObject.travelLineInfoResp.class);
			tvName.setText(lineInfoResp.fullname + " ");
			tvTicketLeft.setText(Html.fromHtml("当前余票："+ "<font color=\"#f65c00\">"+ lineInfoResp.tickets + "张" + "</font>") + " ");
			tvVisaDetail.setText(lineInfoResp.visanotice);
			String orderinfo = getIntent().getStringExtra("railtravelinfo");
			if(!TextUtils.isEmpty(orderinfo)){
				travelInfo = new Gson().fromJson(orderinfo, RailTravelInfo.class);
				DecimalFormat df = new DecimalFormat("############.##");
				try {
					if (Integer.parseInt(travelInfo.childTicket) != 0) {
						price = df.format(Integer.parseInt(travelInfo.adultTicket)* Float.parseFloat(travelInfo.adultPrice) + Integer.parseInt(travelInfo.childTicket)* Float.parseFloat(travelInfo.kidPrice));
					} else {
						price = df.format(Integer.parseInt(travelInfo.adultTicket)* Float.parseFloat(travelInfo.adultPrice));
					}
					tvMoney.setText("本单消费：" + price + "元");
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}
		}
		
		lineid = getIntent().getStringExtra("lineid");
				
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester.RESPONSE_TYPE_TRAVEL_PAY:
			GsonResponseObject.travePayResp payResp = (GsonResponseObject.travePayResp)msg.obj;
			if(null!=payResp && "0".equals(payResp.status) && orderInfo !=null){
				orderInfo.setOrder_num(payResp.order_no);
				//调用支付宝接口返回结果
				if (Config.IS_USE_REAL_PRICE) {
					PayInfo.pay(this, handler, orderInfo.getFullname(), orderInfo.getIntroduction(), orderInfo.getTotal_price(), orderInfo.getOrder_num());
				} else {
					PayInfo.pay(this, handler, orderInfo.getFullname(), orderInfo.getIntroduction(), "0.01", orderInfo.getOrder_num());
				}
			}else{
				//弹窗
				PromptDialog.Dialog(this, "支付失败", "当前网络状态不佳", "稍后再试", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						addToCollection();
					}
				});	
				
			}
			break;
		case PayInfo.RESPONSE_ALIPAY_RESULT:
			if (msg.obj != null) {
				Result result = (Result) msg.obj;
				if (result != null) {
					if (result.isTransactionSecc()) {
						orderInfo.setIspaid("1");
					} else {
						orderInfo.setIspaid("0");
					}
					Log.d(TAG,"payInfo = " + result.getPayInfo());
					DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "railwifidb", null);
					SQLiteDatabase db = helper.getWritableDatabase();
					DaoMaster daoMaster = new DaoMaster(db);
					DaoSession daoSession = daoMaster.newSession();
					TravelOrderInfoDao travelOrderInfoDao = daoSession.getTravelOrderInfoDao();
					travelOrderInfoDao.insert(orderInfo);
					db.close();
					if (result.isTransactionSecc()) {
						Log.d(TAG,"isTransactionSecc succ");
						Intent intent = new Intent(this, RailTravelOrderPayResultActivity.class);
						intent.putExtra("travelOrderInfo", new Gson().toJson(orderInfo, TravelOrderInfo.class));
						startActivity(intent); 
						this.setResult(RESULT_OK);
						this.finish();
					}else{
						//弹窗
						if (!result.isUserCancle()) {
							PromptDialog.Dialog(this, "支付失败", "当前网络状态不佳", "稍后再试", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									Intent intent = new Intent(RailTravelOrderDetailActivity.this, RailTravelOrderHistoryAcitivity.class);
							        startActivity(intent); 
							        setResult(RESULT_OK);
							        finish();
								}
							});	
						} else {
							Intent intent = new Intent(RailTravelOrderDetailActivity.this, RailTravelOrderHistoryAcitivity.class);
					        startActivity(intent); 
					        setResult(RESULT_OK);
					        finish();
						}
					}
					
				}
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
		return R.layout.activity_railtravel_order_detail;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if(travelInfo !=null && lineInfoResp!=null && !TextUtils.isEmpty(lineid)){
			orderInfo = new TravelOrderInfo(null, Info.getDevId(this), Info.getDevId(this), lineInfoResp.tag, lineInfoResp.color, lineInfoResp.name, lineInfoResp.fullname, lineInfoResp.introduction, lineid, "" + price, travelInfo.adultTicket, travelInfo.childTicket, travelInfo.time, travelInfo.name, travelInfo.cellPhone, travelInfo.emailAddr, travelInfo.IdCardfNum, System.currentTimeMillis()+"", "", "0", lineInfoResp.out_img_path);
		}
		HashMap<String, String> label = new HashMap<String, String>();
		
		
		switch (v.getId()) {
		case R.id.btn_pay:
			CmmobiClickAgentWrapper.onEvent(this, "t_tra_order", "1");
			if(travelInfo != null && !TextUtils.isEmpty(lineid)){
				Requester.requestTravelPay(handler, Info.getDevId(this), lineid , travelInfo.time, "" + price, travelInfo.adultTicket, travelInfo.childTicket, travelInfo.name, travelInfo.cellPhone, travelInfo.emailAddr, travelInfo.IdCardfNum);
			}
			break;
		case R.id.btn_pay_later:
			CmmobiClickAgentWrapper.onEvent(this, "t_tra_order", "2");
			addToCollection();
			break;
		default:
			break;
		}		
		
	}
	
	private void addToCollection(){
		if(orderInfo !=null){
			DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "railwifidb", null);
	        SQLiteDatabase db = helper.getWritableDatabase();
	        DaoMaster daoMaster = new DaoMaster(db);
	        DaoSession daoSession = daoMaster.newSession();
	        TravelOrderInfoDao travelOrderInfoDao = daoSession.getTravelOrderInfoDao();
	        travelOrderInfoDao.insert(orderInfo);
	        db.close();
	        Intent intent = new Intent(this, RailTravelOrderHistoryAcitivity.class);
	        startActivity(intent); 
	        this.setResult(RESULT_OK);
	        this.finish();
		}
	}
}
