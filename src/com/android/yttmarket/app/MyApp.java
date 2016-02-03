package com.android.yttmarket.app;

import java.io.File;

import cn.jpush.android.api.JPushInterface;
import cn.smssdk.SMSSDK;

import com.activeandroid.ActiveAndroid;
import com.android.yttmarket.utils.NetUtil;
import com.android.yttmarket.utils.SharePreferenceUtil;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.lib.uil.ToastUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

/**
 * 用于初始化ImageLoader。若不执行初始化，下载图片前清除缓存，存在潜在空指针问题
 * 
 * @author Administrator
 * 
 */
public class MyApp extends Application {

	private LocationClient mLocationClient = null;
	public static int mNetWorkState;
	private SharePreferenceUtil mSpUtil;

	private static MyApp mApplication;

	@Override
	public void onCreate() {
		super.onCreate();
		initImageLoader(this);
		ActiveAndroid.initialize(this);
		// 初始化百度地图
//		SDKInitializer.initialize(this);
		
//		initJPush();
		// BugReportHandler crashHandler = BugReportHandler.getInstance();
		// crashHandler.init(getApplicationContext());
		// city
		initData();
	}

	private void initJPush() {
		// JPushInterface.setDebugMode(true); // 设置开启日志,发布时请关闭日志
		JPushInterface.init(this); // 初始化 JPush
	}

	public void initImageLoader(Context context) {
		File cacheDir = StorageUtils.getOwnCacheDirectory(context, "ytt/Cache");// 获取到缓存的目录地址
		Log.d("cacheDir", cacheDir.getPath());
		ImageLoaderConfiguration config = new ImageLoaderConfiguration
				.Builder(context)
				.threadPoolSize(3)// 线程池内加载的数量
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024)
				// 50 Mb
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.discCache(new UnlimitedDiscCache(cacheDir))//自定义缓存路径
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	private LocationClientOption getLocationClientOption() {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("all");
		option.setScanSpan(0);
		return option;
	}

	private void initData() {
		mApplication = this;
		mNetWorkState = NetUtil.getNetworkState(this);
		mLocationClient = new LocationClient(this, getLocationClientOption());
		mSpUtil = new SharePreferenceUtil(this,
				SharePreferenceUtil.CITY_SHAREPRE_FILE);
		// IntentFilter filter = new IntentFilter(NET_CHANGE_ACTION);
		// registerReceiver(netChangeReceiver, filter);
	}


	public synchronized SharePreferenceUtil getSharePreferenceUtil() {
		if (mSpUtil == null)
			mSpUtil = new SharePreferenceUtil(this,
					SharePreferenceUtil.CITY_SHAREPRE_FILE);
		return mSpUtil;
	}

	public synchronized LocationClient getLocationClient() {
		if (mLocationClient == null)
			mLocationClient = new LocationClient(this,
					getLocationClientOption());
		return mLocationClient;
	}





	public static abstract interface EventHandler {
		public abstract void onCityComplite();

		public abstract void onNetChange();
	}


	// BroadcastReceiver netChangeReceiver = new BroadcastReceiver() {
	//
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// // TODO Auto-generated method stub
	// if (intent.getAction().equals(NET_CHANGE_ACTION)) {
	// if (mListeners.size() > 0)// 通知接口完成加载
	// for (EventHandler handler : mListeners) {
	// handler.onNetChange();
	// }
	// }
	// mNetWorkState = NetUtil.getNetworkState(mApplication);
	// }
	//
	// };

	public static synchronized MyApp getInstance() {
		// if (null == mApplication) {
		// mApplication = new MyApp();
		// }
		return mApplication;
	}

}
