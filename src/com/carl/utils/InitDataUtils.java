package com.carl.utils;

import java.io.IOException;
import java.util.Properties;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

public class InitDataUtils {
	private static Properties prop = new Properties();
	static{
		try {
			prop.load(InitDataUtils.class.getClassLoader().getResourceAsStream("proxyUrl.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static JSONArray getSearchUrl() {
		String u =(String) prop.get("url");
		JSONArray a  =JSON.parseObject(u).getJSONArray("url");
		return a;
	}
	public static int getPageCount() {
		int pageCount = Integer.parseInt(prop.getProperty("pageCount"));
		return pageCount;
	}
}
