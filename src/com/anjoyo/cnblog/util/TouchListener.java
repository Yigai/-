package com.anjoyo.cnblog.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.anjoyo.cnblog.activitys.HomeActivity;

public class TouchListener implements OnTouchListener {
	Context context;
	float oldDist;
	float newDist;
	float oldY;
	float newY;
	List<Float> list = new ArrayList<Float>();
	boolean on_off;
	public TouchListener(Context context,boolean on_off) {
		this.context = context;
		this.on_off = on_off;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// DOWN MOVE UP
		// event.getX();//指的是控件的x坐标
		float f2 = event.getRawX();
		list.add(f2);
		oldDist = list.get(0);// 存集合里 又因为onTouch方法会不断会掉 这个值会不断加入集合中

		switch (event.getAction() & MotionEvent.ACTION_MASK) {// 获取动作 多点触控
		case MotionEvent.ACTION_DOWN:// 如果是按下
			oldY = event.getY();// 得到按下时控件的Y轴值
			return on_off;
		case MotionEvent.ACTION_UP:// 如果是抬起
			newDist = event.getRawX();// 获取手指抬起时获得的屏幕新的X的值
			if (oldDist < 100 && newDist - oldDist > 200) {// 如果手指按下的位置靠近屏幕左边100像素内，并且新位置的值减去旧位置的值大于200(从左向右滑动)
				// 打开菜单栏
				Intent intent = new Intent();
				intent.setAction("open");
				context.sendBroadcast(intent);
				HomeActivity.isopen = true;
			}
			// 如果新位置值减去旧位置值小于200(从右向左滑动)或者<新旧位置值差值大于0 并且新减旧小于200(从左向右滑动距离太短)
			// 并且是在距离屏幕100像素内>
			if (newDist - oldDist < -200
					|| (newDist - oldDist > 0 && newDist - oldDist < 200 && oldDist < 100)) {
				// 关闭菜单
				Intent intent = new Intent();
				intent.setAction("back");
				context.sendBroadcast(intent);
			}
			// 如果现在菜单是打开着的并且是从右向左滑动 且滑动距离很短(小于200) 则将菜单重新弹出
			if (newDist - oldDist < 0 && newDist - oldDist > -200
					&& HomeActivity.isopen) {
				// 打开菜单栏
				Intent intent = new Intent();
				intent.setAction("open");
				context.sendBroadcast(intent);
				HomeActivity.isopen = true;
			} else {
				list = new ArrayList<Float>();
				// 如果控件Y轴的变化大于50就认为他是要上下滑动listview不去执行左右开关操作
				newY = event.getY();
				// 如果手势是弧形 且弧形平缓就理解为是左右滑动
				if (Math.abs(newY - oldY) < 40
						&& Math.abs(newDist - oldDist) > 40) {
					return true;
				} else {// 如果手势是弧形 且弧形陡峭就理解为是上下滑动 不在执行打开关闭菜单操作 认为用户是在滑动listview
					return false;
				}
			}
		case MotionEvent.ACTION_MOVE:
			newDist = event.getRawX();
			float distance = newDist - oldDist;
			Intent intent = new Intent();
			intent.putExtra("distance", distance);
			intent.putExtra("old", oldDist);
			intent.setAction("move");
			context.sendBroadcast(intent);
			return false;
		}

		return true;
	}

}
