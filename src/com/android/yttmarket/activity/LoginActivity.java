package com.android.yttmarket.activity;


import cn.bmob.v3.Bmob;
import cn.bmob.v3.listener.SaveListener;

import com.android.bmob.signup.YTTUser;
import com.android.bmob.signup.SignUpActivity;
import com.android.yttmarket.R;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.demo.AccessTokenKeeper;
import com.sina.weibo.sdk.demo.Constants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.User;
import com.sina.weibo.sdk.utils.LogUtil;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ToggleButton;

public class LoginActivity extends Activity implements OnClickListener, OnCheckedChangeListener{
	
	private ToggleButton mTgBtnShowPsw;
	private EditText mEditPsw;
	private EditText mEditUid;
	private ImageView mBtnClearUid;
	private ImageView mBtnClearPsw;
	
	private static final String TAG = "weibosdk";
	/**通过Bmob登录*/
	private final int LOG_BY_BMOB = 1;
	/**通过微博登录*/
	private final int LOG_BY_WEIBO = 2;

    /** 显示认证后的信息，如 AccessToken */
    private AuthInfo mAuthInfo;
    
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;

    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private SsoHandler mSsoHandler;
    
    /** 用户信息接口 */
    private UsersAPI mUsersAPI;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Bmob.initialize(this, "5dd615dd69969d14a905067dcd723f20");
		initUI();
		setOnListener();
		initUid();
	}
	
	/**
	 * 初始化记住的用户名
	 */
	private void initUid() {
		SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
		String uid = sp.getString("uid", "");
		mEditUid.setText(uid);
	}

	private void setOnListener() {
		mEditUid.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (mEditUid.getText().toString().length() > 0) {
					mBtnClearUid.setVisibility(View.VISIBLE);
					if (mEditPsw.getText().toString().length() >0) {
						mBtnLogin.setEnabled(true);
					} else {
						mBtnLogin.setEnabled(false);
					}
				} else {
					mBtnLogin.setEnabled(false);
					mBtnClearUid.setVisibility(View.INVISIBLE);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		mEditPsw.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (mEditPsw.getText().toString().length() > 0) {
					mBtnClearPsw.setVisibility(View.VISIBLE);
					if (mEditUid.getText().toString().length() > 0) {
						mBtnLogin.setEnabled(true);
					} else {
						mBtnLogin.setEnabled(false);
					}
				} else {
					mBtnLogin.setEnabled(false);
					mBtnClearPsw.setVisibility(View.INVISIBLE);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		mBtnLogin.setOnClickListener(this);
		mBtnClearUid.setOnClickListener(this);
		mBtnClearPsw.setOnClickListener(this);
		mTgBtnShowPsw.setOnCheckedChangeListener(this);
		findViewById(R.id.img_back).setOnClickListener(this);
//		findViewById(R.id.btn_login_wb).setOnClickListener(this);
		findViewById(R.id.tv_quick_sign_up).setOnClickListener(this);
	}

	private void initUI() {
		mBtnLogin = (Button) findViewById(R.id.btn_login);
		mEditUid = (EditText) findViewById(R.id.edit_uid);
		mEditPsw = (EditText) findViewById(R.id.edit_psw);
		mBtnClearUid = (ImageView) findViewById(R.id.img_login_clear_uid);
		mBtnClearPsw = (ImageView) findViewById(R.id.img_login_clear_psw);
		mTgBtnShowPsw = (ToggleButton) findViewById(R.id.tgbtn_show_psw);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login:	//登录
			login();
			break;
		case R.id.img_back:	//返回
			finish();
			break;
//		case R.id.btn_login_wb:	//微博登录
//			loginWB();
//			break;
		case R.id.img_login_clear_uid:	//清除用户名
			clearText(mEditUid);
			break;
		case R.id.img_login_clear_psw:	//清除密码
			clearText(mEditPsw);
			break;
		case R.id.tv_quick_sign_up:	//快速注册
			startActivity(new Intent(this, SignUpActivity.class));
			break;

		default:
			break;
		}
	}
	
	/**
	 * 微博登录
	 */
	private void loginWB() {
		mAuthInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        mSsoHandler = new SsoHandler(this, mAuthInfo);
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        if (mAccessToken.isSessionValid()) {
        	getUserData();
        } else {
        	// 调用新浪微博登录Activity
        	mSsoHandler.authorize(new AuthListener());
        }
	}
	
	/**
	 * 获取用户信息
	 */
	protected void getUserData() {
		if (mAccessToken != null) {
			// 获取用户信息接口
			mUsersAPI = new UsersAPI(this, Constants.APP_KEY, mAccessToken);
			 long uid = Long.parseLong(mAccessToken.getUid());
             mUsersAPI.show(uid, mListener);
		}
	}
	
	/**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                // 调用 User#parse 将JSON串解析成User对象
                User user = User.parse(response);
                if (user != null) {
                    Toast.makeText(LoginActivity.this, 
                            "登录成功", 
                            Toast.LENGTH_SHORT).show();
                    //通过微博登录
                    setSP(LOG_BY_WEIBO);
                    Intent data = new Intent();
                    data.putExtra("screen_name", user.screen_name);
                    data.putExtra("profile_image_url", user.profile_image_url);
					setResult(com.android.yttmarket.constants.Constants.INTENT_KEY.LOGIN_RESULT_SUCCESS_CODE, data );
//                	UILUtils.displayImage(LoginActivity.this, user.profile_image_url, mImgUserIcon);
					finish();
                } else {
                    Toast.makeText(LoginActivity.this, "无法获取用户信息", Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            LogUtil.e(TAG, e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(LoginActivity.this, info.toString(), Toast.LENGTH_LONG).show();
        }
    };
	private Button mBtnLogin;
    
    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     * 
     * @see {@link Activity#onActivityResult}
     */
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     *    该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {
        
        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(LoginActivity.this, mAccessToken);
                Toast.makeText(LoginActivity.this, 
                        R.string.weibosdk_demo_toast_auth_success, Toast.LENGTH_SHORT).show();
                loginWB();
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                String message = getString(R.string.weibosdk_demo_toast_auth_failed);
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(LoginActivity.this, 
                    R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(LoginActivity.this, 
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
	/**
	 * 清空控件文本
	 * @param mEditUid2
	 */
	private void clearText(EditText edit) {
		edit.setText("");
	}
	
	/**
	 * 将登录途径保存到SharedPreferences，1为Bmob，2为微博
	 */
	private void setSP(int type) {
		//保存当前位置到SharedPreferences
		SharedPreferences sp = this
				.getSharedPreferences("login_type", Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putInt("login_type", type);
		edit.commit();
	}

	/**
	 * 登录按钮
	 */
	private void login() {
		String userName = mEditUid.getText().toString();
		String pwd = mEditPsw.getText().toString();

		final YTTUser user = new YTTUser();
		user.setUsername(userName);
		user.setPassword(pwd);
		user.login(this, new SaveListener() {
			public void onSuccess() {
				//将用户名保存到Preference
				SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
				Editor edit = sp.edit();
				edit.putString("uid", mEditUid.getText().toString());
				edit.commit();
				
				String id2 = user.getObjectId();
				Toast.makeText(LoginActivity.this, "登录成功",
						Toast.LENGTH_SHORT).show();
				//通过Bmob登录
				setSP(LOG_BY_BMOB);
				Intent intent = new Intent();
				intent.putExtra("uid", id2);
				setResult(com.android.yttmarket.constants.Constants.INTENT_KEY.LOGIN_RESULT_SUCCESS_CODE, intent );
				LoginActivity.this.finish();
				
			}

			public void onFailure(int arg0, String arg1) {
				Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_LONG)
						.show();
			}
		});
	}

	/**
	 * 是否显示密码
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			//显示密码
			mEditPsw.setTransformationMethod(
					HideReturnsTransformationMethod.getInstance());
		} else {
			//隐藏密码
			mEditPsw.setTransformationMethod(
					PasswordTransformationMethod.getInstance());
		}
	}

}
