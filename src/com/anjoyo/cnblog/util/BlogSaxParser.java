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

import com.anjoyo.cnblog.entry.BlogsInfo;

public class BlogSaxParser extends DefaultHandler {
	private ArrayList<BlogsInfo> list;
	boolean isFeed;
	String tag;
	BlogsInfo blogsInfo;
	private StringBuffer sb = new StringBuffer();

	public BlogSaxParser() {
		this.list = new ArrayList<BlogsInfo>();
		isFeed = false;
	}

	/**
	 * 返回解析后的集合数据
	 * 
	 * @param newsUrl
	 *            解析的接口地址
	 * @return 返回博客内容
	 * @throws IOException
	 *             IO异常
	 * @throws SAXException
	 *             SAX异常
	 * @throws ParserConfigurationException
	 *             SAX解析异常
	 */
	public ArrayList<BlogsInfo> getBlogsInfos(String newsUrl)
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
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		super.startElement(uri, localName, qName, attributes);
		tag = qName;
		sb.delete(0, sb.length());
		if (qName.equals("entry")) {
			isFeed = true;
			blogsInfo = new BlogsInfo();
		} else if (qName.equals("feed")) {
			isFeed = false;
		} else if (qName.equals("link") && isFeed) {
			blogsInfo.setLink(attributes.getValue("href"));// 取出博客内容的超链接地址
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, qName);
		tag = "";
		if (qName.equals("entry")) {
			list.add(blogsInfo);
		}

	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
		sb.append(ch, start, length);
		String s = sb.toString();
		if (tag.equals("id") && isFeed) {
			blogsInfo.setId(Integer.parseInt(s));
		} else if (tag.equals("title") && isFeed) {
			blogsInfo.setTitle(s);
		} else if (tag.equals("summary") && isFeed) {
			blogsInfo.setSummary(s);
		} else if (tag.equals("published") && isFeed) {
			blogsInfo.setPublished(s);
		} else if (tag.equals("updated") && isFeed) {
			blogsInfo.setUpdated(s);
		} else if (tag.equals("name") && isFeed) {
			blogsInfo.setAuthorName(s);
		} else if (tag.equals("uri") && isFeed) {
			blogsInfo.setAuthorUri(s);
		} else if (tag.equals("avatar") && isFeed) {
			blogsInfo.setAuthorAvatar(s);
		} else if (tag.equals("link") && isFeed) {
			blogsInfo.setLink(s);
		} else if (tag.equals("diggs") && isFeed) {
			blogsInfo.setDiggs(Integer.parseInt(s));
		} else if (tag.equals("views") && isFeed) {
			blogsInfo.setViews(Integer.parseInt(s));
		} else if (tag.equals("comments") && isFeed) {
			blogsInfo.setComments(Integer.parseInt(s));
		}

	}

}
