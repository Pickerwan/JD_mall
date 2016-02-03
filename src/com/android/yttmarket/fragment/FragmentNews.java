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
public class FragmentNews extends Fragment implements OnClickListener {

	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (view != null) {
			// 防止多次new出片段对象，造成图片错乱问题
			return view;
		}
		view = inflater.inflate(R.layout.fragment_news, container, false);
		initview();
		return view;
	}

	private void initview() {
		view.findViewById(R.id.layout_notification_message).setOnClickListener(
				this);
		view.findViewById(R.id.layout_activity_yuntt).setOnClickListener(
				this);
		view.findViewById(R.id.layout_with_customer).setOnClickListener(
				this);
		view.findViewById(R.id.layout_gourmet_secrets).setOnClickListener(
				this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_notification_message:
             ToastUtils.showToast(getActivity(), "通知消息");
			break;
		case R.id.layout_activity_yuntt:
			ToastUtils.showToast(getActivity(), "云天团活动");
			break;
		case R.id.layout_with_customer:
			ToastUtils.showToast(getActivity(), "与客服聊天");
			break;
		case R.id.layout_gourmet_secrets:
			ToastUtils.showToast(getActivity(), "美食秘籍");
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
