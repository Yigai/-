package com.anjoyo.cnblog.activitys.newsmarks;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.anjoyo.cnblog.activitys.HomeActivity;
import com.anjoyo.cnblog.activitys.R;
import com.anjoyo.cnblog.activitys.hotnews.HotNewsContentActivity;
import com.anjoyo.cnblog.adapter.NewsMarkersAdapter;
import com.anjoyo.cnblog.entry.HotNewsInfo;
import com.anjoyo.cnblog.sql.NewsSQLHelper;
import com.anjoyo.cnblog.util.TouchListener;

public class NewsMarksActivity extends Activity {
	Button checkAll, chkDel;
	ListView listview;
	TextView tv, tvNull;
	ArrayList<HotNewsInfo> list;
	NewsMarkersAdapter adapter;
	boolean bool = true;
	private TouchListener touchListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newsmarks_layout);
		listview = (ListView) findViewById(R.id.mark_list);
		checkAll = (Button) findViewById(R.id.check_all);
		chkDel = (Button) findViewById(R.id.check_delete);
		tv = (TextView) findViewById(R.id.count);
		tvNull = (TextView) findViewById(R.id.mark_null);
		list = NewsSQLHelper.getDBInstance(NewsMarksActivity.this).getNews();
		adapter = new NewsMarkersAdapter(NewsMarksActivity.this, list);
		listview.setAdapter(adapter);
		tv.setText("共" + list.size() + "条");
		if (list.isEmpty()) {
			tvNull.setText("暂无记录");
		}

		touchListener = new TouchListener(NewsMarksActivity.this, false);
		listview.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (touchListener.onTouch(listview, event)) {
					return true;
				} else if (HomeActivity.isopen) {
					return true;
				}

				return false;
			}
		});

		checkAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (bool) {
					for (HotNewsInfo news : list) {
						news.setChecked(true);
						checkAll.setText("取消");
						bool = false;
					}
				} else {
					for (HotNewsInfo news : list) {
						news.setChecked(false);
						checkAll.setText("全选");
						bool = true;
					}
				}
				adapter.notifyDataSetChanged();
			}
		});
		chkDel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				for (HotNewsInfo news : list) {
					if (news.isChecked()) {
						NewsSQLHelper.getDBInstance(NewsMarksActivity.this)
								.deleteNews(news.getId());
					}
				}
				list = new ArrayList<HotNewsInfo>();
				list = NewsSQLHelper.getDBInstance(NewsMarksActivity.this)
						.getNews();
				adapter = new NewsMarkersAdapter(NewsMarksActivity.this, list);
				listview.setAdapter(adapter);
				tv.setText("共" + list.size() + "条");
				if (list.isEmpty()) {
					tvNull.setText("暂无记录");
				}

			}
		});

		 listview.setOnItemClickListener(new OnItemClickListener() {
		
		 @Override
		 public void onItemClick(AdapterView<?> listview, View item, int position,
		 long id) {
			 Intent intent = new Intent(NewsMarksActivity.this, HotNewsContentActivity.class);
//				int id = intent.getIntExtra("id", 0);
//				String newstitle = intent.getStringExtra("newstitle");
//				String link = intent.getStringExtra("link");
			 intent.putExtra("id", list.get(position).getId());
			 intent.putExtra("newstitle", list.get(position).getTitle());
			 intent.putExtra("link", list.get(position).getLink());
			 startActivity(intent);
		 }
		
		 });

	}
}
