package com.android.yttmarket.activity;

import java.util.ArrayList;
import java.util.Random;

import com.android.yttmarket.R;
import com.android.yttmarket.bean.GoodsInfo;
import com.android.yttmarket.constants.Constants;
import com.android.yttmarket.utils.NumberUtils;
import com.lib.uil.UILUtils;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ShakeActivity extends Activity implements OnClickListener,
		SensorEventListener {

	private SensorManager mSensorManager;
	private Vibrator mVibrator;
	private final int ROCKPOWER = 15;// 这是传感器系数
	private View mViewResult;
	private int num = 3;	//剩余次数
	private TextView mTvNum;
	
	private ArrayList<GoodsInfo> list = new ArrayList<GoodsInfo>();
	private ImageView mImgGoods;
	private TextView mTvName;
	private TextView mTvPrice;
	private GoodsInfo info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shake);
		initData();
		initView();
		initSensor();
		findViewById(R.id.btn_back).setOnClickListener(this);
		findViewById(R.id.btn_detail).setOnClickListener(this);
		findViewById(R.id.btn_close).setOnClickListener(this);
	}
	
	private void initData() {
		list.add(new GoodsInfo("100001", "Levi's李维斯男士休闲时尚潮流短袖T恤82176-0005 灰/白 L", "http://7xi38r.com1.z0.glb.clouddn.com/@/server_anime/goodsicons/goods01.jpg", "服饰鞋包", 153.00, "好评96%", 1224, 1, 0));
		list.add(new GoodsInfo("100002", "Levi's李维斯505系列男士舒适直脚牛仔裤00505-1185 牛仔色 36 34", "http://7xi38r.com1.z0.glb.clouddn.com/@/server_anime/goodsicons/goods02.jpg", "服饰鞋包", 479.00, "好评95%", 645, 0, 0));
		list.add(new GoodsInfo("100003", "GXG男装 京东专供 2015夏装新款 男士时尚白色修身圆领短袖T恤#42244315 白色 M", "http://7xi38r.com1.z0.glb.clouddn.com/@/server_anime/goodsicons/goods03.jpg", "服饰鞋包", 149.00, "暂无评价", 1856, 0, 0));
		list.add(new GoodsInfo("100004", "Apple iPad mini ME276CH/A 配备 Retina 显示屏 7.9英寸平板电脑 （16G WiFi版）深空灰色", "http://7xi38r.com1.z0.glb.clouddn.com/@/server_anime/goodsicons/goods04.jpg", "电脑数码", 2138.00, "好评97%", 865, 0, 0));
		list.add(new GoodsInfo("100005", "联想（ThinkPad）轻薄系列E450C(008CD) 14英寸笔记本电脑 （i3-4005U 4GB 500G+8GSSD 1G WIN8.1）", "http://7xi38r.com1.z0.glb.clouddn.com/@/server_anime/goodsicons/goods05.jpg", "电脑数码", 3299.00, "好评95%", 236, 0, 0));
		list.add(new GoodsInfo("100006", "罗技（Logitech）G502 自适应游戏鼠标", "http://7xi38r.com1.z0.glb.clouddn.com/@/server_anime/goodsicons/goods06.jpg", "服饰鞋包", 499.00, "好评95%", 115, 0, 0));
		list.add(new GoodsInfo("100007", "瑞士军刀（Swissgear）SA7777WH 12英寸时尚休闲型双肩电脑背包 米白色", "http://7xi38r.com1.z0.glb.clouddn.com/@/server_anime/goodsicons/goods07.jpg", "服饰鞋包", 199.00, "好评95%", 745, 0, 0));
		list.add(new GoodsInfo("100008", "创见（Transcend） 340系列 256G SATA3 固态硬盘(TS256GSSD340)", "http://7xi38r.com1.z0.glb.clouddn.com/@/server_anime/goodsicons/goods08.jpg", "电脑数码", 569.00, "好评95%", 854, 1, 0));
		list.add(new GoodsInfo("100009", "佳能（Canon） EOS 700D 单反套机 （EF-S 18-135mm f/3.5-5.6 IS STM镜头）", "http://7xi38r.com1.z0.glb.clouddn.com/@/server_anime/goodsicons/goods09.jpg", "电脑数码", 5099.00, "好评94%", 991, 0, 0));
		list.add(new GoodsInfo("100010", "飞轮威尔（F-WHEEL) 智能电动独轮车 自平衡独轮车 海豚系列拉杆 支架 音响 蓝牙 白色D1续航20KM无支架", "http://7xi38r.com1.z0.glb.clouddn.com/@/server_anime/goodsicons/goods10.jpg", "运动户外", 2999.00, "好评93%", 1145, 0, 0));
		list.add(new GoodsInfo("100011", "永久21速26寸铝合金自行车 禧玛诺变速 铝肩可调锁死减震山地车 QJ243 自营", "http://7xi38r.com1.z0.glb.clouddn.com/@/server_anime/goodsicons/goods11.jpg", "运动户外", 1088.00, "好评92%", 909, 0, 0));
		list.add(new GoodsInfo("100012", "我们都一样年轻又彷徨 自营", "http://7xi38r.com1.z0.glb.clouddn.com/@/server_anime/goodsicons/goods12.jpg", "图书音像", 25.40, "好评95%", 1443, 0, 0));
		list.add(new GoodsInfo("100013", "近在远方", "http://7xi38r.com1.z0.glb.clouddn.com/@/server_anime/goodsicons/goods13.jpg", "图书音像", 19.70, "好评98%", 3702, 0, 0));
		list.add(new GoodsInfo("100014", "自在的旅行", "http://7xi38r.com1.z0.glb.clouddn.com/@/server_anime/goodsicons/goods14.jpg", "图书音像", 38.40, "好评97%", 442, 1, 0));
		list.add(new GoodsInfo("100015", "Photoshop专业抠图技法 赠光盘1张", "http://7xi38r.com1.z0.glb.clouddn.com/@/server_anime/goodsicons/goods15.jpg", "图书音像", 57.80, "好评93%", 765, 0, 0));
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 加速度传感器
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
		// 还有SENSOR_DELAY_UI、SENSOR_DELAY_FASTEST、SENSOR_DELAY_GAME等，
		// 根据不同应用，需要的反应速率不同，具体根据实际情况设定
				SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	/**
	 * 显示商品
	 */
	private void showGoods() {
		MediaPlayer player = MediaPlayer.create(this, R.raw.shakeing);
		player.start();
		mVibrator.vibrate(500);// 设置震动。
		if (mViewResult.getVisibility() == View.VISIBLE) {
			return;
		}
		if (num > 0) {
			num--;
			mTvNum.setText("" + num);
		} else {
			Toast.makeText(this, "今天的机会用完了，明天再来吧~", Toast.LENGTH_SHORT).show();
			return;
		}
		Random random = new Random();
		int nextInt = random.nextInt(list.size());
		info = list.get(nextInt);
		mTvName.setText(info.getGoodsName());
		mTvPrice.setText(NumberUtils.formatPrice(info.getGoodsPrice()));
		UILUtils.displayImage(this, info.getGoodsIcon(), mImgGoods);
		mTvName.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				MediaPlayer player2 = MediaPlayer.create(ShakeActivity.this, R.raw.shake_something);
				player2.start();
				mViewResult.setVisibility(View.VISIBLE);
			}
		}, 800);
	}
	
	@Override
	protected void onStop() {
		mSensorManager.unregisterListener(this);// 退出界面后，把传感器释放。-----节省电源
		super.onStop();
	}

	private void initSensor() {
		// 获取传感器管理服务
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		// 震动服务
		mVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE); // 震动需要在androidmainfest里面注册哦亲
	}

	private void initView() {
		mViewResult = findViewById(R.id.layout_result);
		mImgGoods = (ImageView) findViewById(R.id.img_goods);
		mTvName = (TextView) findViewById(R.id.tv_name);
		mTvPrice = (TextView) findViewById(R.id.tv_price);
		mTvNum = (TextView) findViewById(R.id.tv_num);
		final ImageView imgHand = (ImageView) findViewById(R.id.img_shake_hand);
		ImageView imgAnim = (ImageView) findViewById(R.id.img_anim);
		imgHand.postDelayed(new Runnable() {

			@Override
			public void run() {
				ViewHelper.setPivotX(imgHand, imgHand.getWidth() / 2f);
				ViewHelper.setPivotY(imgHand, imgHand.getHeight());
				ObjectAnimator.ofFloat(imgHand, "rotation", -30, 30, 0)
						.setDuration(1500).start();
				imgHand.postDelayed(this, 3000);
			}
		}, 1500);
		AnimationDrawable drawable = (AnimationDrawable) imgAnim.getDrawable();
		drawable.start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_close:
			mViewResult.setVisibility(View.GONE);
			break;
		case R.id.btn_detail:
			gotoDetail();
			break;

		default:
			break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (mViewResult.getVisibility() == View.VISIBLE) {
				mViewResult.setVisibility(View.GONE);
				return false;
			}
			break;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 商品详情
	 */
	private void gotoDetail() {
		Intent intent = new Intent(this, DetailActivity.class);
		intent.putExtra(Constants.INTENT_KEY.INFO_TO_DETAIL, info);
		startActivity(intent );
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		int sensorType = event.sensor.getType();
		// values[0]:X轴，values[1]：Y轴，values[2]：Z轴
		float[] values = event.values;
		if (sensorType == Sensor.TYPE_ACCELEROMETER) {
			// 在 这个if里面写监听，写要摇一摇干么子，知道么？猪头~~~
			if ((Math.abs(values[0]) > ROCKPOWER || Math.abs(values[1]) > ROCKPOWER || Math.abs(values[2]) > ROCKPOWER)) {
				showGoods();
			} 
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

}
