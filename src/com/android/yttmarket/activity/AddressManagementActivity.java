package com.android.yttmarket.activity;

import com.an.cityselect.City;
import com.an.cityselect.CityConstant;
import com.an.cityselect.CitySelectActivity;
import com.android.yttmarket.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class AddressManagementActivity extends Activity implements
		OnClickListener {

	private City city;
	private TextView mTvSendCity; // 地址

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address_management);
		initview();
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

	private void initview() {
		findViewById(R.id.btn_save).setOnClickListener(this);
		mTvSendCity=(TextView) findViewById(R.id.tv_city1);
		findViewById(R.id.layout_destination).setOnClickListener(this);		
		
		SharedPreferences sp = this.getSharedPreferences(
				"baidumap_location", Context.MODE_PRIVATE);
		String location = sp.getString("location", "北京市");
		mTvSendCity.setText(location);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.address_management, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.layout_destination:
			Intent in = new Intent(this, CitySelectActivity.class);
			in.putExtra(CityConstant.CITY_CODE, city);
			startActivityForResult(in, CityConstant.REQUEST_CITY);
			this.overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
			break;
		case R.id.btn_save:

			break;

		default:
			break;
		}

	}

}
