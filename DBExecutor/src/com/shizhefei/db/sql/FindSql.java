package com.shizhefei.db.sql;

import java.util.ArrayList;
import java.util.List;

import com.shizhefei.db.exception.CheckExcption;
import com.shizhefei.db.sql.function.IFunction;

/**
 * 带有查询方法的sql</br>
 * 
 * 特别注意该类用了装饰模式，所以父类的所有方法都要重写一遍
 * 
 * @author 试着飞 </br> Date: 13-11-18
 */
public class FindSql extends Sql implements ISelect, IFind, IWhere {
	private FindBuilder findBuilder;
	private WhereBuilder whereBuilder;
	private SelectBuilder selectBuilder;

	public FindSql(Class<?> tableClass) {
		super(tableClass);
		findBuilder = new FindBuilder();
		selectBuilder = new SelectBuilder(table);
	}

	@Override
	public FindSql groupBy(String columnName) {
		findBuilder.groupBy(columnName);
		return this;
	}

	@Override
	public FindSql orderBy(String columnName, boolean desc) {
		findBuilder.orderBy(columnName, desc);
		return this;
	}

	@Override
	public FindSql limit(int limit) {
		findBuilder.limit(limit);
		return this;
	}

	@Override
	public FindSql offset(int offset) {
		findBuilder.offset(offset);
		return this;
	}

	public FindSql where(WhereBuilder where) {
		this.whereBuilder = where;
		return this;
	}

	@Override
	public FindSql where(String columnName, String op, Object value) {
		this.whereBuilder = WhereBuilder.create(columnName, op, value);
		return this;
	}

	@Override
	public FindSql and(String columnName, String op, Object value) {
		if (this.whereBuilder == null)
			throw new CheckExcption("where 语法有问题,请先执行 where函数");
		this.whereBuilder.and(columnName, op, value);
		return this;
	}

	@Override
	public FindSql or(String columnName, String op, Object value) {
		if (this.whereBuilder == null)
			throw new CheckExcption("where 语法有问题,请先执行 where函数");
		this.whereBuilder.or(columnName, op, value);
		return this;
	}

	@Override
	public FindSql expr(String columnName, String op, Object value) {
		if (this.whereBuilder == null)
			throw new CheckExcption("where 语法有问题,请先执行 where函数");
		this.whereBuilder.expr(columnName, op, value);
		return this;
	}

	@Override
	public String getSqlText() {
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append(selectBuilder.getSqlText());
		if (whereBuilder != null) {
			sqlBuilder.append(" where ").append(whereBuilder.getSqlText());
		}
		sqlBuilder.append(findBuilder.getSqlText());
		return sqlBuilder.toString();
	}

	@Override
	public List<Object> getBindValues() {
		List<Object> values = new ArrayList<Object>();
		if (whereBuilder != null) {
			values.addAll(whereBuilder.getBindValues());
		}
		values.addAll(findBuilder.getBindValues());
		return values;
	}

	@Override
	public FindSql where(String expressions, Object[] values) {
		this.whereBuilder = new WhereBuilder();
		whereBuilder.where(expressions, values);
		return this;
	}

	@Override
	public FindSql select(String... columnNames) {
		selectBuilder.select(columnNames);
		return this;
	}

	@Override
	public FindSql where(String expressions) {
		whereBuilder.where(expressions);
		return this;
	}

	@Override
	public FindSql select(IFunction... functions) {
		selectBuilder.select(functions);
		return this;
	}

}
