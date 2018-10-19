package com.anjoyo.cnblog.activitys;

import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.anjoyo.cnblog.activitys.allblogs.AllBlogsActivity;
import com.anjoyo.cnblog.activitys.day10blogs.Day10BlogsActivity;
import com.anjoyo.cnblog.activitys.hotnews.HotNewsActivity;
import com.anjoyo.cnblog.activitys.hour48blogs.Hour48BlogsActivity;
import com.anjoyo.cnblog.activitys.latestnews.LatestNewsActivity;
import com.anjoyo.cnblog.activitys.newsmarks.NewsMarksActivity;
import com.anjoyo.cnblog.activitys.offlineblogs.OfflineBlogsActivity;
import com.anjoyo.cnblog.activitys.recommendednews.RecommendedNewsActivity;
import com.anjoyo.cnblog.activitys.search.SearchBlogerActivity;
import com.anjoyo.cnblog.activitys.setting.SettingActivity;
import com.anjoyo.cnblog.util.ImageEnorDis;

@SuppressWarnings("deprecation")
public class HomeActivity extends ActivityGroup implements OnClickListener {
	ViewGroup myBody;
	TextView title;
	public static View left_menu;
	public static boolean isopen = false;// 表示菜单栏的打开状态
	AlertDialog dialog;
	// Handler handler = new Handler() {
	// public void handleMessage(Message msg) {
	// LinearLayout.LayoutParams lp = (LayoutParams) left_menu
	// .getLayoutParams();
	// if (isopen) {
	// lp.leftMargin = Math.min(lp.leftMargin + 20, 0);
	//
	// } else {
	// lp.leftMargin = Math.max(lp.leftMargin - 20,
	// -left_menu.getWidth());
	// }
	// left_menu.setLayoutParams(lp);
	//
	// };
	// };
	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals("open")) {
				new AsyncMove2().execute();
			}
			if (intent.getAction().equals("back")) {
				isopen = false;
				new AsyncMove2().execute();
			}
			if (intent.getAction().equals("move")) {
				float f = intent.getFloatExtra("distance", 0);
				float old = intent.getFloatExtra("old", 150);
				LinearLayout.LayoutParams lp = (LayoutParams) left_menu
						.getLayoutParams();
				if (isopen && f < 0) {
					lp.leftMargin = (int) f;

				} else if (!isopen && f > 0 && old < 100) {
					lp.leftMargin = (int) (-left_menu.getWidth() + f);
				}
				left_menu.setLayoutParams(lp);
			}
			if (intent.getAction().equals("toOffLine")) {
				myBody.removeAllViews();
				new AsyncMove2().execute();
				myBody.addView(getLocalActivityManager().startActivity(
						"9",
						new Intent(HomeActivity.this, OfflineBlogsActivity.class)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
						.getDecorView());
				isopen = false;
				title.setText("离线浏览");
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_main);

		super.onCreate(savedInstanceState);
		setUpTextView();
		IntentFilter inf = new IntentFilter();
		inf.addAction("open");
		inf.addAction("back");
		inf.addAction("move");
		inf.addAction("toOffLine");
		registerReceiver(receiver, inf);

		title = (TextView) findViewById(R.id.title);
		myBody = (ViewGroup) findViewById(R.id.body);
		left_menu = findViewById(R.id.left_menu);// 菜单
		
		SharedPreferences spf = getSharedPreferences("SP", MODE_WORLD_READABLE);
		ImageEnorDis.isDownImg = spf.getBoolean("downImg", true);
		WindowManager wm = (WindowManager) HomeActivity.this
				.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();// 得到屏幕的宽度

		LinearLayout.LayoutParams lp = (LayoutParams) left_menu
				.getLayoutParams();
		lp.width = width / 10 * 8;
		lp.leftMargin = -width / 10 * 8;
		left_menu.setLayoutParams(lp);

		ViewGroup.LayoutParams lp2 = (LayoutParams) myBody.getLayoutParams();
		lp2.width = width;
		myBody.setLayoutParams(lp2);
		LayoutParams lp3 = (LayoutParams) title.getLayoutParams();
		lp3.width = width;
		title.setLayoutParams(lp3);
		
		
		int[] i = { R.drawable.commlist_head_bg, R.drawable.left_menu_bg5,
				R.drawable.left_menu_bg6, R.drawable.left_menu_bg7 };
		left_menu.setBackgroundResource(i[spf.getInt("bg", 0)]);
		// 默认显示新闻Activity
		setUpChildActivity("1", HotNewsActivity.class,"http://wcf.open.cnblogs.com/news/hot/30");
		title.setText("热门新闻");
	}

	public void setUpTextView() {
		initTextView(R.id.hot_news);
		initTextView(R.id.lately_news);
		initTextView(R.id.recommend_news);
		initTextView(R.id.recommend_blogs);
		initTextView(R.id.read48_rank);
		initTextView(R.id.recommend_rank);
		initTextView(R.id.booktag);
		initTextView(R.id.history);
		initTextView(R.id.search);
		initTextView(R.id.settings);
		initTextView(R.id.exit);
	}

	public void initTextView(int id) {
		TextView tv = (TextView) findViewById(id);
		tv.setOnClickListener(this);
	}

	public void setUpChildActivity(String activityid, Class<?> activityClassTye,String url) {
		myBody.removeAllViews();
		//
		// AsyncMove2 t = new AsyncMove2();
		// t.start();
		new AsyncMove2().execute();
		myBody.addView(getLocalActivityManager().startActivity(
				activityid,
				new Intent(HomeActivity.this, activityClassTye)
						.putExtra("URL", url)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
				.getDecorView());
		isopen = false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.hot_news:// 热门新闻
			setUpChildActivity("1", HotNewsActivity.class,"http://wcf.open.cnblogs.com/news/hot/30");
			title.setText("热门新闻");
			break;

		case R.id.lately_news:// 最新新闻
			setUpChildActivity("2", LatestNewsActivity.class,"http://wcf.open.cnblogs.com/news/recent/paged/");
			title.setText("最新新闻");
			break;
		case R.id.recommend_news:// 推荐新闻
			setUpChildActivity("3", RecommendedNewsActivity.class,"http://wcf.open.cnblogs.com/news/recommend/paged/");
			title.setText("推荐新闻");
			break;
		case R.id.recommend_blogs:// 所有博客
			setUpChildActivity("4", AllBlogsActivity.class,"http://wcf.open.cnblogs.com/blog/sitehome/paged/");
			title.setText("所有博客");
			break;
		case R.id.read48_rank:// 48小时阅读排行榜
			setUpChildActivity("5", Hour48BlogsActivity.class,"");//此处没有将48小时的接口地址发送过去  48小时Activity直接写死了
			title.setText("48小时阅读排行榜");
			break;
		case R.id.recommend_rank:// 10天内阅读排行榜
			setUpChildActivity("6", Day10BlogsActivity.class,"");
			title.setText("10天内阅读排行榜");
			break;
		case R.id.booktag:// 书签
			setUpChildActivity("7", NewsMarksActivity.class,"");
			title.setText("书签");
			break;
		case R.id.history:// 离线阅读
			setUpChildActivity("8", OfflineBlogsActivity.class,"");
			title.setText("浏览");
			break;
		case R.id.search:// 搜索
			setUpChildActivity("9", SearchBlogerActivity.class,"");
			title.setText("搜索");
			break;
		case R.id.settings:// 设置
			setUpChildActivity("10", SettingActivity.class,"");
			title.setText("设置");
			break;
		case R.id.exit:// 退出
			System.exit(-1);//
			break;
		}

	}

	class AsyncMove2 extends AsyncTask<Integer, Integer, String> {

		@Override
		// 后台操作
		// 参数列表对应的是第一个参数
		// 方法的返回值对应的是第三个参数
		protected String doInBackground(Integer... params) {
			LinearLayout.LayoutParams lp = (LayoutParams) left_menu
					.getLayoutParams();
			int moveX = -lp.leftMargin;
			int times = moveX / 10;
			if (times <= 0) {
				times = left_menu.getWidth() / 10;
			}
			for (int i = 0; i < times + 2; i++) {
				publishProgress(0);
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return "";
		}

		@Override
		protected void onProgressUpdate(Integer... values) {

			LinearLayout.LayoutParams lp = (LayoutParams) left_menu
					.getLayoutParams();
			if (isopen) {
				lp.leftMargin = Math.min(lp.leftMargin + 20, 0);

			} else {
				lp.leftMargin = Math.max(lp.leftMargin - 20,
						-left_menu.getWidth());
			}
			left_menu.setLayoutParams(lp);

			super.onProgressUpdate(values);
		}
	}

	// AsyncTask<Params, Progress, Result>
	// class AsyncMove extends Thread {
	// @Override
	// public void run() {
	// LinearLayout.LayoutParams lp = (LayoutParams) left_menu
	// .getLayoutParams();
	// int moveX = -lp.leftMargin;
	// int times = moveX / 10;
	// if (times <= 0) {
	// times = left_menu.getWidth() / 10;
	// }
	// for (int i = 0; i < times + 2; i++) {
	// handler.sendEmptyMessage(0);
	// try {
	// Thread.sleep(5);
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }
	// }

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// 如果是返回键
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_UP) {
			if (!isopen) {
				isopen = true;
				// AsyncMove t = new AsyncMove();
				// t.start();
				new AsyncMove2().execute();
			} else {
				Builder builder = new Builder(HomeActivity.this);
				dialog = builder
						.setTitle("退出")
						.setMessage("确定退出博客园吗？")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										finish();

									}
								}).setNegativeButton("取消", null).create();
				dialog.show();

			}
			return true;
		}

		// 如果是菜单键
		if (event.getKeyCode() == KeyEvent.KEYCODE_MENU
				&& event.getAction() == KeyEvent.ACTION_UP) {
			if (!isopen) {
				isopen = true;
			} else {
				isopen = false;
			}
			System.out.println("菜单键被点击");
			// 进行菜单的打开关闭操作
			// AsyncMove t = new AsyncMove();
			// t.start();
			new AsyncMove2().execute();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		isopen = false;
		unregisterReceiver(receiver);
	}
	
}
