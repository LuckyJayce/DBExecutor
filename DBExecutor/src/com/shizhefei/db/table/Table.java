package com.shizhefei.db.table;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据库表
 * 
 * @author 试着飞 </br> Date: 13-11-18
 */
public class Table {
	/** id字段 */
	private Column id;
	/** 所有的字段，包括id */
	private Map<String, Column> columns;
	/** 表的名字 */
	private String tableName;
	private Class<?> tableClass;
	/** id是否自增长 **/
	private boolean isAutoIncrement;

	Table(Class<?> tableClass, String tableName, Map<String, Column> columns, Column id, boolean isAutoIncrement) {
		this.tableClass = tableClass;
		this.tableName = tableName;
		this.columns = Collections.unmodifiableMap(columns);
		this.isAutoIncrement = isAutoIncrement;
		this.id = id;
	}

	/**
	 * 获取表对应的class
	 * 
	 * @return
	 */
	public Class<?> getTableClass() {
		return tableClass;
	}

	/**
	 * 获取表的名字
	 * 
	 * @return
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * 获取id字段
	 * 
	 * @return
	 */
	public Column getId() {
		return id;
	}

	/**
	 * 返回所有的字段，包括id字段，key为字段的名字<br/>
	 * 特别注意：返回的是不可修改的map，一旦remove，put操作就会触发异常
	 * 
	 * @return 表中所有字段
	 */
	public Map<String, Column> getColumns() {
		return columns;
	}

	public Map<String, Column> getWhitoutIdColumns() {
		HashMap<String, Column> hashMap = new HashMap<String, Column>(columns);
		hashMap.remove(id.getName());
		return hashMap;
	}

	/**
	 * id是否是自增长
	 * 
	 * @return
	 */
	public boolean isAutoIncrement() {
		return isAutoIncrement;
	}

}
