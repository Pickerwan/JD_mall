package com.android.yttmarket.adapter;

import java.util.List;

import com.android.yttmarket.R;
import com.android.yttmarket.bean.IndexGalleryItemData;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class GalleryAdapter extends
		RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

	/**
	 * ItemClick的回调接口
	 * 
	 * @author ydy
	 * 
	 */
	public interface OnItemClickLitener {
		void onItemClick(View view, int position);
	}

	private OnItemClickLitener mOnItemClickLitener;

	public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
		this.mOnItemClickLitener = mOnItemClickLitener;
	}

	private LayoutInflater mInflater;
	private List<IndexGalleryItemData> listData = null;
	private ImageLoader imageLoader = ImageLoader.getInstance();

	public GalleryAdapter(Context context, List<IndexGalleryItemData> datats) {
		mInflater = LayoutInflater.from(context);
		listData = datats;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public ViewHolder(View arg0) {
			super(arg0);
		}

		ImageView mImg;
		TextView mTxt;
		TextView mDesc;
	}

	@Override
	public int getItemCount() {
		return listData.size();
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return super.getItemId(position);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View view = mInflater.inflate(R.layout.activity_index_gallery_item,
				viewGroup, false);
		ViewHolder viewHolder = new ViewHolder(view);

		viewHolder.mImg = (ImageView) view
				.findViewById(R.id.id_index_gallery_item_image);
		viewHolder.mDesc = (TextView) view
				.findViewById(R.id.index_gallery_item_desc);
		
		viewHolder.mTxt = (TextView) view
				.findViewById(R.id.index_gallery_item_text);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(final ViewHolder viewHolder, final int i) {

		imageLoader
				.displayImage(listData.get(i).getImageUrl(), viewHolder.mImg);
		
		viewHolder.mTxt.setText(listData.get(i).getPrice());
		viewHolder.mDesc.setText(listData.get(i).getDesc());
		// viewHolder.mImg.setImageResource(listData.get(i));

		// 如果设置了回调，则设置点击事件
		if (mOnItemClickLitener != null) {
			viewHolder.itemView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mOnItemClickLitener.onItemClick(viewHolder.itemView, i);
				}
			});

		}

	}

}