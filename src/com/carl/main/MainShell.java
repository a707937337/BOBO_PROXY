package com.carl.main;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSON;
import com.carl.pojo.Proxy;
import com.carl.thread.SpiderThread;
import com.carl.thread.VerifyProxyThread;
import com.carl.utils.InitDataUtils;

public class MainShell {
	// 创建固定数量线程池,避免线程数量太多,服务器拒绝返回数据.
	private static ExecutorService pool = Executors.newFixedThreadPool(100);
	private static String unVerifyProxyPath = MainShell.class.getClassLoader().getResource("").getPath() + "ips.txt";
	private static String usefulProxyPath = MainShell.class.getClassLoader().getResource("").getPath() + "verifiedProxy.txt";

	public static void main(String[] args) throws IOException, InterruptedException {
		spiderMode();
		// verifyMode();

	}

	/**
	 * 功能: 代理验证模式
	 * 调用者: Main
	 * 参数: @throws IOException
	 * 修改者: Carl.Huang
	 * 日期: 2014年6月30日 下午9:57:14
	 * 待做: 异常处理
	 */
	public static void verifyMode() throws IOException {
		Set<Proxy> proxies = new HashSet<Proxy>();
		List<String> list = FileUtils.readLines(new File(unVerifyProxyPath));
		for (String s : list) {
			Proxy p = JSON.parseObject(s, Proxy.class);
			proxies.add(p);
			if (p.getType().toLowerCase().equals("http"))
				pool.execute(new VerifyProxyThread(p));
		}
		pool.shutdown();
	}

	/**
	 * 功能: 代理采集模式
	 * 调用者: Main
	 * 参数:
	 * 修改者: Carl.Huang
	 * 日期: 2014年6月30日 下午9:57:45
	 * 待做:
	 */
	public static void spiderMode() {
		int pageCount = InitDataUtils.getPageCount();
		List<Object> urls = InitDataUtils.getSearchUrl();
		for (int i = 0; i < urls.size(); i++) {
			for (int j = 1; j < pageCount; j++) {
				String url = urls.get(i) + "" + j;
				pool.execute(new SpiderThread(url));
			}
		}
		pool.shutdown();
	}

	/**
	 * 功能: 采集所得数据处理.
	 * 调用者: 采集线程回调
	 * 参数: @param data
	 * 修改者: Carl.Huang
	 * 日期: 2014年6月30日 下午9:53:04
	 * 待做:
	 */
	public synchronized static void spiderDataHandler(Set<Proxy> data) {
		try {
			StringBuffer sb = new StringBuffer();
			for (Proxy p : data) {
				sb.append(JSON.toJSON(p));
				sb.append("\r\n");
			}
			String ips = sb.toString();
			FileUtils.writeStringToFile(new File(unVerifyProxyPath), ips, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 功能: 验证线程验证代理有效,进行储存
	 * 调用者: 验证线程回调
	 * 参数: @param proxy 代理数据
	 * 修改者: Carl.Huang 日期:
	 * 2014年6月30日 下午9:51:51
	 * 待做:
	 */
	public synchronized static void verifyDataHandler(Proxy proxy) {
		try {
			FileUtils.writeStringToFile(new File(usefulProxyPath), JSON.toJSON(proxy) + "\r\n", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
