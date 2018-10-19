package com.anjoyo.cnblog.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anjoyo.cnblog.activitys.R;
import com.anjoyo.cnblog.activitys.setting.SettingActivity;

public class LeftmenuBgAtapter extends BaseAdapter {
	private List<Integer> list;
	private Context context;

	public LeftmenuBgAtapter(Context context, List<Integer> list) {
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		final ViewHolder vh;

		if (arg1 == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			arg1 = inflater.inflate(R.layout.set_bg_item, null);
			vh = new ViewHolder();
			vh.name = (TextView) arg1.findViewById(R.id.bg_name);
			vh.iv = (ImageView) arg1.findViewById(R.id.left_menu_bg);
			vh.ck = (ImageView) arg1.findViewById(R.id.check);
			arg1.setTag(vh);
		} else {
			vh = (ViewHolder) arg1.getTag();
		}
		vh.iv.setBackgroundResource(list.get(arg0));
		if (arg0 == 0)
			vh.name.setText("默认壁纸");
		else
			vh.name.setText("壁纸" + arg0);
		if (arg0 == SettingActivity.checkPositon) {
			vh.ck.setVisibility(View.VISIBLE);
		} else {
			vh.ck.setVisibility(View.INVISIBLE);
		}
		return arg1;
	}

	private static class ViewHolder {
		public TextView name;
		public ImageView iv;
		public ImageView ck;
	}
}
