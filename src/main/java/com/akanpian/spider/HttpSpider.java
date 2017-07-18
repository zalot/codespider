package com.akanpian.spider;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public abstract class HttpSpider {
	public static Map<String, String> PGS = new LinkedHashMap<String, String>();
	public static Executor exec = Executors.newFixedThreadPool(5);
	protected static CookieManager cookie = new CookieManager();
	static {
		CookieHandler.setDefault(cookie);
	}

	public List<HttpCookie> getCookies(URI uri) {
		CookieStore ckst = cookie.getCookieStore();
		return ckst.get(uri);
	}

	public List<HttpCookie> getCookies() {
		CookieStore cookieJar = cookie.getCookieStore();
		return cookieJar.getCookies();
	}

	public void addCookie(URI uri, HttpCookie ck) {
		cookie.getCookieStore().add(uri, ck);
	}

	abstract void login();

}
