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

public class SpiderThread extends Thread {
	private String urlStr;
	private Set<Proxy> proxySet = new HashSet<Proxy>();
	@Override
	public void run() {
		try {
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:31.0) Gecko/20100101 Firefox/31.0");
			conn.setRequestProperty("Host","www.xici.net.co");
			conn.setRequestProperty("Cache-Control","max-age=0");
			conn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			conn.connect();
			int code = -1;
			if((code = conn.getResponseCode())!=200){
				System.out.println("线程[" + getId() + "] 返回错误...["+code+"]\t"+urlStr);
				MainShell.spiderDataHandler(null);
				return;
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

	public SpiderThread(String urlStr) {
		super();
		this.urlStr = urlStr;
	}
}
