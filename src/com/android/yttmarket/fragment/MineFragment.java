package com.android.yttmarket.fragment;

import com.android.yttmarket.activity.AddressManagementActivity;
import com.android.yttmarket.activity.BoxActivity;
import com.android.yttmarket.activity.FavorActivity;
import com.android.yttmarket.activity.HistoryActivity;
import com.android.yttmarket.activity.LoginActivity;
import com.android.yttmarket.activity.MainActivity;
import com.android.yttmarket.activity.MessageCenterActivity;
import com.android.yttmarket.activity.MoreActivity;
import com.android.yttmarket.activity.OrdersActivity;
import com.android.yttmarket.activity.PurseActivity;
import com.android.yttmarket.activity.WebActivity;
import com.android.yttmarket.adapter.Adapter_GridView;
import com.android.yttmarket.constants.Constants;
import com.android.yttmarket.R;
import com.lib.uil.UILUtils;
import com.umeng.fb.FeedbackAgent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class MineFragment extends Fragment implements OnClickListener {
	
	
	private GridView my_gridView_user;
	private Adapter_GridView adapter_GridView;
	//资源文件
	private int[] pic_path={R.drawable.user_3,R.drawable.user_4,R.drawable.user_5};
	private View layout;
	private View mViewNotLogined;
	private View mViewLogined;
	private TextView mTvUid;
	private ImageView mImgUserIcon;
	private String uid;
	private String icon;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (layout != null) {
			initLogin();
			// 防止多次new出片段对象，造成图片错乱问题
			return layout;
		}
		layout = inflater.inflate(R.layout.fragment_mine, container, false);
		initView();
		setOnListener();
		initLogin();
		return layout;
	}

	/**
	 * 初始化登录信息
	 * 
	 * @param activity
	 * @param isLogined
	 */
	private void initLogin() {
		MainActivity activity = (MainActivity) getActivity();
		boolean isLogined = activity.getLogined();
		if (isLogined) {
			// 读取登录类型
			SharedPreferences sp = activity.getSharedPreferences("login_type",
					Context.MODE_PRIVATE);
			int type = sp.getInt("login_type", 0);
			switch (type) {
			case 1: // 通过Bmob登录
				break;
			case 2: // 通过微博登录
				icon = activity.getIcon();
				UILUtils.displayImage(getActivity(), icon, mImgUserIcon);
				break;

			default:
				break;
			}
			uid = activity.getUid();
			mViewNotLogined.setVisibility(View.GONE);
			mViewLogined.setVisibility(View.VISIBLE);
			mTvUid.setText(uid);
		} else {
			mViewNotLogined.setVisibility(View.VISIBLE);
			mViewLogined.setVisibility(View.GONE);
		}
	}

	private void setOnListener() {
		layout.findViewById(R.id.layout_mine_order).setOnClickListener(this);
		layout.findViewById(R.id.layout_mine_wallet).setOnClickListener(this);
		layout.findViewById(R.id.layout_mine_messages).setOnClickListener(this);
		layout.findViewById(R.id.personal_login_button)
				.setOnClickListener(this);
		
		layout.findViewById(R.id.mine_box).setOnClickListener(this);
		layout.findViewById(R.id.layout_mine_feedback).setOnClickListener(this);
		layout.findViewById(R.id.layout_mine_collects).setOnClickListener(this);
		layout.findViewById(R.id.layout_mine_address_management).setOnClickListener(this);
		
//		layout.findViewById(R.id.layout_mine_history).setOnClickListener(this);
//		layout.findViewById(R.id.layout_mine_appoint).setOnClickListener(this);
//		layout.findViewById(R.id.layout_mine_account_center).setOnClickListener(this);
//		layout.findViewById(R.id.layout_mine_service_manager).setOnClickListener(this);
//		layout.findViewById(R.id.layout_mine_discuss).setOnClickListener(this);
//		layout.findViewById(R.id.layout_mine_android_my_jd_assitant)
//				.setOnClickListener(this);
		layout.findViewById(R.id.tv_more).setOnClickListener(this);
	}

	private void initView() {
		mViewNotLogined = layout.findViewById(R.id.layout_not_logined);
		mViewLogined = layout.findViewById(R.id.layout_logined);
		mTvUid = (TextView) layout.findViewById(R.id.tv_login_uid);
		mImgUserIcon = (ImageView) layout.findViewById(R.id.default_user_icon);
		
		my_gridView_user=(GridView)layout.findViewById(R.id.gridView_user);
    	my_gridView_user.setSelector(new ColorDrawable(Color.TRANSPARENT));
    	adapter_GridView=new Adapter_GridView(getActivity(), pic_path);
    	my_gridView_user.setAdapter(adapter_GridView);
    	my_gridView_user.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				//进入本机拥有传感器界面
				Toast.makeText(getActivity(), "点击了第"+position+"个位置",Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		// 将layout从父组件中移除
		ViewGroup parent = (ViewGroup) layout.getParent();
		parent.removeView(layout);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Constants.INTENT_KEY.LOGIN_REQUEST_CODE) {
			if (resultCode == Constants.INTENT_KEY.LOGIN_RESULT_SUCCESS_CODE) {
				SharedPreferences sp = getActivity().getSharedPreferences(
						"login_type", Context.MODE_PRIVATE);
				int type = sp.getInt("login_type", 0);
				String uid = "";
				String icon = "";
				switch (type) {
				case 1:
					uid = data.getStringExtra("uid");
					break;
				case 2:
					uid = data.getStringExtra("screen_name");
					icon = data.getStringExtra("profile_image_url");
					UILUtils.displayImage(getActivity(), icon, mImgUserIcon);
					break;

				default:
					break;
				}
				mTvUid.setText(uid);
				mViewNotLogined.setVisibility(View.GONE);
				mViewLogined.setVisibility(View.VISIBLE);
				// 将登录结果设置给MainActivity
				MainActivity activity = (MainActivity) getActivity();
				activity.setIsLogined(true, uid, icon);
			}
		} else if (requestCode == Constants.INTENT_KEY.REQUEST_MOREACTIVITY) {
			initLogin();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.personal_login_button: // 登录
			login();
			break;
		case R.id.layout_mine_feedback: // 意见反馈
			new FeedbackAgent(getActivity()).startFeedbackActivity();
			break;
//		case R.id.layout_mine_android_my_jd_assitant: // 贴心服务
//			Intent intent = new Intent(getActivity(), WebActivity.class);
//			intent.putExtra("direction", 5);
//			startActivity(intent);
//			break;
		case R.id.tv_more: // 更多
			Intent intent2 = new Intent(getActivity(), MoreActivity.class);
			startActivityForResult(intent2,
					Constants.INTENT_KEY.REQUEST_MOREACTIVITY);
			break;
		case R.id.layout_mine_order: // 全部订单
			startActivity(new Intent(getActivity(), OrdersActivity.class));
			break;
		case R.id.layout_mine_wallet: // 我的钱包
			startActivity(new Intent(getActivity(), PurseActivity.class));
			break;
		case R.id.layout_mine_messages: // 我的消息
			startActivity(new Intent(getActivity(), MessageCenterActivity.class));
			break;
		case R.id.layout_mine_collects: // 我的关注
			startActivity(new Intent(getActivity(), FavorActivity.class));
			break;
		case R.id.mine_box: // 百宝箱
			startActivity(new Intent(getActivity(), BoxActivity.class));
			break;
		case R.id.layout_mine_address_management: // 收货地址管理
			startActivity(new Intent(getActivity(), AddressManagementActivity.class));
			break;
			
//		case R.id.layout_mine_history: // 浏览记录
//			startActivity(new Intent(getActivity(), HistoryActivity.class));
//			break;
//		case R.id.layout_mine_appoint: // 我的预约
//			Intent intent3 = new Intent(getActivity(), WebActivity.class);
//			intent3.putExtra("direction", 9);
//			startActivity(intent3);
//			break;
//		case R.id.layout_mine_account_center: // 账户与安全
//			Intent intent4 = new Intent(getActivity(), WebActivity.class);
//			intent4.putExtra("direction", 12);
//			startActivity(intent4);
//			break;
//		case R.id.layout_mine_service_manager: // 服务管家
//			Intent intent5 = new Intent(getActivity(), WebActivity.class);
//			intent5.putExtra("direction", 10);
//			startActivity(intent5);
//			break;
//		case R.id.layout_mine_discuss: // 评价商品
//			Intent intent6 = new Intent(getActivity(), WebActivity.class);
//			intent6.putExtra("direction", 11);
//			startActivity(intent6);
//			break;
		case R.id.layout_more: // 评价商品
			Intent intent6 = new Intent(getActivity(), WebActivity.class);
			intent6.putExtra("direction", 11);
			startActivity(intent6);
			break;

		default:
			break;
		}
	}

	private void login() {
		Intent intent = new Intent(getActivity(), LoginActivity.class);
		startActivityForResult(intent, Constants.INTENT_KEY.LOGIN_REQUEST_CODE);
	}

}
