package com.shizhefei.db.utils;

/**
 * 通过反射创建对象，两种方式创建，通过默认的构造器创建，如果不成功在通过系统的unsafe类去创建
 * 
 * @author Administrator
 * 
 * @author 试着飞 </br> Date: 13-11-18
 */
public class ObjectFactory<T> {
	private Class<T> c;
	// 是否通过默认的方式创建实例
	private boolean isDefault;

	public ObjectFactory(Class<T> tableClass) {
		super();
		this.c = tableClass;
		this.isDefault = true;
	}

	public T newInstance() throws Exception {
		T r = null;
		if (isDefault) {
			try {
				r = c.newInstance();
			} catch (Exception e) {
				isDefault = false;
				r = UnsafeAllocator.getUnsafeAllocator().newInstance(c);
			}
		} else {
			r = UnsafeAllocator.getUnsafeAllocator().newInstance(c);
		}
		return r;
	}
}