package com.anjoyo.cnblog.entry;

public class HotNewsInfo {
	private int id;// 新闻标识id
	private String title;// 新闻标题
	private String summary;// 缩略预览
	private String published;// 新闻发布时间
	private String updated;// 博客园的更新时间
	private String link;// 去往新闻正文的url地址
	private int diggs;// 推荐数
	private int Views;// 浏览数量
	private int Comments;// 评论数量
	private String topic;// 未知
	private String topicIcon;// 未知
	private String sourceName;// 新闻来源/出处
	private boolean isChecked;

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getPublished() {
		return published;
	}

	public void setPublished(String published) {
		this.published = published;
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

	public int getDiggs() {
		return diggs;
	}

	public void setDiggs(int diggs) {
		this.diggs = diggs;
	}

	public int getViews() {
		return Views;
	}

	public void setViews(int views) {
		Views = views;
	}

	public int getComments() {
		return Comments;
	}

	public void setComments(int comments) {
		Comments = comments;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getTopicIcon() {
		return topicIcon;
	}

	public void setTopicIcon(String topicIcon) {
		this.topicIcon = topicIcon;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	@Override
	public String toString() {
		return "HotNewsInfo [id=" + id + ", title=" + title + ", summary="
				+ summary + ", published=" + published + ", updated=" + updated
				+ ", link=" + link + ", diggs=" + diggs + ", Views=" + Views
				+ ", Comments=" + Comments + ", topic=" + topic
				+ ", topicIcon=" + topicIcon + ", sourceName=" + sourceName
				+ ", getId()=" + getId() + ", getTitle()=" + getTitle()
				+ ", getSummary()=" + getSummary() + ", getPublished()="
				+ getPublished() + ", getUpdated()=" + getUpdated()
				+ ", getLink()=" + getLink() + ", getDiggs()=" + getDiggs()
				+ ", getViews()=" + getViews() + ", getComments()="
				+ getComments() + ", getTopic()=" + getTopic()
				+ ", getTopicIcon()=" + getTopicIcon() + ", getSourceName()="
				+ getSourceName() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}

}
