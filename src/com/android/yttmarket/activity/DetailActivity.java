package com.android.yttmarket.activity;

import java.util.ArrayList;
import java.util.List;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import com.activeandroid.query.Select;
import com.android.yttmarket.bean.GoodsInfo;
import com.android.yttmarket.bean.InCart;
import com.android.yttmarket.constants.Constants;
import com.android.yttmarket.fragment.DetailBannerItemFragment;
import com.android.yttmarket.utils.DBUtils;
import com.android.yttmarket.utils.NumberUtils;
import com.android.yttmarket.R;
import com.lib.uil.ToastUtils;
import com.lib.uil.UILUtils;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class DetailActivity extends FragmentActivity implements OnClickListener {

	private int[] mBanner = new int[] { R.drawable.ip1, R.drawable.ip2,
			R.drawable.ip3,R.drawable.ip4 };

	private ViewPager mPager;
	private ImageView mImgFavor;
	private TextView mTvCollect;
	private MenuDrawer mDrawer;

	private GoodsInfo mGoodsInfo;
	private InCart mInCart;
	private ArrayList<GoodsInfo> historyList = new ArrayList<GoodsInfo>();
	private HistoryAdapter mAdapter;

	private TextView mTvCount1;
	private TextView mTvCount2;

	private TextView mTvInCartNum;

//	private int index ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		SysApplication.getInstance().addDetailActivity(this);
//		SysApplication.getInstance().removeExtraActivity();
		initData();
		saveHistory();
		initMenu();
		initView();
		setOnListener();
		initPager();
		initListView();
		initInCartNum();
		initHistory();
	}

	private void setOnListener() {
		findViewById(R.id.btn_add_to_cart).setOnClickListener(this);
		findViewById(R.id.btn_goto_cart).setOnClickListener(this);
		findViewById(R.id.btn_collect).setOnClickListener(this);
		findViewById(R.id.img_history).setOnClickListener(this);
		findViewById(R.id.img_back).setOnClickListener(this);
	}

	private void initView() {
		mTvInCartNum = (TextView) findViewById(R.id.tv_incart);
		mTvCount1 = (TextView) findViewById(R.id.tv_count_page);
		mTvCount2 = (TextView) findViewById(R.id.tv_all_page);
		mTvCollect = (TextView) findViewById(R.id.tv_collect);
		mImgFavor = (ImageView) findViewById(R.id.img_favor);
		TextView tvTitle = (TextView) findViewById(R.id.tv_title);
		TextView tvPrice = (TextView) findViewById(R.id.tv_price);
		TextView tvVip = (TextView) findViewById(R.id.tv_vip);
		ImageView imgVip = (ImageView) findViewById(R.id.img_vip);
		tvTitle.setText(mGoodsInfo.getGoodsName());
		tvPrice.setText(NumberUtils.formatPrice(mGoodsInfo.getGoodsPrice()));
		// 判断是否收藏
		mTvCount2.setText("/"+String.valueOf(mBanner.length));
		if (DBUtils.hasFavor(mGoodsInfo)) {
			mTvCollect.setText("已关注");
			mImgFavor.setImageResource(R.drawable.pd_collect_p);
		}
		int isPhone = mGoodsInfo.getIsPhone();
		if (isPhone == 1) {
			imgVip.setVisibility(View.VISIBLE);
			tvVip.setVisibility(View.VISIBLE);
		} else {
			imgVip.setVisibility(View.GONE);
			tvVip.setVisibility(View.GONE);
		}
	}

	private void initData() {
		Intent intent = getIntent();
		mGoodsInfo = (GoodsInfo) intent
				.getSerializableExtra(Constants.INTENT_KEY.INFO_TO_DETAIL);
		mInCart = new InCart(mGoodsInfo.getGoodsId(),
				mGoodsInfo.getGoodsName(), mGoodsInfo.getGoodsIcon(),
				mGoodsInfo.getGoodsType(), mGoodsInfo.getGoodsPrice(),
				mGoodsInfo.getGoodsPercent(), mGoodsInfo.getGoodsComment(),
				mGoodsInfo.getIsPhone(), mGoodsInfo.getIsFavor(), 1);
	}

	/**
	 * 保存到浏览历史
	 */
	private void saveHistory() {
		GoodsInfo info = new Select().from(GoodsInfo.class)
				.where("goodsId=? AND isFavor=0", mGoodsInfo.getGoodsId())
				.executeSingle();
		// 删除同样的数据
		if (info != null) {
			info.delete();
		}
		// 插入到历史数据库
		mGoodsInfo.setIsFavor(0);
		DBUtils.save(mGoodsInfo);
	}

	/**
	 * 购物车中商品数量
	 */
	private void initInCartNum() {
		int num = DBUtils.getInCartNum();
		if (num > 0) {
			mTvInCartNum.setVisibility(View.VISIBLE);
			mTvInCartNum.setText("" + num);
		} else {
			mTvInCartNum.setVisibility(View.INVISIBLE);
		}
	}

	private void initPager() {
		mPager = (ViewPager) findViewById(R.id.pager);
		FragmentManager fm = getSupportFragmentManager();
		MyPagerAdapter adapter = new MyPagerAdapter(fm);
		mPager.setAdapter(adapter);
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				mTvCount1.setText(arg0 + 1 + "");
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	private void initListView() {
		ListView listView = (ListView) findViewById(R.id.listView1);
		mAdapter = new HistoryAdapter();
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mDrawer.toggleMenu();
				Intent intent = new Intent(DetailActivity.this,
						DetailActivity.class);
				intent.putExtra(Constants.INTENT_KEY.INFO_TO_DETAIL,
						historyList.get(position));
				startActivity(intent);
			}
		});
	}

	private void initHistory() {
		new HistoryTask().execute();
	}

	private void initMenu() {
		mDrawer = MenuDrawer
				.attach(this, MenuDrawer.Type.OVERLAY, Position.END);
		mDrawer.setMenuView(R.layout.menudrawer_history);
		mDrawer.setContentView(R.layout.activity_detail);
		// 菜单宽度
		mDrawer.setMenuSize(260);
		// 菜单无阴影
		mDrawer.setDropShadowEnabled(false);
	}

	class MyPagerAdapter extends FragmentStatePagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			DetailBannerItemFragment fragment = new DetailBannerItemFragment();
			fragment.setResId(mBanner[position]);
			fragment.setPosition(position);
			return fragment;
		}

		@Override
		public int getCount() {
			return mBanner.length;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_back:
			finish();
			break;
		case R.id.btn_goto_cart: // 购物车
			gotoHomePage();
			break;
		case R.id.btn_collect: // 收藏
			collect();
			break;
		case R.id.btn_add_to_cart: // 加入购物车
			add2Cart();
			break;
		case R.id.img_history: // 浏览记录
			mDrawer.toggleMenu();
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mDrawer.isMenuVisible()) {
				mDrawer.closeMenu();
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 加入购物车
	 */
	private void add2Cart() {
		InCart inCart = new Select().from(InCart.class)
				.where("goodsId=?", mInCart.getGoodsId()).executeSingle();
		if (inCart != null) {
			// 若购物车中有，则数量+1
			inCart.setNum(inCart.getNum() + 1);
			inCart.save();
		} else {
			mInCart.save();
		}
		// 刷新购物车商品数量
		initInCartNum();
		// Toast.makeText(this, "已添加到购物车", Toast.LENGTH_SHORT).show();
		ToastUtils.showToast(this, "已添加至购物车");
		// 通知主页刷新购物车商品数
		Intent intent = new Intent();
		intent.setAction(Constants.BROADCAST_FILTER.FILTER_CODE);
		intent.putExtra(Constants.BROADCAST_FILTER.EXTRA_CODE,
				Constants.INTENT_KEY.REFRESH_INCART);
		sendBroadcast(intent);
	}

	/**
	 * 跳转到首页购物车
	 */
	private void gotoHomePage() {
		startActivity(new Intent(this, MainActivity.class));
		Intent intent = new Intent();
		intent.setAction(Constants.BROADCAST_FILTER.FILTER_CODE);
		intent.putExtra(Constants.BROADCAST_FILTER.EXTRA_CODE,
				Constants.INTENT_KEY.FROM_DETAIL);
		sendBroadcast(intent);
		finish();
		overridePendingTransition(0, 0);
	}

	/**
	 * 收藏/取消收藏
	 */
	private void collect() {
		// 判断是否收藏
		mGoodsInfo = mGoodsInfo.clone();
		if (DBUtils.hasFavor(mGoodsInfo)) {
			// 由于DBUtil的方法，需要设置favor = 1，否则会无法取消关注
			mGoodsInfo.setIsFavor(1);
			DBUtils.delete(mGoodsInfo);
			mTvCollect.setText("关注");
			mImgFavor.setImageResource(R.drawable.pd_collect_n);
			ToastUtils.showToast(this, "取消成功");
		} else {
			mGoodsInfo.setIsFavor(1);
			DBUtils.save(mGoodsInfo);
			mTvCollect.setText("已关注");
			mImgFavor.setImageResource(R.drawable.pd_collect_p);
			ToastUtils.showToast(this, "关注成功");
		}
		// 通知收藏界面刷新
		Intent intent = new Intent();
		intent.setAction("updateFavor");
		sendBroadcast(intent);
	}

	class HistoryAdapter extends BaseAdapter {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View inflate = null;
			ViewHolder holder = null;
			if (convertView == null) {
				inflate = getLayoutInflater().inflate(
						R.layout.item_detail_menu_list, null);
				holder = new ViewHolder();
				holder.imgGoods = (ImageView) inflate
						.findViewById(R.id.img_icon);
				holder.tvGoodsName = (TextView) inflate
						.findViewById(R.id.tv_title);
				holder.tvGoodsPrice = (TextView) inflate
						.findViewById(R.id.tv_price);
				inflate.setTag(holder);
			} else {
				inflate = convertView;
				holder = (ViewHolder) inflate.getTag();
			}
			GoodsInfo info = historyList.get(position);
			holder.tvGoodsName.setText(info.getGoodsName());
			holder.tvGoodsPrice.setText(NumberUtils.formatPrice(info
					.getGoodsPrice()));
			UILUtils.displayImage(DetailActivity.this, info.getGoodsIcon(),
					holder.imgGoods);
			return inflate;
		}

		@Override
		public int getCount() {
			return historyList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

	}

	class ViewHolder {
		ImageView imgGoods;
		TextView tvGoodsName;
		TextView tvGoodsPrice;
	}

	class HistoryTask extends AsyncTask<Void, Void, List<GoodsInfo>> {

		@Override
		protected List<GoodsInfo> doInBackground(Void... params) {
			return DBUtils.getHistory();
		}

		@Override
		protected void onPostExecute(List<GoodsInfo> result) {
			super.onPostExecute(result);
			historyList.clear();
			historyList.addAll(result);
			mAdapter.notifyDataSetChanged();
			DetailActivity.this.findViewById(R.id.progressBar1).setVisibility(
					View.GONE);
		}

	}

}
