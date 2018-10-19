package com.anjoyo.cnblog.adapter;

import java.text.ParseException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.anjoyo.cnblog.activitys.R;
import com.anjoyo.cnblog.entry.CommentsInfo;
import com.anjoyo.cnblog.util.ConvertDate;

public class NewsCommentsAdapter extends BaseAdapter {
	private ArrayList<CommentsInfo> commentsData;
	private Activity context;

	public NewsCommentsAdapter(Activity context,ArrayList<CommentsInfo> commentsData) {
		this.commentsData = commentsData;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return commentsData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return commentsData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		if (convertView == null) {
			Log.v("TAG", "1111");
			final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.newscommentitem_layout, null);
			holder = new ViewHolder();

			holder.comments_list_item_content = (WebView) convertView
					.findViewById(R.id.comments_list_item_content);
			holder.comments_list_item_updated = (TextView) convertView
					.findViewById(R.id.comments_list_item_updated);
			holder.comments_list_item_authorName = (TextView) convertView
					.findViewById(R.id.comments_list_item_authorName);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.comments_list_item_content.loadDataWithBaseURL(null,
				commentsData.get(position).getContent(), "text/html", "utf-8",
				null);

		try {
			holder.comments_list_item_updated
					.setText(ConvertDate.publishedToDate(commentsData.get(
							position).getPublished()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		holder.comments_list_item_authorName.setText(commentsData.get(position)
				.getAuthorName());
		return convertView;
	}

	static class ViewHolder {

		WebView comments_list_item_content;
		TextView comments_list_item_updated;
		TextView comments_list_item_authorName;

	}

}
