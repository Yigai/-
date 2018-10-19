package com.anjoyo.cnblog.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.anjoyo.cnblog.entry.BlogsInfo;
/**
 * 根据博主的园子名字 解析该博主的所有的博客列表
 * @author ChnAdo
 *
 */
public class GetPersonalBlogList extends DefaultHandler {
	
	private ArrayList<BlogsInfo> list;
	private String tag;
	private BlogsInfo blogInfo;
	private Boolean isFeed;
	private String[] blogerInfo;
	private StringBuffer sb = new StringBuffer();
	
	public GetPersonalBlogList(String blogApp,int pageIndex,int pageSize){
		this.list = new ArrayList<BlogsInfo>();
		blogerInfo = new String[4];
		isFeed = false;
		URL url;
		try {
			url = new URL("http://wcf.open.cnblogs.com/blog/u/" + blogApp + "/posts/" + pageIndex + "/" + pageSize);
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			InputStream in = urlConn.getInputStream();
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			sp.parse(in, this);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<BlogsInfo> getBlogsInfo() {
		return list;
	}
	
	public String[] getBlogerInfo(){
		return blogerInfo;
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		Log.v("tag", "startDocument");
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
		Log.v("tag", "endDocument");
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		Log.v("tag", "startElement:"+qName);
		tag = qName;
		sb.delete(0, sb.length());
		if(qName.equals("entry")){
			isFeed = true;
			blogInfo = new BlogsInfo();
		}else if(qName.equals("feed")){
			isFeed = false;
		}else if(qName.equals("link")&&isFeed){
			blogInfo.setLink(attributes.getValue("href"));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		Log.v("tag", "endElement:"+qName);
		tag = "";
		if(qName.equals("entry")){
			list.add(blogInfo);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		sb.append(ch, start, length);
		String s = sb.toString();
		if(tag.equals("id")&&isFeed){
			blogInfo.setId(Integer.parseInt(s));
		}else if(tag.equals("title")&&isFeed){
			blogInfo.setTitle(s);
		}else if(tag.equals("summary")){
			blogInfo.setSummary(s);
		}else if(tag.equals("published")){
			blogInfo.setPublished(s);
		}else if(tag.equals("updated")&&isFeed){
			blogInfo.setUpdated(s);
		}else if(tag.equals("avatar")){
			blogInfo.setAuthorAvatar(s);
		}else if(tag.equals("diggs")){
			blogInfo.setDiggs(Integer.parseInt(s));
		}else if(tag.equals("views")){
			blogInfo.setViews(Integer.parseInt(s));
		}else if(tag.equals("comments")){
			blogInfo.setComments(Integer.parseInt(s));
		}else if(tag.equals("name")){
			if(isFeed)
				blogInfo.setAuthorName(s);
			else
				blogerInfo[0] = s;
		}else if(tag.equals("uri")){
			if(isFeed)
				blogInfo.setAuthorUri(s);
			else
				blogerInfo[1] = s;
		}else if(tag.equals("logo")){
			blogerInfo[2] = s;
		}else if(tag.equals("postcount")){
			blogerInfo[3] = s;
		}
	}

}
