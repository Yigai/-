package com.anjoyo.cnblog.activitys.latestnews;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.anjoyo.cnblog.activitys.HomeActivity;
import com.anjoyo.cnblog.activitys.R;
import com.anjoyo.cnblog.activitys.hotnews.HotNewsContentActivity;
import com.anjoyo.cnblog.adapter.HotNewsAdapter;
import com.anjoyo.cnblog.entry.HotNewsInfo;
import com.anjoyo.cnblog.util.HotNewsSaxParse;
import com.anjoyo.cnblog.util.PullToRefreshListView;
import com.anjoyo.cnblog.util.PullToRefreshListView.OnRefreshListener;
import com.anjoyo.cnblog.util.TouchListener;

public class LatestNewsActivity extends Activity {
	static int visiableItemCount;// 当前窗口可见的总数
	static int visiableLastIndex;// 当前窗口可见的总数
	ArrayList<HotNewsInfo> newsList;
	ArrayList<HotNewsInfo> newsList1;
	PullToRefreshListView latestNewslist;
	TouchListener touchListener;
	String url;
	int pageIndex = 1;
	int pageSize = 10;
	public static final int LOAD = 1000;
	public static final int LOADMORE = 2000;
	HotNewsAdapter adapter;
	ProgressDialog dialog;
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOAD:
				adapter = new HotNewsAdapter(LatestNewsActivity.this, newsList);
				latestNewslist.setAdapter(adapter);
				dialog.dismiss();
				break;
			case LOADMORE:
				// 加载更多
				latestNewslist.onMoreComplete();
				adapter.notifyDataSetChanged();
				break;
			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.latestnews_layout);
		latestNewslist = (PullToRefreshListView) findViewById(R.id.latestNewslist);
		url = getIntent().getStringExtra("URL");
		touchListener = new TouchListener(LatestNewsActivity.this, false);
		latestNewslist.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (touchListener.onTouch(latestNewslist, event)) {
					return true;
				} else if (HomeActivity.isopen) {
					return true;
				}

				return false;
			}
		});
		newsList = new ArrayList<HotNewsInfo>();
		dialog = new ProgressDialog(LatestNewsActivity.this);
		dialog.setMessage("数据加载中，请稍后...");
		dialog.show();
		new Thread() {

			@Override
			public void run() {
				newsList = getNewsInfo(pageIndex, pageSize);
				handler.sendEmptyMessage(LOAD);
			}

		}.start();

		latestNewslist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> listview, View item,
					int position, long id) {
				// .....................
				HotNewsInfo hotNew = adapter.getItem(position-1);
				Intent intent = new Intent(LatestNewsActivity.this,
						HotNewsContentActivity.class);
				intent.putExtra("id", hotNew.getId());
				intent.putExtra("newstitle", hotNew.getTitle());
				intent.putExtra("link", hotNew.getLink());
				intent.putExtra("summary", hotNew.getSummary());
				intent.putExtra("updated", hotNew.getUpdated());
				startActivity(intent);
			}

		});

		// 下拉
		latestNewslist.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				new GetDataTaske().execute();

			}
		});
		// 底部加载更过
		latestNewslist.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
						&& !latestNewslist.isclick
						&& visiableItemCount == visiableLastIndex) {
					addArrayList();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				LatestNewsActivity.visiableItemCount = totalItemCount;
				visiableLastIndex = firstVisibleItem + visibleItemCount;

			}
		});

	}
	// 获取数据
	// http://wcf.open.cnblogs.com/news/recent/paged/{pageIndex}/{pageSize}
	public ArrayList<HotNewsInfo> getNewsInfo(int pageIndex, int pageSize) {
		ArrayList<HotNewsInfo> newslist = new ArrayList<HotNewsInfo>();
		String s = url + pageIndex + "/" + pageSize;
		HotNewsSaxParse newsSaxparser = new HotNewsSaxParse();
		try {
			newslist = newsSaxparser.getHotNewsInfos(s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return newslist;
	}

	// 拼接ArrayList
	public void addArrayList() {
		pageIndex = pageIndex + 1;
		Thread thread = new Thread() {
			@Override
			public void run() {
				// 把新取到的数据加入原来的集合中
				newsList.addAll(getNewsInfo(pageIndex, 20));
				handler.sendEmptyMessage(LOADMORE);
			}
		};
		thread.start();

	}

	// 初始加载数据
	public class GetDataTaske extends
			AsyncTask<Void, Void, ArrayList<HotNewsInfo>> {

		@Override
		protected ArrayList<HotNewsInfo> doInBackground(Void... params) {
			// 下拉后增加的内容
			if (newsList1 != null) {
				newsList1.clear();
			} else {
				newsList1 = new ArrayList<HotNewsInfo>();
			}
			newsList1 = getNewsInfo(1, 1);
			return newsList;
		}

		@Override
		protected void onPostExecute(ArrayList<HotNewsInfo> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (newsList1.get(0).getTitle().equals(newsList.get(0).getTitle())) {
				latestNewslist.onRefreshComplete();
			} else {
				newsList.addAll(0, newsList1);
				adapter.notifyDataSetChanged();
				latestNewslist.onRefreshComplete();
			}

		}

	}

}
