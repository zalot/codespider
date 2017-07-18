package com.akanpian.spider;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public abstract class JsoupSpider extends HttpSpider {

	Map<String, String> cookies = new HashMap<String, String>();
	boolean isLogin = false;

	public void login() {
		if (!isLogin) {
			cookies.putAll(this.jsoupCookieLogin());
		}
	}

	protected abstract Map<String, String> jsoupCookieLogin();

	public Document pageDocument(URL url, int timeout) throws IOException {
		return Jsoup.connect(url.toString()).cookies(cookies).timeout(timeout).get();
	}
}
