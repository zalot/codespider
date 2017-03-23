package com.akanpian.spider;

/**
 * Spider Info
 * 
 * @author zhaoheng
 *
 */
public class SInfo {
	public String typeInfo = "";
	public String title = "";
	public String imgURL = "";

	public String getTypeInfo() {
		return typeInfo;
	}

	public void setTypeInfo(String typeInfo) {
		this.typeInfo = typeInfo;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImgURL() {
		return imgURL;
	}

	public void setImgURL(String imgURL) {
		this.imgURL = imgURL;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getInnerHTML() {
		return innerHTML;
	}

	public void setInnerHTML(String innerHTML) {
		this.innerHTML = innerHTML;
	}

	public String getSpiderDetailURL() {
		return spiderDetailURL;
	}

	public void setSpiderDetailURL(String spiderDetailURL) {
		this.spiderDetailURL = spiderDetailURL;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String url = "";
	public String innerHTML = "";

	public String spiderDetailURL = "";
	public int code;

	@Override
	public String toString() {
		return "SInfo [typeInfo=" + typeInfo + ", title=" + title + ", img=" + imgURL + ", detailURL=" + spiderDetailURL
				+ ", innerHTML=" + innerHTML + ", code=" + code + "]";
	}
}
