package com.carl.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import com.carl.main.MainShell;

public class VerifyProxyThread extends Thread {
	private com.carl.pojo.Proxy proxy;
	private Socket socket;

	@Override
	public void run() {
		if ("http".equals(proxy.getType().toLowerCase())) {
//			 verifyHttp();
		} else if (proxy.getType().toLowerCase().contains("sock")) {
			verifySocket();
		}else if (proxy.getType().toLowerCase().contains("qq")) {
			verifySocket();
		}
	}

	public VerifyProxyThread(com.carl.pojo.Proxy proxy) {
		super();
		this.proxy = proxy;
	}

	private void verifyHttp() {
		try {
			InetSocketAddress address = new InetSocketAddress(proxy.getIp(), proxy.getPort());
			socket = new Socket();
			socket.connect(address, 10 * 1000);
			socket.setSoTimeout(5 * 1000);
			OutputStream out = socket.getOutputStream();
			String remoteAddr = "www.baidu.com:80";
			String verifyProxy = "CONNECT " + remoteAddr + " HTTP/1.1\r\nHost: " + remoteAddr
					+ "\r\nAccept: */*\r\nContent-Type: text/html\r\nProxy-Connection: Keep-Alive\r\nContent-length: 0\r\n\r\n";
			IOUtils.write(verifyProxy, out);
			InputStream in = socket.getInputStream();
			BufferedReader bfr = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String line = null;
			while ((line = bfr.readLine()) != null) {
				// System.out.println(line);
				if (line.contains("Connection established")) {
					System.out.println(">代理有效 " + proxy + " 返回:" + line);
					MainShell.verifyDataHandler(proxy);
					break;
				}
			}
		} catch (SocketTimeoutException e) {
			 System.out.println(">代理连接超时 "+proxy);
		} catch (Exception e) {
			 System.out.println(">代理无效 "+e.getMessage()+"\t"+proxy);
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void verifySocket() {
		HttpURLConnection conn = null;
		try {
		/*Socket socket =null;
			socket = new Socket("www.qq.com", 80);
	        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        StringBuffer sb = new StringBuffer();
	        sb.append("GET / HTTP/1.1\r\n")
	           .append("Host:www.baidu.com\r\n")
	           .append("\r\n");
	        out.write(sb.toString());
	        out.flush();
	        String line = null;
	        sb = new StringBuffer();
	        while((line = in.readLine()) != null){
	        	if(line.contains("baidu")){
//	        		System.out.println(">代理有效 \t"+this.proxy);
	        		System.out.println(line);
	        		return;
	        	}
	        }*/
		
				URL url = new URL("http://www.baidu.com");
				// 创建代理服务器
				InetSocketAddress addr = new InetSocketAddress(this.proxy.getIp(), this.proxy.getPort());
				 Proxy proxy = new Proxy(Proxy.Type.SOCKS, addr); // Socket 代理
//				Proxy proxy = new Proxy(Proxy.Type.HTTP, addr); // http 代理
				conn = (HttpURLConnection) url.openConnection(proxy);
				InputStream in = conn.getInputStream();
				String s = IOUtils.toString(in);
//				System.out.println(s);
				if(s.contains("baidu")){
					System.out.println(">代理有效 \t"+this.proxy);
					MainShell.verifyDataHandler(this.proxy);
				}
		} catch (Exception e) {
//			 System.out.println(">代理无效 "+e.getMessage()+"\t"+this.proxy);
		}finally {
//			try {
//				socket.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			if(conn!=null)
				conn.disconnect();
		}
	}
}
