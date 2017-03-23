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

public class Main {
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
		genIndex();
	}

	private static void genIndex() throws Exception {
		String url = "http://www.jinzidu.com/";
		Document doc = Jsoup.connect(url).get();
		Map<String, ArrayList<MInfo>> data = main(doc.getElementById("content"));
		genDetail(data);
		gen(data, "list.ftl", "index.html");
	}

	private static void genDetail(Map<String, ArrayList<MInfo>> data) {
		for (List<MInfo> lifs : data.values()) {
			for (MInfo i : lifs) {
				try {
					File dfd = new File(outDir, "view");
					if (!dfd.exists())
						dfd.mkdirs();
					File df = new File(dfd, i.code + ".html");
					if (!df.exists()) {
						Document doc = Jsoup.connect(i.html).get();
						i.iframe = doc.getElementsByTag("iframe").toString();
						Map<String, MInfo> m = new HashMap<String, MInfo>();
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

	private static Map<String, ArrayList<MInfo>> main(Element emain) {
		Elements etypes = emain.getElementsByTag("h2");
		Elements els = emain.getElementsByTag("ul");
		Element einfo;
		ArrayList<MInfo> is;
		MInfo inf;
		Map<String, ArrayList<MInfo>> data = new LinkedHashMap<String, ArrayList<MInfo>>();
		for (int x = 0; x < etypes.size(); x++) {
			is = new ArrayList<MInfo>();
			for (Element mv : els.get(x).getElementsByTag("li")) {
				einfo = mv.getElementsByClass("pic").get(0);
				inf = new MInfo();
				inf.title = einfo.attr("title").trim();
				inf.img = mv.getElementsByTag("img").get(0).attr("src").trim();
				inf.html = einfo.attr("href").trim();
				inf.info = mv.getElementsByClass("set").get(0).text();
				inf.code = inf.title.hashCode();
				is.add(inf);
			}
			data.put(etypes.get(x).text(), is);
		}
		return data;
	}

	private static void gen(Map data, String tp, String page) throws Exception {
		Template template = cfg.getTemplate(tp);
		Writer out = new OutputStreamWriter(new FileOutputStream(new File(outDir, page)), "UTF-8");
		HashMap<String, Map> d = new HashMap<String, Map>();
		d.put("data", data);
		template.process(d, out);
	}
}
