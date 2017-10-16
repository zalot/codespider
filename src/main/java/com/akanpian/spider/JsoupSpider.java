package com.akanpian.spider;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public abstract class JsoupSpider extends HttpAvSpider {

	Map<String, String> cookies = new HashMap<String, String>();
	boolean isLogin = false;

	public void login() {
		if (!isLogin) {
			cookies.putAll(this.jsoupCookieLogin());
		}
	}

	protected abstract Map<String, String> jsoupCookieLogin();

	public Document doc(URL url, int timeout) {
		int retry = 0;
		while (true) {
			if (retry > 3) {
				break;
			}
			try {
				return Jsoup.connect(url.toString()).userAgent(AGENT).cookies(cookies).timeout(timeout).get();
			} catch (Exception e) {
				retry++;
				System.out.println(url + " doc timeout retry " + retry);
			}
		}
		return null;
	}
}
