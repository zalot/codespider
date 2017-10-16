package com.akanpian.spider.movie.more;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Request;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.akanpian.spider.JsoupSpider;
import com.akanpian.spider.SAv;

/**
 * www.bs345.com
 *
 */
public class TS_Bs345 extends JsoupSpider{
	public static Map<String, String> PGS = new LinkedHashMap<String, String>();
	public static String HOST = "http://www.bs345.com";
	public static int DIRSIZE = 100;

	static {
		PGS.put("亚洲无码", HOST + "/?m=vod-type-id-28-pg-$P.html");
		PGS.put("亚洲有码", HOST + "/?m=vod-type-id-29-pg-$P.html");
		PGS.put("动漫在线", HOST + "/?m=vod-type-id-31-pg-$P.html");
		PGS.put("人妻熟女", HOST + "/?m=vod-type-id-24-pg-$P.html");
		PGS.put("乱伦强奸", HOST + "/?m=vod-type-id-17-pg-$P.html");
		PGS.put("中文字幕", HOST + "/?m=vod-type-id-22-pg-$P.html");
		PGS.put("制服诱惑", HOST + "/?m=vod-type-id-30-pg-$P.html");
		PGS.put("亚洲情色", HOST + "/?m=vod-type-id-19-pg-$P.html");
	}

	public static SAv create(String name, String url, String simg, String img, String cate) {
		SAv s = new SAv();
		s.src = "ts_www.bs345.com";
		s.name = fixDirname(name);
		s.url = url;
		s.simg = simg;
		s.img = img;
		s.cate = cate;
		return s;
	}

	protected Map<String, String> jsoupCookieLogin() {
		try {
			Request r = Jsoup.connect("http://vv2069.pw/wtydfAA.htm").request();
			r.method(Method.POST);
			Response req = Jsoup.connect("http://vv2069.pw/wtydfAA.htm").timeout(30000).execute();
			return req.cookies();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static class TsTask implements Runnable {
		TS_Bs345 spider;
		URL u;
		String type;

		public TsTask(TS_Bs345 spider, URL u, String type) {
			this.spider = spider;
			this.u = u;
			this.type = type;
		}

		public void run() {
			System.out.println("======= START [" + type + "] ->" + u);
			if (STATEMAP.get(u.toString()) == null) {
				Document d = null;
				int all = 0;
				int rt = 0;
				int scount = 0;
				while (true) {
					try {
						d = spider.doc(u, 15000);
						break;
					} catch (Exception e) {
						rt++;
					}
					if (rt > 3) {
						break;
					}
				}
				if (d != null) {
					all = d.getElementsByClass("movie_list").first().child(0).children().size();
					scount = spider.doListpage(d, type);
				}
				spider.saveState(u, all + "=>" + scount);
			}
			System.out.println("======= END [" + type + "] ->" + u);
		}
	}

	public void doSpider(ExecutorService exec) {
		for (String type : PGS.keySet()) {
			try {
				URL u = new URL(PGS.get(type).replace("$P", String.valueOf(1)));
				Document d = doc(u, 15000);
				if (d == null)
					continue;
				String ct = d.getElementsByClass("pagebtn").attr("onclick").split(",")[1];
				ct = ct.substring(0, ct.length()-1);
				int ict = Integer.parseInt(ct);
				// doListpage(d, type);

				List<TsTask> ts = new ArrayList<TsTask>();

				for (int x = 1; x <= ict; x++) {
					u = new URL(PGS.get(type).replace("$P", String.valueOf(x)));
					TsTask task = new TsTask(this, u, type);
					ts.add(task);
					exec.submit(task);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		endSpider();
	}

	private int doListpage(Document d, String cate) {
		Elements es = d.getElementsByClass("movie_list").first().child(0).children();
		Element a, img;

		int scount = 0;
		for (int x = 0; x < es.size(); x++) {
			a = es.get(x);
			img = a.child(0);
			if (!img.tagName().equals("img")) {
				break;
			}

			SAv m = create(a.child(1).text(), a.attr("href"), img.attr("src"), img.attr("link"), cate);
			if (do18Av(m)) {
				scount++;
			}
		}
		return scount;
	}

	private boolean do18Av(SAv m) {
		try {
			m._downImg = true;
			downloadAvSpider(m, DIRSIZE);
			saveAvSpider2DB(m);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void endSpider() {
	}

	public static void main(String[] args) throws InterruptedException {
		TS_Bs345 av = new TS_Bs345();
		ExecutorService exec = Executors.newFixedThreadPool(1);
		av.doSpider(exec);

		exec.shutdown();
		exec.awaitTermination(30, TimeUnit.MINUTES);
	}
}
