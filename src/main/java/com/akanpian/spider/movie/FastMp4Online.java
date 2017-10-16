package com.akanpian.spider.movie;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.akanpian.spider.JsoupSpider;
import com.akanpian.spider.SAv;

public class FastMp4Online extends JsoupSpider {

	public static String[] HOSTS = { "www.46ec.com", "www.11ssff.com" };
	// public static String[] HOSTS = { "www.11ssff.com", "www.11ssff.com" };

	public static Random RND = new Random();
	public static String MP4HOST = "http://m4.26ts.com/";

	@Override
	public File getBaseDir() {
		return new File("H:/backup/other/movie/fast/");
	}

	public static SAv createAv(String name, String url, String simg, String img, String cate, String video, String fh,
			String src, String oname, String key) {
		SAv s = new SAv();
		s.src = src;
		s.name = name;
		s.url = url;
		s.simg = simg;
		s.img = img;
		s.cate = cate;
		s.video = video;
		s.fanhao = fh;
		s.keywords = key;
		return s;
	}

	public static class OnePage implements Runnable {

		FastMp4Online tv;
		URL u;
		String name;
		String host;

		public OnePage(FastMp4Online tv, String url) throws MalformedURLException {
			this.tv = tv;
			this.host = HOSTS[RND.nextInt(HOSTS.length)];
			this.u = new URL("http://" + host + url);
		}

		public void run() {
			System.out.println("======= START " + u);
			if (STATEMAP.get(u.toString()) == null) {
				Document doc = tv.doc(u, 30000);

				String cate = doc.getElementsByClass("pic_list").first().child(0).text();
				Elements es = doc.getElementsByClass("pic_list").first().child(3).getElementsByTag("li");

				Element a, img;
				String video = null, fh = null, oname = null, imgsrc = null, key = null;
				String mid = null;
				SAv av = null;
				int sc = 0;
				for (Element e : es) {
					try {
						// <img
						// src="http://new.26tptp.info/vod/4-skyhd-115-ryu-enami-sky-angel-blue-vol-112/1_1.gif"
						// alt="skyhd-115 亚洲口交沿日本 Ryu Enami 傲慢无礼">
						// http://m4.26ts.com/4-skyhd-115-ryu-enami-sky-angel-blue-vol-112.mp4
						a = e.child(0).child(0);
						img = a.child(0);
						imgsrc = img.attr("src");
						mid = imgsrc.substring(imgsrc.indexOf("vod/") + 4, imgsrc.length() - 8);
						video = MP4HOST + mid + ".mp4";
						oname = mid.substring(mid.indexOf("-") + 1, mid.length());
						fh = img.attr("alt").split(" ")[0];
						key = oname.replaceAll("-", " ");
						av = createAv(img.attr("alt"), host + a.attr("href"), imgsrc, null, cate, video, fh, host,
								oname, key);
						tv.saveAvSpider2DB(av);
						sc++;
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				tv.saveState(u, es.size() + "=>" + sc);
			}
			System.out.println("======= END " + u);
		}

	}

	protected void save(Element e) {

	}

	public static void main(String[] args) throws InterruptedException {
		FastMp4Online av = new FastMp4Online();
		ExecutorService exec = Executors.newFixedThreadPool(20);
		av.doSpider(exec);
		exec.shutdown();
		exec.awaitTermination(30, TimeUnit.MINUTES);
	}

	@Override
	protected Map<String, String> jsoupCookieLogin() {
		return null;
	}

	@Override
	public void doSpider(ExecutorService exec) {

		try {
			exec.submit(new OnePage(this, "/list/2.html"));

			for (int x = 2; x < 150; x++) {
				exec.submit(new OnePage(this, "/list/2_" + x + ".html"));
				// japan
			}

			exec.submit(new OnePage(this, "/list/10.html")); // siwa
			exec.submit(new OnePage(this, "/list/3.html")); // oumei
			for (int x = 2; x < 100; x++) {
				exec.submit(new OnePage(this, "/list/10_" + x + ".html"));
				exec.submit(new OnePage(this, "/list/3_" + x + ".html"));
			}

			exec.submit(new OnePage(this, "/list/8.html")); // toupai
			for (int x = 2; x < 30; x++) {
				exec.submit(new OnePage(this, "/list/8_" + x + ".html"));
				// toupai
			}

			exec.submit(new OnePage(this, "/list/4.html")); // catoon
			for (int x = 2; x < 10; x++) {
				exec.submit(new OnePage(this, "/list/4_" + x + ".html"));
				// catoon
			}
		} catch (MalformedURLException e) {
		}
	}

	@Override
	public void endSpider() {

	}

}
