package com.shizhefei.db.sql;

/**
 * where语句
 * 
 * @author Administrator
 * 
 */
public interface IWhere {
	/**
	 * where("age = 1") 表示 where age = 1 <br>
	 * 
	 * 尽量调用{@link #where(String, Object[])}
	 * 用？占位符设置条件的值，这样避免手动拼接sql语句，以及能够正确的将java的值转化为数据库的值。<br>
	 * 
	 * 以上可以转化为 where("age = ？"，new Object[]{1}) 表示 where age = 1
	 * 
	 * @param expressions
	 * @return
	 */
	public IWhere where(String expressions);

	/**
	 * where("age = ? and ( name = ? or name = ?)",new Object[]{1,"试着飞","小凡"}) <br>
	 * 表示 where age = 1 and ( name = '试着飞' or name = '小凡' ) <br>
	 * 
	 * @param expressions
	 * @param values
	 * @return
	 */
	public IWhere where(String expressions, Object[] values);

	/**
	 * 
	 * where(“age”, "=", 1) 表示sql的 where age = 1;<br/>
	 * where(“age”, "!=", 1) 表示sql的 where age <> 1;<br/>
	 * where(“age”, "<>", 1) 表示sql的 where age <> 1;<br/>
	 * where(“name”, "like", "张_") 表示sql的 where name like '张_';<br/>
	 * where(“name”, "like", "%三") 表示sql的 where name like '%三';<br/>
	 * where(“age”, "in", new int[]{1,2,3})或者where(“age”, "in", list) 表示sql的
	 * where age in(1,2,3);<br/>
	 * where(“age”, "betteen", new int[]{1,10})或者where(“age”, "in", list) 表示sql的
	 * where age betteen 1 and 10;<br/>
	 * 
	 * @param columnName
	 * @param op
	 *            operator: "=","<","!=","<>","LIKE","IN","BETWEEN"...
	 * @param value
	 * @return IWhere
	 */
	public IWhere where(String columnName, String op, Object value);

	/**
	 * 
	 * and(“age”, "=", 1) 表示sql的 and age = 1;<br/>
	 * and(“age”, "!=", 1) 表示sql的 and age <> 1;<br/>
	 * and(“age”, "<>", 1) 表示sql的 and age <> 1;<br/>
	 * and(“name”, "like", "张_") 表示sql的 and name like '张_';<br/>
	 * and(“name”, "like", "%三") 表示sql的 and name like '%三';<br/>
	 * and(“age”, "in", new int[]{1,2,3})或者where(“age”, "in", list) 表示sql的 and
	 * age in(1,2,3);<br/>
	 * and(“age”, "betteen", new int[]{1,10})或者where(“age”, "in", list) 表示sql的
	 * and age betteen 1 and 10;<br/>
	 * 
	 * @param columnName
	 * @param op
	 *            operator: "=","<","!=","<>","LIKE","IN","BETWEEN"...
	 * @param value
	 * @return IWhere
	 */
	public IWhere and(String columnName, String op, Object value);

	/**
	 * 
	 * or(“age”, "=", 1) 表示sql的 or age = 1;<br/>
	 * or(“age”, "!=", 1) 表示sql的 or age <> 1;<br/>
	 * or(“age”, "<>", 1) 表示sql的 or age <> 1;<br/>
	 * or(“name”, "like", "张_") 表示sql的 or name like '张_';<br/>
	 * or(“name”, "like", "%三") 表示sql的 or name like '%三';<br/>
	 * or(“age”, "in", new int[]{1,2,3})或者or(“age”, "in", list) 表示sql的 or age
	 * in(1,2,3);<br/>
	 * or(“age”, "betteen", new int[]{1,10})或者or(“age”, "in", list) 表示sql的 or
	 * age betteen 1 and 10;<br/>
	 * 
	 * @param columnName
	 * @param op
	 *            operator: "=","<","!=","<>","LIKE","IN","BETWEEN"...
	 * @param value
	 * @return IWhere
	 */
	public IWhere or(String columnName, String op, Object value);

	/**
	 * 
	 * @param columnName
	 * @param op
	 *            operator: "=","<","!=","<>","LIKE","IN","BETWEEN"...
	 * @param value
	 * @return IWhere
	 */
	public IWhere expr(String columnName, String op, Object value);

}
