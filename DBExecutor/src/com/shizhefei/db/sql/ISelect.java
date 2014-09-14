package com.shizhefei.db.sql;

import com.shizhefei.db.sql.function.IFunction;

/**
 * 查询语句的中选择语句的接口定义
 * 
 * @author 试着飞 </br> Date: 14-3-21
 */
public interface ISelect {
	/**
	 * select("age","name")表示<br/>
	 * "select age,name from table";<br/>
	 * <br/>
	 * select("count(*) as num")表示<br/>
	 * "select count(*) as num from table"; <br/>
	 * 
	 * @param columnNames
	 * @return
	 */
	public ISelect select(String... columnNames);

	public ISelect select(IFunction... functions);
}
