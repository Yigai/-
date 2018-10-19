package com.anjoyo.cnblog.entry;
/**
 * 
 * @author ChnAdo
 *	表示点击列表的item 得到的新闻的正文内容
 *
 */
public class HotNewsContent {
	private String title;
	private String sourceName;
	private String submitDate;
	private String content;
	private String imageUrl;
	private int prevNews;
	private int nextNews;
	private int commentCount;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSourceName() {
		return sourceName;
	}
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
	public String getSubmitDate() {
		return submitDate;
	}
	public void setSubmitDate(String submitDate) {
		this.submitDate = submitDate;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public int getPrevNews() {
		return prevNews;
	}
	public void setPrevNews(int prevNews) {
		this.prevNews = prevNews;
	}
	public int getNextNews() {
		return nextNews;
	}
	public void setNextNews(int nextNews) {
		this.nextNews = nextNews;
	}
	public int getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}
	
	
}
