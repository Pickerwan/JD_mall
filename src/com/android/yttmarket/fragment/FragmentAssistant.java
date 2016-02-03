package com.android.yttmarket.fragment;

import com.android.yttmarket.R;
import com.lib.uil.ToastUtils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class FragmentAssistant extends Fragment implements OnClickListener {

	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (view != null) {
			// 防止多次new出片段对象，造成图片错乱问题
			return view;
		}
		view = inflater.inflate(R.layout.fragment_assistant, container, false);
		initview();
		return view;
	}

	private void initview() {
		view.findViewById(R.id.layout_express_query).setOnClickListener(this);
		view.findViewById(R.id.layout_weather).setOnClickListener(this);
		view.findViewById(R.id.layout_region).setOnClickListener(this);

	}

	@Override
	public void onClick(View V) {
		switch (V.getId()) {
		case R.id.layout_express_query:
			ToastUtils.showToast(getActivity(), "快递查询");
			break;
		case R.id.layout_weather:
			ToastUtils.showToast(getActivity(), "天气");
			break;
		case R.id.layout_region:
			ToastUtils.showToast(getActivity(), "地区");
			break;

		default:
			break;
		}

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		// 将layout从父组件中移除
		ViewGroup parent = (ViewGroup) view.getParent();
		parent.removeView(view);
	}

}
