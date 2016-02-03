package com.android.yttmarket.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.android.yttmarket.R;
import com.android.yttmarket.activity.DetailActivity;
import com.android.yttmarket.activity.LoginActivity;
import com.android.yttmarket.activity.MainActivity;
import com.android.yttmarket.bean.GoodsInfo;
import com.android.yttmarket.bean.InCart;
import com.android.yttmarket.constants.Constants;
import com.android.yttmarket.utils.DBUtils;
import com.android.yttmarket.utils.NumberUtils;
import com.android.yttmarket.view.DrawerView;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.lib.uil.ScreenUtils;
import com.lib.uil.ToastUtils;
import com.lib.uil.UILUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class CartFragment extends Fragment implements OnClickListener {

	private View root;
	private View layoutNull;
	private CartAdapter mAdapter;
	private View mViewLogin;
	private SwipeMenuListView mListView;
	private TextView mTvPrice; // 合计
	private TextView mTvTotal; // 总额
	private TextView mTvCount; // 选中商品数
	private CheckBox mBtnCheckAll;
	private CheckBox mBtnCheckAllEdit;
	private TextView mTvEdit;
	private View layoutEditBar;
	private View layoutPayBar;
	private ProgressBar mProgressBar;
	private TextView mTvAddUp;

	private ArrayList<InCart> mData = new ArrayList<InCart>();
	private HashMap<String, Boolean> inCartMap = new HashMap<String, Boolean>();// 用于存放选中的项

	private double price; // 总价
	private int num; // 选中的商品数

	private boolean isEdit; // 是否正在编辑

	private OnCheckedChangeListener checkAllListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			mBtnCheckAll.setChecked(isChecked);
			mBtnCheckAllEdit.setChecked(isChecked);
			if (isChecked) {
				checkAll();
			} else {
				inCartMap.clear();
			}
			notifyCheckedChanged();
			mAdapter.notifyDataSetChanged();
		}

	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		MainActivity activity = (MainActivity) getActivity();
		boolean isLogined = activity.getLogined();
		if (root == null) {
			root = inflater.inflate(R.layout.fragment_cart, container, false);

			initView();
			if (isLogined) {
				mViewLogin.setVisibility(View.GONE);
			}
			setOnListener();
			initListView();
			initData();
		} else {
			ViewGroup parent = (ViewGroup) root.getParent();
			if (parent != null)
				parent.removeView(root);
			mProgressBar.setVisibility(View.VISIBLE);
			initData();
			if (isLogined) {
				mViewLogin.setVisibility(View.GONE);
			}
		}

		return root;
