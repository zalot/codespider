package com.akanpian.spider.movie;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.akanpian.utils.DBUtils;

public class Av18MovieSpider extends JsoupSpider {
	public static Map<String, String> PGS = new LinkedHashMap<String, String>();
	public static String HOST = "http://vv2069.pw";
	public static int DIRSIZE = 100;

	static {
		PGS.put("JZ_中文字幕",
				HOST + "/serch_18v/%E4%B8%AD%E6%96%87%E5%AD%97%E5%B9%95%E5%85%A8%E9%83%A8%E5%88%97%E8%A1%A8_$P.htm");
		PGS.put("JZ_無修正全", HOST + "/serch_18v/%E7%84%A1%E4%BF%AE%E6%AD%A3%E5%85%A8%E9%83%A8%E5%88%97%E8%A1%A8_$P.htm");
		PGS.put("JZ_短片", HOST + "/serch_18v/%E7%9F%AD%E7%89%87%E5%8D%80%E5%85%A8%E9%83%A8%E5%88%97%E8%A1%A8_$P.htm");
		PGS.put("JZ_H動畫", HOST + "/serch_18v/H%E5%8B%95%E7%95%AB%E5%85%A8%E9%83%A8%E5%88%97%E8%A1%A8_$P.htm");

		PGS.put("DT_無修正全部列表",
				HOST + "/serch_dt/DT_%E7%84%A1%E4%BF%AE%E6%AD%A3%E5%85%A8%E9%83%A8%E5%88%97%E8%A1%A8_$P.htm");
		PGS.put("DT_中文字幕全部列表",
				HOST + "/serch_dt/DT_%E4%B8%AD%E6%96%87%E5%AD%97%E5%B9%95%E5%85%A8%E9%83%A8%E5%88%97%E8%A1%A8_$P.htm");
		PGS.put("DT_國產自拍全部列表",
				HOST + "/serch_dt/DT_%E5%9C%8B%E7%94%A2%E8%87%AA%E6%8B%8D%E5%85%A8%E9%83%A8%E5%88%97%E8%A1%A8_$P.htm");

		PGS.put("DF_全部列表", HOST + "/serch_18av/DF_%E5%85%A8%E9%83%A8%E5%88%97%E8%A1%A8_$P.htm");
	}

	public static SAv create18av(String name, String url, String simg, String img, String cate) {
		SAv s = new SAv();
		s.src = "18av";
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

	public static class Av18Task implements Runnable {
		Av18MovieSpider spider;
		URL u;
		String type;

		public Av18Task(Av18MovieSpider spider, URL u, String type) {
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
						d = spider.doc(u, 8000);
						break;
					} catch (Exception e) {
						rt++;
					}
					if (rt > 3) {
						break;
					}
				}
				if (d != null) {
					all = d.getElementsByClass("aRF").size();
					scount = spider.doListpage(d, type);
				}
				spider.saveState(u, all + "=>" + scount);
			}
			System.out.println("======= END [" + type + "] ->" + u);
		}
	}

	public void doSpider(ExecutorService exec) {
		login();
		for (String type : PGS.keySet()) {
			try {
				URL u = new URL(PGS.get(type).replace("$P", String.valueOf(1)));
				Document d = doc(u, 15000);
				if (d == null)
					continue;
				String count = d.getElementsByClass("page_previous").get(0).child(0).text();
				// doListpage(d, type);

				List<Av18Task> ts = new ArrayList<Av18Task>();

				for (int x = 1; x < Integer.parseInt(count); x++) {
					u = new URL(PGS.get(type).replace("$P", String.valueOf(x)));
					Av18Task task = new Av18Task(this, u, type);
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
		Elements es = d.getElementsByClass("aRF");
		Element a, img;

		int scount = 0;
		for (int x = 0; x < es.size(); x++) {
			a = es.get(x);
			img = a.child(0);
			if (!img.tagName().equals("img")) {
				break;
			}

			SAv m = create18av(img.attr("alt"), a.attr("href"), img.attr("src"), img.attr("link"), cate);
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

	static StringBuffer sb = new StringBuffer();
	static Map<String, SAv> dupcheck = new HashMap<String, SAv>();

	private synchronized void saveCSV(SAv m) {
		if (dupcheck.get(m.name) == null) {
			// sb.append(m.toCSV()).append("\r\n");
			dupcheck.put(m.name, m);
		}
	}

	@Override
	public void endSpider() {
		if (sb.length() > 10) {
			try {
				File of = new File("e:\\smovie.data");
				if (of.exists()) {
					of.delete();
				}
				of.createNewFile();
				FileWriter fw = new FileWriter(of);
				fw.write(sb.toString());
				fw.flush();
				fw.close();
			} catch (Exception e) {

			}
		}
	}

	public static void _spider() throws Exception {
		Av18MovieSpider av = new Av18MovieSpider();
		ExecutorService exec = Executors.newFixedThreadPool(30);
		av.doSpider(exec);
		exec.shutdown();
		exec.awaitTermination(30, TimeUnit.MINUTES);
	}

	public static void _fixlocalimg() throws Exception {
		SAv ss = new SAv();
		SAv up = new SAv();
		ss.src = "18av";
		
		File f = null;
		String p = null;
		for(SAv s : DBUtils.getSAv(ss)) {
			p = getDownloadPath(s, ".jpg", 100);
			f = new File(BDIR,p);
			if(f.exists()) {
				up.name = s.name;
				up.src = s.src;
				up.img_local = p;
				DBUtils.updateSAv(up);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		_fixlocalimg();
	}
}
