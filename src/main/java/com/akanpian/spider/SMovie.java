package com.akanpian.spider;

public class SMovie {
	public String name;
	public String img;
	public String simg;
	
	public String url;
	public String type;
	public String cate;
	public String src;
	
	@Override
	public String toString() {
		return "SMovie [name=" + name + ", img=" + img + ", simg=" + simg + ", detailUrl=" + url + ", type="
				+ type + ", cate=" + cate + ", src=" + src + "]";
	}
}
