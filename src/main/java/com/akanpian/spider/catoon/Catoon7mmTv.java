package com.akanpian.spider.catoon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;

import com.akanpian.spider.JsoupSpider;

public class Catoon7mmTv extends JsoupSpider {

	@Override
	public File getBaseDir() {
		return new File("H:/backup/other/catoon/");
	}

	public static void main(String[] args) throws InterruptedException {
		Catoon7mmTv av = new Catoon7mmTv();
		ExecutorService exec = Executors.newFixedThreadPool(20);
		av.doSpider(exec);

		exec.shutdown();
		exec.awaitTermination(30, TimeUnit.MINUTES);
	}

	@Override
	protected Map<String, String> jsoupCookieLogin() {
		return null;
	}

	public static class OnePage implements Runnable {

		Catoon7mmTv tv;
		String url;
		String name;

		public OnePage(Catoon7mmTv tv, String url) {
			this.tv = tv;
			this.url = url;
		}

		public void run() {
			try {
				Document doc = tv.doc(new URL(url), 30000);
				name = fixDirname(doc.title().split("\\|")[0].trim());
				System.out.println(name + "->" + url);
				
				Map<String, String> imgs = getImgs(doc.html());
				File dir = new File(tv.getBaseDir().getPath(), name);

				if (!dir.exists()) {
					dir.mkdirs();
				}

				for (String k : imgs.keySet()) {
					if(!downurl(imgs.get(k), dir.getPath() + "/" + fixNumber(k, 6) + ".jpg")) {
					}
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			System.out.println("END->" + name + " URL=" + url);
		}

	}

	static Pattern p = Pattern.compile("Large_cgurl\\[(.+?)\\]\\s+=\\s+\"(.+?)\"");

	public static Map<String, String> getImgs(String ctx) {
		Matcher m = p.matcher(ctx);
		Map<String, String> rs = new HashMap<String, String>();
		int x = 0;
		while (true) {
			if (!m.find(x)) {
				break;
			}
			rs.put(m.group(1), m.group(2));
			x = m.end() + 1;
		}
		return rs;
	}

	@Override
	public void doSpider(ExecutorService exec) {
		File f = new File(getBaseDir(), "downloads.txt");
		try {
			if (f.exists()) {
				FileReader fr = new FileReader(f);
				BufferedReader br = new BufferedReader(fr);
				String line = null;
				while ((line = br.readLine()) != null) {
					if (line.trim().length() > 1) {
						exec.submit(new OnePage(this, line));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void endSpider() {

	}

}
