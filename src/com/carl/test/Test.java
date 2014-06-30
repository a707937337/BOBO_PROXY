package com.carl.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;

public class Test {
	public static void main(String[] args) throws IOException {
		// Socket socket = new Socket("www.baidu.com", 80);
		// BufferedWriter out = new BufferedWriter(new
		// OutputStreamWriter(socket.getOutputStream()));
		// BufferedReader in = new BufferedReader(new
		// InputStreamReader(socket.getInputStream()));
		// StringBuffer sb = new StringBuffer();
		// sb.append("GET / HTTP/1.1\r\n")
		// .append("Host:www.baidu.com\r\n")
		// .append("\r\n");
		// out.write(sb.toString());
		// out.flush();
		// String line = "";
		// while((line = in.readLine()) != null)
		// System.out.println(line);

		try {
			URL url = new URL("http://www.baidu.com");
			// 创建代理服务器
			InetSocketAddress addr = new InetSocketAddress("219.147.172.2", 12345);
			 Proxy proxy = new Proxy(Proxy.Type.SOCKS, addr); // Socket 代理
//			Proxy proxy = new Proxy(Proxy.Type.HTTP, addr); // http 代理
			// 如果我们知道代理server的名字, 可以直接使用
			// 结束
			 HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy);
			InputStream in = conn.getInputStream();
			// InputStream in = url.openStream();
			String s = IOUtils.toString(in);
			System.out.println(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
