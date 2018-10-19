package com.anjoyo.cnblog.activitys.hotnews;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.anjoyo.cnblog.activitys.HomeActivity;
import com.anjoyo.cnblog.activitys.R;
import com.anjoyo.cnblog.adapter.HotNewsAdapter;
import com.anjoyo.cnblog.entry.HotNewsInfo;
import com.anjoyo.cnblog.util.HotNewsSaxParse;
import com.anjoyo.cnblog.util.TouchListener;

public class HotNewsActivity extends Activity {
	String URL;
	ListView hotnewslist;
	private TouchListener touchListener;
	HotNewsAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hotnews_layout);
		URL = getIntent().getStringExtra("URL");
		adapter = new HotNewsAdapter(HotNewsActivity.this);
		hotnewslist = (ListView) findViewById(R.id.hotnewslist);
		touchListener = new TouchListener(HotNewsActivity.this, false);
		hotnewslist.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (touchListener.onTouch(hotnewslist, event)) {
					return true;
				} else if (HomeActivity.isopen) {
					return true;
				}

				return false;
			}
		});

		hotnewslist.setAdapter(adapter);

		if (adapter.isEmpty()) {
			new GetListTask().execute(null, null, null);
		}
		/**
		 * ListView的点击事件
		 */
		hotnewslist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> listview, View item, int position,
					long id) {
				HotNewsInfo hotNew = adapter.getItem(position);
				Intent intent = new Intent(HotNewsActivity.this, HotNewsContentActivity.class);
				intent.putExtra("id", hotNew.getId());
				intent.putExtra("newstitle", hotNew.getTitle());
				intent.putExtra("link", hotNew.getLink());
				intent.putExtra("summary", hotNew.getSummary());
				intent.putExtra("updated", hotNew.getUpdated());
				startActivity(intent);
				
			}
		});
	}
	private class GetListTask extends AsyncTask {
		List<HotNewsInfo> list;
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub

			dialog = new ProgressDialog(HotNewsActivity.this);
			dialog.setMessage("数据加载中，请稍后...");
			dialog.show();
			super.onPreExecute();
		}

		@Override
		protected Object doInBackground(Object... params) {
			HotNewsSaxParse saxHandler = new HotNewsSaxParse();

			try {
				list = saxHandler.getHotNewsInfos(URL);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			}
			return list;
		}

		@Override
		protected void onPostExecute(Object result) {
			adapter.setHotNews(list);
			adapter.notifyDataSetChanged();
			dialog.dismiss();
		}
	}
}
