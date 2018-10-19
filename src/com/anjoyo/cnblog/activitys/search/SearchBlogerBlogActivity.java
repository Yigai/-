package com.anjoyo.cnblog.activitys.search;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.anjoyo.cnblog.activitys.R;
import com.anjoyo.cnblog.activitys.allblogs.ComeBlogerBlogListBlogContentActivity;
import com.anjoyo.cnblog.adapter.BlogListAdapter;
import com.anjoyo.cnblog.entry.BlogsInfo;
import com.anjoyo.cnblog.util.GetPersonalBlogList;
import com.anjoyo.cnblog.util.ImgDownload;
import com.anjoyo.cnblog.util.PullToRefreshListView;
import com.anjoyo.cnblog.util.PullToRefreshListView.OnRefreshListener;

/**
 * 根据传进来的博主空间名字 得到该博主的所有的博客
 * http://wcf.open.cnblogs.com/blog/u/{BLOGAPP}/posts/{PAGEINDEX}/{PAGESIZE}
 * 
 * @author ChnAdo
 * 
 */
public class SearchBlogerBlogActivity extends Activity {
	static int visiableItemCount;// 当前窗口可见的总数
	static int visiableLastIndex;// 当前窗口可见的总数
	int pageIndex = 1;
	ArrayList<BlogsInfo> blogsList;
	ArrayList<BlogsInfo> addBlogsList = null;

	ImageView blog_blogerImg;
	TextView blog_blogerName, blog_blogCount, blogNull;
	PullToRefreshListView blogList;
	ProgressBar blog_progress;
	String blogapp;
	int blogCount;
	ProgressDialog proDialog;
	Thread blogThread;
	BlogListAdapter adapter;
	int pageSize = 10;
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 100:
				proDialog.dismiss();
				blog_progress.setVisibility(View.INVISIBLE);
				if (blogsList == null || blogsList.size() <= 0) {// 如果集合为空
					blogNull.setVisibility(View.VISIBLE);// 让 显示空白的TextView显示出来

				} else {
					adapter = new BlogListAdapter(
							SearchBlogerBlogActivity.this, blogsList);
					blogList.setAdapter(adapter);
				}
				break;

			case 200:
				if (addBlogsList != null) {
					blogsList.addAll(addBlogsList);
					blogList.onMoreComplete();
					adapter.notifyDataSetChanged();
				}
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchblogerblog_layout);
		blog_blogerImg = (ImageView) findViewById(R.id.blog_blogerImg);
		blog_blogerName = (TextView) findViewById(R.id.blog_blogerName);
		blog_blogCount = (TextView) findViewById(R.id.blog_blogCount);
		blogNull = (TextView) findViewById(R.id.blogNull);
		blogList = (PullToRefreshListView) findViewById(R.id.blog_blogshow);
		blog_progress = (ProgressBar) findViewById(R.id.blog_progress);
		Intent intent = getIntent();
		blogapp = intent.getStringExtra("blogapp");
		String updateTime = intent.getStringExtra("updateTime");
		String bloger = intent.getStringExtra("bloger");
		String blogerImgUrl = intent.getStringExtra("blogerImgUrl");
		blogCount = Integer.parseInt(intent.getStringExtra("blogCount"));
		blog_blogCount.setText("共" + (blogCount + 1) + "篇博客");
		blog_blogerName.setText("博主：" + bloger);
		ImgDownload.getImageInstance().getImageDrawable(blogerImgUrl,
				blog_blogerImg);
		getBlogData(1);// 刚进来加载第一页 加载10条数据
		// item点击去往博客详情
		//建议：如果是从博主的博客列表点进去 看 该博主的 所有博客 点了某个博客之后 建议不要再跳转到BlogContentActivity 不然将一直循环下去 直到内存溢出
		//可以单独在提供一个博客正文Activity 不提供 博主头像功能！！
		blogList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> listview, View item,
					int position, long id) {
				if (position > 0 && blogList.isclick) {

				}
				position = position - 1;
				int blogId = blogsList.get(position).getId();
				Intent intent = new Intent(SearchBlogerBlogActivity.this,
						ComeBlogerBlogListBlogContentActivity.class);
				intent.putExtra("id", blogId);
				intent.putExtra("link", blogsList.get(position).getLink());
				intent.putExtra("blogTitle", blogsList.get(position).getTitle());
				intent.putExtra("updateTime", blogsList.get(position)
						.getUpdated());
				intent.putExtra("blogerUrl", blogsList.get(position)
						.getAuthorUri());
				intent.putExtra("bloger", blogsList.get(position)
						.getAuthorName());
				intent.putExtra("summary", blogsList.get(position).getSummary());
				startActivity(intent);
			}

		});
		// 下拉
		blogList.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				new GetDataTaske().execute();

			}
		});
		// 底部加载更过
		blogList.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (blogsList.size() < blogCount) {

					if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
							&& !blogList.isclick
							&& visiableItemCount == visiableLastIndex) {
						addArrayList();
					}
				} else {
					blogList.dismissProgress();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				SearchBlogerBlogActivity.visiableItemCount = totalItemCount;
				visiableLastIndex = firstVisibleItem + visibleItemCount;

			}
		});

	}

	public class GetDataTaske extends
			AsyncTask<Void, Void, ArrayList<BlogsInfo>> {

		@Override
		protected ArrayList<BlogsInfo> doInBackground(Void... params) {
			// 下拉后增加的内容
			if (addBlogsList != null) {
				addBlogsList.clear();
			} else {
				addBlogsList = new ArrayList<BlogsInfo>();
			}
			addBlogsList = getBlogInfo(1);
			return blogsList;
		}

		@Override
		protected void onPostExecute(ArrayList<BlogsInfo> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (addBlogsList.get(0).getTitle()
					.equals(blogsList.get(0).getTitle())) {
				blogList.onRefreshComplete();
			} else {
				blogsList.addAll(0, addBlogsList);
				adapter.notifyDataSetChanged();
				blogList.onRefreshComplete();
			}

		}

	}

	public void getBlogData(int page) {
		proDialog = new ProgressDialog(SearchBlogerBlogActivity.this);
		proDialog.setMessage("数据加载中，请稍后...");
		proDialog.show();
		blog_progress.setVisibility(View.VISIBLE);
		final int pageSize = 10;
		final int pageIndex = page;
		blogThread = new Thread(new Runnable() {

			@Override
			public void run() {
				GetPersonalBlogList bloghand = new GetPersonalBlogList(blogapp,
						pageIndex, pageSize);
				blogsList = bloghand.getBlogsInfo();
				handler.sendEmptyMessage(100);

			}
		});
		blogThread.start();
	}

	// Forever-Kenlen-Ja
	// 拼接ArrayList
	public void addArrayList() {
		pageIndex = pageIndex + 1;
		Thread thread = new Thread() {
			@Override
			public void run() {
				// 把新取到的数据加入原来的集合中
				addBlogsList = new ArrayList<BlogsInfo>();
				addBlogsList = getBlogInfo(pageIndex);
				handler.sendEmptyMessage(200);
			}
		};
		thread.start();

	}

	public ArrayList<BlogsInfo> getBlogInfo(int page) {
		ArrayList<BlogsInfo> bloglist1 = new ArrayList<BlogsInfo>();
		GetPersonalBlogList bloghand = new GetPersonalBlogList(blogapp, page,
				pageSize);
		bloglist1 = bloghand.getBlogsInfo();
		return bloglist1;
	}

}
