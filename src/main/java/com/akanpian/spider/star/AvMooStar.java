package com.akanpian.spider.star;

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

public class AvMooStar extends JsoupSpider {

	public static void main(String[] args) throws InterruptedException {
		AvMooStar star = new AvMooStar();
		ExecutorService exec = Executors.newFixedThreadPool(8);
		star.doSpider(exec);
		exec.shutdown();
		exec.awaitTermination(30, TimeUnit.MINUTES);
	}

	@Override
	protected Map<String, String> jsoupCookieLogin() {
		return new HashMap<String, String>();
	}

	public class AvMoo implements Runnable {

		public URL url = null;
		public AvMooStar spider = null;

		public AvMoo(AvMooStar spider, URL url) {
			this.url = url;
			this.spider = spider;
		}

		public void run() {
			Document doc = doc(url, 10000);
			Elements es = doc.getElementsByClass("item");
			if (es.size() <= 0) {
				return;
			}

			Element a;
			String imgUrl, nvName;
			for (int y = 0; y < es.size(); y++) {
				a = es.get(y).child(0);
				imgUrl = a.child(0).child(0).attr("src");
				nvName = a.child(1).child(0).text();
				downurl(imgUrl, "G:/backup/" + nvName + ".jpg");
			}
		}

	}

	@Override
	public void doSpider(ExecutorService exec) {
		URL url;
		try {

			for (int x = 1; x < 90; x++) {
				url = new URL("https://avio.pw/cn/actresses/page/" + x);
				exec.submit(new AvMoo(this, url));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void endSpider() {

	}

}
