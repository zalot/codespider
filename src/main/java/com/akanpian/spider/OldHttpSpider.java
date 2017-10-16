//package com.akanpian.spider;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStreamWriter;
//import java.io.Writer;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import freemarker.template.Configuration;
//import freemarker.template.DefaultObjectWrapper;
//import freemarker.template.Template;
//import freemarker.template.TemplateExceptionHandler;
//
//public abstract class OldHttpSpider {
//	public static File outDir = new File("D:/work/webmain");
//
//	public static void main(String[] args) throws Exception {
//		// genMovie();
//		Map data = genCatoonList();
//	}
////
////	private static Map genCatoonList() throws Exception {
////		Document doc;
////		Element ea;
////		SCatoon s;
////		Map<String, Object> data = new LinkedHashMap<String, Object>();
////		Map<String, Object> gd = new LinkedHashMap<String, Object>();
////		ArrayList<SCatoon> is = new ArrayList<SCatoon>();
////		for (int x = 1; x < 20; x++) {
////			String url = "http://www.xieeyn.com/shaonvmanhua/list_4_" + x + ".html";
////			doc = Jsoup.connect(url).get();
////			for (Element e : doc.getElementsByClass("piclist").get(0).getElementsByTag("li")) {
////				ea = e.getElementsByTag("a").get(0);
////				s = new SCatoon();
////				s.title = ea.attr("title");
////				s.spiderDetailURL = "http://www.xieeyn.com" + ea.attr("href");
////				s.imgURL = e.getElementsByTag("img").attr("src");
////				is.add(s);
////			}
////			data.put("灏戝コ婕敾绗�" + x + "椤�", is);
////			gd.put("list", data);
////			gd.put("pg1", "catoon_index_1.html");
////			gd.put("pg2", "catoon_index_" + (x + 1) + ".html");
////			gen(gd, "list.ftl", "catoon_index_" + x + ".html");
////			genDetail(is);
////		}
////		return data;
////	}
//
//	private static void genDetail(ArrayList<SCatoon> is) throws IOException {
//		for (SCatoon s : is) {
//			System.out.println(Jsoup.connect(s.spiderDetailURL).get());
//		}
//	}
//
//	private static void genCatoonDetail() {
//
//	}
//
//	private static void genMovie() throws Exception {
//		String url = "http://www.jinzidu.com/";
//		Document doc = Jsoup.connect(url).get();
//		Map<String, ArrayList<SCatoon>> data = getMovieData(doc.getElementById("content"));
//		genMovieDetail(data);
//		// gen(data, "list.ftl", "index.html");
//	}
//
//	private static void genMovieDetail(Map<String, ArrayList<SCatoon>> data) {
//		for (List<SCatoon> lifs : data.values()) {
//			for (SCatoon i : lifs) {
//				try {
//					File dfd = new File(outDir, "view");
//					if (!dfd.exists())
//						dfd.mkdirs();
//					File df = new File(dfd, i.code + ".html");
//					if (!df.exists()) {
//						Document doc = Jsoup.connect(i.innerHTML).get();
//						i.innerHTML = doc.getElementsByTag("iframe").toString();
//						Map<String, SCatoon> m = new HashMap<String, SCatoon>();
//						m.put("player", i);
//						gen(m, "detail.ftl", "view/" + i.code + ".html");
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//
//	private static void init() {
//
//	}
//
//	private static Map<String, ArrayList<SCatoon>> getMovieData(Element emain) {
//		Elements etypes = emain.getElementsByTag("h2");
//		Elements els = emain.getElementsByTag("ul");
//		Element einfo;
//		ArrayList<SCatoon> is;
//		SCatoon inf;
//		Map<String, ArrayList<SCatoon>> data = new LinkedHashMap<String, ArrayList<SCatoon>>();
//		for (int x = 0; x < etypes.size(); x++) {
//			is = new ArrayList<SCatoon>();
//			for (Element mv : els.get(x).getElementsByTag("li")) {
//				einfo = mv.getElementsByClass("pic").get(0);
//				inf = new SCatoon();
//				inf.title = einfo.attr("title").trim();
//				inf.imgURL = mv.getElementsByTag("img").get(0).attr("src").trim();
//				inf.innerHTML = einfo.attr("href").trim();
//				inf.typeInfo = mv.getElementsByClass("set").get(0).text();
//				inf.code = inf.title.hashCode();
//				is.add(inf);
//			}
//			data.put(etypes.get(x).text(), is);
//		}
//		return data;
//	}
//}
