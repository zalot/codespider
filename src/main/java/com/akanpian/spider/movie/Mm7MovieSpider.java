package com.akanpian.spider.movie;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jsoup.Connection;
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

public class Mm7MovieSpider extends JsoupSpider {
	public static Map<String, String> PGS = new LinkedHashMap<String, String>();
	public static int DIRSIZE = 500;
	static {
		PGS.put("中文AV", "https://7mm.tv/pagenum_chjmv_all_$P.html");
		PGS.put("有码AV", "https://7mm.tv/pagenum_jmv_all_$P.html");
		PGS.put("无码AV", "https://7mm.tv/pagenum_unjmv_all_$P.html");
	}

	public static SAv createAv(String name, String url, String simg, String img, String cate, String svideo,
			String fh) {
		SAv s = new SAv();
		s.src = "7mm";
		s.name = fixDirname(name);
		s.url = url;
		s.simg = simg;
		s.img = img;
		s.cate = cate;
		s.svideo = svideo;
		s.fanhao = fh;
		return s;
	}

	protected Map<String, String> jsoupCookieLogin() {
		try {
			Request r = Jsoup.connect("https://7mm.tv/").request();
			r.method(Method.POST);
			Connection con = Jsoup.connect("https://7mm.tv/");
			con.timeout(15000);
			con.header("Accept-Encoding", "gzip,deflate,sdch");
			con.header("Connection", "close");
			con.userAgent(AGENT);
			Response req = con.execute();
			return req.cookies();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static class Mm7 implements Runnable {
		Mm7MovieSpider spider;
		URL u;
		String type;

		public Mm7(Mm7MovieSpider spider, URL u, String type) {
			this.spider = spider;
			this.u = u;
			this.type = type;
		}

		public void run() {
			System.out.println("======= START [" + type + "] ->" + u);
			if (STATEMAP.get(u.toString()) == null) {
				int scount = 0;
				int all = 0;
				Document d = null;
				int rt = 0;
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
					all = d.getElementsByClass("topic_box").size();
					scount = spider.doDocument(d, type);
				}
				spider.saveState(u, all + "=>" + scount);
			}
			System.out.println("======= END [" + type + "] ->" + u);
		}
	}

	public void doSpider(ExecutorService exec) {
		// trustEveryone();
		// login();
		for (String type : PGS.keySet()) {
			URL u = null;
			try {
				u = new URL(PGS.get(type).replace("$P", String.valueOf(1)));
			} catch (Exception e) {
				e.printStackTrace();
			}
			Document d = doc(u, 15000);
			if (d == null)
				continue;
			String count = d.getElementsByClass("page_previous").get(1).child(0).text();
			// doDocument(d, type);

			List<Mm7> ts = new ArrayList<Mm7>();

			for (int x = 1; x < Integer.parseInt(count); x++) {
				try {
					u = new URL(PGS.get(type).replace("$P", String.valueOf(x)));
				} catch (Exception e1) {
					e1.printStackTrace();
					continue;
				}
				Mm7 task = new Mm7(this, u, type);
				ts.add(task);
				exec.submit(task);
			}

		}
	}

	private int doDocument(Document d, String cate) {
		Elements es = d.getElementsByClass("topic_box");
		Element e, vdo;
		String u, name, simg, img, fh, vid;
		int scount = 0;
		SAv av;
		for (int x = 0; x < es.size(); x++) {
			e = es.get(x);
			u = "https://7mm.tv/" + e.child(0).attr("href");
			vdo = e.child(0).child(0).child(0);
			name = e.child(1).child(0).text();
			simg = vdo.attr("poster");
			vid = vdo.attr("srcmv");
			if (!vdo.tagName().equals("video")) {
				break;
			}
			img = vdo.attr("onmouseover");
			img = img.substring(11, img.indexOf(",") - 1);
			fh = img.substring(img.lastIndexOf("/") + 1, img.lastIndexOf("."));
			av = createAv(name, u, simg, img, cate, vid, fh);
			if (do7mmAv(av)) {
				scount++;
			}

		}
		return scount;
	}

	private boolean do7mmAv(SAv av) {
		boolean rt = true;
		try {
			SAv sav = null;
			try {
				sav = DBUtils.getSAvByPK(av);
				if (sav != null) {
					if (sav.keywords == null) {
						if(getDetail(sav)) {
							rt = rt && DBUtils.updateSAv(sav);
						}
					}
				}else {
					getDetail(av);
					saveAvSpider2DB(av);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			av._downSImg = true;
			av._downSVideo = true;
			
			//rt = rt && downloadAvSpider(av, DIRSIZE);
			return rt;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean getDetail(SAv av) throws MalformedURLException {
		try {
			String detailUrls = "", riqi, faxing, daoyan, zhizuo, keys = "", nvyou;
			Document doc = doc(new URL(av.url), 300000);
			Elements as = doc.getElementsByClass("mvinfo_dmm_A");
			Elements btns = doc.getElementsByClass("css_btn_class");
			riqi = as.get(1).text();
			daoyan = as.get(3).child(1).text();
			zhizuo = as.get(4).child(1).text();
			faxing = as.get(5).child(1).text();
			Elements bs = doc.getElementsByClass("mvinfo_dmm_B");
			nvyou = bs.get(0).child(1).text();
			Elements ks = bs.get(1).getElementsByTag("a");

			for (int x = 0; x < ks.size(); x++) {
				keys += " " + ks.get(x).text();
			}

			av.daoyan = daoyan;
			av.nvyouname = nvyou;
			av.keywords = keys;
			av.faxing = faxing;
			av.zhizuo = zhizuo;
			av.riqi = riqi;

			for (int x = 0; x < btns.size(); x++) {
				detailUrls += " " + btns.get(x).text();
			}

			av.detalURLs = detailUrls;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static void main(String[] args) throws Exception {
		Mm7MovieSpider av = new Mm7MovieSpider();
		ExecutorService exec = Executors.newFixedThreadPool(8);
		av.doSpider(exec);
		exec.shutdown();
		exec.awaitTermination(30, TimeUnit.MINUTES);
	}

	@Override
	public void endSpider() {

	}
}
