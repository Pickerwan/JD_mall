package com.android.yttmarket.activity;

import java.lang.reflect.Method;

import com.android.yttmarket.constants.Constants;
import com.android.yttmarket.dialogfragment.ExitDialogFragment;
import com.android.yttmarket.dialogfragment.LightCtrlDialogFragment;
import com.android.yttmarket.fragment.CartFragment;
import com.android.yttmarket.fragment.CategoryFragment;
import com.android.yttmarket.fragment.FragmentMsg;
import com.android.yttmarket.fragment.HomeFragment;
import com.android.yttmarket.fragment.MineFragment;
import com.android.yttmarket.utils.DBUtils;
import com.android.yttmarket.R;
import com.umeng.fb.FeedbackAgent;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	private FragmentTabHost mTabHost;
	private Class[] mFragments = new Class[] { CategoryFragment.class,
			FragmentMsg.class, HomeFragment.class, CartFragment.class,
			MineFragment.class };
	private int[] mTabSelectors = new int[] {
			R.drawable.main_bottom_tab_category,
			R.drawable.main_bottom_tab_find, R.drawable.main_bottom_tab_home,
			R.drawable.main_bottom_tab_cart, R.drawable.main_bottom_tab_mine };
	private String[] mTabSpecs = new String[] { "category", "find", "home",
			"cart", "mine" };

	private boolean isBack; // 是否连续点击返回键
	private boolean isLogined; // 是否已登录
	private String uid; // 用户名
	private String icon; // 用户头像地址
	private MyReceiver receiver;
	private boolean isFromFavor; // 是否从关注界面跳转来
	private boolean isFromDetail; // 是否从详情界面跳转来
	private TextView mTvNumInCart; // 购物车商品数

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		addTab();
		initInCartNum();

	}

	@Override
	protected void onStart() {
		super.onStart();
		// 注册广播接收者
		receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter(
				Constants.BROADCAST_FILTER.FILTER_CODE);
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 如果是从关注界面跳转来，则转到HomePage
		// 不能在onReceive()中直接设置，否则会出现IllegalStateException: Can not perform this
		// action after onSaveInstanceState
		if (isFromFavor) {
			mTabHost.setCurrentTab(2);
			isFromFavor = false;
		} else if (isFromDetail) {
			mTabHost.setCurrentTab(3);
			isFromDetail = false;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	/**
	 * 购物车中商品数量
	 */
	private void initInCartNum() {
		int num = DBUtils.getInCartNum();
		if (num > 0) {
			mTvNumInCart.setVisibility(View.VISIBLE);
			mTvNumInCart.setText("" + num);
		} else {
			mTvNumInCart.setVisibility(View.INVISIBLE);
		}
	}

	private void addTab() {
		for (int i = 0; i < 5; i++) {
			View tabIndicator = getLayoutInflater().inflate(
					R.layout.tab_indicator, null);
			ImageView imageView = (ImageView) tabIndicator
					.findViewById(R.id.imageView1);
			imageView.setImageResource(mTabSelectors[i]);
			if (i == 3) {
				mTvNumInCart = (TextView) tabIndicator
						.findViewById(R.id.textView1);
			}
			mTabHost.addTab(
					mTabHost.newTabSpec(mTabSpecs[i])
							.setIndicator(tabIndicator), mFragments[i], null);
			mTabHost.setCurrentTab(2);
		}
	}

	/**
	 * 打开二维码扫描界面，用于给片段调用
	 */
	// public void scanQRCode() {
	// // 打开扫描界面扫描条形码或二维码
	// Intent openCameraIntent = new Intent(this, CaptureActivity.class);
	// startActivityForResult(openCameraIntent, 0);
	// }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		// 处理扫描结果（在界面上显示）
		if (resultCode == Activity.RESULT_OK) {
			Bundle bundle = data.getExtras();
			String scanResult = bundle.getString("result");
			// TODO 处理扫描结果
			Toast.makeText(this, scanResult, Toast.LENGTH_LONG).show();
			if (scanResult.indexOf("http//") != -1) {
				Uri uri = Uri.parse(scanResult);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}
		}
	}

	/**
	 * 分类
	 */
	public void activeCategory() {
		mTabHost.setCurrentTab(0);
	}

	public void activeCart() {
		mTabHost.setCurrentTab(3);
	}

	public void activeMine() {
		mTabHost.setCurrentTab(4);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			// 若当前不在主页，则先返回主页
			if (mTabHost.getCurrentTab() != 2) {
				mTabHost.setCurrentTab(2);
				return false;
			}
			// 双击返回桌面，默认返回true，调用finish()
			if (!isBack) {
				isBack = true;
				Toast.makeText(this, "再按一次返回键回到桌面", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addCategory(Intent.CATEGORY_HOME);
				startActivity(intent);
				return false;
			}

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 由片段调用，设置登录信息
	 * 
	 * @param isLogined
	 * @param uid
	 * @param icon
	 */
	public void setIsLogined(boolean isLogined, String uid, String icon) {
		this.isLogined = isLogined;
		this.uid = uid;
		this.icon = icon;
	}

	/**
	 * 由片段调用，获取是否已登录
	 * 
	 * @return
	 */
	public boolean getLogined() {
		return isLogined;
	}

	/**
	 * 由片段调用，获取用户名
	 * 
	 * @return
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * 由片段调用，获取用户头像地址
	 * 
	 * @return
	 */
	public String getIcon() {
		return icon;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		setIconEnable(menu, true);
		MenuItem item = menu.add(0, 1, 1, "设置");
		item.setIcon(R.drawable.main_menu_setup);
		MenuItem item2 = menu.add(0, 2, 2, "浏览记录");
		item2.setIcon(R.drawable.main_menu_history);
		MenuItem item3 = menu.add(0, 3, 3, "意见反馈");
		item3.setIcon(R.drawable.main_menu_feedback);
		MenuItem item4 = menu.add(0, 4, 4, "亮度调节");
		item4.setIcon(R.drawable.main_menu_lightmode_day);
		MenuItem item5 = menu.add(0, 5, 5, "关于");
		item5.setIcon(R.drawable.main_menu_about);
		MenuItem item6 = menu.add(0, 6, 6, "退出");
		item6.setIcon(R.drawable.main_menu_exit);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1: // 设置
			startActivity(new Intent(this, MoreActivity.class));
			break;
		case 2: // 浏览记录
			startActivity(new Intent(this, HistoryActivity.class));
			break;
		case 3: // 意见反馈
			new FeedbackAgent(this).startFeedbackActivity();
			break;
		case 4: // 亮度调节
			LightCtrlDialogFragment lightCtrlDialogFragment = new LightCtrlDialogFragment();
			lightCtrlDialogFragment.show(getSupportFragmentManager(), null);
			break;
		case 5: // 关于
			startActivity(new Intent(this, AboutActivity.class));
			break;
		case 6: // 退出
			ExitDialogFragment fragment = new ExitDialogFragment();
			fragment.show(getSupportFragmentManager(), null);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	// enable为true时，菜单添加图标有效，enable为false时无效。4.0系统默认无效
	private void setIconEnable(Menu menu, boolean enable) {
		try {
			Class<?> clazz = Class
					.forName("com.android.internal.view.menu.MenuBuilder");
			Method m = clazz.getDeclaredMethod("setOptionalIconsVisible",
					boolean.class);
			m.setAccessible(true);

			// MenuBuilder实现Menu接口，创建菜单时，传进来的menu其实就是MenuBuilder对象(java的多态特征)
			m.invoke(menu, enable);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String extra = intent
					.getStringExtra(Constants.BROADCAST_FILTER.EXTRA_CODE);
			if (extra.equals(Constants.INTENT_KEY.FROM_FAVOR)) {
				isFromFavor = true;
			} else if (extra.equals(Constants.INTENT_KEY.REFRESH_INCART)) {
				initInCartNum();
			} else if (extra.equals(Constants.INTENT_KEY.FROM_DETAIL)) {
				isFromDetail = true;
			} else if (extra.equals(Constants.INTENT_KEY.LOGOUT)) {
				isLogined = false;
			}
		}

	}

}
