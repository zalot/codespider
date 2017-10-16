package com.akanpian.spider.movie;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.akanpian.spider.JsoupSpider;

public class AsianStreetMeatSpider extends JsoupSpider {

	public static URL url = null;

	@Override
	protected Map<String, String> jsoupCookieLogin() {
		return new HashMap<String, String>();
	}

	public static class ASM implements Runnable {
		AsianStreetMeatSpider spider;
		String url;
		String path;

		public ASM(AsianStreetMeatSpider spider, String url, String path) {
			this.spider = spider;
			this.url = url;
			this.path = path;
		}

		public void run() {
			try {
				File f = new File(path);
				if (!f.exists()) {
					System.out.println("download " + url);
					spider.downurl(url, path);
				} else {
					System.out.println("exists " + url);
				}
			} catch (Exception e) {

			}
		}
	}

	@Override
	public void doSpider(ExecutorService exec) {
		trustEveryone();
		try {
			url = new URL("https://asianstreetmeat.com/");
		} catch (MalformedURLException e) {
		}
		doDoc(doc(url, 300000), exec);
	}

	protected void doDoc(Document doc, ExecutorService exec) {
		Elements els = doc.getElementsByTag("video");
		Element e;
		String videoUrl = null;
		String name = null;

		if (!getBaseDir().exists()) {
			getBaseDir().mkdirs();
		}

		File asmDir = new File(getBaseDir(), "asianstreetmeat");
		if (!asmDir.exists()) {
			asmDir.mkdirs();
		}
		for (int x = 0; x < els.size(); x++) {
			try {
				e = els.get(x);
				videoUrl = e.child(2).attr("href");
				if (videoUrl.indexOf("trailor.mp4") >= 0) {
					name = videoUrl.substring(videoUrl.indexOf("photogroup") + "photogroup".length(),
							videoUrl.indexOf("trailor") - 1);
					exec.submit(new ASM(this, videoUrl, asmDir.getPath() + "\\" + name + ".mp4"));
				} else {
					// https://cdn.streetmeatasia.com/diddy/diddy.mp4
					name = videoUrl.substring(videoUrl.lastIndexOf("/") + 1, videoUrl.length());
					exec.submit(new ASM(this, videoUrl, asmDir.getPath() + "\\" + name));
				}
			} catch (Exception ex) {
			}
		}
		System.out.println("over");
	}

	@Override
	public void endSpider() {

	}

	public static void main(String[] args) throws InterruptedException {
		AsianStreetMeatSpider av = new AsianStreetMeatSpider();
		ExecutorService exec = Executors.newFixedThreadPool(10);
		av.doSpider(exec);

		exec.shutdown();
		exec.awaitTermination(30, TimeUnit.MINUTES);
	}
}
