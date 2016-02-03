package com.android.yttmarket.dialogfragment;

import com.android.yttmarket.R;
import com.android.yttmarket.utils.CacheUtils;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class CacheDialogFragment extends DialogFragment implements OnClickListener {

	private FragmentActivity mActivity;
	private TextView mTvCacheSize;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = getActivity();
		View inflate = inflater.inflate(R.layout.fragment_dialog_cache, null);
		inflate.findViewById(R.id.btn_ok).setOnClickListener(this);
		inflate.findViewById(R.id.btn_cancel).setOnClickListener(this);
		getDialog().getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.TRANSPARENT));
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		return inflate;
	}
	
	/**
	 * 用于显示缓存大小的TextView
	 */
	public void setTextView(TextView mTvCacheSize) {
		this.mTvCacheSize = mTvCacheSize;
	}

	@Override
	public void onClick(View v) {
		dismiss();
		switch (v.getId()) {
		case R.id.btn_ok:
			clearCache();
			break;

		default:
			break;
		}
	}

	private void clearCache() {
		mTvCacheSize.setText("0KB");
		CacheUtils.clearCache();
		Toast.makeText(mActivity, "缓存已清除", Toast.LENGTH_SHORT).show();
	}
	
}
