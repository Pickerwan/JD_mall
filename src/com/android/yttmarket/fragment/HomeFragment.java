package com.android.yttmarket.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.an.cityselect.City;
import com.android.yttmarket.R;
import com.android.yttmarket.activity.DetailActivity;
import com.android.yttmarket.activity.MainActivity;
import com.android.yttmarket.activity.SearchActivity;
import com.android.yttmarket.adapter.GalleryAdapter;
import com.android.yttmarket.adapter.GalleryAdapter.OnItemClickLitener;
import com.android.yttmarket.adapter.IndexGalleryAdapter;
import com.android.yttmarket.app.MyApp;
import com.android.yttmarket.bean.GoodsInfo;
import com.android.yttmarket.bean.IndexGalleryItemData;
import com.android.yttmarket.constants.Constants;
import com.android.yttmarket.utils.NetUtil;
import com.android.yttmarket.utils.SharePreferenceUtil;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.lib.uil.ToastUtils;
import com.nineoldandroids.animation.ObjectAnimator;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class HomeFragment extends Fragment implements OnClickListener {

	private static final int LOACTION_OK = 0;
	private static final int ON_NEW_INTENT = 1;
	private static final int GET_CITY_RESULT = 2;

	private ViewPager mPager;
	private City mCurCityinfo;
	private TextView mCityNameTv;
	private City mCurCity;
	public LocationClient mLocationClient = null;
	private MyApp mApplication;
	private SharePreferenceUtil mSpUtil;
//	private Boolean IsActivityFinished = false;
	private String cityName;

	private PullToRefreshScrollView mPtrScrollView;
	private int[] mBanner = new int[] { R.drawable.img_home_banner1,
			R.drawable.img_home_banner2, R.drawable.img_home_banner3,
			R.drawable.img_home_banner4 };
	private GoodsInfo[] mInfos;
	private ImageView mImageView;
	private ImageView mImgCover;
	private int mLastPos;// 记录上一次ViewPager的位置
	private boolean isDragging;// 是否被拖拽
	private boolean isFoucusRight; // ScrollView是否滚动到右侧
	private View view;
	
	//左右滑动物品展示
	private RecyclerView mRecyclerView1;  
	private RecyclerView mRecyclerView2;  
	
    private GalleryAdapter mAdapter1; 
    private GalleryAdapter mAdapter2;
    private List<IndexGalleryItemData> mDatas1 = new ArrayList<IndexGalleryItemData>();
    private List<IndexGalleryItemData> mDatas2 = new ArrayList<IndexGalleryItemData>();

	private IndexGalleryItemData mItemData = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initGoodsInfos();
		if (view != null) {
			// 防止多次new出片段对象，造成图片错乱问题
			return view;
		}
		view = inflater.inflate(R.layout.fragment_home, container, false);
		initData();
		initView();
		initPager();
		autoScroll();
		mApplication = MyApp.getInstance();

		mLocationClient = mApplication.getLocationClient(); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		return view;
	}
	private void initData() {
		
		mItemData = new IndexGalleryItemData();
		mItemData.setId(1);
		mItemData.setImageUrl("drawable://" + R.drawable.index_gallery_01);
		mItemData.setDesc("第一件产品描述第一件产品描述第一件产品描述第一件产品描述第一件产品描述第一件产品描述");
		mItemData.setPrice("￥79.00");
		mDatas1.add(mItemData);

		mItemData = new IndexGalleryItemData();
		mItemData.setId(2);
		mItemData.setImageUrl("drawable://" + R.drawable.index_gallery_02);
		mItemData.setPrice("￥89.00");
		mItemData.setDesc("第二件产品描述");
		mDatas1.add(mItemData);

		mItemData = new IndexGalleryItemData();
		mItemData.setId(3);
		mItemData.setImageUrl("drawable://" + R.drawable.index_gallery_03);
		mItemData.setPrice("￥99.00");
		mItemData.setDesc("第三件产品描述");
		mDatas1.add(mItemData);

		mItemData = new IndexGalleryItemData();
		mItemData.setId(4);
		mItemData.setImageUrl("drawable://" + R.drawable.index_gallery_04);
		mItemData.setPrice("￥109.00");
		mItemData.setDesc("第四件产品描述");
		mDatas1.add(mItemData);

		mItemData = new IndexGalleryItemData();
		mItemData.setId(5);
		mItemData.setImageUrl("drawable://" + R.drawable.index_gallery_05);
		mItemData.setPrice("￥119.00");
		mItemData.setDesc("第五件产品描述");
		mDatas1.add(mItemData);

		mItemData = new IndexGalleryItemData();
		mItemData.setId(6);
		mItemData.setImageUrl("drawable://" + R.drawable.index_gallery_06);
		mItemData.setPrice("￥129.00");
		mItemData.setDesc("第六件产品描述");
		mDatas1.add(mItemData);

		mItemData = new IndexGalleryItemData();
		mItemData.setId(7);
		mItemData.setImageUrl("drawable://" + R.drawable.index_gallery_07);
		mItemData.setPrice("￥139.00");
		mItemData.setDesc("第七件产品描述");
		mDatas1.add(mItemData);

		mItemData = new IndexGalleryItemData();
		mItemData.setId(8);
		mItemData.setImageUrl("drawable://" + R.drawable.index_gallery_08);
		mItemData.setPrice("￥69.00");
		mItemData.setDesc("第八件产品描述");
		mDatas1.add(mItemData);

		mItemData = new IndexGalleryItemData();
		mItemData.setId(9);
		mItemData.setImageUrl("drawable://" + R.drawable.index_gallery_09);
		mItemData.setPrice("￥99.00");
		mItemData.setDesc("第九件产品描述");
		mDatas1.add(mItemData);

		mItemData = new IndexGalleryItemData();
		mItemData.setId(10);
		mItemData.setImageUrl("drawable://" + R.drawable.index_gallery_10);
		mItemData.setPrice("￥109.00");
		mItemData.setDesc("第一件产品描述");
		mDatas2.add(mItemData);

		mItemData = new IndexGalleryItemData();
		mItemData.setId(11);
		mItemData.setImageUrl("drawable://" + R.drawable.index_gallery_11);
		mItemData.setPrice("￥119.00");
		mItemData.setDesc("第二件产品描述");
		mDatas2.add(mItemData);

		mItemData = new IndexGalleryItemData();
		mItemData.setId(12);
		mItemData.setImageUrl("drawable://" + R.drawable.index_gallery_12);
		mItemData.setPrice("￥129.00");
		mItemData.setDesc("第三件产品描述");
		mDatas2.add(mItemData);

		mItemData = new IndexGalleryItemData();
		mItemData.setId(13);
		mItemData.setImageUrl("drawable://" + R.drawable.index_gallery_13);
		mItemData.setPrice("￥139.00");
		mItemData.setDesc("第四件产品描述");
		mDatas2.add(mItemData);

		mItemData = new IndexGalleryItemData();
		mItemData.setId(14);
		mItemData.setImageUrl("drawable://" + R.drawable.index_gallery_14);
		mItemData.setPrice("￥149.00");
		mItemData.setDesc("第五件产品描述");
		mDatas2.add(mItemData);

	}

	private void initGoodsInfos() {
		mInfos = new GoodsInfo[] {
				new GoodsInfo(
						"100001",
						"Levi's李维斯男士休闲时尚潮流短袖T恤82176-0005 灰/白 L",
						"http://7xi38r.com1.z0.glb.clouddn.com/@/server_anime/goodsicons/goods01.jpg",
						"服饰鞋包", 153.00, "好评96%", 1224, 1, 0),
				new GoodsInfo(
						"100002",
						"Levi's李维斯505系列男士舒适直脚牛仔裤00505-1185 牛仔色 36 34",
						"http://7xi38r.com1.z0.glb.clouddn.com/@/server_anime/goodsicons/goods02.jpg",
						"服饰鞋包", 479.00, "好评95%", 645, 0, 0),
				new GoodsInfo(
						"100003",
						"GXG男装 京东专供 2015夏装新款 男士时尚白色修身圆领短袖T恤#42244315 白色 M",
						"http://7xi38r.com1.z0.glb.clouddn.com/@/server_anime/goodsicons/goods03.jpg",
						"服饰鞋包", 149.00, "暂无评价", 1856, 0, 0),
				new GoodsInfo(
						"100004",
						"Apple iPad mini ME276CH/A 配备 Retina 显示屏 7.9英寸平板电脑 （16G WiFi版）深空灰色",
						"http://7xi38r.com1.z0.glb.clouddn.com/@/server_anime/goodsicons/goods04.jpg",
						"电脑数码", 2138.00, "好评97%", 865, 0, 0),
				new GoodsInfo(
						"100005",
						"联想（ThinkPad）轻薄系列E450C(008CD) 14英寸笔记本电脑 （i3-4005U 4GB 500G+8GSSD 1G WIN8.1）",
						"http://7xi38r.com1.z0.glb.clouddn.com/@/server_anime/goodsicons/goods05.jpg",
						"电脑数码", 3299.00, "好评95%", 236, 0, 0),
				new GoodsInfo(
						"100006",
						"罗技（Logitech）G502 自适应游戏鼠标",
						"http://7xi38r.com1.z0.glb.clouddn.com/@/server_anime/goodsicons/goods06.jpg",
						"服饰鞋包", 499.00, "好评95%", 115, 0, 0),
				new GoodsInfo(
						"100007",
						"瑞士军刀（Swissgear）SA7777WH 12英寸时尚休闲型双肩电脑背包 米白色",
						"http://7xi38r.com1.z0.glb.clouddn.com/@/server_anime/goodsicons/goods07.jpg",
						"服饰鞋包", 199.00, "好评95%", 745, 0, 0),
				new GoodsInfo(
						"100008",
						"创见（Transcend） 340系列 256G SATA3 固态硬盘(TS256GSSD340)",
						"http://7xi38r.com1.z0.glb.clouddn.com/@/server_anime/goodsicons/goods08.jpg",
						"电脑数码", 569.00, "好评95%", 854, 1, 0),
				new GoodsInfo(
						"100009",
						"佳能（Canon） EOS 700D 单反套机 （EF-S 18-135mm f/3.5-5.6 IS STM镜头）",
						"http://7xi38r.com1.z0.glb.clouddn.com/@/server_anime/goodsicons/goods09.jpg",
						"电脑数码", 5099.00, "好评94%", 991, 0, 0),
				new GoodsInfo(
						"100010",
						"飞轮威尔（F-WHEEL) 智能电动独轮车 自平衡独轮车 海豚系列拉杆 支架 音响 蓝牙 白色D1续航20KM无支架",
						"http://7xi38r.com1.z0.glb.clouddn.com/@/server_anime/goodsicons/goods10.jpg",
						"运动户外", 2999.00, "好评93%", 1145, 0, 0),
				new GoodsInfo(
						"100011",
						"永久21速26寸铝合金自行车 禧玛诺变速 铝肩可调锁死减震山地车 QJ243 自营",
						"http://7xi38r.com1.z0.glb.clouddn.com/@/server_anime/goodsicons/goods11.jpg",
						"运动户外", 1088.00, "好评92%", 909, 0, 0),
				new GoodsInfo(
						"100012",
						"我们都一样年轻又彷徨 自营",
						"http://7xi38r.com1.z0.glb.clouddn.com/@/server_anime/goodsicons/goods12.jpg",
						"图书音像", 25.40, "好评95%", 1443, 0, 0),
				new GoodsInfo(
						"100013",
						"近在远方",
						"http://7xi38r.com1.z0.glb.clouddn.com/@/server_anime/goodsicons/goods13.jpg",
						"图书音像", 19.70, "好评98%", 3702, 0, 0),
				new GoodsInfo(
						"100014",
						"自在的旅行",
						"http://7xi38r.com1.z0.glb.clouddn.com/@/server_anime/goodsicons/goods14.jpg",
						"图书音像", 38.40, "好评97%", 442, 1, 0),
				new GoodsInfo(
						"100015",
						"Photoshop专业抠图技法 赠光盘1张",
						"http://7xi38r.com1.z0.glb.clouddn.com/@/server_anime/goodsicons/goods15.jpg",
						"图书音像", 57.80, "好评93%", 765, 0, 0) };
	}

	private void initView() {
		
		view.findViewById(R.id.im_home_to_cart).setOnClickListener(this);
		view.findViewById(R.id.im_home_to_personal).setOnClickListener(this);
		view.findViewById(R.id.im_home_to_category).setOnClickListener(this);
		mCityNameTv = (TextView) view.findViewById(R.id.tv_home_city);

		view.findViewById(R.id.weather_location).setOnClickListener(this);
		view.findViewById(R.id.layout_home_search).setOnClickListener(this);
		
		//得到控件  
        mRecyclerView1= (RecyclerView)view.findViewById(R.id.index_huobao_recyclerview_horizontal);  
        mRecyclerView2 = (RecyclerView)view.findViewById(R.id.index_xinpin_recyclerview_horizontal);  
        
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity());  
        linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);  
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity());  
        linearLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);  
        mRecyclerView1.setLayoutManager(linearLayoutManager1);  
        mRecyclerView2.setLayoutManager(linearLayoutManager2);  
        //设置适配器  
        mAdapter1 = new GalleryAdapter(getActivity(), mDatas1);  
        mAdapter2 = new GalleryAdapter(getActivity(), mDatas2);  
        mRecyclerView1.setAdapter(mAdapter1); 
        mRecyclerView2.setAdapter(mAdapter2); 
		
		view.findViewById(R.id.imageview_black_chocolate).setOnClickListener(
				this);
		view.findViewById(R.id.imageview_white_chocolate).setOnClickListener(
				this);
		view.findViewById(R.id.imageview_milk_chocolate).setOnClickListener(
				this);
		view.findViewById(R.id.imageview_truffles_chocolate)
				.setOnClickListener(this);
		mPtrScrollView = (PullToRefreshScrollView) view
				.findViewById(R.id.ptrScrollView_home);
		mPtrScrollView
				.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
					public void onRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						new GetDataTask().execute();
					}
				});

		mImgCover = (ImageView) view.findViewById(R.id.img_cover);
		mImageView = (ImageView) view.findViewById(R.id.img_indicator01);
		
		  mAdapter1.setOnItemClickLitener(new OnItemClickLitener()  
	        {  
	            @Override  
	            public void onItemClick(View view, int position)  
	            {  
				Toast.makeText(getActivity(), position + "火爆的", Toast.LENGTH_SHORT)
						.show();
			}
	        });  
		  mAdapter2.setOnItemClickLitener(new OnItemClickLitener()  
	        {  
	            @Override  
	            public void onItemClick(View view, int position)  
	            {  
//				Toast.makeText(getActivity(), position + "新品的", Toast.LENGTH_SHORT)
//						.show();
				Intent intent = new Intent(getActivity(), DetailActivity.class);
				intent.putExtra(Constants.INTENT_KEY.INFO_TO_DETAIL, mInfos[position]);
				startActivity(intent);
			}
	        });  
	        mRecyclerView1.setAdapter(mAdapter1);
	        mRecyclerView2.setAdapter(mAdapter2);

	}

	private void activeCategory() {
		MainActivity activity = (MainActivity) getActivity();
		activity.activeCategory();
	}
	private void activeCart() {
		MainActivity activity = (MainActivity) getActivity();
		activity.activeCart();
	}
	private void activeMine() {
		MainActivity activity = (MainActivity) getActivity();
		activity.activeMine();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		// 将layout从父组件中移除
		ViewGroup parent = (ViewGroup) view.getParent();
		parent.removeView(view);
	}

	private void initPager() {
		mPager = (ViewPager) view.findViewById(R.id.pager_banner);
		FragmentManager fm = getChildFragmentManager();
		MyPagerAdapter adapter = new MyPagerAdapter(fm);
		mPager.setAdapter(adapter);
		mPager.setCurrentItem(1000);
		mPager.setOnPageChangeListener(new MyPagerListener());
	}

	/**
	 * 自动滚动
	 */
	private void autoScroll() {
		mPager.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (!isDragging) {
					// 若用户没有拖拽，则自动滚动
					mPager.setCurrentItem(mPager.getCurrentItem() + 1);
				}
				if (isFoucusRight) {
					
				} else {
				}
				isFoucusRight = !isFoucusRight;
				mPager.postDelayed(this, 2000);
			}
		}, 2000);
	}

	class MyPagerAdapter extends FragmentStatePagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			BannerItemFragment fragment = new BannerItemFragment();
			fragment.setResId(mBanner[position % mBanner.length]);
			fragment.setGoodsInfo(mInfos[position % mBanner.length]);
			return fragment;
		}

		@Override
		public int getCount() {
			return 10000;
		}

	}

	private void startActivityForResult() {
		// Intent i = new Intent(getActivity(), SelectCtiyActivity.class);
		// startActivityForResult(i, 0);
	}

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOACTION_OK:
				cityName = (String) msg.obj;
				cityName = cityName.replace("市", "");
				updateWeather();
				break;
			case ON_NEW_INTENT:
				cityName = mCurCity.getCity();
				mSpUtil.setCity(mCurCity.getCity());
				updateWeather();
				break;
			case GET_CITY_RESULT:
				updateCityInfo();
				break;
			default:
				break;
			}
		}

	};

	/**
	 * 更新天气界面
	 */
	private void updateCityInfo() {
		if (mCurCityinfo != null) {

			mCityNameTv.setText(mCurCityinfo.getCity());

		} else {
			ToastUtils.showToast(getActivity(), "获取城市信息失败");

		}
	}

	private void updateWeather() {
		if (NetUtil.getNetworkState(getActivity()) == NetUtil.NETWORN_NONE) {
			ToastUtils.showToast(getActivity(), "请检查网络");
			return;
		}
		if (mCurCity == null) {
			ToastUtils.showToast(getActivity(), "未找到此城市,请重新定位或选择...");
			return;
		}

		// 启动线程获取信息
		new Thread() {
			@Override
			public void run() {
				super.run();
				Looper.prepare();
				// weather
				mHandler.sendEmptyMessage(GET_CITY_RESULT);
				Looper.loop();
			}

		}.start();

	}

	private void StartLocation() {
		if (NetUtil.getNetworkState(getActivity()) != NetUtil.NETWORN_NONE) {
			if (!mLocationClient.isStarted())
				mLocationClient.start();
			mLocationClient.requestLocation();// 开始定位
			ToastUtils.showToast(getActivity(), "正在定位...");
		} else {
			ToastUtils.showToast(getActivity(), "请检查网络");
		}
	}

	public BDLocationListener myListener = new MyLocationListener();

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {

			mCityNameTv.setText(location.getCity());
		}
	}

	// 设置相关参数
	private void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开gps
		option.setProdName("com.baidu.location.service_v2.2");
		option.setAddrType("all");
		mLocationClient.setLocOption(option);
	}

	// public BDLocationListener mLocationListener = new BDLocationListener() {
	//
	//
	//
	// @Override
	// public void onReceiveLocation(BDLocation location) {
	// if (IsActivityFinished)
	// return;
	// if (location == null || TextUtils.isEmpty(location.getCity())) {
	// final Dialog dialog = new Dialog(getActivity());
	// dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	// dialog.setContentView(R.layout.ytt_dialog);
	// dialog.setCanceledOnTouchOutside(false);
	// TextView title = (TextView) dialog
	// .findViewById(R.id.yunshi_dialog_title);
	// TextView content = (TextView) dialog
	// .findViewById(R.id.yunshi_dialog_content);
	// TextView ok = (TextView) dialog
	// .findViewById(R.id.yunshi_dialog_id_ok);
	// TextView cancel = (TextView) dialog
	// .findViewById(R.id.yunshi_dialog_id_cancel);
	// title.setText("定位失败");
	// content.setText(Html.fromHtml("是否手动选择城市？"));
	// ok.setText("取消");
	// ok.setOnClickListener(new View.OnClickListener() {
	//
	// @Override
	// public void onClick(View arg0) {
	// // TODO Auto-generated method stub
	//
	// dialog.dismiss();
	// }
	// });
	// cancel.setText("确定");
	// cancel.setOnClickListener(new View.OnClickListener() {
	//
	// @Override
	// public void onClick(View arg0) {
	// startActivityForResult();
	// dialog.dismiss();
	// }
	// });
	// dialog.show();
	//
	// return;
	// }
	// String cityName = location.getCity();
	// mLocationClient.stop();
	// Message msg = mHandler.obtainMessage();
	// msg.what = LOACTION_OK;
	// msg.obj = cityName;
	// mHandler.sendMessage(msg);// 更新天气
	// }
	// };

	class MyPagerListener implements OnPageChangeListener {

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			int width = mImgCover.getWidth();
			LayoutParams layoutParams = (LayoutParams) mImageView
					.getLayoutParams();
			int rightMargin = layoutParams.rightMargin;
			int endPos = (width + rightMargin) * (position % 4);
			int startPos = 0;
			if (mLastPos < position) {
				// 图片向右移动
				startPos = (width + rightMargin) * (position % 4 - 1);
			} else {
				// 图片向左移动
				startPos = (width + rightMargin) * (position % 4 + 1);
			}
			ObjectAnimator.ofFloat(mImgCover, "translationX", startPos, endPos)
					.setDuration(300).start();
			mLastPos = position;
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			switch (state) {
			case ViewPager.SCROLL_STATE_DRAGGING:
				// 用户拖拽
				isDragging = true;
				break;
			case ViewPager.SCROLL_STATE_IDLE:
				// 空闲状态
				isDragging = false;
				break;
			case ViewPager.SCROLL_STATE_SETTLING:
				// 被释放时
				isDragging = false;
				break;

			default:
				break;
			}
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_home_city: // 切换到主页
			activeCategory();
			break;
		case R.id.layout_home_search:
			gotoSearch();
			break;
		case R.id.weather_location:
			StartLocation();
			setLocationOption();
			mLocationClient.start();
			break;
		case R.id.im_home_to_cart:
			activeCart();
			break;
		case R.id.im_home_to_personal:
			activeMine();
			break;
		case R.id.im_home_to_category:
			activeCategory();
			break;
			
			
		// case R.id.layout_my_focus: // 我的关注
		// startActivity(new Intent(getActivity(), FavorActivity.class));
		// break;
		case R.id.imageview_black_chocolate: // 白菜秒杀
			gotoDetail(4);
			break;
		case R.id.imageview_white_chocolate: // 手机专享
			gotoDetail(5);
			break;
		case R.id.imageview_milk_chocolate: // 奶粉放心购
			gotoDetail(6);
			break;
		case R.id.imageview_truffles_chocolate: // 值得买
			gotoDetail(7);
			break;
		case R.id.imageview_home8: // 京东众筹
			gotoDetail(11);
			break;
		default:
			break;
		}
	}

	/**
	 * 转到商品详情
	 */
	private void gotoDetail(int index) {
		Intent intent = new Intent(getActivity(), DetailActivity.class);
		intent.putExtra(Constants.INTENT_KEY.INFO_TO_DETAIL, mInfos[index]);
		startActivity(intent);
	}

	private void gotoSearch() {
		Intent intent = new Intent(getActivity(), SearchActivity.class);
		startActivity(intent);
		// activity开启无动画
		getActivity().overridePendingTransition(0, 0);
	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {
		protected String[] doInBackground(Void... params) {
			// 下拉刷新
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
			return null;
		}

		protected void onPostExecute(String[] result) {
			mPtrScrollView.onRefreshComplete();// 关闭刷新动画
		}
		

	}

}
