package com.anjoyo.cnblog.entry;

public class BlogsInfo {
	private int id;
	private String title;
	private String summary;
	private String published;
	private String updated;
	private String authorName;
	private String authorAvatar;
	private String authorUri;
	private String link;
	private int diggs;
	private int views;
	private int comments;

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

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getAuthorAvatar() {
		return authorAvatar;
	}

	public void setAuthorAvatar(String authorAvatar) {
		this.authorAvatar = authorAvatar;
	}

	public String getAuthorUri() {
		return authorUri;
	}

	public void setAuthorUri(String authorUri) {
		this.authorUri = authorUri;
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
		return views;
	}

	public void setViews(int views) {
		this.views = views;
	}

	public int getComments() {
		return comments;
	}

	public void setComments(int comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		return "BlogsInfo [id=" + id + ", title=" + title + ", summary="
				+ summary + ", published=" + published + ", updated=" + updated
				+ ", authorName=" + authorName + ", authorAvatar="
				+ authorAvatar + ", authorUri=" + authorUri + ", link=" + link
				+ ", diggs=" + diggs + ", views=" + views + ", comments="
				+ comments + "]";
	}

}
