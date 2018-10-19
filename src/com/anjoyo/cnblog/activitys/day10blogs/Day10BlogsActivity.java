package com.anjoyo.cnblog.activitys.day10blogs;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.anjoyo.cnblog.activitys.HomeActivity;
import com.anjoyo.cnblog.activitys.R;
import com.anjoyo.cnblog.activitys.allblogs.BlogContentActivity;
import com.anjoyo.cnblog.adapter.BlogListAdapter;
import com.anjoyo.cnblog.entry.BlogsInfo;
import com.anjoyo.cnblog.util.BlogSaxParser;
import com.anjoyo.cnblog.util.TouchListener;

public class Day10BlogsActivity extends Activity {
	//10天内热门排行榜 服务器最多只会返回42条数据
	private final static String URL = "http://wcf.open.cnblogs.com/blog/TenDaysTopDiggPosts/42";
	private ListView blogsIn48h;
	private ImageView recommendNoNet;
	private TextView refresh;
	private BlogListAdapter adapter;
	private TouchListener touchListener;
	private ConnectivityManager connectivity;
	//广播接收器	//不能做耗时操作
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("SetNetAction"))
				startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));//直接去往设置--网络设置页面
			//如果你的app没有root权限 是不能直接在当前程序里 直接修改网络设置 等一些高风险操作
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.day10blogs_layout);

		blogsIn48h = (ListView) findViewById(R.id.blogs_in_48h);
		recommendNoNet = (ImageView) findViewById(R.id.recommend_no_network);
		refresh = (TextView) findViewById(R.id.recommend_refresh);
		adapter = new BlogListAdapter(this);
		touchListener = new TouchListener(this, false);
		blogsIn48h.setAdapter(adapter);
		//ListView的item点击事件   去往 博客正文详情====== BlogContentActivity
		blogsIn48h.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//如果当前的GPRS可用或者WIFI可用 去往博客正文详情====== BlogContentActivity
				if (isNetworkConnected(Day10BlogsActivity.this)) {
					BlogsInfo blog = adapter.getItem(position);
					Intent intent = new Intent(Day10BlogsActivity.this,
							BlogContentActivity.class);
					intent.putExtra("id", blog.getId());
					intent.putExtra("link", blog.getLink());
					intent.putExtra("blogTitle", blog.getTitle());
					intent.putExtra("bloger", blog.getAuthorName());
					intent.putExtra("blogerUrl", blog.getAuthorUri());
					intent.putExtra("updateTime", blog.getUpdated());
					intent.putExtra("summary", blog.getSummary());
					startActivity(intent);
				} else {
					Toast.makeText(Day10BlogsActivity.this, "没有网络啊亲...",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		//滑动事件
		blogsIn48h.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (touchListener.onTouch(blogsIn48h, event))
					return true;
				else if (HomeActivity.isopen)
					return true;
				return false;
			}
		});
		//如果没网 点击 弹出对话框让用户选择
		refresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				checkNet();
			}
		});

		connectivity = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		//动态注册广播接收器
		IntentFilter filter = new IntentFilter();
		filter.addAction("SetNetAction");
		registerReceiver(receiver, filter);
		//刚进来 检查网络》。。。。。。。
		checkNet();
	}

//卸载广播接收器
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

/**
 * 检查网络
 */
	public boolean isNetworkConnected(Context context) {  
	    if (context != null) {  
	        ConnectivityManager mConnectivityManager = (ConnectivityManager) context  
	                .getSystemService(Context.CONNECTIVITY_SERVICE);  
	        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();  
	        if (mNetworkInfo != null) {  
	            return mNetworkInfo.isAvailable();  
	        }  
	    }  
	    return false;  
	}
	private void checkNet() {
		//如果网络可用
		if (isNetworkConnected(Day10BlogsActivity.this)) {
			blogsIn48h.setVisibility(View.VISIBLE);//让列表可见
			if (adapter.isEmpty())//如果此时没数据
				new GetListTask().execute();//异步去获取数据
		} else {//如果网络不可用 弹出对话框让用户去设置网络  如果不设置 不作操作 如果设置 去往 设置----网络设置
			recommendNoNet.setVisibility(View.VISIBLE);
			refresh.setVisibility(View.VISIBLE);
			blogsIn48h.setVisibility(View.GONE);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("是否进行网络设置？");
			builder.setTitle("当前没有连接网络！");
			builder.setPositiveButton("是",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Day10BlogsActivity.this.sendBroadcast(new Intent()
									.setAction("SetNetAction"));//发一个广播   Action是“SetNetAction” 可以让 相应的 接收器接收到
							dialog.dismiss();
						}
					});
			builder.setNegativeButton("否",
					//如果不去进行网络设置 再弹出对话框 让用户选择是否去往离线列表
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							AlertDialog.Builder builder = new AlertDialog.Builder(
									Day10BlogsActivity.this);
							builder.setMessage("是否进行离线浏览？");
							builder.setTitle("当前没有连接网络！");
							builder.setPositiveButton("是",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											//如果去了离线 则 离开当前Activity
											Day10BlogsActivity.this.sendBroadcast(new Intent()
													.setAction("toOffLine"));
											dialog.dismiss();
										}
									});
							builder.setNegativeButton("否", null);
							builder.create().show();
						}
					});
			builder.create().show();
		}
	}
//异步加载48小时排行榜的数据
	private class GetListTask extends AsyncTask {
		private List<BlogsInfo> list;
		private ProgressDialog dialog;

		protected void onPreExecute() {
			dialog = new ProgressDialog(Day10BlogsActivity.this);
			dialog.setMessage("数据加载中,请稍后...");
			dialog.show();
		}

		@Override
		protected Object doInBackground(Object... params) {
			BlogSaxParser handler = new BlogSaxParser();
			try {
				list = handler.getBlogsInfos(URL);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			if (null != list) {
				recommendNoNet.setVisibility(View.GONE);
				refresh.setVisibility(View.GONE);
				adapter.setBlogs(list);
				adapter.notifyDataSetChanged();
			}
			dialog.dismiss();
		}
	}
}
