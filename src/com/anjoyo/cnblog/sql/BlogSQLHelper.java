package com.anjoyo.cnblog.sql;

import java.util.ArrayList;

import com.anjoyo.cnblog.entry.BlogInfo_Offline;
import com.anjoyo.cnblog.entry.BlogsInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class BlogSQLHelper {
	SQLiteDatabase db;
	Context context;
	OfflineBlogSQL offlineSQL = null;
	static BlogSQLHelper mDBInstance;
	// 数据库的名字
	public static final String DB_NAME = "CNBLOGS_OFFLINE.db";
	// 存储书签的表名
	public static final String TB_NAME = "BlogOffline";
	// 数据库版本号
	public static final int VERSION = 1;
	// 建表语句
	/*
	 * _id url title newsId
	 */
	// SQLite 只有INTEGER TEXT REAL BLOB 这四种特有的数据类型 但是却支持常见的其他数据库字段
	// BLOB类型：BLOB类型的数据数据不做任何转换，以输入形式存储。
	public final String createTBNEWS = "create table "
			+ " if not exists "
			+ TB_NAME
			+ " (_id integer primary key autoincrement not null,"
			+ " blogTitle varchar(200),"
			+ " bloger varchar(100),"
			+ " blogerUrl varchar(200),"
			+ " blogSummary varchar(200) , blogId integer unique , storeTime long , updateTime varchar , blogText blob , blogUrl varchar(200) not null )";

	/**
	 * 
	 * @param context
	 * @return mDBInstance 返回NewsSQLHelper 对象
	 */
	public static BlogSQLHelper getDBInstance(Context context) {
		if (mDBInstance == null) {
			mDBInstance = new BlogSQLHelper(context);
		}
		return mDBInstance;
	}

	/**
	 * 构造 初始化offlineSQL 初始化DB对象
	 * 
	 * @param context
	 */
	private BlogSQLHelper(Context context) {
		this.context = context;
		offlineSQL = new OfflineBlogSQL(context, DB_NAME, null, VERSION);
		db = offlineSQL.getWritableDatabase();
	}

	/**
	 * 插入离线博客
	 * 
	 * @param text
	 *            表示新闻内容
	 * @param info
	 *            博客信息
	 */
	public void insertIntoDB(String text, BlogsInfo info) {
		ContentValues values = new ContentValues();
		values.clear();
		String Summary = info.getSummary();
		String title = info.getTitle();
		String bloger = info.getAuthorName();
		String blogerUrl = info.getAuthorUri();
		String bloglink = info.getLink();
		long storeTime = System.currentTimeMillis();
		String updateTime = info.getUpdated();
		int blogId = info.getId();
		values.put("blogSummary", Summary);
		values.put("blogTitle", title);
		values.put("bloger", bloger);
		values.put("blogerUrl", blogerUrl);
		values.put("blogUrl", bloglink);
		values.put("storeTime", storeTime);
		values.put("updateTime", updateTime);
		values.put("blogId", blogId);
		values.put("blogText", text);
		long res = 0;
		int dbid = 0;
		// 如果书签已经存在了 二次插入 需要进行 数据是否存在的判断
		Cursor c = db.rawQuery("select * from " + TB_NAME + " where blogId="
				+ blogId, null);
		if (c.moveToFirst()) {
			dbid = c.getInt(c.getColumnIndex("blogId"));
		}
		if (dbid != blogId) {
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
	 * 查询所有数据 -------------列表
	 * 
	 * @return 返回离线博客列表
	 */
	public ArrayList<BlogInfo_Offline> getBlogDBData() {
		ArrayList<BlogInfo_Offline> list = new ArrayList<BlogInfo_Offline>();
		Cursor cursor = db.rawQuery("select * from " + TB_NAME, null);
		int count = cursor.getColumnCount();
		if (count <= 0) {
			return null;
		} else {
			while (cursor.moveToNext()) {
				BlogInfo_Offline blog_off = new BlogInfo_Offline();
				blog_off.setBloger(cursor.getString(cursor
						.getColumnIndex("bloger")));
				blog_off.setBlogTitle(cursor.getString(cursor
						.getColumnIndex("blogTitle")));
				blog_off.setBlogUrl(cursor.getString(cursor
						.getColumnIndex("blogUrl")));
				blog_off.setBlogId(cursor.getInt(cursor
						.getColumnIndex("blogId")));
				blog_off.setBlogText(cursor.getString(cursor
						.getColumnIndex("blogText")));
				blog_off.setUpdateTime(cursor.getString(cursor
						.getColumnIndex("updateTime")));
				blog_off.setStoreTime(cursor.getLong(cursor
						.getColumnIndex("storeTime")));
				blog_off.setBlogSummary(cursor.getString(cursor
						.getColumnIndex("blogSummary")));
				blog_off.setBlogerUrl(cursor.getString(cursor
						.getColumnIndex("blogerUrl")));
				list.add(blog_off);
			}
			cursor.close();
			return list;
		}

	}

	/**
	 * 查询一条详情-------------某条博客的详情
	 * 
	 * @return 返回一条博客详情
	 */
	public BlogInfo_Offline getItemData(int blogId) {
		Cursor cursor = db.rawQuery("select * from " + TB_NAME
				+ " where blogId=" + blogId, null);
		BlogInfo_Offline blog_off = new BlogInfo_Offline();
		if (cursor.getColumnCount() == 0 || cursor == null) {
			return null;
		} else {
			cursor.moveToFirst();
			blog_off.setBloger(cursor.getString(cursor.getColumnIndex("bloger")));
			blog_off.setBlogTitle(cursor.getString(cursor
					.getColumnIndex("blogTitle")));
			blog_off.setBlogUrl(cursor.getString(cursor
					.getColumnIndex("blogUrl")));
			blog_off.setBlogId(cursor.getInt(cursor.getColumnIndex("blogId")));
			blog_off.setBlogText(cursor.getString(cursor
					.getColumnIndex("blogText")));
			blog_off.setUpdateTime(cursor.getString(cursor
					.getColumnIndex("updateTime")));
			blog_off.setStoreTime(cursor.getLong(cursor
					.getColumnIndex("storeTime")));
			blog_off.setBlogSummary(cursor.getString(cursor
					.getColumnIndex("blogSummary")));
			blog_off.setBlogerUrl(cursor.getString(cursor
					.getColumnIndex("blogerUrl")));
			cursor.close();
			return blog_off;
		}

	}

	/**
	 * 删除一条离线博客
	 * 
	 * @param id
	 *            离线博客ID
	 */
	public void deleteItem(int id) {
		db.execSQL("delete from " + TB_NAME + " where blogId =" + id);
		Toast.makeText(context, "数据已删除", Toast.LENGTH_SHORT).show();
	}

	/**
	 * 删除全部离线博客
	 * 
	 * @param id
	 *            离线博客ID
	 */
	public void deleteAll() {
		db.execSQL("delete from " + TB_NAME);
		Toast.makeText(context, "数据已全部删除！！", Toast.LENGTH_SHORT).show();
	}

	/**
	 * 博客的数据库类
	 * 
	 * @author ChnAdo
	 * 
	 */
	class OfflineBlogSQL extends SQLiteOpenHelper {

		public OfflineBlogSQL(Context context, String name,
				CursorFactory factory, int version) {
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
