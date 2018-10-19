package com.anjoyo.cnblog.activitys.hotnews;

import java.io.IOException;

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
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anjoyo.cnblog.activitys.R;
import com.anjoyo.cnblog.entry.HotNewsContent;
import com.anjoyo.cnblog.entry.HotNewsInfo;
import com.anjoyo.cnblog.sql.NewsSQLHelper;
import com.anjoyo.cnblog.util.GetNewsContent;

public class HotNewsContentActivity extends Activity implements OnClickListener{
	public static final int DEFAULTTIMEOUT = 3000;
	public static final int FADE_OUT = 1;
	public static final int SHOW_PROGRESS = 2;
	HotNewsInfo newsinfo;
	TextView newsContent_title;
	WebView newsContent;
	RelativeLayout rightPanel;
	ImageView newsComment, save_news, to_top;
	HotNewsContent content;
	boolean mShowing;
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case FADE_OUT:
				hideRightPanel();
				break;

			case SHOW_PROGRESS:
				if (mShowing) {
					msg = obtainMessage(SHOW_PROGRESS);
					sendMessageDelayed(msg, 1000);
				}
				break;

			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hotnewscontent_layout);
		Intent intent = getIntent();
		int id = intent.getIntExtra("id", 0);
		String newstitle = intent.getStringExtra("newstitle");
		String link = intent.getStringExtra("link");
		String summary = intent.getStringExtra("summary");
		String updated = intent.getStringExtra("updated");
		newsinfo = new HotNewsInfo();
		newsinfo.setId(id);
		newsinfo.setTitle(newstitle);
		newsinfo.setLink(link);
		newsinfo.setSummary(summary);
		newsinfo.setUpdated(updated);
		newsContent_title = (TextView) findViewById(R.id.news_content_title);
		newsContent = (WebView) findViewById(R.id.news_content);
		rightPanel = (RelativeLayout) findViewById(R.id.right_panel);
		newsComment = (ImageView) findViewById(R.id.hotnews_comment);
		save_news = (ImageView) findViewById(R.id.save_hotnews);
		to_top = (ImageView) findViewById(R.id.to_top);

		newsContent_title.setText(newstitle);
		WebSettings settings = newsContent.getSettings();
		settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		// WebSettings settings = newsContent.getSettings();
		// settings.setUseWideViewPort(true);
		// settings.setLoadWithOverviewMode(true);
		new GetContentTask(id).execute();

		newsComment.setOnClickListener(this);
		save_news.setOnClickListener(this);
		to_top.setOnClickListener(this);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		showRightPanel();

		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 显示右侧面板
	 */
	public void showRightPanel() {
		if (!mShowing) {// 如果此时面板没显示 就让它显示出来
			rightPanel.setVisibility(View.VISIBLE);
			mShowing = true;
		}
//		handler.sendEmptyMessage(SHOW_PROGRESS);
		Message msg = handler.obtainMessage(FADE_OUT);
		handler.removeMessages(FADE_OUT);
		handler.sendMessageDelayed(msg, DEFAULTTIMEOUT);
	}

	/**
	 * 隐藏右侧面板
	 */
	public void hideRightPanel() {
		if (mShowing) {
			handler.removeMessages(1);
			rightPanel.setVisibility(View.GONE);
			mShowing = false;
		}
	}

	class GetContentTask extends AsyncTask {
		ProgressDialog dialog;
		int id;

		public GetContentTask(int id) {
			this.id = id;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog = new ProgressDialog(HotNewsContentActivity.this);
			dialog.setMessage("数据加载中，请稍后...");
			dialog.show();
		}

		@Override
		protected Object doInBackground(Object... params) {
			GetNewsContent getcontent = new GetNewsContent();
			try {
				content = getcontent.getNewsContentInfo(id);
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
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			// 加载webview

			newsContent.loadDataWithBaseURL(null, content.getContent(),
					"text/html", "UTF-8", null);
			dialog.dismiss();
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.hotnews_comment://跳转到评论页面
			Intent intent = new Intent(HotNewsContentActivity.this, NewsCommentsActivity.class);
			intent.putExtra("ID", newsinfo.getId());
			intent.putExtra("newsTitle", newsinfo.getTitle());
			intent.putExtra("COMURL", "http://wcf.open.cnblogs.com/news/item/");
			startActivity(intent);
			break;

		case R.id.save_hotnews://保存书签
			NewsSQLHelper newshelper = NewsSQLHelper.getDBInstance(HotNewsContentActivity.this);
			newshelper.insertintoNews(newsinfo.getLink(), newsinfo.getTitle(), newsinfo.getId());
			break;
		case R.id.to_top://滚回顶部
			newsContent.scrollTo(0, 0);
			break;
		}
	}

}
