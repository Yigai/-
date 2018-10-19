package com.anjoyo.cnblog.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

public class CheckNetWork {
	/**
	 * 检查网络是否连接
	 * 
	 * @param context
	 * @return true-连接正常<br>
	 *         false-网络异常
	 */

	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	public static boolean checkNetworkInfo(final Context context) {
		boolean isNetWork = false;
		// 如果3G网络和wifi网络都未连接，且不是处于正在连接状态 则进入Network Setting界面 由用户配置网络连接
		if (isNetworkConnected(context)) {
			isNetWork = true;
		} else {
			isNetWork = false;
		}
		if (!isNetWork) {
			AlertDialog.Builder alert = new AlertDialog.Builder(context);
			alert.setTitle("没网 啊，少年！ ");
			alert.setMessage("有网走遍天下，没网蜗居一隅");
			alert.setPositiveButton("返回", null);
			alert.setNegativeButton("设置网络",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							context.startActivity(new Intent(
									android.provider.Settings.ACTION_WIFI_SETTINGS));// 进入无线网络配置界面
						}

					});
			alert.create().show();
		}

		return isNetWork;

	}

}
