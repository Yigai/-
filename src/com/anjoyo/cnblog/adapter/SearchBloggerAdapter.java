package com.anjoyo.cnblog.adapter;

import java.text.ParseException;
import java.util.ArrayList;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anjoyo.cnblog.activitys.R;
import com.anjoyo.cnblog.activitys.search.SearchBlogerActivity;
import com.anjoyo.cnblog.entry.BlogerInfo;
import com.anjoyo.cnblog.util.ConvertDate;
import com.anjoyo.cnblog.util.ImgDownload;

public class SearchBloggerAdapter extends BaseAdapter {

	Context context;
	ArrayList<BlogerInfo> blogerList;
	LayoutInflater layoutInflater;

	public SearchBloggerAdapter(Context context,
			ArrayList<BlogerInfo> blogerList) {
		this.context = context;
		this.blogerList = blogerList;
		layoutInflater = LayoutInflater.from(context);

	}

	@Override
	public int getCount() {

		if (blogerList == null)
			return 0;
		else
			return blogerList.size();
	}

	@Override
	public Object getItem(int position) {

		return blogerList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {

			holder = new ViewHolder();
			convertView = layoutInflater.inflate(R.layout.searchbloger_item,
					null);

			holder.blogerImg = (ImageView) convertView
					.findViewById(R.id.blogger_img);
			holder.blogerName = (TextView) convertView
					.findViewById(R.id.blogger_name);
			holder.blogCount = (TextView) convertView
					.findViewById(R.id.blog_count);
			holder.timeDay = (TextView) convertView.findViewById(R.id.time_day);

			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		String time = null;
		;
		BlogerInfo info = blogerList.get(position);
		try {
			time = ConvertDate.publishedToDate(info.getUpdated());
		} catch (ParseException e) {

			e.printStackTrace();
		}
//		SpannableString spanString = new SpannableString(info.getTitle());  
//	    BackgroundColorSpan span = new BackgroundColorSpan(Color.RED);  
//	    spanString.setSpan(span, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  
	    String blogerName = info.getTitle();
	    String replace = SearchBlogerActivity.searchText.getText().toString();
	    blogerName = blogerName.replace(replace, "<font color=#FF0000>"+replace+"</font>");
		holder.blogerName.setText(Html.fromHtml(blogerName));
		holder.blogCount.setText(info.getPostcount() + "篇博文");
		holder.timeDay.setText(time);

		String imgUrl = info.getAvatar();
		//如果要求将博主的名字 里的关键词  设置成红色 如何操作？
		//此处如果没有做异步 头像下载会导致ListView 滚动变卡
		ImgDownload.getImageInstance().getImageDrawable(imgUrl,
				holder.blogerImg);
		return convertView;
	}

	class ViewHolder {
		ImageView blogerImg;
		TextView blogerName, timeDay, timeSecond, blogCount;
	}
}
