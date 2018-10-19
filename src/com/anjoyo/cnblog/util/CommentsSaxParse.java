package com.anjoyo.cnblog.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.anjoyo.cnblog.entry.CommentsInfo;

public class CommentsSaxParse extends DefaultHandler {
	ArrayList<CommentsInfo> list;
	StringBuffer sb = new StringBuffer();
	boolean isFeed;
	String tag;
	CommentsInfo commentsInfo;

	public CommentsSaxParse() {
		this.list = new ArrayList<CommentsInfo>();

	}

	/**
	 * 获取评论信息
	 * 
	 * @param url
	 *            评论的URL地址
	 * @return 评论
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public ArrayList<CommentsInfo> getCommentsInfo(String newsUrl)
			throws IOException, ParserConfigurationException, SAXException {
		URL url = new URL(newsUrl);
		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
		InputStream in = urlConn.getInputStream();
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = spf.newSAXParser();
		sp.parse(in, this);
		return list;
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		super.startElement(uri, localName, qName, attributes);
		tag = qName;
		sb.delete(0, sb.length());
		if (qName.equals("entry")) {
			isFeed = true;
			commentsInfo = new CommentsInfo();
		} else if (qName.equals("feed")) {
			isFeed = false;
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, qName);

		tag = "";
		if (qName.equals("entry")) {
			list.add(commentsInfo);
		}

	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
		sb.append(ch, start, length);
		String s = sb.toString();
		if (tag.equals("id") && isFeed) {
			commentsInfo.setId(Integer.parseInt(s));
		} else if (tag.equals("title") && isFeed) {
			commentsInfo.setTitle(s);
		} else if (tag.equals("published") && isFeed) {
			commentsInfo.setPublished(s);
		} else if (tag.equals("updated") && isFeed) {
			commentsInfo.setUpdated(s);
		} else if (tag.equals("name") && isFeed) {
			commentsInfo.setAuthorName(s);
		} else if (tag.equals("uri") && isFeed) {
			commentsInfo.setAuthorUri(s);
		} else if (tag.equals("content") && isFeed) {
			commentsInfo.setContent(s);
		}

	}
}
