package com.shizhefei.db.sql.function;

public class MinFunction implements IFunction {

	private String sql;
	public MinFunction(String forColum, String asName) {
		super();
		StringBuilder builder = new StringBuilder();
		builder.append("MIN(").append(forColum).append(") as ").append(asName).append(" ");
		sql = builder.toString();
	}

	@Override
	public String getSqlText() {
		return sql;
	}

}
