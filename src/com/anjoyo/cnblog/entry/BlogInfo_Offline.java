package com.anjoyo.cnblog.entry;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 离线博客信息的 实体类
 * @author ChnAdo
 *
 */
public class BlogInfo_Offline {
	private String blogTitle;
	private String blogUrl;
	private String bloger;
	private String blogerUrl;
	private String blogText;
	private String blogSummary;
	private String updateTime;
	private long storeTime;
	private int blogId;
	public String getBlogTitle() {
		return blogTitle;
	}
	public void setBlogTitle(String blogTitle) {
		this.blogTitle = blogTitle;
	}
	public String getBlogUrl() {
		return blogUrl;
	}
	public void setBlogUrl(String blogUrl) {
		this.blogUrl = blogUrl;
	}
	public String getBloger() {
		return bloger;
	}
	public void setBloger(String bloger) {
		this.bloger = bloger;
	}
	public String getBlogerUrl() {
		return blogerUrl;
	}
	public void setBlogerUrl(String blogerUrl) {
		this.blogerUrl = blogerUrl;
	}
	public String getBlogText() {
		return blogText;
	}
	public void setBlogText(String blogText) {
		this.blogText = blogText;
	}
	public String getBlogSummary() {
		return blogSummary;
	}
	public void setBlogSummary(String blogSummary) {
		this.blogSummary = blogSummary;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public long getStoreTime() {
		return storeTime;
	}
	public void setStoreTime(long storeTime) {
		this.storeTime = storeTime;
	}
	public int getBlogId() {
		return blogId;
	}
	public void setBlogId(int blogId) {
		this.blogId = blogId;
	}
	
	
	//转换时间
	public String getStoreTimeChanged(){
		Date date = new Date(storeTime);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
		
		String time = sdf.format(date);
		return time;
	}
	
	
	
	@Override
	public String toString() {
		return "BlogInfo_Offline [blogTitle=" + blogTitle + ", blogUrl="
				+ blogUrl + ", bloger=" + bloger + ", blogerUrl=" + blogerUrl
				+ ", blogText=" + blogText + ", blogSummary=" + blogSummary
				+ ", updateTime=" + updateTime + ", storeTime=" + storeTime
				+ ", blogId=" + blogId + "]";
	}
	
	
	
	
}	
