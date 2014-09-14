package com.shizhefei.db.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 查询语句的建造者
 * 
 * @author 试着飞 </br> Date: 13-11-18
 */
public class FindBuilder implements IFind, ISql {
	protected List<OrderBy> orderByList;
	protected int limit = 0;
	protected int offset = 0;

	@Override
	public IFind groupBy(String columnName) {
		throw new RuntimeException("暂时还没有实现 groupBy的功能");
	}

	public IFind orderBy(String columnName, boolean desc) {
		if (orderByList == null) {
			orderByList = new ArrayList<OrderBy>(2);
		}
		orderByList.add(new OrderBy(columnName, desc));
		return this;
	}

	public IFind limit(int limit) {
		this.limit = limit;
		return this;
	}

	public IFind offset(int offset) {
		this.offset = offset;
		return this;
	}

	@Override
	public String getSqlText() {
		StringBuilder sql = new StringBuilder(30);
		if (orderByList != null) {
			sql.append(" ORDER BY ");
			for (int i = 0; i < orderByList.size(); i++) {
				sql.append(orderByList.get(i).toString()).append(',');
			}
			if (sql.charAt(sql.length() - 1) == ',') {
				sql.deleteCharAt(sql.length() - 1);
			}
		}
		if (limit > 0) {
			sql.append(" LIMIT ").append(limit);
			sql.append(" OFFSET ").append(offset);
		}
		return sql.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getBindValues() {
		return Collections.EMPTY_LIST;
	}

	protected class OrderBy {
		private String columnName;
		private boolean desc;

		public OrderBy(String columnName, boolean desc) {
			this.columnName = columnName;
			this.desc = desc;
		}

		@Override
		public String toString() {
			return columnName + (desc ? " DESC" : " ASC");
		}
	}
}
