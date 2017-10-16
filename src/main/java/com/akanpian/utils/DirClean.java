package com.akanpian.utils;

import java.io.File;

public class DirClean {
public static void main(String[] args) {
	File dir = new File("H:/backup/other/spider/18av");

	File cd, ccd;
	for (String cds : dir.list()) {
		cd = new File(dir, cds);
		if (cd.isDirectory()) {

			for (String ccds : cd.list()) {
				ccd = new File(cd, ccds);
				if (ccd.isDirectory()) {
					try {
						Runtime.getRuntime().exec(new String[] { "cmd", "/c", "rd", "/S", "/Q", ccd.getPath() });
					} catch (Exception e) {
						System.out.println(e.getMessage() + " -> " + ccd.getPath());
					}
				}
			}
		}
	}
}
}
