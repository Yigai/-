package com.anjoyo.cnblog.activitys.allblogs;

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
import com.anjoyo.cnblog.adapter.BlogListAdapter;
import com.anjoyo.cnblog.entry.BlogsInfo;
import com.anjoyo.cnblog.util.BlogSaxParser;
import com.anjoyo.cnblog.util.PullToRefreshListView;
import com.anjoyo.cnblog.util.PullToRefreshListView.OnRefreshListener;
import com.anjoyo.cnblog.util.TouchListener;

public class AllBlogsActivity extends Activity {
	static int visiableItemCount;// 当前窗口可见的总数
	static int visiableLastIndex;// 当前窗口可见的总数
	ArrayList<BlogsInfo> newsList;
	ArrayList<BlogsInfo> newsList1;
	PullToRefreshListView latestNewslist;
	TouchListener touchListener;
	String url;
	int pageIndex = 1;
	int pageSize = 10;
	public static final int LOAD = 1000;
	public static final int LOADMORE = 2000;
	BlogListAdapter adapter;
	ProgressDialog dialog;
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOAD:
				adapter = new BlogListAdapter(AllBlogsActivity.this, newsList);
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
		setContentView(R.layout.allblogs_layout);
		adapter = new BlogListAdapter(AllBlogsActivity.this, newsList);
		latestNewslist = (PullToRefreshListView) findViewById(R.id.latestNewslist);
		url = getIntent().getStringExtra("URL");
		touchListener = new TouchListener(AllBlogsActivity.this, false);
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
		newsList = new ArrayList<BlogsInfo>();
		dialog = new ProgressDialog(AllBlogsActivity.this);
		dialog.setMessage("数据加载中，请稍后...");
		dialog.show();
		new Thread() {

			@Override
			public void run() {
				newsList = getNewsInfo(pageIndex, pageSize);
				handler.sendEmptyMessage(LOAD);
			}

		}.start();
		//item点击去往博客详情
		latestNewslist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> listview, View item,
					int position, long id) {
//				if (position>0&&latestNewslist.isclick) {
//					
//				}
				position = position-1;
				int blogId = newsList.get(position).getId();
				Intent intent = new Intent(AllBlogsActivity.this, BlogContentActivity.class);
				intent.putExtra("id", blogId);
				intent.putExtra("link", newsList.get(position).getLink());
				intent.putExtra("blogTitle", newsList.get(position).getTitle());
				intent.putExtra("updateTime",  newsList.get(position).getUpdated());
				intent.putExtra("blogerUrl", newsList.get(position).getAuthorUri());
				intent.putExtra("bloger", newsList.get(position).getAuthorName());
				intent.putExtra("summary", newsList.get(position).getSummary());
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
				AllBlogsActivity.visiableItemCount = totalItemCount;
				visiableLastIndex = firstVisibleItem + visibleItemCount;

			}
		});

	}

	// 获取数据
	// http://wcf.open.cnblogs.com/news/recent/paged/{pageIndex}/{pageSize}
	public ArrayList<BlogsInfo> getNewsInfo(int pageIndex, int pageSize) {
		ArrayList<BlogsInfo> newslist = new ArrayList<BlogsInfo>();
		String s = url + pageIndex + "/" + pageSize;
		BlogSaxParser newsSaxparser = new BlogSaxParser();
		try {
			newslist = newsSaxparser.getBlogsInfos(s);
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
			AsyncTask<Void, Void, ArrayList<BlogsInfo>> {

		@Override
		protected ArrayList<BlogsInfo> doInBackground(Void... params) {
			// 下拉后增加的内容
			if (newsList1 != null) {
				newsList1.clear();
			} else {
				newsList1 = new ArrayList<BlogsInfo>();
			}
			newsList1 = getNewsInfo(1, 1);
			return newsList;
		}

		@Override
		protected void onPostExecute(ArrayList<BlogsInfo> result) {
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
