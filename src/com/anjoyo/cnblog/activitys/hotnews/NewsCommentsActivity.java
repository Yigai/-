package com.anjoyo.cnblog.activitys.hotnews;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.anjoyo.cnblog.activitys.R;
import com.anjoyo.cnblog.adapter.NewsCommentsAdapter;
import com.anjoyo.cnblog.entry.CommentsInfo;
import com.anjoyo.cnblog.util.CommentsSaxParse;

public class NewsCommentsActivity extends Activity {
	int pageIndex = 1;// 默认是第一页 每次+1 表示翻页
	int pageSize = 10;// 一页显示10条数据
	public static final int LOAD = 1000;
	public static final int LOADMORE = 2000;
	public static final int LOADING = 3000;
	CommentsSaxParse commentsparser;
	String url;
	int id;
	ArrayList<CommentsInfo> commentsData = null;
	ListView listview;
	String newsTitle;
	TextView newstitle;
	NewsCommentsAdapter adapter;
	View loadMoreView;
	Button loadMoreButton;
	int visibaleLastIndex;
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOAD:
				adapter = new NewsCommentsAdapter(NewsCommentsActivity.this,
						commentsData);
				newstitle.setText(newsTitle);
				listview.setAdapter(adapter);
				break;
			case LOADMORE:
				loadMoreButton.setText("正在加载中...");
				adapter.notifyDataSetChanged();
				loadMoreButton.setText("查看更多...");
				break;
			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newscomments_layout);
		url = getIntent().getStringExtra("COMURL");
		newsTitle = getIntent().getStringExtra("newsTitle");
		id = getIntent().getIntExtra("ID", 0);
		newstitle = (TextView) findViewById(R.id.comments_list_item_title);
		listview = (ListView) findViewById(R.id.comments_listview);

		commentsData = new ArrayList<CommentsInfo>();
		new Thread() {

			@Override
			public void run() {
				commentsData.addAll(getCommentsData(id, 1, 10));
				handler.sendEmptyMessage(LOAD);
			}

		}.start();
		// listview.addHeaderView(v);

		loadMoreView = getLayoutInflater().inflate(
				R.layout.comments_loadmoreview_layout, null);
		loadMoreButton = (Button) loadMoreView
				.findViewById(R.id.loadMoreButton);
		loadMoreButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {// 此处点击按钮加载下10条数据

				addArrayList();
			}
		});
		listview.addFooterView(loadMoreView);
		/**
		 * ListView滚动监听器 实现滚动到底部 自动加载 加载完移除底部的加载更多View
		 */
		// listview.setOnScrollListener(new OnScrollListener() {
		//
		// @Override//滚动状态发生变化
		// public void onScrollStateChanged(AbsListView view, int scrollState) {
		// int itemsLastIntex = adapter.getCount()-1;
		// int lastIndex = itemsLastIntex+1;
		// if
		// (scrollState==OnScrollListener.SCROLL_STATE_IDLE&&visibaleLastIndex==lastIndex)
		// {
		// //可以写自动 加载的代码 此处可以执行异步加载数据的操作
		// }
		//
		// }
		//
		// @Override//滚动中
		// public void onScroll(AbsListView view, int firstVisibleItem,
		// int visibleItemCount, int totalItemCount) {
		//
		// if (totalItemCount==dataSiz+1) {
		// // 如果说加载出来的总条数等于服务器上的最大条数 可以在此处移除掉底部的FooterView
		// listview.removeFooterView(loadMoreView);
		// Toast.makeText(NewsCommentsActivity.this, "数据已经全部加载完毕,歇会儿吧！",
		// Toast.LENGTH_SHORT);
		// }
		//
		//
		// }
		// });
	}

	// 1、获取数据
	// http://wcf.open.cnblogs.com/news/item/207430/comments/1/10
	/**
	 * 
	 * @param POSTID
	 *            新闻ID
	 * @param PAGEINDEX
	 *            　页数
	 * @param PAGESIZE
	 *            每页数量
	 * @return 评论内容
	 */
	public ArrayList<CommentsInfo> getCommentsData(int POSTID, int PAGEINDEX,
			int PAGESIZE) {
		ArrayList<CommentsInfo> commentsData = new ArrayList<CommentsInfo>();
		commentsparser = new CommentsSaxParse();
		// 得到评论内容
		try {
			commentsData = commentsparser.getCommentsInfo(url + POSTID
					+ "/comments/" + PAGEINDEX + "/" + PAGESIZE);
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

		return commentsData;
	}

	// 2、拼接ArrayList
	public void addArrayList() {
		pageIndex = pageIndex + 1;
		Thread thread = new Thread() {
			@Override
			public void run() {
				// 把新取到的数据加入原来的集合中
				commentsData.addAll(getCommentsData(id, pageIndex, 10));
				handler.sendEmptyMessage(LOADMORE);
			}
		};
		thread.start();

	}

}
