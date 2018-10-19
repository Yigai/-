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

import com.anjoyo.cnblog.entry.HotNewsInfo;

public class HotNewsSaxParse extends DefaultHandler {
private ArrayList<HotNewsInfo> list;
	boolean isFeed;
	String tag;
	HotNewsInfo hotNewsInfo;
	private StringBuffer sb = new StringBuffer();
	public HotNewsSaxParse(){
		this.list = new ArrayList<HotNewsInfo>();
		isFeed = false;
	}
	/**
	 * 返回解析后的集合数据
	 * @param newsUrl 解析的接口地址
	 * @return 返回热门新闻内容
	 * @throws IOException IO异常
	 * @throws SAXException SAX异常
	 * @throws ParserConfigurationException  SAX解析异常
	 */
	public ArrayList<HotNewsInfo> getHotNewsInfos(String newsUrl) throws IOException, ParserConfigurationException, SAXException{
		URL url = new URL(newsUrl);
		HttpURLConnection urlConn  = (HttpURLConnection) url.openConnection();
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
			hotNewsInfo = new HotNewsInfo();
		}else if (qName.equals("feed")) {
			isFeed = false;
		}else if (qName.equals("link")&&isFeed) {
			hotNewsInfo.setLink(attributes.getValue("href"));//取出新闻详细内容的超链接地址
		}
		
		
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, qName);
		tag = "";
		if (qName.equals("entry")) {
			list.add(hotNewsInfo);
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
		if (tag.equals("id")&&isFeed) {
			hotNewsInfo.setId(Integer.parseInt(s));
		}else if (tag.equals("title")&&isFeed) {
			hotNewsInfo.setTitle(s);
		}else if (tag.equals("summary")&&isFeed) {
			hotNewsInfo.setSummary(s);
		}else if (tag.equals("published")&&isFeed) {
			hotNewsInfo.setPublished(s);
		}else if (tag.equals("updated")&&isFeed) {
			hotNewsInfo.setUpdated(s);
		}else if (tag.equals("diggs")&&isFeed) {
			hotNewsInfo.setDiggs(Integer.parseInt(s));
		}else if (tag.equals("views")&&isFeed) {
			hotNewsInfo.setViews(Integer.parseInt(s));
		}else if (tag.equals("comments")&&isFeed) {
			hotNewsInfo.setComments(Integer.parseInt(s));
		}else if (tag.equals("topic")&&isFeed) {
			hotNewsInfo.setTopic(s);
		}else if (tag.equals("topicIcon")&&isFeed) {
			hotNewsInfo.setTopicIcon(s);
		}else if (tag.equals("sourceName")&&isFeed) {
			hotNewsInfo.setSourceName(s);
		}
		
	}

}
