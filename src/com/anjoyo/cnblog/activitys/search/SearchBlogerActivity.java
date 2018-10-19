package com.anjoyo.cnblog.activitys.search;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anjoyo.cnblog.activitys.HomeActivity;
import com.anjoyo.cnblog.activitys.R;
import com.anjoyo.cnblog.activitys.allblogs.BlogContentActivity;
import com.anjoyo.cnblog.adapter.SearchBloggerAdapter;
import com.anjoyo.cnblog.entry.BlogerInfo;
import com.anjoyo.cnblog.util.BlogerSAXHandler;
import com.anjoyo.cnblog.util.CheckNetWork;
import com.anjoyo.cnblog.util.TouchListener;

/**
 * 根据输入的字符串 模糊查找博主
 * 
 * @author ChnAdo
 * 
 */
public class SearchBlogerActivity extends Activity {
	public static final String searchBloggers = "http://wcf.open.cnblogs.com/blog/bloggers/search?t=";
	ListView resList;// 显示搜索结果
	Button shearchGo, searchClear;
	public static EditText searchText;
	TextView searchNull;
	ProgressBar progressBar;

	TouchListener touchListener;
	String searchContent;
	ArrayList<BlogerInfo> blogerList;
	SearchBloggerAdapter adapter;
	BlogerInfo blogerinfo;
	/*
	 * 取到博主信息 更新UI
	 */
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				progressBar.setVisibility(View.INVISIBLE);// 数据取到了 隐藏进度条
				if (blogerList != null && blogerList.size() > 0) {
					adapter = new SearchBloggerAdapter(
							SearchBlogerActivity.this, blogerList);
					resList.setAdapter(adapter);
				} else {
					searchNull.setText("没有搜索到数据");
					searchNull.setVisibility(View.VISIBLE);
				}
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchbloger_layout);
		resList = (ListView) findViewById(R.id.resultList);
		shearchGo = (Button) findViewById(R.id.search_go);
		searchClear = (Button) findViewById(R.id.search_clear);
		searchText = (EditText) findViewById(R.id.search_text);
		searchNull = (TextView) findViewById(R.id.searchNull);
		progressBar = (ProgressBar) findViewById(R.id.blog_progress);

		blogerList = new ArrayList<BlogerInfo>();
		// ListVew 左右滑动 回到主菜单
		touchListener = new TouchListener(SearchBlogerActivity.this, false);
		resList.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (touchListener.onTouch(resList, event)) {
					return true;
				} else if (HomeActivity.isopen) {
					return true;
				}

				return false;
			}
		});
		shearchGo.setOnClickListener(searchLinstener);
		searchClear.setOnClickListener(clearLinstener);
		// Listview点击事件 去往 博主博客列表
		resList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> listview, View item,
					int position, long id) {
				blogerinfo = blogerList.get(position);
				String blogapp = blogerinfo.getBlogapp();
				String updateTime = blogerinfo.getUpdated();
				String blogerName = blogerinfo.getTitle();
				String blogerimgUrl = blogerinfo.getAvatar();
				String blogCount = blogerinfo.getPostcount();
				Intent i = new Intent(SearchBlogerActivity.this,
						SearchBlogerBlogActivity.class);
				i.putExtra("blogapp", blogapp);
				i.putExtra("updateTime", updateTime);
				i.putExtra("bloger", blogerName);
				i.putExtra("blogerImgUrl", blogerimgUrl);
				i.putExtra("blogCount", blogCount);
				startActivity(i);
			}
		});
	}

	// 清除按钮的点击事件
	OnClickListener clearLinstener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			blogerList.clear();
			adapter = new SearchBloggerAdapter(SearchBlogerActivity.this, blogerList);
			resList.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			searchNull.setVisibility(View.VISIBLE);
			searchNull.setText("没有可显示内容");
			searchText.setText("");
		}
	};
	// 搜索按钮的点击事件
	OnClickListener searchLinstener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			searchNull.setVisibility(View.INVISIBLE);// 开始搜索让 Text不可见
			searchContent = searchText.getText().toString().trim();
			try {
				searchContent = URLEncoder.encode(searchContent, "UTF-8");// 将输入的字符串进行格式转换
																			// 防止提交服务器时出现编码异常
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (searchContent == null || searchContent == ""
					|| searchContent.length() <= 0) {
				Toast.makeText(SearchBlogerActivity.this, "请输入内容进行查询！",
						Toast.LENGTH_SHORT).show();
				searchNull.setVisibility(View.VISIBLE);
			} else {
				if (!CheckNetWork.checkNetworkInfo(SearchBlogerActivity.this)) {
					AlertDialog.Builder dialog = new AlertDialog.Builder(
							SearchBlogerActivity.this);
					dialog.setTitle("网络异常！！");
					dialog.setMessage("没有网络，请设置网络");
					dialog.setPositiveButton("去设置网络",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent(
											Settings.ACTION_WIFI_SETTINGS);
									startActivity(intent);
								}
							});
					dialog.setNegativeButton("取消", null).create().show();
				} else {
					progressBar.setVisibility(View.VISIBLE);
					getBlogerData(searchContent);
				}
			}

		}
	};

	// 获取数据
	public void getBlogerData(String searchCont) {
		final String url = searchBloggers + searchCont;
		new Thread() {

			@Override
			public void run() {
				BlogerSAXHandler blogerHandler = new BlogerSAXHandler();
				try {
					blogerList = blogerHandler.getBlogerInfo(url);
					handler.sendEmptyMessage(1);
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
			}

		}.start();
	}

}
