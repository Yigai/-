package com.anjoyo.cnblog.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.anjoyo.cnblog.entry.HotNewsContent;

public class GetNewsContent extends DefaultHandler {

	HotNewsContent hotnewscontentinfo;
	StringBuffer sb = new StringBuffer();
	String tag;
	boolean isContent;

	/**
	 * 
	 * @param id
	 *            新闻ID
	 * @return 得到的新闻内容
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public HotNewsContent getNewsContentInfo(int id) throws IOException,
			ParserConfigurationException, SAXException {
		URL url = new URL("http://wcf.open.cnblogs.com/news/item/" + id);
		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
		InputStream in = urlConn.getInputStream();
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = spf.newSAXParser();
		sp.parse(in, this);
		return hotnewscontentinfo;
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
		hotnewscontentinfo = new HotNewsContent();
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
		if (qName.equals("Content")) {
			isContent = true;
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, qName);
		if (qName.equals("Content")) {
			isContent = false;
		}
		if (!isContent) {
			tag = "";
		}

	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
		sb.append(ch, start, length);
		String s = sb.toString();
		if (tag.equals("Title")) {
			hotnewscontentinfo.setTitle(s);
		} else if (tag.equals("SourceName")) {
			hotnewscontentinfo.setSourceName(s);
		} else if (tag.equals("SubmitDate")) {
			hotnewscontentinfo.setSubmitDate(s);
		} else if (tag.equals("Content")) {
			hotnewscontentinfo.setContent(s);
		} else if (tag.equals("ImageUrl")) {
			hotnewscontentinfo.setImageUrl(s);
		} else if (tag.equals("PrevNews")) {
			hotnewscontentinfo.setPrevNews(Integer.parseInt(s));
		} else if (tag.equals("NextNews")) {
			hotnewscontentinfo.setNextNews(Integer.parseInt(s));
		} else if (tag.equals("CommentCount")) {
			hotnewscontentinfo.setCommentCount(Integer.parseInt(s));
		}

	}
}
