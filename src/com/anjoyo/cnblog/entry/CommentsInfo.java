package com.anjoyo.cnblog.entry;

/**
 * 表示新闻评论对象 <entry> <id>204020</id> <title type="text" />
 * <published>2014-05-08T18:08:05+08:00</published>
 * <updated>2014-05-11T12:24:06Z</updated> <author> <name>LikeWind</name>
 * <uri>http://home.cnblogs.com/u/88217/</uri> </author> <content
 * type="text">马云应聘服务员，失败的原因不是因为才能，而是因为... 你懂的！</content> </entry>
 * 
 * @author ChnAdo
 * 
 */
public class CommentsInfo {
	private int id;
	private String title;
	private String published;
	private String updated;
	private String authorName;
	private String authorUri;
	private String content;

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

	public String getAuthorUri() {
		return authorUri;
	}

	public void setAuthorUri(String authorUri) {
		this.authorUri = authorUri;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "CommentsInfo [id=" + id + ", title=" + title + ", published="
				+ published + ", updated=" + updated + ", authorName="
				+ authorName + ", authorUri=" + authorUri + ", content="
				+ content + "]";
	}

}
