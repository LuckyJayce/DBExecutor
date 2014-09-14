package com.shizhefei.db.converters;

/**
 * sql类型与java类型 互相转化的转化器
 * 
 * @author 试着飞 </br> Date: 13-11-18
 */
public interface IColumnConverter {

	public Object toSqlValue(Object value);

	public Object toJavaValue(Object value);

	public DBType getDBType();
}
