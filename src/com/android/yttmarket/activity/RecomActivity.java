package com.android.yttmarket.activity;

import com.android.yttmarket.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

public class RecomActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recom);
		View btnIgnore = findViewById(R.id.btn_ignore);
		btnIgnore.setOnClickListener(this);
		btnIgnore.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				gotoMain();
			}
		}, 2000);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ignore:
			gotoMain();
			break;

		default:
			break;
		}
	}

	private void gotoMain() {
		startActivity(new Intent(this, MainActivity.class));
		finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//不允许返回
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}
