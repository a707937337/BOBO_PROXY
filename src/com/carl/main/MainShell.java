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
	// �����̶������̳߳�,�����߳�����̫��,�������ܾ���������.
	private static ExecutorService pool = Executors.newFixedThreadPool(100);
	private static String unVerifyProxyPath = MainShell.class.getClassLoader().getResource("").getPath() + "ips.txt";
	private static String usefulProxyPath = MainShell.class.getClassLoader().getResource("").getPath() + "verifiedProxy.txt";

	public static void main(String[] args) throws IOException, InterruptedException {
		spiderMode();
		// verifyMode();

	}

	/**
	 * ����: ������֤ģʽ
	 * ������: Main
	 * ����: @throws IOException
	 * �޸���: Carl.Huang
	 * ����: 2014��6��30�� ����9:57:14
	 * ����: �쳣����
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
	 * ����: ����ɼ�ģʽ
	 * ������: Main
	 * ����:
	 * �޸���: Carl.Huang
	 * ����: 2014��6��30�� ����9:57:45
	 * ����:
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
	 * ����: �ɼ��������ݴ���.
	 * ������: �ɼ��̻߳ص�
	 * ����: @param data
	 * �޸���: Carl.Huang
	 * ����: 2014��6��30�� ����9:53:04
	 * ����:
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
	 * ����: ��֤�߳���֤������Ч,���д���
	 * ������: ��֤�̻߳ص�
	 * ����: @param proxy ��������
	 * �޸���: Carl.Huang ����:
	 * 2014��6��30�� ����9:51:51
	 * ����:
	 */
	public synchronized static void verifyDataHandler(Proxy proxy) {
		try {
			FileUtils.writeStringToFile(new File(usefulProxyPath), JSON.toJSON(proxy) + "\r\n", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
