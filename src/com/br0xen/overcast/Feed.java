package com.br0xen.overcast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.os.AsyncTask;

public class Feed {
	public int id;
	public String title = "";
	public String url = "";
	public String file_name = "";
	public String full_file_name = "";
	public int file_length = 0;
	public String file_type = "";
	public Date pub_date = new Date(0);
	public boolean updated = false;

	public Feed(String url) {
		this.url = url;
	}

	public void updateFeed() {
		DownloadFilesTask downloadTask = new DownloadFilesTask();
		downloadTask.execute(url);
	}

	@Override
	public String toString() {
		if(this.title != null) {
			if(this.title.equals("")) {
				return this.url;
			} else {
				return this.title;
			}
		} else {
			this.title="";
			return "";
		}
	}

	public void setURL(String new_url) {
		this.url = new_url;
	}

	public void updateFeed(String feed_xml) {
		if(feed_xml.equals("")) { 
			this.title = "Error Loading Feed!";
			this.updated = true;
			return; 
		}
		// Parse the feed and update the object
		Document feed_doc = parseFeed(feed_xml);
		if(feed_doc != null) {
			Element feed_element = feed_doc.getDocumentElement();
			if(!feed_element.hasChildNodes()) { return; }
			Element newest_feed = xmlGetFirstNode(feed_element, "item");
			this.title = xmlGetNodeContents(newest_feed, "title");
			this.full_file_name = xmlGetAttr(newest_feed, "enclosure", "url");
			int slashIndex = full_file_name.lastIndexOf('/');
			this.file_name = full_file_name.substring(slashIndex+1);
			try{
				this.file_length = Integer.parseInt(xmlGetAttr(newest_feed, "enclosure", "length"));
			} catch(NumberFormatException nfe) {
				this.file_length = 0;
			}
			this.file_type = xmlGetAttr(newest_feed, "enclosure", "type");
			String pd = xmlGetNodeContents(newest_feed, "pubDate");
			DateFormat formatter;
			formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z");
			try {
				this.pub_date = (Date) formatter.parse(pd);
			} catch(Exception e) {
				this.pub_date = new Date(0);
			}
		} else {
			this.title = "Error Loading Feed!";
			this.updated = true;
			return; 
		}
		this.updated = true;
	}

	private Document parseFeed(String xml) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(xml));
			is.setEncoding("UTF-8");
			return builder.parse(is);
		} catch(ParserConfigurationException pce) {
			pce.printStackTrace();
			return null;
		} catch(SAXException se) {
			se.printStackTrace();
			return null;
		} catch(IOException ioe) {
			ioe.printStackTrace();
			return null;
		}
	}

	private Element xmlGetFirstNode(Element xmlDoc, String tagName) {
		if(xmlDoc.hasChildNodes()) {
			NodeList w = xmlDoc.getElementsByTagName(tagName);
			return (Element) w.item(0);
		}
		return null;
	}

	private String xmlGetNodeContents(Element xmlDoc, String tagName) {
		if(xmlDoc!=null) {
			if(xmlDoc.hasChildNodes()) {
				String textVal = null;
				NodeList nl = xmlDoc.getElementsByTagName(tagName);
				if(nl != null && nl.getLength() > 0) {
					Element el = (Element)nl.item(0);
					textVal = el.getFirstChild().getNodeValue();
				}
				return textVal;
			}
		}
		return null;
	}

	private String xmlGetAttr(Element xmlDoc, String tagName, String attrName) {
		if(xmlDoc!=null) {
			if(xmlDoc.hasChildNodes()) {
				NodeList w = xmlDoc.getElementsByTagName(tagName);
				NamedNodeMap attrs;
				if(w.getLength() > 0) {
					attrs = w.item(0).getAttributes();
					if(attrs.getLength() > 0) {
						return attrs.getNamedItem(attrName).getTextContent();
					}
				}
			}
		}
		return "";
	}

	private class DownloadFilesTask extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... urls) {
			HttpClient client = new DefaultHttpClient();
			String ret = "";
			try {
				String line = "";
				HttpGet request = new HttpGet(urls[0]);
				HttpResponse response = client.execute(request);
				BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				while((line=rd.readLine()) != null) {
					ret+=line+System.getProperty("line.separator");
				}
			} catch(IllegalArgumentException e1) {
				e1.printStackTrace();
			} catch(IOException e2) {
				e2.printStackTrace();
			}
			return ret;
		}
		protected void onProgressUpdate(Void... progress) { }
		protected void onPostExecute(String result) { 
			updateFeed(result);
		}
	}
}
