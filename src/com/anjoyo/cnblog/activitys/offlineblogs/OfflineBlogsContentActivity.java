package com.anjoyo.cnblog.activitys.offlineblogs;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.anjoyo.cnblog.activitys.R;
import com.anjoyo.cnblog.activitys.allblogs.BlogContentActivity;
import com.anjoyo.cnblog.activitys.search.SearchBlogerBlogActivity;
import com.anjoyo.cnblog.entry.BlogInfo_Offline;
import com.anjoyo.cnblog.entry.BlogerInfo;
import com.anjoyo.cnblog.sql.BlogSQLHelper;
import com.anjoyo.cnblog.util.BlogerSAXHandler;
import com.anjoyo.cnblog.util.CheckNetWork;

public class OfflineBlogsContentActivity extends Activity {
	TextView offline_content_title, offline_content_bloger;
	WebView offline_content_text;
	BlogInfo_Offline blog_offine;
	AlertDialog.Builder dalog;
	final String BLOGER_URL = "http://wcf.open.cnblogs.com/blog/bloggers/search?t=";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.offlineblogscontent_layout);
		// 如果是3.0以上的API 不准在主线程里执行网络操作！！！！！！！
		offline_content_title = (TextView) findViewById(R.id.offline_content_title);
		offline_content_bloger = (TextView) findViewById(R.id.offline_content_bloger);
		offline_content_text = (WebView) findViewById(R.id.offline_content_text);
		Intent intent = getIntent();
		int blogId = intent.getIntExtra("blogId", 0);
		getData(blogId);
	}

	/**
	 * 根据int blogId 查询出博客信息
	 * 
	 * @param blogId
	 */
	public void getData(int blogId) {
		blog_offine = new BlogInfo_Offline();
		if (blogId <= 0) {// 没取到
			blog_offine = null;
		} else {
			blog_offine = BlogSQLHelper.getDBInstance(
					OfflineBlogsContentActivity.this).getItemData(blogId);
		}
		if (blog_offine == null) {
			offline_content_title.setText("没有此数据");
		} else {
			offline_content_title.setText(blog_offine.getBlogTitle());
			offline_content_bloger.setText("博主：" + blog_offine.getBloger());
			offline_content_text.loadDataWithBaseURL(null,
					blog_offine.getBlogText(), "text/html", "UTF-8", null);
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			menuDialog();
		}
		return super.onKeyDown(keyCode, event);
	}

	public void menuDialog() {
		final String itemName = blog_offine.getBlogTitle();
		dalog = new AlertDialog.Builder(OfflineBlogsContentActivity.this);
		dalog.setTitle("请选择您要的操作：");
		dalog.setView(null);
		dalog.setItems(new String[] { "跳转至网页查看", "查看博主其他博文", "删除此记录", "取消" },
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog1, int which) {
						switch (which) {
						case 0:
							Intent intent = new Intent(
									OfflineBlogsContentActivity.this,
									BlogContentActivity.class);
							intent.putExtra("id", blog_offine.getBlogId());
							intent.putExtra("link", blog_offine.getBlogUrl());
							intent.putExtra("blogTitle",
									blog_offine.getBlogTitle());
							intent.putExtra("updateTime",
									blog_offine.getUpdateTime());
							intent.putExtra("blogerUrl",
									blog_offine.getBlogerUrl());
							intent.putExtra("bloger", blog_offine.getBloger());
							intent.putExtra("summary",
									blog_offine.getBlogSummary());
							if (CheckNetWork
									.checkNetworkInfo(OfflineBlogsContentActivity.this)) {
								startActivity(intent);
							} else {
								// .....
							}

							break;

						case 1:

							if (CheckNetWork.checkNetworkInfo(OfflineBlogsContentActivity.this)) {
								BlogerSAXHandler saxHandler = new BlogerSAXHandler();
								List<BlogerInfo> list = null;
								BlogerInfo blogerInfo = null;
								try {
									String app = URLEncoder.encode(
											blog_offine.getBloger(), "UTF-8");
									list = saxHandler.getBlogerInfo(BLOGER_URL + app);
								} catch (UnsupportedEncodingException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
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
								if (list != null && list.size() > 0) {
									blogerInfo = list.get(0);
									String blogapp = blogerInfo.getBlogapp();
									String updateTime = blogerInfo.getUpdated();
									String blogerName = blogerInfo.getTitle();
									String blogerImgUrl = blogerInfo.getAvatar();
									String blogCount = blogerInfo.getPostcount();
									//跳转到博主的博客列表
									Intent intent1 = new Intent(OfflineBlogsContentActivity.this,
											SearchBlogerBlogActivity.class);

									intent1.putExtra("blogapp", blogapp);
									intent1.putExtra("updateTime", updateTime);
									intent1.putExtra("bloger", blogerName);
									intent1.putExtra("blogerImgUrl", blogerImgUrl);
									intent1.putExtra("blogCount", blogCount);

									startActivity(intent1);
								} else {
									Toast.makeText(OfflineBlogsContentActivity.this, "数据获取失败",
											Toast.LENGTH_SHORT).show();
								}
							}
							break;
						case 2:
							AlertDialog.Builder alert = new AlertDialog.Builder(
									OfflineBlogsContentActivity.this);
							alert.setView(null);
							alert.setTitle("删除此项");
							alert.setMessage("警告：将删除" + itemName);
							alert.setPositiveButton("确定",
									new OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											BlogSQLHelper
													.getDBInstance(
															OfflineBlogsContentActivity.this)
													.deleteItem(
															blog_offine
																	.getBlogId());
										}
									});
							alert.setNegativeButton("取消", null);
							alert.create().show();
							break;
						case 3:
							dialog1.dismiss();
							break;
						}
					}
				});
		dalog.create().show();
	}

}
