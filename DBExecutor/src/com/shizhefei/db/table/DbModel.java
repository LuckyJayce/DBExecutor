package com.shizhefei.db.table;

import java.util.Date;
import java.util.HashMap;

/**
 * 
 * @author 试着飞 </br> Date: 13-11-18
 */
public class DbModel {

	/**
	 * key: columnName value: valueStr
	 */
	private HashMap<String, String> dataMap = new HashMap<String, String>();

	public String getString(String columnName) {
		return dataMap.get(columnName);
	}

	public int getInt(String columnName) {
		return Integer.valueOf(getString(columnName));
	}

	public boolean getBoolean(String columnName) {
		String value = getString(columnName);
		if (value != null) {
			return value.length() == 1 ? "1".equals(value) : Boolean
					.valueOf(value);
		}
		return false;
	}

	public double getDouble(String columnName) {
		return Double.valueOf(getString(columnName));
	}

	public float getFloat(String columnName) {
		return Float.valueOf(getString(columnName));
	}

	public long getLong(String columnName) {
		return Long.valueOf(getString(columnName));
	}

	public Date getDate(String columnName) {
		long date = Long.valueOf(getString(columnName));
		return new Date(date);
	}

	public java.sql.Date getSqlDate(String columnName) {
		long date = Long.valueOf(getString(columnName));
		return new java.sql.Date(date);
	}

	public void add(String columnName, String valueStr) {
		dataMap.put(columnName, valueStr);
	}

	/**
	 * @return key: columnName
	 */
	public HashMap<String, String> getDataMap() {
		return dataMap;
	}
}
