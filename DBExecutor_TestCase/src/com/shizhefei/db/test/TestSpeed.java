package com.shizhefei.db.test;

import java.util.ArrayList;
import java.util.List;

import android.test.AndroidTestCase;

import com.shizhefei.db.DBExecutor;
import com.shizhefei.db.annotations.Id;

/**
 * 测试性能
 * 
 * @author 试着飞
 * 
 */
public class TestSpeed extends AndroidTestCase {
	List<DDDD> insertEntrys = new ArrayList<DDDD>();
	private DBExecutor executor;
	private long startTime = 0;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		executor = DBExecutor.getInstance(getContext());
		String _String = "a";
		float _float = 1.0f;
		double _double = 2.0d;
		boolean _boolean = true;
		char _char = '@';
		long _long = 78678L;
		byte _byte = 2;
		byte[] bs = new byte[] { 43, 2, 5, 7, 89, 3 };
		DDDD dddd = new DDDD(_String, _double, _float, _boolean, _char, _long,
				_byte, bs);
		for (int i = 0; i < 4000; i++) {
			insertEntrys.add(dddd);
		}
		startTime = System.currentTimeMillis();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		System.out.println("####################  用时时间:"
				+ (System.currentTimeMillis() - startTime)
				+ " 毫秒                ############################");
	}

	public void testInert() throws Exception {
		assertTrue(executor.insertAll(insertEntrys));
		System.out.println("testInert  插入 :" + insertEntrys.size() + "条数据");
	}

	public void tesTFindMy() throws Exception {
		List<DDDD> dddds = executor.findAll(DDDD.class);
		System.out.print("testFindMy  找到的" + dddds.size() + "条数数据 ");
	}

}

class DDDD {
	@Id
	private int id;
	private String _String;
	private double _double;
	private float _float;
	private boolean _boolean;
	private char _char;
	private long _long;
	private byte _byte;
	private byte[] bs;

	public DDDD(String _String, double _double, float _float, boolean _boolean,
			char _char, long _long, byte _byte, byte[] bs) {
		super();
		this._String = _String;
		this._double = _double;
		this._float = _float;
		this._boolean = _boolean;
		this._char = _char;
		this._long = _long;
		this._byte = _byte;
		this.bs = bs;
	}

	public DDDD(int id, String _String, double _double, float _float,
			boolean _boolean, char _char, long _long, byte _byte, byte[] bs) {
		super();
		this.id = id;
		this._String = _String;
		this._double = _double;
		this._float = _float;
		this._boolean = _boolean;
		this._char = _char;
		this._long = _long;
		this._byte = _byte;
		this.bs = bs;
	}

}
