package com.shizhefei.db.sql.function;

public class DistinctFunction implements IFunction {

	private String sql;
	public DistinctFunction(String forColum, String asName) {
		super();
		StringBuilder builder = new StringBuilder();
		builder.append("Distinct(").append(forColum).append(") as ").append(asName).append(" ");
		sql = builder.toString();
	}

	@Override
	public String getSqlText() {
		return sql;
	}

}
