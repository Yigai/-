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

public class GetBlogContent extends DefaultHandler{

	String list;
	StringBuffer sb = new StringBuffer();
	String tag;
	/**
	 * 
	 * @param id
	 *            博客ID
	 * @return 得到的博客内容
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public String getBlogContentInfo(int id) throws IOException,
			ParserConfigurationException, SAXException {
		URL url = new URL("http://wcf.open.cnblogs.com/blog/post/body/" + id);
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
		if (qName.equals("string")) {
			tag = qName;
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, qName);
		if (qName.equals("string")) {
			tag = "";
		}

	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
		sb.append(ch, start, length);
		if (tag.equals("string")) {
			list = sb.toString();
		}
		
	}
}
