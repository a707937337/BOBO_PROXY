package com.carl.thread;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.carl.main.MainShell;
import com.carl.pojo.Proxy;

public class PageThread extends Thread {
	private String urlStr;
	private Set<Proxy> proxySet = new HashSet<Proxy>();
	@Override
	public void run() {
		try {
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.connect();
			if(conn.getResponseCode()!=200){
				System.out.println("线程[" + getId() + "] 抓取页面出错,返回错误."+urlStr);
				MainShell.spiderDataHandler(null);
			}
			InputStream in = conn.getInputStream();
			String page = IOUtils.toString(in, "utf-8");
			conn.disconnect();
			Document document = Jsoup.parse(page);
			Elements es = document.select("table").select("tr");
			for (int i = 1; i < es.size(); i++) {
				String ip = es.get(i).select("td").eq(1).text();
				int port = Integer.parseInt(es.get(i).select("td").eq(2).text());
				String type = es.get(i).select("td").eq(5).text();
				proxySet.add(new Proxy(ip, port, type));
			}
			MainShell.spiderDataHandler(proxySet);
			System.out.println("线程[" + getId() + "] 抓取页面完毕");
		} catch (Exception e) {
			
		}
	}

	public PageThread(String urlStr) {
		super();
		this.urlStr = urlStr;
	}
}
