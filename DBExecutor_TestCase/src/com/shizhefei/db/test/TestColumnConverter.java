package com.shizhefei.db.test;

import android.test.AndroidTestCase;

import com.shizhefei.db.converters.ColumnConverterFactory;
import com.shizhefei.db.converters.DBType;
import com.shizhefei.db.converters.IColumnConverter;

/**
 * 
 * 测试类型转化器
 * 
 * @author 试着飞
 * 
 */
public class TestColumnConverter extends AndroidTestCase {
	public void testColumnConverter() throws Exception {
		boolean isOk = true;
		IColumnConverter converter = ColumnConverterFactory
				.getColumnConverter(isOk);
		Object value = converter.toSqlValue(isOk);
		System.out.println("转化成sql的数据：" + value);
		Object re = converter.toJavaValue(value);
		System.out.println("转化回java的数据：" + re);
	}

	public void testSelfColumnConverter() throws Exception {
		// 这里定义了自己的转化器，并注册到了ColumnConverterFactory中
		IColumnConverter self = new IColumnConverter() {

			@Override
			public String toSqlValue(Object value) {
				Person person = (Person) value;
				String s = person.name + "###" + person.age;
				return s;
			}

			@Override
			public Person toJavaValue(Object value) {
				String s = String.valueOf(value);
				String[] vas = s.split("###");
				String name = vas[0];
				int age = Integer.parseInt(vas[1]);
				return new Person(age, name);
			}

			@Override
			public DBType getDBType() {
				return DBType.TEXT;
			}
		};
		ColumnConverterFactory.regist(Person.class, self);
		// 这里开始演示使用的过程
		Person person = new Person(11, "小明");
		IColumnConverter converter = ColumnConverterFactory
				.getColumnConverter(person);
		Object value = converter.toSqlValue(person);
		System.out.println("转化成sql的数据：" + value);
		Object re = converter.toJavaValue(value);
		System.out.println("转化回java的数据：" + re);
	}

	private static class Person {
		private int age;
		private String name;

		public Person(int age, String name) {
			super();
			this.age = age;
			this.name = name;
		}

	}
}
