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

import android.util.Log;

import com.anjoyo.cnblog.entry.BlogerInfo;
/**
 * 	博主信息的SAX解析器
 * @author ChnAdo
 *
 */
public class BlogerSAXHandler extends DefaultHandler{
	
	private ArrayList<BlogerInfo> list;
	private String tag;
	private BlogerInfo blogerInfo;
	private Boolean isFeed; 
	private StringBuffer sb = new StringBuffer();
	
	public BlogerSAXHandler(){
		this.list = new ArrayList<BlogerInfo>();
		isFeed = false;
	}

	public ArrayList<BlogerInfo> getBlogerInfo(String blogurl) throws IOException, ParserConfigurationException, SAXException{
		URL url = new URL(blogurl);
		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
		InputStream in = urlConn.getInputStream();
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = spf.newSAXParser();
		sp.parse(in, this);
		return list;
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		tag = qName;
		sb.delete(0, sb.length());
		if(qName.equals("entry")){
			isFeed = true;
			blogerInfo = new BlogerInfo();
		}else if(qName.equals("feed")){
			isFeed = false;
		}else if(qName.equals("link")&&isFeed){
			blogerInfo.setLink(attributes.getValue("href"));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		tag = "";
		if(qName.equals("entry")){
			list.add(blogerInfo);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		sb.append(ch, start, length);
		String s = sb.toString();
		if(tag.equals("id")&&isFeed){
			blogerInfo.setId(s);
		}else if(tag.equals("title")&&isFeed){
			blogerInfo.setTitle(s);
		}else if(tag.equals("blogapp")){
			blogerInfo.setBlogapp(s);
		}else if(tag.equals("avatar")){
			blogerInfo.setAvatar(s);
		}else if(tag.equals("updated")&&isFeed){
			blogerInfo.setUpdated(s);
		}else if(tag.equals("postcount")){
			blogerInfo.setPostcount(s);
		}
	}

}
