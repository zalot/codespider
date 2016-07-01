package com.akanpian.spider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

public class Main {

	public static Context CTX = new Context();

	public static class Context {
		public Configuration freectx = new Configuration();
	}

	public static void main(String[] args) throws Exception {
		String realpath = System.getProperty("user.dir");
		File inDir = new File(new File(realpath), "src/main/resources/");
		File outDir = new File("D:/");
		operator(inDir, inDir);
	}

	private static void operator(File inDir, File outDir) throws Exception {
		// main index
		Configuration cfg = new Configuration();
		cfg.setDirectoryForTemplateLoading(inDir);
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
		Map<String, String> data = new HashMap<String, String>();
		Template template = cfg.getTemplate("index.ftl");
		Writer out = new OutputStreamWriter(new FileOutputStream(new File(outDir, "index.html")), "UTF-8");
		template.process(data, out);
	}
}
