package com.shizhefei.db.converters;

import java.sql.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 试着飞 Date: 13-11-18
 */
public class ColumnConverterFactory {

	private static BaseConverter nullConverter = new BaseConverter() {
		@Override
		public DBType getDBType() {
			return DBType.NULL;
		}
	};
	private final static Map<Class<?>, IColumnConverter> map;

	public static IColumnConverter getColumnConverter(Object value) {
		if (value == null)
			return nullConverter;
		return getColumnConverter(value.getClass());
	}

	public static IColumnConverter getColumnConverter(Class<?> valueClass) {
		return map.get(valueClass);
	}

	/**
	 * 注册valueClass类的数据转化器
	 * 
	 * @param valueClass
	 * @param converter
	 */
	public static void regist(Class<?> valueClass, IColumnConverter converter) {
		map.put(valueClass, converter);
	}

	/**
	 * 解除valueClass类的数据转化器
	 * 
	 * @param valueClass
	 * @param converter
	 */
	public static void unRegist(Class<?> valueClass, IColumnConverter converter) {
		map.remove(valueClass);
	}

	/**
	 * 是否支持valueClass类的数据转化
	 * 
	 * @param valueClass
	 * @return 是否支持valueClass类的数据转化
	 */
	public static boolean isSuport(Class<?> valueClass) {
		return map.containsKey(valueClass);
	}

	static {
		map = new ConcurrentHashMap<Class<?>, IColumnConverter>();
		reg(new ByteConverter(), boolean.class, Boolean.class);
		reg(new IntegerConverter(), int.class, Integer.class);
		reg(new DoubleConverter(), double.class, Double.class);
		reg(new FlaotConverter(), float.class, Float.class);
		reg(new LongConverter(), long.class, Long.class);
		reg(new ShortConverter(), short.class, Short.class);
		reg(new ByteConverter(), byte.class, Byte.class);
		reg(new StringConverter(), String.class);
		reg(new ByteArrayConverter(), byte[].class);
		reg(new BooleanConverter(), boolean.class, Boolean.class);
		reg(new CharConverter(), char.class, Character.class);
		reg(new DateConverter(), Date.class);
	}

	private static void reg(IColumnConverter converter, Class<?>... valueClass) {
		for (Class<?> class1 : valueClass) {
			map.put(class1, converter);
		}
	}

}
