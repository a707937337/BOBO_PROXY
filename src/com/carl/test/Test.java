package com.carl.test;

import java.io.IOException;
import java.util.Properties;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

public class Test {
	public static void main(String[] args) throws IOException {
//		JSONObject j = new JSONObject();
//		List<String>urls = new ArrayList<String>();
//		urls.add("http://www.xici.net.co/nn/");
//		urls.add("http://www.xici.net.co/nt/");
//		urls.add("http://www.xici.net.co/wn/");
//		urls.add("http://www.xici.net.co/wt/");
//		urls.add("http://www.xici.net.co/qq/");
//		j.put("url", urls);
//		System.out.println(j.toJSONString());
//		String u = j.toJSONString();
//		
		Properties prop = new Properties();
		prop.load(Test.class.getClassLoader().getResourceAsStream("proxyUrl.properties"));
		String u =(String) prop.get("url");
		JSONArray a  =JSON.parseObject(u).getJSONArray("url");
		for (Object object : a) {
			System.out.println(object);
		}
		
	}
}
