package com.shizhefei.db.test.a;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author 试着飞
 * 
 */
public class EntryUtils {
	/**
	 * 简单的打印
	 * 
	 * @param object
	 */
	public static void toString(Object object) {
		try {
			Map<String, Object> values = objectToMap(object);
			for (Entry<String, Object> value : values.entrySet()) {
				System.out.print(value.getKey() + ":" + value.getValue() + " ");
			}
			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 得到类的字段集合
	 * 
	 * @param class1
	 * @param filter
	 *            字段过滤器
	 * @return
	 */
	public static List<Field> getFields(Class<?> class1, FieldFilter filter) {
		List<Field> fields = new ArrayList<Field>();
		while (class1 != null) {
			Field[] fs = class1.getDeclaredFields();
			for (Field field : fs) {
				if (filter.accept(field)) {
					field.setAccessible(true);
					fields.add(field);
				}
			}
			class1 = class1.getSuperclass();
		}
		return fields;
	}

	/**
	 * <pre>
	 * 将对象的字段用map存储，并返回map,key 保存字段名，value保存字段值
	 *  过滤字段的规则见{@link #DEFAULT_FILTER}
	 * </pre>
	 * 
	 * @param object
	 * @return
	 */
	public static Map<String, Object> objectToMap(Object object) {
		return objectToMap(object, DEFAULT_FILTER);
	}

	/**
	 * 将object 打印成 json格式<br/>
	 * <h1 style="color:#f00">
	 * #注意：object的字段必须是基本类型，如果不是就请用开源的gson-2.2.2.jar的类库去解析</h1>
	 * 
	 * <pre>
	 * 对象的字段用map存储，并返回map,key 保存字段名，value保存字段值
	 * </pre>
	 * 
	 * @param object
	 * @return
	 */
	public static String objectToJson(Object object) {
		return objectToJson(object, DEFAULT_FILTER);
	}

	/**
	 * object 的class里面的成员变量只能是基本类型, 如果不希望打印的字段请用transient关键字修饰 过滤字段的规则见
	 * {@link #DEFAULT_FILTER}
	 * 
	 * @param object
	 * @param filter
	 *            字段的过滤器
	 * @return
	 */
	public static Map<String, Object> objectToMap(Object object,
			FieldFilter filter) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Class<?> class1 = object.getClass();
		try {
			while (class1 != null) {
				Field[] fs = class1.getDeclaredFields();
				for (Field field : fs) {
					if (filter.accept(field)) {
						field.setAccessible(true);
						map.put(field.getName(), field.get(object));
					}
				}
				class1 = class1.getSuperclass();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 将object 打印成 json格式<br/>
	 * <h1 style="color:#f00">
	 * #注意：object的字段必须是基本类型，如果不是就请用开源的gson-2.2.2.jar的类库去解析</h1>
	 * 
	 * @param object
	 * @param filter
	 *            字段的过滤器
	 * @return
	 */
	public static String objectToJson(Object object, FieldFilter filter) {
		Map<String, Object> map = objectToMap(object, filter);
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		boolean has = false;
		for (Entry<String, Object> entry : map.entrySet()) {
			if (entry.getValue() == null)
				continue;
			builder.append("\"").append(entry.getKey()).append("\"");
			builder.append(":");
			builder.append(entry.getValue());
			builder.append(",");
			has = true;
		}
		if (has)
			builder.deleteCharAt(builder.length() - 1);
		builder.append("}");
		return builder.toString();
	}

	/**
	 * 过滤
	 * 
	 * @author Administrator
	 * 
	 */
	public static interface FieldFilter {
		/**
		 * 是否需要显示
		 * 
		 * @return
		 */
		public boolean accept(Field field);
	}

	/**
	 * 默认的过滤器 *
	 * 
	 * <pre>
	 * 接受除 static ，transient 修饰的字段
	 * </pre>
	 */
	public static final FieldFilter DEFAULT_FILTER = new FieldFilter() {

		@Override
		public boolean accept(Field field) {
			int modifiers = field.getModifiers();
			return !Modifier.isStatic(modifiers)
					&& !Modifier.isTransient(modifiers);
		}
	};

}
