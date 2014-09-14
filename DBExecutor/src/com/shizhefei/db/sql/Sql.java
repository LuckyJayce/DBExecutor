package com.shizhefei.db.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.shizhefei.db.table.Table;
import com.shizhefei.db.table.TableFactory;

/**
 * Sql 用于执行的sql语句
 * 
 * @author 试着飞 </br> Date: 13-11-18
 */
public abstract class Sql implements ISql {
	private static final List<Object> EMPTY_LIST = new ArrayList<Object>(0);
	/** 表 */
	protected Table table;
	/**是否检查表是否存在 ，默认为true如果检查到表不存在自动创建，设置为false 不做检查*/
	private boolean checkTableExit = true;

	public boolean isCheckTableExit() {
		return checkTableExit;
	}

	/**
	 * 设置执行sql时检查表是否存在，默认为true如果检查到表不存在自动创建，设置为false 不做检查
	 * @param checkTableExit
	 */
	public void setCheckTableExit(boolean checkTableExit) {
		this.checkTableExit = checkTableExit;
	}

	public Table getTable() {
		return table;
	}

	protected Sql(Class<?> tableClass) {
		super();
		this.table = TableFactory.getTable(tableClass);

	}

	protected Sql(Table table) {
		super();
		this.table = table;

	}

	public static Sql valueOf(Class<?> tableClass, String sql) {
		return valueOf(tableClass, sql, EMPTY_LIST);
	}

	/**
	 * 创建sql
	 * 
	 * @param tableClass
	 * @param sql
	 * @param bindValues
	 * @return
	 */
	public static Sql valueOf(Class<?> tableClass, String sql, List<Object> bindValues) {
		Table table = TableFactory.getTable(tableClass);
		return valueOf(table, sql, bindValues);
	}

	/**
	 * 创建sql
	 * 
	 * @param tableClass
	 * @param sql
	 * @param bindValues
	 * @return
	 */
	public static Sql valueOf(Class<?> tableClass, String sql, Object[] bindValues) {
		Table table = TableFactory.getTable(tableClass);
		return valueOf(table, sql, Arrays.asList(bindValues));
	}

	/**
	 * 创建sql
	 * 
	 * @param table
	 * @param sql
	 * @return
	 */
	protected static Sql valueOf(Table table, String sql) {
		return valueOf(table, sql, EMPTY_LIST);
	}

	protected static Sql valueOf(Table table, String sql, Object[] bindValues) {
		return new SimpleSql(table, sql, Arrays.asList(bindValues));
	}

	protected static Sql valueOf(Table table, String sql, List<Object> bindValues) {
		return new SimpleSql(table, sql, bindValues);
	}

	private static class SimpleSql extends Sql {

		private StringBuilder sqlBuilder;
		private List<Object> bindValues;

		public SimpleSql(Table table, String sql, List<Object> binds) {
			super(table);
			sqlBuilder = new StringBuilder(40);
			this.sqlBuilder.append(sql);
			if (binds != null && !binds.isEmpty()) {
				this.bindValues = new ArrayList<Object>(binds.size());
				this.bindValues.addAll(binds);
			} else {
				this.bindValues = new ArrayList<Object>(0);
			}
		}

		@Override
		public String getSqlText() {
			return sqlBuilder.toString();
		}

		@Override
		public List<Object> getBindValues() {
			return bindValues;
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("sql语句:").append(getSqlText());
		builder.append("绑定的值:");
		for (Object object : getBindValues()) {
			builder.append(object).append("##");
		}
		return builder.toString();
	}

}
