package com.shizhefei.db.utils;

import java.lang.reflect.Array;

public class ArrayUtils {

	public static Object[] toArray(Object val) {
		int arrlength = Array.getLength(val);
		Object[] outputArray = new Object[arrlength];
		for (int i = 0; i < arrlength; ++i) {
			outputArray[i] = Array.get(val, i);
		}
		return outputArray;
	}
}
