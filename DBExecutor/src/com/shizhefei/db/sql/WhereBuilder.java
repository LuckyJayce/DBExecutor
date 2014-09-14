package com.shizhefei.db.sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.shizhefei.db.utils.ArrayUtils;

/**
 * where语句的建造者
 * 
 * @author 试着飞 </br> Date: 13-11-18
 */
public class WhereBuilder implements ISql, IWhere {

	private List<Object> bindValues = new ArrayList<Object>();
	private List<String> whereItems = new ArrayList<String>();

	@Override
	public WhereBuilder where(String columnName, String op, Object value) {
		WhereBuilder result = new WhereBuilder();
		result.appendCondition(null, columnName, op, value);
		return result;
	}

	public static WhereBuilder create(String columnName, String op, Object value) {
		return new WhereBuilder().where(columnName, op, value);
	}

	@Override
	public WhereBuilder and(String columnName, String op, Object value) {
		appendCondition(whereItems.size() == 0 ? null : "AND", columnName, op, value);
		return this;
	}

	@Override
	public WhereBuilder or(String columnName, String op, Object value) {
		appendCondition(whereItems.size() == 0 ? null : "OR", columnName, op, value);
		return this;
	}

	@Override
	public WhereBuilder expr(String columnName, String op, Object value) {
		appendCondition(null, columnName, op, value);
		return this;
	}

	@Override
	public String getSqlText() {
		if (whereItems.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (String item : whereItems) {
			sb.append(item);
		}
		return sb.toString();
	}

	@Override
	public List<Object> getBindValues() {
		return bindValues;
	}

	private void appendCondition(String conj, String columnName, String op, Object value) {
		StringBuilder sqlSb = new StringBuilder();
		if (whereItems.size() > 0) {
			sqlSb.append(" ");
		}

		// append conj
		if (conj != null && !"".endsWith(conj)) {
			sqlSb.append(conj + " ");
		}

		// append columnName
		sqlSb.append(columnName);

		// convert op
		if ("!=".equals(op)) {
			op = "<>";
		} else if ("==".equals(op)) {
			op = "=";
		}

		// append op & value
		if (value == null) {
			if ("=".equals(op)) {
				sqlSb.append(" IS NULL");
			} else if ("<>".equals(op) || "!=".equals(op)) {
				sqlSb.append(" IS NOT NULL");
			} else {
				sqlSb.append(" " + op + " NULL");
			}
		} else {
			int valueCount = 1;
			if (value instanceof Collection) {
				Collection<?> items = (Collection<?>) value;
				valueCount = items.size();
				bindValues.addAll(items);
			} else if (value.getClass().isArray()) {
				Object[] objects = ArrayUtils.toArray(value);
				valueCount = objects.length;
				for (Object object : objects) {
					bindValues.add(object);
				}
			} else {
				bindValues.add(value);
				valueCount = 1;
			}
			sqlSb.append(" " + op + " ");
			if ("IN".equalsIgnoreCase(op) || "NOT IN".equalsIgnoreCase(op)) {
				sqlSb.append('(');
				for (int i = 0; i < valueCount; i++) {
					sqlSb.append('?');
					sqlSb.append(',');
				}
				sqlSb.deleteCharAt(sqlSb.length() - 1);
				sqlSb.append(')');
			} else if ("BETWEEN".equalsIgnoreCase(op)) {
				if (valueCount != 2)
					throw new RuntimeException("and 操作符  必须由两个值的 数组或集合");
				sqlSb.append("( ? and ? )");
			} else {
				if (valueCount != 1)
					throw new RuntimeException("除in between的 操作符外  必须是一个值");
				sqlSb.append('?');
			}
		}
		whereItems.add(sqlSb.toString());
	}

	@Override
	public IWhere where(String expressions) {
		where(expressions, null);
		return this;
	}

	@Override
	public IWhere where(String expressions, Object[] values) {
		WhereBuilder result = new WhereBuilder();
		whereItems.clear();
		bindValues.clear();
		whereItems.add(expressions);
		if (values != null) {
			for (Object object : values) {
				bindValues.add(object);
			}
		}
		return result;
	}

}
