package com.android.bmob.signup;

import java.util.HashMap;
import java.util.Random;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.listener.SaveListener;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

import com.android.yttmarket.R;

import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivity extends Activity implements OnClickListener {

	private EditText mEditUser;
	private EditText mEditPsw;
	private Button mBtnSignUp;
	private boolean ready;

	private boolean isBtnChecked = true;
	private EditText mEditPswVal;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		// 初始化Bmob
//		 Bmob.initialize(this, "5dd615dd69969d14a905067dcd723f20");
		// 短信初始化
		SMSSDK.initSDK(this, "e7099c1a7f00", "0afa8e9d3f9ec2280d62d95875eb7cff");
		
		
		mBtnSignUp = (Button) findViewById(R.id.btn_sign_up);
		mEditUser = (EditText) findViewById(R.id.edit_uid);
		mEditPsw = (EditText) findViewById(R.id.edit_psw);
		mEditPswVal = (EditText) findViewById(R.id.edit_psw_val);
		CheckBox btnCheck = (CheckBox) findViewById(R.id.btn_check);
		TextWatcher watcher = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				setSignable();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		};
		mEditUser.addTextChangedListener(watcher);
		mEditPsw.addTextChangedListener(watcher);
		mEditPswVal.addTextChangedListener(watcher);
		btnCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				isBtnChecked = isChecked;
				setSignable();
			}

		});
		mBtnSignUp.setOnClickListener(this);
		findViewById(R.id.img_back).setOnClickListener(this);
	}

	/**
	 * 是否符合注册条件
	 */
	private void setSignable() {
		if (isBtnChecked) {

			if (mEditUser.getText().toString().length() > 5
					&& mEditPsw.getText().toString().length() > 5
					&& mEditPswVal.getText().toString().length() > 5) {
				mBtnSignUp.setEnabled(true);
			} else {
				mBtnSignUp.setEnabled(false);
			}
		} else {
			mBtnSignUp.setEnabled(false);
		}
	}

	private void signup() {
		String userName = mEditUser.getText().toString();
		String pwd = mEditPsw.getText().toString();
		String pwdVal = mEditPswVal.getText().toString();

		if (!pwd.equals(pwdVal)) {
			mEditPswVal.setText("");
			mEditPswVal.setError("两次输入的密码不一致");
			return;
		}
		YTTUser user = new YTTUser();
		user.setUsername(userName);
		user.setPassword(pwd);
		user.signUp(this, new SaveListener() {
			public void onSuccess() {
				Toast.makeText(SignUpActivity.this, "注册成功， 请登录",
						Toast.LENGTH_LONG).show();
				finish();
			}

			public void onFailure(int arg0, String arg1) {
				mEditUser.setError("用户名已存在");
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_back:
			finish();
			break;
		case R.id.btn_sign_up:
			// signup();
			sign();
			break;

		default:
			break;
		}
	}

	private void sign() {
		// 打开注册页面
		RegisterPage registerPage = new RegisterPage();
		registerPage.setRegisterCallback(new EventHandler() {
			public void afterEvent(int event, int result, Object data) {
				// 解析注册结果
				if (result == SMSSDK.RESULT_COMPLETE) {
					@SuppressWarnings("unchecked")
					HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
					String country = (String) phoneMap.get("country");
					String phone = (String) phoneMap.get("phone");

					// 提交用户信息
					registerUser(country, phone);
				}
			}
		});
		registerPage.show(this);

	}

	// 提交用户信息
	private void registerUser(String country, String phone) {
		Random rnd = new Random();
		int id = Math.abs(rnd.nextInt());
		String uid = String.valueOf(id);
		String nickName = "SmsSDK_User_" + uid;
		String avatar = String.valueOf(id);
		SMSSDK.submitUserInfo(uid, nickName, avatar, country, phone);
	}

	protected void onDestroy() {
		if (ready) {
			// 销毁回调监听接口
			SMSSDK.unregisterAllEventHandler();
		}
		super.onDestroy();
	}

}
