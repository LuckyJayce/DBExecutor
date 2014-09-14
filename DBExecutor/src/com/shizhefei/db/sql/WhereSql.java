package com.shizhefei.db.sql;

import java.util.ArrayList;
import java.util.List;

import com.shizhefei.db.exception.CheckExcption;

/**
 * 带有where操作的sql语句
 * 
 * op说明 ：operator: "=","<","LIKE","IN","BETWEEN"...
 * 
 * @author 试着飞 </br> Date: 13-11-18
 */
public class WhereSql extends Sql implements IWhere {
	protected WhereBuilder whereBuilder;
	protected Sql frontSql;

	public WhereSql(Sql frontSql) {
		super(frontSql.getTable());
		this.frontSql = frontSql;
	}

	public WhereSql where(WhereBuilder where) {
		this.whereBuilder = where;
		return this;
	}

	@Override
	public WhereSql where(String columnName, String op, Object value) {
		this.whereBuilder = WhereBuilder.create(columnName, op, value);
		return this;
	}

	@Override
	public WhereSql and(String columnName, String op, Object value) {
		if (this.whereBuilder == null)
			throw new CheckExcption("where 语法有问题,请先执行 where函数");
		this.whereBuilder.and(columnName, op, value);
		return this;
	}

	@Override
	public WhereSql or(String columnName, String op, Object value) {
		if (this.whereBuilder == null)
			throw new CheckExcption("where 语法有问题,请先执行 where函数");
		this.whereBuilder.or(columnName, op, value);
		return this;
	}

	@Override
	public WhereSql expr(String columnName, String op, Object value) {
		if (this.whereBuilder == null)
			throw new CheckExcption("where 语法有问题,请先执行 where函数");
		this.whereBuilder.expr(columnName, op, value);
		return this;
	}

	@Override
	public String getSqlText() {
		StringBuilder sqlBuilder = new StringBuilder(frontSql.getSqlText());
		if (whereBuilder != null)
			sqlBuilder.append(" where ").append(whereBuilder.getSqlText());
		return sqlBuilder.toString();
	}

	@Override
	public List<Object> getBindValues() {
		List<Object> values = new ArrayList<Object>();
		values.addAll(frontSql.getBindValues());
		if (whereBuilder != null)
			values.addAll(whereBuilder.getBindValues());
		return values;
	}

	@Override
	public WhereSql where(String expressions, Object[] values) {
		whereBuilder.where(expressions, values);
		return this;
	}

	@Override
	public WhereSql where(String expressions) {
		whereBuilder.where(expressions);
		return this;
	}

}
