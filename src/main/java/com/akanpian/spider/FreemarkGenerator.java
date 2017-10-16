package com.akanpian.spider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

public class FreemarkGenerator {
	public static File INDIR = new File(new File(System.getProperty("user.dir")), "src/main/resources/");
	public static Configuration FREEMARK_CONFIG = new Configuration();
	static {
		try {
			FREEMARK_CONFIG.setDirectoryForTemplateLoading(INDIR);
		} catch (IOException e) {
			e.printStackTrace();
		}
		FREEMARK_CONFIG.setObjectWrapper(new DefaultObjectWrapper());
		FREEMARK_CONFIG.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
	}

	protected static void gen(Map data, String tempName, File outPage) throws Exception {
		Template template = FREEMARK_CONFIG.getTemplate(tempName);
		Writer out = new OutputStreamWriter(new FileOutputStream(outPage), "UTF-8");
		template.setEncoding("UTF-8");
		template.process(data, out);
	}
}
