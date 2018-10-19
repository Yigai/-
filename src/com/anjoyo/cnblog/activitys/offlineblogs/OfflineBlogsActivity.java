package com.anjoyo.cnblog.activitys.offlineblogs;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.anjoyo.cnblog.activitys.HomeActivity;
import com.anjoyo.cnblog.activitys.R;
import com.anjoyo.cnblog.adapter.OfflineBlogAdapter;
import com.anjoyo.cnblog.entry.BlogInfo_Offline;
import com.anjoyo.cnblog.sql.BlogSQLHelper;
import com.anjoyo.cnblog.util.TouchListener;

public class OfflineBlogsActivity extends Activity {

	public static TextView blogNull;
	TextView title;
	ListView offListview;
	Context context;
	private TouchListener touchListener;

	public static ArrayList<BlogInfo_Offline> blogOff;
	BlogInfo_Offline curBlogs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.offlineblogs_layout);

		context = this;
		// title = (TextView) findViewById(R.id.offline_title);
		blogNull = (TextView) findViewById(R.id.offline_null);
		blogNull.setOnTouchListener(new TouchListener(this, true));
		// touchArea = (TextView) findViewById(R.id.touchArea);
		offListview = (ListView) findViewById(R.id.offline_listview);

		flushAdapter();

		offListview.setOnItemClickListener(clickBlog);
		// offListview.setOnItemLongClickListener(clickLong);
		touchListener = new TouchListener(this, false);
		// 滑动回到主菜单
		offListview.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (touchListener.onTouch(offListview, event))
					return true;
				else if (HomeActivity.isopen)
					return true;
				return false;
			}
		});
	}
	
	@Override
	protected void onRestart() {
		flushAdapter();
		super.onRestart();
	}

	// item的点击事件 进入离线正文
	OnItemClickListener clickBlog = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			curBlogs = blogOff.get(position);
			Intent intent = new Intent(OfflineBlogsActivity.this, OfflineBlogsContentActivity.class);
			int blogId = curBlogs.getBlogId();
			intent.putExtra("blogId", blogId);
			startActivity(intent);
		}
	};

	public void flushAdapter() {
		blogOff = new ArrayList<BlogInfo_Offline>();
		blogOff = BlogSQLHelper.getDBInstance(context).getBlogDBData();
		OfflineBlogAdapter adapter = new OfflineBlogAdapter(this, blogOff);
		offListview.setAdapter(adapter);
		if (!blogOff.isEmpty()) {
			blogNull.setVisibility(View.INVISIBLE);
		} else {
			blogNull.setVisibility(View.VISIBLE);
		}

		// Off.this.notifyDataSetChanged();
	}

}
