package com.android.yttmarket.fragment;

import java.util.HashMap;
import java.util.List;

import com.an.cityselect.City;
import com.an.cityselect.CityConstant;
import com.an.cityselect.CitySelectActivity;
import com.android.yttmarket.activity.GoodsListActivity;
import com.android.yttmarket.bean.CategoryMenu.CategoryItem;
import com.android.yttmarket.bean.CategoryMenu.CategoryItem.Menu;
import com.android.yttmarket.utils.ListViewForScrollView;
import com.android.yttmarket.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class FilterMenuFragment extends Fragment implements OnClickListener,
		OnCheckedChangeListener {

	// private ArrayList<String> mTypes = new ArrayList<String>();
	private View layout;
	private FilterListAdapter mAdapter;
	private ToggleButton mTgBtnSendJD; // 京东配送
	private ToggleButton mTgBtnReadLeftOnly; // 仅看有货
	private ToggleButton mTgBtnCOD; // 货到付款

	private boolean isSendJD; // 是否京东配送
	private boolean isReadLeftOnly; // 是否仅看有货
	private boolean isCOD; // 是否货到付款
	private CategoryItem categoryItem;
	private List<Menu> menu;

	private City city;
	private TextView mTvSendCity; // 地址
	private int selectedPosition;	//点击菜单的position
	
	private HashMap<Integer, String> resultMap = new HashMap<Integer, String>();	//存储选择结果

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initFilterTypes();
		layout = inflater.inflate(R.layout.fragment_filter_menu, container,
				false);
		initView();
		setOnListener();
		initListView();
		return layout;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == CityConstant.RESULT_CITY
				&& requestCode == CityConstant.REQUEST_CITY) {
			City resultCity = data.getParcelableExtra(CityConstant.CITY_CODE);
			if (resultCity.getProvince().equals("")) {
				// 如果没有选择省份，则保持原来的地址
				return;
			}
			city = resultCity;
			mTvSendCity.setText(city.getProvince() + city.getCity()
					+ city.getDistrict());
		}
	}

	private void initView() {
		mTgBtnSendJD = (ToggleButton) layout
				.findViewById(R.id.tgbtn_filter_send_jd);
		mTgBtnReadLeftOnly = (ToggleButton) layout
				.findViewById(R.id.tgbtn_filter_read_left_only);
		mTgBtnCOD = (ToggleButton) layout.findViewById(R.id.tgbtn_filter_cod);
		mTvSendCity = (TextView) layout.findViewById(R.id.tv_send_city);
		//设置保存的地址z   
		SharedPreferences sp = getActivity().getSharedPreferences(
				"baidumap_location", Context.MODE_PRIVATE);
		String location = sp.getString("location", "北京市");
		mTvSendCity.setText(location);
	}

	private void setOnListener() {
		mTgBtnSendJD.setOnCheckedChangeListener(this);
		mTgBtnReadLeftOnly.setOnCheckedChangeListener(this);
		mTgBtnCOD.setOnCheckedChangeListener(this);
		layout.findViewById(R.id.tv_ok).setOnClickListener(this);
		layout.findViewById(R.id.tv_cancel).setOnClickListener(this);
		layout.findViewById(R.id.btn_reset_menu).setOnClickListener(this);
		layout.findViewById(R.id.layout_destination).setOnClickListener(this);
	}

	/**
	 * 初始化类型选择项
	 */
	private void initListView() {
		ListViewForScrollView listView = (ListViewForScrollView) layout
				.findViewById(R.id.listView_filter_menu);
		mAdapter = new FilterListAdapter();
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				selectedPosition = position;
				addToStack(position, menu.get(position).getMenuname());
			}
		});
	}

	/**
	 * 初始化类型数据
	 */
	private void initFilterTypes() {
		menu = categoryItem.getMenu();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_ok:
		case R.id.tv_cancel:
			((GoodsListActivity) getActivity()).toggleFilterMenu();
			break;
		case R.id.btn_reset_menu: // 重置过滤菜单
			resetMenu();
			break;
		case R.id.layout_destination: // 城市选择
			selectCity();
			break;

		default:
			break;
		}
	}

	/**
	 * 城市选择
	 */
	private void selectCity() {
		Intent in = new Intent(getActivity(), CitySelectActivity.class);
		in.putExtra(CityConstant.CITY_CODE, city);
		startActivityForResult(in, CityConstant.REQUEST_CITY);
		getActivity().overridePendingTransition(R.anim.slide_in_right,
				R.anim.slide_out_left);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO
		switch (buttonView.getId()) {
		case R.id.tgbtn_filter_send_jd: // 京东配送
			isSendJD = !isSendJD;
			break;
		case R.id.tgbtn_filter_read_left_only: // 仅看有货
			isReadLeftOnly = !isReadLeftOnly;
			break;
		case R.id.tgbtn_filter_cod: // 货到付款
			isCOD = !isCOD;
			break;

		default:
			break;
		}
	}

	/**
	 * 片段添加到栈
	 * 
	 * @param type
	 * @param filterMenuFragment
	 */
	private void addToStack(int position, String title) {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		// 栈加入弹出动画 动画要写在add()方法之前
		ft.setCustomAnimations(R.anim.slide_in_right, 0, 0,
				R.anim.slide_out_right);
		FilterMenuFragment2 menuFragment = new FilterMenuFragment2();
		menuFragment.setMenu(menu.get(position), title, position);
		ft.add(R.id.menu_container, menuFragment);
		// ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		ft.addToBackStack(null);
		ft.commit();
	}

	/**
	 * 重置过滤菜单
	 */
	private void resetMenu() {
		mTgBtnSendJD.setChecked(false);
		mTgBtnCOD.setChecked(false);
		mTgBtnReadLeftOnly.setChecked(false);
		resultMap.clear();
		mAdapter.notifyDataSetChanged();
	}

	class FilterListAdapter extends BaseAdapter {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View inflate = getActivity().getLayoutInflater().inflate(
					R.layout.item_fragment_filter_menu_list, null);
			TextView tvType = (TextView) inflate.findViewById(R.id.tv_type);
			TextView tvSelectedResult = (TextView) inflate
					.findViewById(R.id.tv_selected_result);
			String result = resultMap.get(position);
			if (result != null) {
				if (result.equals("全部")) {
					tvSelectedResult.setTextColor(Color.BLACK);
				} else {
					tvSelectedResult.setTextColor(Color.RED);
				}
				tvSelectedResult.setText(result);
			} else {
				tvSelectedResult.setTextColor(Color.BLACK);
				tvSelectedResult.setText("全部");
			}
			tvType.setText(menu.get(position).getMenuname());
			return inflate;
		}

		@Override
		public int getCount() {
			return menu.size();
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

	/**
	 * 传入菜单
	 * 
	 * @param categoryItem
	 */
	public void setCategoryItem(CategoryItem categoryItem) {
		this.categoryItem = categoryItem;
	}

	public void setSelectedResult(String result) {
		resultMap.put(selectedPosition, result);
		mAdapter.notifyDataSetChanged();
	}

}
