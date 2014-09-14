package com.shizhefei.db.sql;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * sql语句的缓存区
 * 
 * @author 试着飞 </br> Date: 13-11-18
 */
public class SqlCache {
	SqlCache() {
	}

	public String getInsertSql(Class<?> tableClass, boolean isAppendId) {
		if (isAppendId)
			return insertHasIdSqls.get(tableClass);
		return insertSqls.get(tableClass);
	}

	public void putInsertSql(Class<?> tableClass, String sql, boolean isAppendId) {
		if (isAppendId)
			insertHasIdSqls.put(tableClass, sql);
		else
			insertSqls.put(tableClass, sql);
	}

	public String getUpdateSql(Class<?> c, String[] updateColumnNames) {
		long hashcode = getHashCode(c, updateColumnNames);
		return updateSqls.get(hashcode);
	}

	public void putUpdateSql(Class<?> c, String[] updateColumnNames, String sql) {
		long hashcode = getHashCode(c, updateColumnNames);
		updateSqls.put(hashcode, sql);
	}

	private long getHashCode(Class<?> c, String[] updateColumnNames) {
		return c.getName().hashCode() + Arrays.hashCode(updateColumnNames);
	}

	private Map<Class<?>, String> insertHasIdSqls = new ConcurrentHashMap<Class<?>, String>();

	private Map<Class<?>, String> insertSqls = new ConcurrentHashMap<Class<?>, String>();

	private Map<Long, String> updateSqls = new ConcurrentHashMap<Long, String>();
}
