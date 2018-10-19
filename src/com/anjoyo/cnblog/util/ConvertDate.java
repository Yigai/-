package com.anjoyo.cnblog.util;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@SuppressLint("SimpleDateFormat")
public class ConvertDate {
	public static String updateToDate(String update) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss");
		String res = null;
		if(update != null){
			res = sdf1.format(sdf.parse(update));
			if(res != null);
//				
			else
				res = " 时间错误";
		}
		else
		    	res = "存储时间为空";
		
		return res;
	}
	public static String publishedToDate(String published) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss");
		return sdf1.format(sdf.parse(published));
	}
	public static String SubmitDateToDate(String SubmitDate) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss");
		return sdf1.format(sdf.parse(SubmitDate));
	}
	public static long updateToMillisecond(String update) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		return sdf.parse(update).getTime();
	}
	public static long publishedToMillisecond(String published) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		return sdf.parse(published).getTime();
	}
	public static long SubmitDateToMillisecond(String SubmitDate) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
		return sdf.parse(SubmitDate).getTime();
	}
}
