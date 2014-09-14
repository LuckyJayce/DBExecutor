package com.shizhefei.db.sql;

/**
 * 查询语句
 * 
 * @author 试着飞 </br> Date: 13-11-18
 */
public interface IFind {

	public IFind groupBy(String columnName);

	public IFind orderBy(String columnName, boolean desc);

	public IFind limit(int limit);

	public IFind offset(int offset);
}
