package com.ssiot.remote;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.ssiot.fish.HeadActivity;
import com.ssiot.fish.R;

public class RegisterAct extends HeadActivity{
	private static final String tag = "RegisterAct";
	EditText mPhoneEdit;
	EditText mVerifyEdit;
	View mSmsBtn;
	EditText mPswdEdit;
	EditText mPswdEdit2;
	EditText mInviteEdit;
	CheckBox mLicenceBox;
	View mRegisterBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mobile_login_regesiter_layout);//act_register
		initViews();
	}
	
	private void initViews(){
		mPhoneEdit = (EditText) findViewById(R.id.et_phone_num);
		mSmsBtn = findViewById(R.id.tv_send_verify_code);
		mVerifyEdit = (EditText) findViewById(R.id.et_verify_code);
		mPswdEdit = (EditText) findViewById(R.id.et_register_password);
		mPswdEdit2 = (EditText) findViewById(R.id.et_register_password_2);
		mRegisterBtn = findViewById(R.id.btn_submit);
		
	}
}