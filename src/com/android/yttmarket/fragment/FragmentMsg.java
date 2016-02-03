package com.android.yttmarket.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.yttmarket.R;
import com.android.yttmarket.activity.SearchActivity;
import com.android.yttmarket.adapter.StaggeredAdapter;
import com.android.yttmarket.bean.DuitangInfo;
import com.android.yttmarket.utils.Helper;
import com.android.yttmarket.utils.ImageFetcher;
import com.android.yttmarket.view.DrawerView;
import com.android.yttmarket.view.StaggeredGridView;
import com.android.yttmarket.view.StaggeredGridView.OnItemClickListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.AsyncTask.Status;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class FragmentMsg extends Fragment implements OnClickListener {

	/** head 头部 的左侧菜单 按钮 */
	private ImageView top_head;
	protected SlidingMenu side_drawer;

	private ImageFetcher mImageFetcher;
	private StaggeredAdapter mAdapter;
	private ContentTask task = new ContentTask(getActivity(), 2);

	View root;
	private StaggeredGridView gridView;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (root == null) {
			root = inflater.inflate(R.layout.fragment_msg, container, false);
			root.findViewById(R.id.top_head).setVisibility(View.VISIBLE);
			side_drawer = new DrawerView(getActivity()).initSlidingMenu();

			initView();
		} else {
			ViewGroup parent = (ViewGroup) root.getParent();
			if (parent != null)
				parent.removeView(root);
		}

		return root;
	}

	private void initView() {

		top_head = (ImageView) root.findViewById(R.id.top_head);

		root.findViewById(R.id.layout_category_search).setOnClickListener(this);
		mImageFetcher = new ImageFetcher(getActivity(), 240);
		mImageFetcher.setLoadingImage(R.drawable.empty_photo);
		gridView = (StaggeredGridView) root
				.findViewById(R.id.staggeredGridView1);

		int margin = getResources().getDimensionPixelSize(R.dimen.margin);

		gridView.setFastScrollEnabled(true);

		mAdapter = new StaggeredAdapter(getActivity(), mImageFetcher);
		gridView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
		AddItemToContainer(1, 1);
		AddItemToContainer(2, 1);
		AddItemToContainer(3, 1);

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void onItemClick(StaggeredGridView parent, View view,
					int position, long id) {
				Intent intent = new Intent().setClass(getActivity(),
						ImageDetailActivity.class);
				intent.putExtra(ImageDetailActivity.EXTRA_IMAGE, position);
				Bundle bu = new Bundle();
				ArrayList list = new ArrayList();
				list.add(mAdapter.getmInfos());
				bu.putParcelableArrayList("list",
						(ArrayList<? extends Parcelable>) list); // ---
				intent.putExtras(bu);
				startActivity(intent);

			}
		});

		top_head.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (side_drawer.isMenuShowing()) {
					side_drawer.showContent();
				} else {
					side_drawer.showMenu();
				}
			}
		});

	}

	private void AddItemToContainer(int pageindex, int type) {

		if (task.getStatus() != Status.RUNNING) {
			String url = "http://www.duitang.com/album/1733789/masn/p/"
					+ pageindex + "/24/";
			Log.d("MainActivity", "current url:" + url);
			ContentTask task = new ContentTask(getActivity(), type);
			task.execute(url);
		}

	}

	private void gotoSearch() {
		Intent intent = new Intent(getActivity(), SearchActivity.class);
		startActivity(intent);
		// activity开启无动画
		getActivity().overridePendingTransition(0, 0);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_category_search:
			gotoSearch();
			break;

		default:
			break;
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 将layout从父组件中移除
		ViewGroup parent = (ViewGroup) root.getParent();
		parent.removeView(root);
	}

	private class ContentTask extends
			AsyncTask<String, Integer, List<DuitangInfo>> {

		private Context mContext;
		private int mType = 1;

		public ContentTask(Context context, int type) {
			super();
			mContext = context;
			mType = type;
		}

		@Override
		protected List<DuitangInfo> doInBackground(String... params) {
			try {
				return parseNewsJSON(params[0]);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<DuitangInfo> result) {
			if (mType == 1) {

				mAdapter.addItemTop(result);
				mAdapter.notifyDataSetChanged();

			} else if (mType == 2) {
				mAdapter.addItemLast(result);
				mAdapter.notifyDataSetChanged();

			}

		}

		@Override
		protected void onPreExecute() {
		}

		public List<DuitangInfo> parseNewsJSON(String url) throws IOException {
			List<DuitangInfo> duitangs = new ArrayList<DuitangInfo>();
			String json = "";
			if (Helper.checkConnection(mContext)) {
				try {
					json = Helper.getStringFromUrl(url);

				} catch (IOException e) {
					Log.e("IOException is : ", e.toString());
					e.printStackTrace();
					return duitangs;
				}
			}
			Log.d("MainActiivty", "json:" + json);

			try {
				if (null != json) {
					JSONObject newsObject = new JSONObject(json);
					JSONObject jsonObject = newsObject.getJSONObject("data");
					JSONArray blogsJson = jsonObject.getJSONArray("blogs");

					for (int i = 0; i < blogsJson.length(); i++) {
						JSONObject newsInfoLeftObject = blogsJson
								.getJSONObject(i);
						DuitangInfo newsInfo1 = new DuitangInfo();
						newsInfo1
								.setAlbid(newsInfoLeftObject.isNull("albid") ? ""
										: newsInfoLeftObject.getString("albid"));
						newsInfo1
								.setIsrc(newsInfoLeftObject.isNull("isrc") ? ""
										: newsInfoLeftObject.getString("isrc"));
						newsInfo1.setMsg(newsInfoLeftObject.isNull("msg") ? ""
								: newsInfoLeftObject.getString("msg"));
						newsInfo1.setHeight(newsInfoLeftObject.getInt("iht"));
						duitangs.add(newsInfo1);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return duitangs;
		}
	}

}
