package com.anjoyo.cnblog.entry;

public class BlogerInfo {
	private String id;//链接URL跟link一致
	private String title;//作者名字
	private String updated;//发布时间
	private String link;//链接URL
	private String blogapp;//园子名字
	private String avatar;//头像地址
	private String postcount;//总博客数  (+1)
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUpdated() {
		return updated;
	}
	public void setUpdated(String updated) {
		this.updated = updated;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getBlogapp() {
		return blogapp;
	}
	public void setBlogapp(String blogapp) {
		this.blogapp = blogapp;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getPostcount() {
		return postcount;
	}
	public void setPostcount(String postcount) {
		this.postcount = postcount;
	}
	@Override
	public String toString() {
		return "BlogerInfo [id=" + id + ", title=" + title + ", updated="
				+ updated + ", link=" + link + ", blogapp=" + blogapp
				+ ", avatar=" + avatar + ", postcount=" + postcount + "]";
	}
	
	
}
