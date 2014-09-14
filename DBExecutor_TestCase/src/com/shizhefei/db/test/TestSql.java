package com.shizhefei.db.test;

import java.sql.Date;
import java.util.List;

import android.test.AndroidTestCase;

import com.shizhefei.db.DBExecutor;
import com.shizhefei.db.sql.Sql;
import com.shizhefei.db.sql.SqlFactory;
import com.shizhefei.db.table.DbModel;
import com.shizhefei.db.table.TableFactory;
import com.shizhefei.db.test.a.EntryUtils;
import com.shizhefei.db.test.a.Person;

/**
 * 
 * 测试如何生成sql对象，执行增删改查的sql
 * 
 * @author 试着飞
 * 
 */
public class TestSql extends AndroidTestCase {

	private DBExecutor db;
	private long startTime;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		db = DBExecutor.getInstance(getContext());
		startTime = System.currentTimeMillis();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		System.out.println("#####  用时时间:"
				+ (System.currentTimeMillis() - startTime) + " 毫秒       #####");
	}

	public void testInsert() throws Exception {
		testFindAll();
		Person person = new Person("试着飞", true, new Date(
				System.currentTimeMillis()));
		assertTrue(db.insert(person));
		System.out.println("-------数据插入后 ------------------");
		testFindAll();
	}

	public void testFind() throws Exception {
		Person person = db.findById(Person.class, 1);
		EntryUtils.toString(person);
	}

	public void testFind02() throws Exception {
		Sql sql = SqlFactory.find(Person.class).where("id", "=", 1)
				.and("name", "=", "试着飞");
		List<Person> persons = db.executeQuery(sql);
		for (Person sss : persons) {
			EntryUtils.toString(sss);
		}
		System.out.println(persons.size());
	}

	public void testFindAll() throws Exception {
		List<Person> person = db.findAll(Person.class);
		for (Person sss : person) {
			EntryUtils.toString(sss);
		}
		assertNotNull(person);
	}

	public void testDelete() throws Exception {
		testFindAll();
		assertTrue(db.deleteById(Person.class, 2));
		System.out.println("-------删除id 为2后------------------");
		testFindAll();
	}

	public void testDeleteAll() throws Exception {
		testFindAll();
		assertTrue(db.deleteAll(Person.class));
		System.out.println("-------删除全部后------------------");
		testFindAll();
	}

	public void testUpdate() throws Exception {
		testFindAll();
		Person person = new Person(4, "hha", false, new Date(100));
		assertTrue(db.updateById(person));
		System.out.println("-------数据更新后------------------");
		testFindAll();
	}

	public void testExecteSql() throws Exception {
		testFindAll();
		Person person = new Person("大黄粉剂", false, new Date(55444));
		Sql sql = SqlFactory.insert(person);
		assertTrue(db.execute(sql));
		System.out.println("-------数据插入后 ------------------");
		testFindAll();
	}

	public void testSelfBuidSql() throws Exception {
		testFindAll();
		String tableName = TableFactory.getTableName(Person.class);
		String s = "insert into " + tableName + " (name) VALUES(?)";
		Sql sql = Sql.valueOf(Person.class, s, new String[] { "放大镜" });
		assertTrue(db.execute(sql));
		System.out.println("-------数据插入后 ------------------");
		testFindAll();
	}

	public void testSelfBuidSql02() throws Exception {
		String tableName = TableFactory.getTableName(Person.class);
		String s = "select count(*) as nnn from " + tableName;
		Sql sql = Sql.valueOf(Person.class, s);
		List<DbModel> dbModels = db.executeQueryGetDBModels(sql);
		System.out.print("执行 sql 语句 ：" + sql + " 后返回数据:");
		System.out.println("  " + dbModels.get(0).getLong("nnn"));
	}

	public void testCount() throws Exception {
		System.out.println(db.count(Person.class));
	}

}
