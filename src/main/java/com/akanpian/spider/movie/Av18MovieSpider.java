package com.akanpian.spider.movie;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Request;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.akanpian.jdbc.ConnectionTool;
import com.akanpian.spider.JsoupSpider;
import com.akanpian.spider.SMovie;

public class Av18MovieSpider extends JsoupSpider {

	public static Connection con = null;
	static {
		PGS.put("中文字幕",
				"http://vv2069.pw/serch_18v/%E4%B8%AD%E6%96%87%E5%AD%97%E5%B9%95%E5%85%A8%E9%83%A8%E5%88%97%E8%A1%A8_$P.htm");
		PGS.put("無修正全",
				"http://vv2069.pw/serch_18v/%E7%84%A1%E4%BF%AE%E6%AD%A3%E5%85%A8%E9%83%A8%E5%88%97%E8%A1%A8_$P.htm");
		PGS.put("短片",
				"http://vv2069.pw/serch_18v/%E7%9F%AD%E7%89%87%E5%8D%80%E5%85%A8%E9%83%A8%E5%88%97%E8%A1%A8_$P.htm");
		PGS.put("H動畫", "http://vv2069.pw/serch_18v/H%E5%8B%95%E7%95%AB%E5%85%A8%E9%83%A8%E5%88%97%E8%A1%A8_$P.htm");
	}

	public static SMovie create18av(String name, String url, String simg, String img, String cate) {
		SMovie s = new SMovie();
		s.src = "18av";
		s.type = "av";
		s.name = name;
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
			Document d;
			try {
				d = spider.pageDocument(u, 30000);
				if (d != null) {
					spider.doDocument(d, type);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("over[" + type + "] ->" + u);
		}
	}

	public void doPage() throws Exception {
		login();
		for (String type : Av18MovieSpider.PGS.keySet()) {
			URL u = new URL(PGS.get(type).replace("$P", String.valueOf(1)));
			Document d = pageDocument(u, 15000);
			String count = d.getElementsByClass("page_previous").get(0).child(0).text();
			doDocument(d, type);

			List<Av18Task> ts = new ArrayList<Av18Task>();

			for (int x = 2; x < Integer.parseInt(count); x++) {
				u = new URL(PGS.get(type).replace("$P", String.valueOf(x)));
				Av18Task task = new Av18Task(this, u, type);
				ts.add(task);
				exec.execute(task);
			}
		}
	}

	private int doDocument(Document d, String cate) {
		Elements es = d.getElementsByClass("aRF");
		Element a, img;

		int scount = 0;
		for (int x = 0; x < es.size(); x++) {
			a = es.get(x);
			img = a.child(0);
			if (!img.tagName().equals("img")) {
				break;
			}
			saveMovie(create18av(img.attr("alt"), a.attr("href"), img.attr("src"), img.attr("link"), cate));
			scount++;
		}
		return scount;
	}

	private synchronized void saveMovie(SMovie m) {
		if (con == null) {
			try {
				con = ConnectionTool.getEmdbDerbyConnection();
			} catch (Exception e) {
				e.printStackTrace();
				con = null;
			}
		}

		if (con != null) {
			try {
				PreparedStatement ps = con
						.prepareStatement("insert into smovie(name,img,simg,url,src,type,cate) values(?,?,?,?,?,?,?)");
				int i = 1;
				ps.setString(i++, m.name);
				ps.setString(i++, m.img);
				ps.setString(i++, m.simg);
				ps.setString(i++, m.url);
				ps.setString(i++, m.src);
				ps.setString(i++, m.type);
				ps.setString(i++, m.cate);
				ps.execute();
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Av18MovieSpider av = new Av18MovieSpider();
		av.doPage();
		System.out.println("over");
	}
}
