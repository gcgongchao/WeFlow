package com.cmmobi.looklook.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.prompt.Prompt;

/**
 * 修改登陆密码界面
 * 
 * @author youtian
 * 
 */
public class SettingLoginPwdActivity extends ZActivity {
	
	private ImageView iv_back;
	private TextView tv_commit;
	private EditText et_currentpwd;
	private EditText et_newpwd;
	private EditText et_repeatnewpwd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_loginpwd);
		
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		iv_back.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SettingLoginPwdActivity.this,LookLookActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				SettingLoginPwdActivity.this.finish();
				return false;
			}
		});
		tv_commit = (TextView) findViewById(R.id.tv_commit);
		tv_commit.setOnClickListener(this);
		
		et_currentpwd = (EditText) findViewById(R.id.et_currentpwd);
		et_newpwd = (EditText) findViewById(R.id.et_newpwd);
		et_repeatnewpwd = (EditText) findViewById(R.id.et_repeatnewpwd);
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		CmmobiClickAgentWrapper.onResume(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		CmmobiClickAgentWrapper.onPause(this);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester3.RESPONSE_TYPE_PASSWORD_CHANGE:
			try {
				ZDialog.dismiss();
				GsonResponse3.passwordChangeResponse res = (GsonResponse3.passwordChangeResponse) msg.obj;
				if(res != null){
					if(res.status.equals("0")){
						Prompt.Dialog(this, false, "提示", "密码修改成功", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								SettingLoginPwdActivity.this.finish();
							}
						});
					}/*else if(res.status.equals("200600")){
						Prompt.Dialog(this, false, "提示", Constant.CRM_STATUS[Integer.parseInt(res.crm_status)], null);
					}*/else{
						Prompt.Dialog(this, false, "提示", "操作失败，请稍后再试", null);
					}
				}else{
					Prompt.Dialog(this, false, "提示", "操作失败，网络不给力", null);
				}
				
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_back:
			this.onBackPressed();
			break;
		case R.id.tv_commit:
			String currentpwd = et_currentpwd.getText().toString().trim();
			String newpwd = et_newpwd.getText().toString().trim();
			String repeatnewpwd = et_repeatnewpwd.getText().toString().trim();
			if(currentpwd==null || newpwd==null || repeatnewpwd==null || currentpwd.equals("") || newpwd.equals("") || repeatnewpwd.equals("")){
				Prompt.Dialog(this, false, "提示", "输入不能为空", null);
			}else if(!newpwd.equals(repeatnewpwd)){
				Prompt.Dialog(this, false, "提示", "两次输入的新密码不一致", null);
			}else if(newpwd.equals(currentpwd)){
				Prompt.Dialog(this, false, "提示", "新密码不能和旧密码一致，需要重新输入", null);
			}else if(newpwd.length() <6 || newpwd.length() >16){
				Prompt.Dialog(this, false, "提示", "新密码的长度为6~16个字符", null);
			}else {
				ZDialog.show(R.layout.progressdialog, false, true, this);
				Requester3.passwordChange(handler, currentpwd, newpwd, "1");
			}
			break;
		default:
			break;
		}
		
	}
}
