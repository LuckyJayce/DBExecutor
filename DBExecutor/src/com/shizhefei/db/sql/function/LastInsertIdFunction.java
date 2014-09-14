package com.shizhefei.db.sql.function;

public class LastInsertIdFunction implements IFunction {

	private String sql;
	public LastInsertIdFunction(String asName) {
		super();
		StringBuilder builder = new StringBuilder();
		builder.append("last_insert_rowid() as ").append(asName).append(" ");
		sql = builder.toString();
	}

	@Override
	public String getSqlText() {
		return sql;
	}

}
