package com.akanpian.spider;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import com.akanpian.utils.DBUtils;

public abstract class HttpAvSpider {
	public static String AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.221 Safari/537.36 SE 2.X MetaSr 1.0";
	public static Connection CON = null;
	public static File BDIR = new File("H:/backup/other/spider/");
	public static File STATEFILE = new File(BDIR, "status.txt");
	public static Map<String, String> STATEMAP = new HashMap<String, String>();
	public static FileWriter SFW;
	public static BufferedWriter SFBW;
	public static FileReader SFR;
	public static BufferedReader SFBR;

	public File getBaseDir() {
		return BDIR;
	}

	public abstract void doSpider(ExecutorService exec);

	public abstract void endSpider();

	protected static void initState() {
		try {
			if (!STATEFILE.exists())
				return;
			if (SFW == null) {
				SFR = new FileReader(STATEFILE);
				SFBR = new BufferedReader(SFR);
			}
			String line = null;
			String[] is = null;
			while ((line = SFBR.readLine()) != null) {
				is = line.trim().split(",");
				STATEMAP.put(is[0], is[1]);
			}
			System.out.println("init state ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void saveState(URL u, String info) {
		try {
			if (!STATEFILE.exists())
				STATEFILE.createNewFile();
			if (SFW == null) {
				SFW = new FileWriter(STATEFILE, true);
				SFBW = new BufferedWriter(SFW);
			}
			SFBW.append(u.toString() + "," + info + System.lineSeparator());
			SFBW.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected abstract void login();

	protected static CookieManager cookie = new CookieManager();
	static {
		CookieHandler.setDefault(cookie);
		initState();
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

	protected static boolean downurl(String url, String pathName) {
		File f = new File(pathName);
		if (f.exists()) {
			return false;
		}
		HttpURLConnection conn = null;
		BufferedInputStream bi = null;
		FileOutputStream bs = null;
		try {
			URL ul = new URL(url);
			conn = (HttpURLConnection) ul.openConnection();
			conn.setConnectTimeout(15000);// 设置连接超时时间
			conn.setReadTimeout(15000);// 设置访问超时时间
			conn.addRequestProperty("User-Agent", AGENT);
			bi = new BufferedInputStream(conn.getInputStream());
			bs = new FileOutputStream(pathName);
			byte[] by = new byte[1024];
			int len = 0;
			while ((len = bi.read(by)) != -1) {
				bs.write(by, 0, len);
			}
			bs.close();
			bi.close();
			return true;
		} catch (Exception e) {
			System.out.println("error download - > " + e.getMessage());
			return false;
		} finally {
			if (bs != null) {
				try {
					bs.close();
				} catch (IOException e) {
				}
			}

			if (bi != null) {
				try {
					bi.close();
				} catch (IOException e) {
				}
			}
			bs = null;
			bi = null;
		}
	}

	private String download(SAv av, String url, String suffix, int size) {
		String tPath = getDownloadPath(av, suffix, size);
		File f = new File(getBaseDir(), tPath);
		if (f.exists())
			return tPath;
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}
		if (downurl(url, f.getPath())) {
			return tPath;
		}
		return null;
	}

	protected boolean downloadAvSpider(SAv m, int dirSize) {
		synchronized (getBaseDir()) {
			if (!getBaseDir().exists()) {
				getBaseDir().mkdirs();
			}
		}

		boolean su = true;
		if (m._downSImg) {
			m.simg_local = download(m, m.simg, "_s.jpg", dirSize);
			if (m.simg_local == null) {
				su = false;
			}
		}
		if (m._downImg) {
			m.img_local = download(m, m.img, ".jpg", dirSize);
			if (m.img_local == null) {
				su = false;
			}
		}
		if (m._downSVideo) {
			m.svideo_local = download(m, m.svideo, ".mp4", dirSize);
			if (m.svideo_local == null) {
				su = false;
			}
		}
		return su;
	}

	public static String getDownloadPath(SAv m, String suffix, int dirSize) {
		String bp = null;
		if (m.name != null && m.src != null) {
			int b1 = Math.abs(m.name.hashCode()) % dirSize;
			bp = m.src + "/" + b1 + "/" + m.name + suffix;
		}
		return bp;
	}

	protected synchronized void saveAvSpider2DB(SAv m) throws Exception {
		DBUtils.insert("avspider", m);
	}

	public static void trustEveryone() {
		try {
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});

			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, new X509TrustManager[] { new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}
			} }, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String fixNumber(String i, int size) {
		return String.format("%0" + size + "d", Integer.parseInt(i));
	}

	public static String fixDirname(String name) {
		return name.replaceAll("/", "-");
	}
}
