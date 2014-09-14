package com.shizhefei.db.table;

import java.lang.reflect.Field;

import com.shizhefei.db.converters.ColumnConverterFactory;
import com.shizhefei.db.converters.IColumnConverter;

/**
 * 数据库的表字段
 * 
 * @author 试着飞 </br> Date: 13-11-18
 */
public class Column {
	/**
	 * 字段名
	 */
	private String name;
	/**
	 * 字段的反射
	 */
	private Field columnfield;
	/**
	 * 类型转化
	 */
	private IColumnConverter columnConverter;
	/**
	 * 默认值
	 */
	private String defalutValue;

	public Column(Field columnfield) {
		this(columnfield, ColumnUtils.getColumnName(columnfield));
	}

	public Column(Field columnfield, String name) {
		super();
		this.name = name;
		this.columnfield = columnfield;
		columnConverter = ColumnConverterFactory.getColumnConverter(columnfield
				.getType());
		defalutValue = ColumnUtils.getDefalutValue(columnfield);
	}

	public String getName() {
		return name;
	}

	public Field getColumnField() {
		return columnfield;
	}

	public String getDefalutValue() {
		return defalutValue;
	}

	public IColumnConverter getColumnConverter() {
		return columnConverter;
	}

}
