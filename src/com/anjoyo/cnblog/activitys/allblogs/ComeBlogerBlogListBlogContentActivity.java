package com.anjoyo.cnblog.activitys.allblogs;

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
import android.widget.Toast;

import com.anjoyo.cnblog.activitys.R;
import com.anjoyo.cnblog.activitys.hotnews.NewsCommentsActivity;
import com.anjoyo.cnblog.entry.BlogerInfo;
import com.anjoyo.cnblog.entry.BlogsInfo;
import com.anjoyo.cnblog.sql.BlogSQLHelper;
import com.anjoyo.cnblog.util.GetBlogContent;
/**
 * 此Activity是从博主博客列表中跳转过来的 功能同BlogContentActivity类似 
 * 但是为了不导致逻辑混乱此Activity删除了 博主信息获取以及再次跳转
 * @author ChnAdo
 *
 */
public class ComeBlogerBlogListBlogContentActivity extends Activity implements
		OnClickListener {
	WebView blogContent;
	RelativeLayout rightPanel;

	TextView title;
	ImageView blogComment;
	ImageView saveBlog;
	ImageView toTop;

	BlogsInfo blogsInfo;
	boolean mShowing;// 控制右面板显示
	boolean blogerShowing;// 控制博主面板显示
	public static final int DEFAULTTIMEOUT = 3000;
	public static final int FADE_OUT = 1;
	public static final int SHOW_PROGRESS = 2;
	public static final int BLOGER_OUT = 3;
	public static final int BLOGER_SHOW = 4;
	String content;// 表示取得的正文内容 //源码是一堆html标签 带有css样式(字体颜色 大小 效果 等等)
					// 可以被WebView控件自动识别
	public static final String BLOGER_URL = "http://wcf.open.cnblogs.com/blog/bloggers/search?t=";
	BlogerInfo blogerInfo;
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
			case BLOGER_OUT:
				break;
			case BLOGER_SHOW:
				if (blogerShowing) {
					msg = obtainMessage(BLOGER_SHOW);
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
		setContentView(R.layout.comeblogerbloglistblogcontent_layout);
		blogContent = (WebView) findViewById(R.id.news_content);
		rightPanel = (RelativeLayout) findViewById(R.id.right_panel);
		title = (TextView) findViewById(R.id.news_content_title);
		blogComment = (ImageView) findViewById(R.id.hotnews_comment);
		saveBlog = (ImageView) findViewById(R.id.save_hotnews);
		toTop = (ImageView) findViewById(R.id.to_top);
		Intent intent = getIntent();
		int id = intent.getIntExtra("id", 0);
		String link = intent.getStringExtra("link");
		String blogTitle = intent.getStringExtra("blogTitle");
		String updateTime = intent.getStringExtra("updateTime");
		String blogerUrl = intent.getStringExtra("blogerUrl");
		String bloger = intent.getStringExtra("bloger");
		String summary = intent.getStringExtra("summary");
		blogsInfo = new BlogsInfo();
		blogsInfo.setId(id);
		blogsInfo.setLink(link);
		blogsInfo.setTitle(blogTitle);
		blogsInfo.setUpdated(updateTime);
		blogsInfo.setAuthorUri(blogerUrl);
		blogsInfo.setAuthorName(bloger);
		blogsInfo.setSummary(summary);
		// 博客标题
		title.setText(blogTitle);
		// 博主名字
		blogComment.setOnClickListener(this);
		saveBlog.setOnClickListener(this);
		toTop.setOnClickListener(this);

		WebSettings settings = blogContent.getSettings();
		settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		new GetContentTask(id).execute();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.hotnews_comment:// 评论
			Intent intent = new Intent(
					ComeBlogerBlogListBlogContentActivity.this,
					NewsCommentsActivity.class);
			intent.putExtra("ID", blogsInfo.getId());
			intent.putExtra("newsTitle", blogsInfo.getTitle());
			intent.putExtra("COMURL", "http://wcf.open.cnblogs.com/blog/post/");
			startActivity(intent);
			break;

		case R.id.save_hotnews:// 保存
			BlogSQLHelper.getDBInstance(
					ComeBlogerBlogListBlogContentActivity.this).insertIntoDB(
					content, blogsInfo);
			break;
		case R.id.to_top:// 回到顶部
			blogContent.scrollTo(0, 0);
			break;
		}

	}

	/**
	 * 异步任务 加载博客正文
	 * 
	 * @author ChnAdo
	 * 
	 */
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
			dialog = new ProgressDialog(
					ComeBlogerBlogListBlogContentActivity.this);
			dialog.setMessage("数据加载中，请稍后...");
			dialog.show();
		}

		@Override
		protected Object doInBackground(Object... params) {
			GetBlogContent getcontent = new GetBlogContent();
			try {
				content = getcontent.getBlogContentInfo(id);
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
			if (content != null) {
				blogContent.loadDataWithBaseURL(null, content, "text/html",
						"UTF-8", null);
			} else {
				Toast.makeText(ComeBlogerBlogListBlogContentActivity.this,
						"文章过长服务器限制数据返回！", Toast.LENGTH_LONG).show();
			}

			dialog.dismiss();
		}
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
		// handler.sendEmptyMessage(SHOW_PROGRESS);
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

}
