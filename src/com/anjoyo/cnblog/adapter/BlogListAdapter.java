package com.anjoyo.cnblog.adapter;

import java.text.ParseException;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.anjoyo.cnblog.activitys.R;
import com.anjoyo.cnblog.entry.BlogsInfo;
import com.anjoyo.cnblog.util.ConvertDate;

public class BlogListAdapter extends BaseAdapter {
	Context context;
	List<BlogsInfo> blogslist;
	LayoutInflater inflater;
	private SharedPreferences spf;
	private boolean change;
	

	public BlogListAdapter(Context context) {
		this.inflater = LayoutInflater.from(context);
		spf= context.getSharedPreferences("SP", context.MODE_PRIVATE);
		change = spf.getBoolean("change", false);
	}

	public BlogListAdapter(Context context, List blogslist) {
		this.inflater = LayoutInflater.from(context);
		this.blogslist = blogslist;
		spf= context.getSharedPreferences("SP", context.MODE_PRIVATE);
		change = spf.getBoolean("change", false);
	}
	public void setBlogs(List<BlogsInfo> blogslist) {
		this.blogslist = blogslist;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (blogslist == null) {
			return 0;
		}
		return blogslist.size();
	}

	@Override
	public BlogsInfo getItem(int position) {
		// TODO Auto-generated method stub
		if (blogslist == null) {
			return null;
		}
		return blogslist.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	static class ViewHolder {
		TextView hotnews_item_title;
		TextView hotnews_item_summary;
		TextView hotnews_item_authorName;
		TextView hotnews_item_view_Num;
		TextView hotnews_item_comments_Num;
		TextView hotnews_item_updated;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.blogs_layout_item, null);
			holder.hotnews_item_title = (TextView) convertView
					.findViewById(R.id.hotnews_item_title);
			holder.hotnews_item_summary = (TextView) convertView
					.findViewById(R.id.hotnews_item_summary);
			holder.hotnews_item_authorName = (TextView) convertView
					.findViewById(R.id.hotnews_item_authorname);
			holder.hotnews_item_view_Num = (TextView) convertView
					.findViewById(R.id.hotnews_item_views_num);
			holder.hotnews_item_comments_Num = (TextView) convertView
					.findViewById(R.id.hotnews_item_comments_num);
			holder.hotnews_item_updated = (TextView) convertView
					.findViewById(R.id.hotnews_item_updated);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (change) {
			if (position % 2 == 0)
				convertView.setBackgroundColor(Color.parseColor("#FFF5EE"));
			else
				convertView.setBackgroundColor(Color.parseColor("#FFEFDB"));
		} else {
			if (position % 2 == 0)
				convertView.setBackgroundColor(0xffcdddfa);
			else
				convertView.setBackgroundColor(0xfff5fffa);
		}

		holder.hotnews_item_title.setText(blogslist.get(position).getTitle());
		// holder.hotnews_item_summary
		// .setText(SetSummary(blogslist.get(position).getSummary()));
		holder.hotnews_item_summary.setText(Html.fromHtml("<html><body>"
				+ SetSummary(blogslist.get(position).getSummary())
				+ "</body></html>", null, null));
		holder.hotnews_item_authorName.setText(blogslist.get(position)
				.getAuthorName());
		holder.hotnews_item_view_Num.setText(""
				+ blogslist.get(position).getViews());
		holder.hotnews_item_comments_Num.setText(""
				+ blogslist.get(position).getComments());
		try {
			holder.hotnews_item_updated.setText(ConvertDate
					.updateToDate(blogslist.get(position).getUpdated()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return convertView;
	}

	/**
	 * 
	 * @return 设置summary的长度为100
	 */
	public String SetSummary(String summary) {
		// Spanned s = Html.fromHtml("<html><body>" + content.getContent()
		// + "</body></html>", null, null);

		String result = "暂无可预览内容";
		if (summary != null && summary.length() > 100) {
			result = summary.substring(0, 100) + "...";
		} else if (summary != null && summary.length() <= 100) {
			result = summary;
		} else {
			result = "暂无可预览内容";
		}
		return result;
	}
}
