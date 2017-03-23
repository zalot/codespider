package com.akanpian.spider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

public abstract class BaseSpider {
	public static File inDir = new File(new File(System.getProperty("user.dir")), "src/main/resources/");
	public static File outDir = new File("D:/work/webmain");
	public static Configuration cfg = new Configuration();
	static {
		try {
			cfg.setDirectoryForTemplateLoading(inDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
	}

	public static void main(String[] args) throws Exception {
		// genMovie();
		Map data = genCatoonList();
	}

	private static Map genCatoonList() throws Exception {
		Document doc;
		Element ea;
		SInfo s;
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		Map<String, Object> gd = new LinkedHashMap<String, Object>();
		ArrayList<SInfo> is = new ArrayList<SInfo>();
		;
		for (int x = 1; x < 20; x++) {
			String url = "http://www.xieeyn.com/shaonvmanhua/list_4_" + x + ".html";
			doc = Jsoup.connect(url).get();
			for (Element e : doc.getElementsByClass("piclist").get(0).getElementsByTag("li")) {
				ea = e.getElementsByTag("a").get(0);
				s = new SInfo();
				s.title = ea.attr("title");
				s.spiderDetailURL = "http://www.xieeyn.com" + ea.attr("href");
				s.imgURL = e.getElementsByTag("img").attr("src");
				is.add(s);
			}
			data.put("少女漫画第" + x + "页", is);
			gd.put("list", data);
			gd.put("pg1", "catoon_index_1.html");
			gd.put("pg2", "catoon_index_" + (x + 1) + ".html");
			gen(gd, "list.ftl", "catoon_index_" + x + ".html");
			genDetail(is);
		}
		return data;
	}

	private static void genDetail(ArrayList<SInfo> is) throws IOException {
		for (SInfo s : is) {
			System.out.println(Jsoup.connect(s.spiderDetailURL).get());
		}
	}

	private static void genCatoonDetail() {

	}

	private static void genMovie() throws Exception {
		String url = "http://www.jinzidu.com/";
		Document doc = Jsoup.connect(url).get();
		Map<String, ArrayList<SInfo>> data = getMovieData(doc.getElementById("content"));
		genMovieDetail(data);
		// gen(data, "list.ftl", "index.html");
	}

	private static void genMovieDetail(Map<String, ArrayList<SInfo>> data) {
		for (List<SInfo> lifs : data.values()) {
			for (SInfo i : lifs) {
				try {
					File dfd = new File(outDir, "view");
					if (!dfd.exists())
						dfd.mkdirs();
					File df = new File(dfd, i.code + ".html");
					if (!df.exists()) {
						Document doc = Jsoup.connect(i.innerHTML).get();
						i.innerHTML = doc.getElementsByTag("iframe").toString();
						Map<String, SInfo> m = new HashMap<String, SInfo>();
						m.put("player", i);
						gen(m, "detail.ftl", "view/" + i.code + ".html");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void init() {

	}

	private static Map<String, ArrayList<SInfo>> getMovieData(Element emain) {
		Elements etypes = emain.getElementsByTag("h2");
		Elements els = emain.getElementsByTag("ul");
		Element einfo;
		ArrayList<SInfo> is;
		SInfo inf;
		Map<String, ArrayList<SInfo>> data = new LinkedHashMap<String, ArrayList<SInfo>>();
		for (int x = 0; x < etypes.size(); x++) {
			is = new ArrayList<SInfo>();
			for (Element mv : els.get(x).getElementsByTag("li")) {
				einfo = mv.getElementsByClass("pic").get(0);
				inf = new SInfo();
				inf.title = einfo.attr("title").trim();
				inf.imgURL = mv.getElementsByTag("img").get(0).attr("src").trim();
				inf.innerHTML = einfo.attr("href").trim();
				inf.typeInfo = mv.getElementsByClass("set").get(0).text();
				inf.code = inf.title.hashCode();
				is.add(inf);
			}
			data.put(etypes.get(x).text(), is);
		}
		return data;
	}

	protected static void gen(Map data, String tp, String page) throws Exception {
		Template template = cfg.getTemplate(tp);
		Writer out = new OutputStreamWriter(new FileOutputStream(new File(outDir, page)), "UTF-8");
		HashMap<String, Map> d = new HashMap<String, Map>();
		d.put("data", data);
		template.process(d, out);
	}
}
