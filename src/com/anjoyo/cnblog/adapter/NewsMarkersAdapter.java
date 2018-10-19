package com.anjoyo.cnblog.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.anjoyo.cnblog.activitys.R;
import com.anjoyo.cnblog.entry.HotNewsInfo;

public class NewsMarkersAdapter extends BaseAdapter {
	ArrayList<HotNewsInfo> list;
	LayoutInflater inflater;

	public NewsMarkersAdapter(Context context, ArrayList<HotNewsInfo> list) {
		inflater = LayoutInflater.from(context);
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public HotNewsInfo getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return list.get(position).getId();
	}

	static class ViewHolder {
		TextView markTitle;
		TextView markLink;
		CheckBox chk;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.newsmarkers_item, null);
			holder.markTitle = (TextView) convertView
					.findViewById(R.id.markers_title);
			holder.markLink = (TextView) convertView
					.findViewById(R.id.markers_link);
			holder.chk = (CheckBox) convertView.findViewById(R.id.check_button);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (position % 2 == 0) {
			convertView.setBackgroundColor(0xFFC9DAF9);
		} else {
			convertView.setBackgroundColor(0xFFFFFFFF);
		}
		holder.markTitle.setText(list.get(position).getTitle());
		holder.markLink.setText(list.get(position).getLink());
		holder.chk.setChecked(list.get(position).isChecked());
		holder.chk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				list.get(position).setChecked(holder.chk.isChecked());
			}
		});

		return convertView;
	}

}
