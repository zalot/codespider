package com.akanpian.utils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.derby.shared.common.error.DerbySQLIntegrityConstraintViolationException;

import com.akanpian.spider.SAv;

public class DBUtils {

	public static Map<String, Field[]> COLUMNS = new HashMap<String, Field[]>();

	public static boolean insert(String table, Object obj) throws Exception {

		return insert(ConnectionUtils.getEmbeddedDerby(), table, obj);
	}

	public static String table = "avspider";

	public static SAv getSAvByPK(SAv obj) throws Exception {
		SAv s = new SAv();
		s.name = obj.name;
		s.src = obj.src;
		List l = getSAv(ConnectionUtils.getEmbeddedDerby(), s);
		if (l.size() > 0)
			return (SAv) l.get(0);
		return null;
	}

	public static List<SAv> getSAv(Object obj) throws Exception {
		return getSAv(ConnectionUtils.getEmbeddedDerby(), obj);
	}

	public static boolean updateSAv(SAv obj) throws Exception {
		return update(ConnectionUtils.getEmbeddedDerby(), obj);
	}

	public static List<SAv> getSAv(Connection con, Object obj) throws Exception {
		List<SAv> robjs = new ArrayList<SAv>();
		if (con != null) {
			if (COLUMNS.get(obj.getClass().getName()) == null) {
				COLUMNS.put(obj.getClass().getName(), obj.getClass().getFields());
			}
			Field[] fs = COLUMNS.get(obj.getClass().getName());
			String where = "";
			Object v = null;
			String vs = null;
			for (Field f : fs) {
				if (f.getName().indexOf("_") >= 0) {
					continue;
				}
				v = f.get(obj);
				if (v == null)
					continue;
				vs = v.toString();
				if (vs.indexOf("%") >= 0) {
					where += f.getName() + " like '" + vs + "' and ";
				} else {
					where += f.getName() + " = '" + vs + "' and ";
				}
			}

			Object o = null;
			Statement s = null;
			try {
				where = where.substring(0, where.length() - " and ".length());
				s = con.createStatement();
				ResultSet rs = s.executeQuery("select * from " + table + " where " + where);

				while (rs.next()) {
					o = obj.getClass().newInstance();
					for (Field f : fs) {
						if (f.getName().indexOf("_") >= 0) {
							continue;
						}
						f.set(o, rs.getString(f.getName()));
					}
					robjs.add((SAv) o);
				}
				return robjs;
			} finally {
				if (s != null) {
					try {
						s.close();
					} catch (SQLException e) {
					}
				}
				s = null;
			}
		}
		return robjs;
	}

	public static boolean update(Connection con, SAv obj) throws Exception {

		if (con != null) {
			if (COLUMNS.get(obj.getClass().getName()) == null) {
				COLUMNS.put(obj.getClass().getName(), obj.getClass().getFields());
			}
			Field[] fs = COLUMNS.get(obj.getClass().getName());
			Object v;
			String vs;
			String update = "", where = "";
			for (Field f : fs) {
				if (f.getName().indexOf("_") == 0 || f.getName().equals("name") || f.getName().equals("src")) {
					continue;
				}
				v = f.get(obj);
				if (v == null)
					continue;
				vs = v.toString();
				update += f.getName() + "='" + vs + "',";
			}
			Statement ps = null;
			try {
				update = update.substring(0, update.length() - 1);
				if (obj.name != null)
					where += " and name ='" + obj.name + "'";
				if (obj.src != null)
					where += " and src='" + obj.src + "'";
				where = where.substring(5, where.length());
				ps = con.createStatement();
				int rs = ps.executeUpdate("update " + table + " set " + update + " where " + where);
				ps.close();
				return rs > 0 ? true : false;
			} catch (DerbySQLIntegrityConstraintViolationException ce) {

			} finally {
				if (ps != null) {
					try {
						ps.close();
					} catch (SQLException e) {
					}
				}
				ps = null;
			}
		}

		return false;
	}

	public static boolean insert(Connection con, String table, Object obj) throws Exception {

		if (con != null) {
			if (COLUMNS.get(obj.getClass().getName()) == null) {
				COLUMNS.put(obj.getClass().getName(), obj.getClass().getFields());
			}
			Field[] fs = COLUMNS.get(obj.getClass().getName());
			String columns = "";
			String qu = "";
			for (Field f : fs) {
				if (f.getName().indexOf("_") == 0) {
					continue;
				}
				columns += f.getName() + ",";
				qu += "?,";
			}

			columns = columns.substring(0, columns.length() - 1);
			qu = qu.substring(0, qu.length() - 1);
			PreparedStatement ps = null;
			try {
				ps = con.prepareStatement("insert into " + table + "(" + columns + ") values(" + qu + ")");

				int i = 1;
				Object v = null;
				for (Field f : fs) {
					if (f.getName().indexOf("_") >= 0) {
						continue;
					}
					v = f.get(obj);
					ps.setString(i++, v == null ? "" : v.toString());
				}
				ps.execute();
				ps.close();
			} catch (DerbySQLIntegrityConstraintViolationException ce) {

			} finally {
				if (ps != null) {
					try {
						ps.close();
					} catch (SQLException e) {
					}
				}
				ps = null;
			}
			return true;
		}

		return false;
	}

	public static void main(String[] args) throws Exception {
		SAv s = new SAv();
		s.name = "1";

		System.out.println(getSAv(s).get(0).cate);

		s.cate = "1";

		updateSAv(s);

		System.out.println(getSAvByPK(s));
	}
}
