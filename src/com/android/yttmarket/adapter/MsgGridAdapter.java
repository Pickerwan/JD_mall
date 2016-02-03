package com.android.yttmarket.adapter;

import com.android.yttmarket.R;
import com.lib.uil.UILUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MsgGridAdapter extends BaseAdapter {
	
	Context context;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		ViewHolder viewholder = null;
		if (convertView == null) {
			viewholder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(
					R.layout.item_category_grid, null);
			viewholder.imgCategoryGrid = (ImageView) convertView
					.findViewById(R.id.img_category_grid);
			viewholder.tvCategoryGrid = (TextView) convertView
					.findViewById(R.id.tv_category_grid);
			view.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}
//		viewholder.tvCategoryGrid.setText(menuList.get(selectedPosition)
//				.getCategoryitem().get(position).getTypename());
//		UILUtils.displayImage(context, menuList.get(selectedPosition)
//				.getCategoryitem().get(position).getImgurl(), holder.imgCategoryGrid);
		return convertView;
	}

	@Override
	public int getCount() {
//		if (menuList.size() > 0) {
//			categoryitem = menuList.get(selectedPosition).getCategoryitem();
//			return categoryitem.size();
//		}
		return 0;
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
	ImageView imgCategoryGrid;
	TextView tvCategoryGrid;
}
