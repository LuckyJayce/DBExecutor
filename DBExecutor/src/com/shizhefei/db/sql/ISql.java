package com.shizhefei.db.sql;

import java.util.List;

/**
 * Sql语句必须具备的方法
 * 
 * @author 试着飞 </br> Date: 13-11-18
 */
public interface ISql {
	public abstract String getSqlText();

	public abstract List<Object> getBindValues();
}
