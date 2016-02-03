package com.android.yttmarket.dialogfragment;

import com.android.yttmarket.R;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class LightCtrlDialogFragment extends DialogFragment implements OnClickListener {

	private FragmentActivity mActivity;
	private SeekBar mSeekBarBrightness;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = getActivity();
		View inflate = inflater.inflate(R.layout.fragment_dialog_light_ctrl, null);
		mSeekBarBrightness = (SeekBar) inflate.findViewById(R.id.seekBar_light);
		inflate.findViewById(R.id.btn_ok).setOnClickListener(this);
		inflate.findViewById(R.id.btn_default).setOnClickListener(this);
		initBrightness();
		getDialog().getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.TRANSPARENT));
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		return inflate;
	}
	
	/**
	 * 亮度调节
	 */
	private void initBrightness() {
		// 取得当前亮度
		int normal = Settings.System.getInt(mActivity.getContentResolver(),
				Settings.System.SCREEN_BRIGHTNESS, 255);
		// 进度条绑定当前亮度
		mSeekBarBrightness.setProgress(normal);
		mSeekBarBrightness
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// 取得当前进度
						int tmpInt = seekBar.getProgress();

						// 当进度小于80时，设置成80，防止太黑看不见的后果。
						if (tmpInt < 80) {
							tmpInt = 80;
						}
						// 根据当前进度改变亮度
						WindowManager.LayoutParams wl = mActivity.getWindow()
								.getAttributes();
						float tmpFloat = (float) tmpInt / 255;
						if (tmpFloat > 0 && tmpFloat <= 1) {
							wl.screenBrightness = tmpFloat;
						}
						mActivity.getWindow().setAttributes(wl);
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
					}
				});

	}
	
	@Override
	public void onClick(View v) {
		dismiss();
		switch (v.getId()) {
		case R.id.btn_ok:
			break;
		case R.id.btn_default:
			int tmpInt = Settings.System.getInt(mActivity.getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS, -1);
			WindowManager.LayoutParams wl = mActivity.getWindow().getAttributes();
			float tmpFloat = (float) tmpInt / 255;
			if (tmpFloat > 0 && tmpFloat <= 1) {
				wl.screenBrightness = tmpFloat;
			}
			mActivity.getWindow().setAttributes(wl);
			break;

		default:
			break;
		}
	}
	
}
