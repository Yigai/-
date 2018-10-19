package com.anjoyo.cnblog.activitys.setting;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;

import com.anjoyo.cnblog.activitys.HomeActivity;
import com.anjoyo.cnblog.activitys.R;
import com.anjoyo.cnblog.adapter.LeftmenuBgAtapter;
import com.anjoyo.cnblog.util.ImageEnorDis;
import com.anjoyo.cnblog.util.TouchListener;

public class SettingActivity extends Activity {
	private List<Integer> list;
	private GridView gv;
	private CheckBox downImg;
	private CheckBox eyesMode;
	private Button feedback, about;
	private LeftmenuBgAtapter adapter;
	public static int checkPositon;

	private SharedPreferences spf;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_layout);

		View v = findViewById(R.id.set);
		v.setOnTouchListener(new TouchListener(this, true));

		list = new ArrayList<Integer>();
		list.add(R.drawable.commlist_head_bg);
		list.add(R.drawable.left_menu_bg5);
		list.add(R.drawable.left_menu_bg6);
		list.add(R.drawable.left_menu_bg7);

		gv = (GridView) findViewById(R.id.gridview);
		downImg = (CheckBox) findViewById(R.id.downImg_choose);
		eyesMode = (CheckBox) findViewById(R.id.eyes_choose);
		feedback = (Button) findViewById(R.id.feedback_bt);
		about = (Button) findViewById(R.id.about_bt);
		spf = getSharedPreferences("SP", MODE_PRIVATE);
		checkPositon = spf.getInt("bg", 0);
		adapter = new LeftmenuBgAtapter(this, list);
		eyesMode.setChecked(spf.getBoolean("change", false));
		gv.setAdapter(adapter);

		downImg.setChecked(ImageEnorDis.isDownImg);
		/**
		 * 是否下载博主头像
		 */
		downImg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked)
					ImageEnorDis.isDownImg = true;// 下载头像
				else
					ImageEnorDis.isDownImg = false;// 不下载头像

				SharedPreferences spf = SettingActivity.this.getSharedPreferences(
						"SP", MODE_PRIVATE);
				Editor et = spf.edit();
				et.putBoolean("downImg", isChecked);
				et.commit();

			}
		});


		eyesMode.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				SharedPreferences spf = SettingActivity.this.getSharedPreferences(
						"SP", MODE_PRIVATE);
				Editor et = spf.edit();
				et.putBoolean("change", isChecked);
				et.commit();
			}
		});

		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				checkPositon = position;
				adapter.notifyDataSetChanged();
				HomeActivity.left_menu.setBackgroundResource(list.get(position));
				SharedPreferences spf = SettingActivity.this.getSharedPreferences(
						"SP", MODE_PRIVATE);
				Editor et = spf.edit();
				et.putInt("bg", position);
				et.commit();
			}
		});

		feedback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_SENDTO);
				intent.setType("message/rfc822");
				intent.putExtra(Intent.EXTRA_SUBJECT, "这是单方发送的邮件主题");
				intent.putExtra(Intent.EXTRA_TEXT, "这是单方发送的邮件内容");
				startActivity(Intent.createChooser(intent, "发送邮件..."));
			}
		});
		about.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutInflater inflater = getLayoutInflater();
				View view = inflater.inflate(R.layout.dialog_text, null);
				AlertDialog.Builder builder = new AlertDialog.Builder(
						SettingActivity.this);
				AlertDialog dialog = builder.create();
				dialog.setView(view, 0, 0, 0, 0);
				dialog.show();
			}
		});
	}
}