//
//		MainActivity activity = (MainActivity) getActivity();
//		boolean isLogined = activity.getLogined();
//		if (layout != null) {
//			mProgressBar.setVisibility(View.VISIBLE);
//			initData();
//			if (isLogined) {
//				mViewLogin.setVisibility(View.GONE);
//			}
//			// 防止多次new出片段对象，造成图片错乱问题
//			return layout;
//		}
//		initView();
//		if (isLogined) {
//			mViewLogin.setVisibility(View.GONE);
//		}
//		setOnListener();
//		initListView();
//		initData();
	}

	private void setOnListener() {
		mTvEdit.setOnClickListener(this);
		// 防止点击穿透
		layoutEditBar.setOnClickListener(this);
		layoutPayBar.setOnClickListener(this);
		mBtnCheckAll.setOnCheckedChangeListener(checkAllListener);
		mBtnCheckAllEdit.setOnCheckedChangeListener(checkAllListener);
		root.findViewById(R.id.btn_login_cart).setOnClickListener(this);
		root.findViewById(R.id.btn_collect).setOnClickListener(this);
		root.findViewById(R.id.btn_delete).setOnClickListener(this);
		root.findViewById(R.id.btn_pay).setOnClickListener(this);
		root.findViewById(R.id.btn_more).setOnClickListener(this);
	}

	private void initView() {
		layoutNull = root.findViewById(R.id.layout_null);
		mTvEdit = (TextView) root.findViewById(R.id.tv_edit_cart);
		mTvPrice = (TextView) root.findViewById(R.id.tv_price);
		mTvTotal = (TextView) root.findViewById(R.id.tv_total);
		mTvCount = (TextView) root.findViewById(R.id.tv_count);
		mBtnCheckAll = (CheckBox) root.findViewById(R.id.btn_check_all);
		mBtnCheckAllEdit = (CheckBox) root
				.findViewById(R.id.btn_check_all_deit);
		mViewLogin = root.findViewById(R.id.layout_login_cart);
		layoutEditBar = root.findViewById(R.id.layout_edit_bar);
		layoutPayBar = root.findViewById(R.id.layout_pay_bar);
		mProgressBar = (ProgressBar) root.findViewById(R.id.progressBar_cart);
	}

	private void initData() {
		// 异步从数据库中获取数据
		new InCartTask().execute();
	}

	/**
	 * 选中商品改变
	 */
	private void notifyCheckedChanged() {
		price = 0;
		num = 0;
		for (int i = 0; i < mData.size(); i++) {
			Boolean isChecked = inCartMap.get(mData.get(i).getGoodsId());
			if (isChecked != null && isChecked) {
				InCart inCart = mData.get(i);
				num += inCart.getNum();
				price += inCart.getGoodsPrice() * inCart.getNum();
			}
		}
		mTvPrice.setText(NumberUtils.formatPrice(price));
		mTvTotal.setText("总额：" + NumberUtils.formatPrice(price));
		mTvCount.setText("(" + num + ")");
		mTvAddUp.setText("小计：" + NumberUtils.formatPrice(price));

	}

	/**
	 * 通知更新购物车商品数量
	 */
	private void notifyInCartNumChanged() {
		// 通知主页刷新购物车商品数
		Intent intent = new Intent();
		intent.setAction(Constants.BROADCAST_FILTER.FILTER_CODE);
		intent.putExtra(Constants.BROADCAST_FILTER.EXTRA_CODE,
				Constants.INTENT_KEY.REFRESH_INCART);
		getActivity().sendBroadcast(intent);
	}

	private void initListView() {
		mListView = (SwipeMenuListView) root.findViewById(R.id.listView_cart);
		View foot = getActivity().getLayoutInflater().inflate(
				R.layout.foot_cart_list, null);
		mTvAddUp = (TextView) foot.findViewById(R.id.tv_add_up);
		mListView.addFooterView(foot, null, false);
		mAdapter = new CartAdapter();
		mListView.setAdapter(mAdapter);
		// step 1. create a MenuCreator
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "open" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				// set item width
				deleteItem
						.setWidth(ScreenUtils.getScreenWidth(getActivity()) / 3);
				// set item title
				deleteItem.setTitle("删除");
				// set item title fontsize
				deleteItem.setTitleSize(18);
				// set item title font color
				deleteItem.setTitleColor(Color.WHITE);
				// add to menu
				menu.addMenuItem(deleteItem);

			}
		};
		// set creator
		mListView.setMenuCreator(creator);

		// step 2. listener item click event
		mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				// index是menu的菜单序号
				deleteItem(position);
			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				InCart inCart = mData.get(position);
				GoodsInfo info = new GoodsInfo(inCart.getGoodsId(), inCart
						.getGoodsName(), inCart.getGoodsIcon(), inCart
						.getGoodsType(), inCart.getGoodsPrice(), inCart
						.getGoodsPercent(), inCart.getGoodsComment(), inCart
						.getIsPhone(), inCart.getIsFavor());
				Intent intent = new Intent(getActivity(), DetailActivity.class);
				intent.putExtra(Constants.INTENT_KEY.INFO_TO_DETAIL, info);
				startActivityForResult(intent,
						Constants.INTENT_KEY.REQUEST_CART_TO_DETAIL);
				getActivity().overridePendingTransition(0, 0);
			}
		});
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		// 将layout从父组件中移除
		ViewGroup parent = (ViewGroup) root.getParent();
		parent.removeView(root);
	}

	/**
	 * 获取数字
	 * 
	 * @param tvNum
	 * @return
	 */
	private int getNum(TextView tvNum) {
		String num = tvNum.getText().toString().trim();
		return Integer.valueOf(num);

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
					break;

				default:
					break;
				}
				mViewLogin.setVisibility(View.GONE);
				MainActivity activity = (MainActivity) getActivity();
				// 将登录结果设置给MainActivity
				activity.setIsLogined(true, uid, icon);
			}
		} else if (requestCode == Constants.INTENT_KEY.REQUEST_CART_TO_DETAIL) {
			// 刷新购物车
			initData();
			// 刷新价格
			notifyCheckedChanged();
		}
	}

	class CartAdapter extends BaseAdapter {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View inflate = null;
			ViewHolder holder = null;
			if (convertView == null) {
				// 复用乱序问题
				inflate = getActivity().getLayoutInflater().inflate(
						R.layout.item_fragment_cart_list, null);
				holder = new ViewHolder();
				holder.btnCheck = (CheckBox) inflate
						.findViewById(R.id.btn_check);
				holder.btnReduce = (Button) inflate
						.findViewById(R.id.btn_cart_reduce);
				holder.btnAdd = (Button) inflate
						.findViewById(R.id.btn_cart_add);
				holder.btnNumEdit = (Button) inflate
						.findViewById(R.id.btn_cart_num_edit);
				holder.imgGoods = (ImageView) inflate
						.findViewById(R.id.img_goods);
				holder.tvGoodsName = (TextView) inflate
						.findViewById(R.id.tv_goods_name);
				holder.tvGoodsPrice = (TextView) inflate
						.findViewById(R.id.tv_goods_price);
				inflate.setTag(holder);
			} else {
				inflate = convertView;
				holder = (ViewHolder) inflate.getTag();
			}
			final InCart inCart = mData.get(position);
			holder.tvGoodsName.setText(inCart.getGoodsName());
			holder.tvGoodsPrice.setText(NumberUtils.formatPrice(inCart
					.getGoodsPrice()));
			holder.btnNumEdit.setText("" + inCart.getNum());
			UILUtils.displayImage(getActivity(), inCart.getGoodsIcon(),
					holder.imgGoods);
			if (inCart.getNum() > 1) {
				holder.btnReduce.setEnabled(true);
			} else {
				holder.btnReduce.setEnabled(false);
			}
			// 避免由于复用触发onChecked()事件
			holder.btnCheck.setOnCheckedChangeListener(null);
			Boolean isChecked = inCartMap.get(inCart.getGoodsId());
			if (isChecked != null && isChecked) {
				holder.btnCheck.setChecked(true);
			} else {
				holder.btnCheck.setChecked(false);
			}
			final ViewHolder holder2 = holder;
			holder.btnReduce.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int num = getNum(holder2.btnNumEdit);
					num--;
					inCart.setNum(num);
					inCart.save();
					notifyInCartNumChanged();
					// 如果被选中，更新价格
					if (holder2.btnCheck.isChecked()) {
						notifyCheckedChanged();
					}
					Log.e("onClick", "holder2.btnCheck.isChecked() = "
							+ holder2.btnCheck.isChecked());
					holder2.btnNumEdit.setText("" + num);
					if (num == 1) {
						holder2.btnReduce.setEnabled(false);
					}
				}
			});
			holder.btnAdd.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					holder2.btnReduce.setEnabled(true);
					int num = getNum(holder2.btnNumEdit);
					num++;
					inCart.setNum(num);
					inCart.save();
					notifyInCartNumChanged();
					// 如果被选中，更新价格
					if (holder2.btnCheck.isChecked()) {
						notifyCheckedChanged();
					}
					Log.e("onClick", "holder2.btnCheck.isChecked() = "
							+ holder2.btnCheck.isChecked());
					holder2.btnNumEdit.setText("" + num);
				}
			});
			holder.btnCheck
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							if (isChecked) {
								inCartMap.put(inCart.getGoodsId(), isChecked);
								// 如果所有项都被选中，则点亮全选按钮
								if (inCartMap.size() == mData.size()) {
									mBtnCheckAll.setChecked(true);
									mBtnCheckAllEdit.setChecked(true);
								}
							} else {
								// 如果之前是全选状态，则取消全选状态
								if (inCartMap.size() == mData.size()) {
									mBtnCheckAll
											.setOnCheckedChangeListener(null);
									mBtnCheckAllEdit
											.setOnCheckedChangeListener(null);
									mBtnCheckAll.setChecked(false);
									mBtnCheckAllEdit.setChecked(false);
									mBtnCheckAll
											.setOnCheckedChangeListener(checkAllListener);
									mBtnCheckAllEdit
											.setOnCheckedChangeListener(checkAllListener);
								}
								inCartMap.remove(inCart.getGoodsId());
							}
							notifyCheckedChanged();
						}
					});
			return inflate;
		}

		@Override
		public int getCount() {
			// 若mData.size() == 0，显示layoutNull
			if (mData.size() == 0) {
				mListView.setVisibility(View.GONE);
				mTvEdit.setVisibility(View.GONE);
				layoutEditBar.setVisibility(View.GONE);
				layoutPayBar.setVisibility(View.GONE);
				layoutNull.setVisibility(View.VISIBLE);
				isEdit = false;
			} else {
				mListView.setVisibility(View.VISIBLE);
				mTvEdit.setVisibility(View.VISIBLE);
				layoutNull.setVisibility(View.GONE);
				if (isEdit) {
					layoutEditBar.setVisibility(View.VISIBLE);
				} else {
					layoutPayBar.setVisibility(View.VISIBLE);
				}
			}
			return mData.size();
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
		CheckBox btnCheck;
		Button btnAdd;
		Button btnReduce;
		Button btnNumEdit;
		ImageView imgGoods;
		TextView tvGoodsName;
		TextView tvGoodsPrice;
	}

	class InCartTask extends AsyncTask<Void, Void, List<InCart>> {

		@Override
		protected List<InCart> doInBackground(Void... params) {
			return DBUtils.getInCart();
		}

		@Override
		protected void onPostExecute(List<InCart> result) {
			super.onPostExecute(result);
			mData.clear();
			mData.addAll(result);
			if (mBtnCheckAll.isChecked()) {
				checkAll();
			}
			mAdapter.notifyDataSetChanged();
			notifyCheckedChanged();
			if (mData.size() == 0) {
				mListView.setVisibility(View.GONE);
			} else {
				mListView.setVisibility(View.VISIBLE);
			}
			mProgressBar.setVisibility(View.GONE);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login_cart: // 登录
			gotoLogin();
			break;
		case R.id.btn_collect: // 移入关注
			add2Collect();
			break;
		case R.id.btn_delete: // 删除
			deleteInCart();
			break;
		case R.id.tv_edit_cart: // 编辑
			editInCart();
			break;
		case R.id.btn_pay: // 结算
			pay();
			break;
		case R.id.btn_more: // 去秒杀
			MainActivity activity = (MainActivity) getActivity();
			activity.activeCategory();
			break;

		default:
			break;
		}
	}

	/**
	 * 结算
	 */
	private void pay() {
		if (num == 0) {
			ToastUtils.showToast(getActivity(), "您还没有选择商品哦！");
		} else {
			ToastUtils.showToast(getActivity(), "恭喜，付款成功！");
		}
	}

	/**
	 * 全选，将数据加入inCartMap
	 */
	private void checkAll() {
		for (int i = 0; i < mData.size(); i++) {
			inCartMap.put(mData.get(i).getGoodsId(), true);
		}
	}

	/**
	 * 删除列表项
	 */
	private void deleteItem(int position) {
		InCart inCart = mData.get(position);
		DBUtils.deleteCart(inCart);
		inCartMap.remove(inCart.getGoodsId());
		mData.remove(position);
		notifyCheckedChanged();
		notifyInCartNumChanged();
		mAdapter.notifyDataSetChanged();

	}

	/**
	 * 编辑
	 */
	private void editInCart() {
		isEdit = !isEdit;
		if (isEdit) {
			mTvEdit.setText("完成");
			layoutPayBar.setVisibility(View.GONE);
			layoutEditBar.setVisibility(View.VISIBLE);
		} else {
			mTvEdit.setText("编辑");
			layoutPayBar.setVisibility(View.VISIBLE);
			layoutEditBar.setVisibility(View.GONE);
		}
	}

	/**
	 * 删除选中项
	 */
	private void deleteInCart() {
		// TODO Auto-generated method stub
		if (inCartMap.size() == 0) {
			ToastUtils.showToast(getActivity(), "您还没有选择商品哦！");
			return;
		}
		for (int i = 0; i < mData.size(); i++) {
			Boolean isChecked = inCartMap.get(mData.get(i).getGoodsId());
			if (isChecked != null && isChecked) {
				InCart inCart = mData.get(i);
				DBUtils.deleteCart(inCart);
			}
		}
		inCartMap.clear();
		mBtnCheckAll.setChecked(false);
		mBtnCheckAllEdit.setChecked(false);
		notifyCheckedChanged();
		notifyInCartNumChanged();
		initData();
	}

	/**
	 * 移入关注
	 */
	private void add2Collect() {
		if (inCartMap.size() == 0) {
			ToastUtils.showToast(getActivity(), "您还没有选择商品哦！");
			return;
		}
		for (int i = 0; i < mData.size(); i++) {
			Boolean isChecked = inCartMap.get(mData.get(i).getGoodsId());
			if (isChecked != null && isChecked) {
				InCart inCart = mData.get(i);
				GoodsInfo goodsInfo = new GoodsInfo(inCart.getGoodsId(),
						inCart.getGoodsName(), inCart.getGoodsIcon(),
						inCart.getGoodsType(), inCart.getGoodsPrice(),
						inCart.getGoodsPercent(), inCart.getGoodsComment(),
						inCart.getIsPhone(), 1);
				// 如果没有收藏过，则加入收藏
				if (!DBUtils.hasFavor(goodsInfo)) {
					goodsInfo.save();
				}
			}
		}
		ToastUtils.showToast(getActivity(), "关注成功！");
	}

	/**
	 * 跳转到登录界面
	 */
	private void gotoLogin() {
		Intent intent = new Intent(getActivity(), LoginActivity.class);
		startActivityForResult(intent, Constants.INTENT_KEY.LOGIN_REQUEST_CODE);
		getActivity().overridePendingTransition(0, 0);
	}

}
