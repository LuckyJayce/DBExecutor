package com.shizhefei.db.sql.function;

public class MaxFunction implements IFunction {

	private String sql;
	public MaxFunction(String forColum, String asName) {
		super();
		StringBuilder builder = new StringBuilder();
		builder.append("MAX(").append(forColum).append(") as ").append(asName).append(" ");
		sql = builder.toString();
	}

	@Override
	public String getSqlText() {
		return sql;
	}

}
