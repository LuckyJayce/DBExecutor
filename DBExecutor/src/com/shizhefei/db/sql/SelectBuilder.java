package com.shizhefei.db.sql;

import java.util.ArrayList;
import java.util.List;

import com.shizhefei.db.sql.function.IFunction;
import com.shizhefei.db.table.Table;
/**
 * 查询语句的中选择语句的建造者
 * 
 * @author 试着飞 </br> Date: 14-3-21
 */
public class SelectBuilder implements ISelect {
	private Table table;
	private List<String> mColumnNames = new ArrayList<String>();
	private List<IFunction> mFunctions = new ArrayList<IFunction>();

	public SelectBuilder(Table table) {
		super();
		this.table = table;
	}

	@Override
	public SelectBuilder select(String... columnNames) {
		if (columnNames != null) {
			for (String string : columnNames) {
				mColumnNames.add(string);
			}
		}
		return this;
	}

	public String getSqlText() {
		if (!mColumnNames.isEmpty()) {
			StringBuilder builder = new StringBuilder("select ");
			for (String string : mColumnNames) {
				builder.append(string).append(",");
			}
			builder.deleteCharAt(builder.length() - 1);
			builder.append(" from ").append(table.getTableName()).append(" ");
			return builder.toString();
		} else if (!mFunctions.isEmpty()) {
			StringBuilder builder = new StringBuilder("select ");
			for (IFunction function : mFunctions) {
				builder.append(function.getSqlText()).append(",");
			}
			builder.deleteCharAt(builder.length() - 1);
			builder.append(" from ").append(table.getTableName()).append(" ");
			return builder.toString();
		} else {
			return "select * from " + table.getTableName() + " ";
		}
	}

	@Override
	public ISelect select(IFunction... functions) {
		if (functions != null) {
			for (IFunction string : functions) {
				mFunctions.add(string);
			}
		}
		return this;
	}

}
