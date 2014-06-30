package com.carl.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.apache.commons.io.IOUtils;

import com.carl.main.MainShell;
import com.carl.pojo.Proxy;

public class VerifyProxyThread extends Thread {
	private Proxy proxy;
	private Socket socket;

	@Override
	public void run() {
		try {
			InetSocketAddress address = new InetSocketAddress(proxy.getIp(), proxy.getPort());
			socket = new Socket();
			socket.connect(address, 10 * 1000);
			socket.setSoTimeout(5*1000);
			OutputStream out = socket.getOutputStream();
			String remoteAddr = "www.baidu.com:80";
			String verifyProxy = "CONNECT " + remoteAddr + " HTTP/1.1\r\nHost: " + remoteAddr
					+ "\r\nAccept: */*\r\nContent-Type: text/html\r\nProxy-Connection: Keep-Alive\r\nContent-length: 0\r\n\r\n";
			IOUtils.write(verifyProxy, out);
			InputStream in = socket.getInputStream();
			BufferedReader bfr = new BufferedReader(new InputStreamReader(in,"UTF-8"));
			String line = null;
			while((line = bfr.readLine())!=null){
//				System.out.println(line);
				if(line.contains("Connection established")){
					System.out.println(">代理有效 "+proxy+" 返回:"+line);
					MainShell.verifyDataHandler(proxy);
					break;
				}
			}
		}catch(SocketTimeoutException e){
//			System.out.println(">代理连接超时 "+proxy);
		}
		catch (Exception e) {
//			System.out.println(">代理无效 "+e.getMessage()+"\t"+proxy);
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}//HTTP/1.0 200 Connection established\r\n\r\n

	public VerifyProxyThread(Proxy proxy) {
		super();
		this.proxy = proxy;
	}

}
