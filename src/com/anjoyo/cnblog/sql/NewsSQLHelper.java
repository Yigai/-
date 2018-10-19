package com.anjoyo.cnblog.sql;

import java.util.ArrayList;

import com.anjoyo.cnblog.entry.HotNewsInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class NewsSQLHelper {
	SQLiteDatabase db;
	Context context;
	NewsSQL newsSQL = null;
	static NewsSQLHelper mDBInstance;
	// 数据库的名字
	public static final String DB_NAME = "CNBLOGS_NEWS.db";
	// 存储书签的表名
	public static final String TB_NAME = "newstab";
	// 数据库版本号
	public static final int VERSION = 1;
	// 建表语句
	/*
	 * _id url title newsId
	 */
	public final String createTBNEWS = "create table " + " if not exists "
			+ TB_NAME + " (_id integer primary key autoincrement not null,"
			+ " url varchar(100) unique not null,"
			+ " title varchar(100) not null," + " newsId int not null)";

	/**
	 * 
	 * @param context
	 * @return mDBInstance 返回NewsSQLHelper 对象
	 */
	public static NewsSQLHelper getDBInstance(Context context) {
		if (mDBInstance == null) {
			mDBInstance = new NewsSQLHelper(context);
		}
		return mDBInstance;
	}

	/**
	 * 构造 初始化NewsSQL 初始化DB对象
	 * 
	 * @param context
	 */
	private NewsSQLHelper(Context context) {
		this.context = context;
		newsSQL = new NewsSQL(context, DB_NAME, null, VERSION);
		db = newsSQL.getWritableDatabase();
	}

	/**
	 * 插入数据
	 * 
	 * @param url
	 *            新闻的URL地址
	 * @param title
	 *            新闻的标题
	 * @param id
	 *            新闻的ID
	 */
	public void insertintoNews(String url, String title, int id) {
		ContentValues values = new ContentValues();
		values.clear();
		values.put("url", url);
		values.put("title", title);
		values.put("newsId", id);
		long res = 0;
		int dbid = 0;
		// 如果书签已经存在了 二次插入 需要进行 数据是否存在的判断
		Cursor c = db.rawQuery("select * from " + TB_NAME + " where newsId="
				+ id, null);
		if (c.moveToFirst()) {
			 dbid = c.getInt(c.getColumnIndex("newsId"));
		}
		if (dbid != id) {
			res = db.insert(TB_NAME, null, values);

			if (res >= 0) {
				Toast.makeText(context, "存储成功", Toast.LENGTH_SHORT).show();
			} else if (res < 0) {
				Toast.makeText(context, "插入失败", Toast.LENGTH_SHORT).show();
			}

		} else {
			Toast.makeText(context, "链接已经存在", Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * 查询出所有的数据 待会适配到适配器中
	 * 
	 * @return news 返回值指的是 查询出的所有数据
	 */
	public ArrayList<HotNewsInfo> getNews() {
		ArrayList<HotNewsInfo> news = new ArrayList<HotNewsInfo>();
		HotNewsInfo n;
		Cursor cursor = db.rawQuery("select * from " + TB_NAME, null);
		int count = cursor.getColumnCount();
		int i = 0;
		if (count <= 0) {// 如果没有数据
			return null;
		}
		while (cursor.moveToNext() && i < count) {
			n = new HotNewsInfo();
			n.setLink(cursor.getString(cursor.getColumnIndex("url")));
			n.setTitle(cursor.getString(cursor.getColumnIndex("title")));
			n.setId(cursor.getInt(cursor.getColumnIndex("newsId")));
			news.add(n);
		}
		cursor.close();
		return news;

	}

	/**
	 * 删除一条新闻
	 * 
	 * @param id
	 *            新闻id
	 */
	public void deleteNews(int id) {
		db.execSQL("delete from " + TB_NAME + " where newsId=" + id);
		Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
	}

	/**
	 * 删除全部新闻
	 * 
	 * @param id
	 *            新闻id
	 */
	public void deleteAllNews(int id) {
		db.execSQL("delete from " + TB_NAME);
	}

	/**
	 * 新闻的数据库类
	 * 
	 * @author ChnAdo
	 * 
	 */
	class NewsSQL extends SQLiteOpenHelper {

		public NewsSQL(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(createTBNEWS);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub

		}

	}
}
