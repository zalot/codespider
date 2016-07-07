package com.akanpian.spider;

/**
 * List info
 * @author zhaoheng
 *
 */
public class MInfo {
	public String info;
	public String title;
	public String html;
	public String img;
	public String url;
	public String getUrl() {
		return "view/" + code + ".html";
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIframe() {
		return iframe;
	}

	public void setIframe(String iframe) {
		this.iframe = iframe;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String iframe;
	public int code;

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}
}
