package com.shizhefei.db.test;

import java.util.Map;
import java.util.Set;

import android.test.AndroidTestCase;

import com.shizhefei.db.DBExecutor;
import com.shizhefei.db.annotations.Check;
import com.shizhefei.db.annotations.Column;
import com.shizhefei.db.annotations.Id;
import com.shizhefei.db.annotations.NotNull;
import com.shizhefei.db.annotations.Table;
import com.shizhefei.db.annotations.Transient;
import com.shizhefei.db.annotations.Unique;
import com.shizhefei.db.sql.SqlFactory;
import com.shizhefei.db.table.TableFactory;

/**
 * 测试表的配置
 * 
 * @author Administrator
 * 
 */
public class TestConfig extends AndroidTestCase {
	private DBExecutor db;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		db = DBExecutor.getInstance(getContext());
	}

	public void testCreateSql() {
		com.shizhefei.db.table.Table table = TableFactory.getTable(QQQQ.class);
		String sql = SqlFactory.createTable(table);
		System.out.println("create table sql:" + sql);
	}

	public void testTableName() throws Exception {
		String tableName = TableFactory.getTableName(QQQQ.class);
		System.out.println("tableName:" + tableName);
	}

	public void testColumnName() throws Exception {
		com.shizhefei.db.table.Table table = TableFactory.getTable(QQQQ.class);
		Map<String, com.shizhefei.db.table.Column> columns = table.getColumns();
		Set<String> columnNames = columns.keySet();
		for (String name : columnNames) {
			System.out.print(name + ",");
		}
		System.out.println();
	}

	public void testCheck() {
		int id = 1;
		String kkkk = "小红";
		int age = 11;
		int cardId = 5899;
		String ddd = "";
		QQQQ qqqq = new QQQQ(id, kkkk, age, cardId, ddd);

		assertTrue(db.insert(qqqq));
		assertTrue(db.deleteById(QQQQ.class, 1));

		qqqq.age = -11;
		assertFalse(db.insert(qqqq));
		assertTrue(db.deleteById(QQQQ.class, 1));
	}

}

@Table(name = "Person_Table")
class QQQQ {
	/**
	 * <pre>
	 * 设置该字段为id字段，不会自增长，
	 * ##########
	 *  默认的情况是自增长，自增长的写法：
	 *  @Id
	 *  private int id;
	 * </pre>
	 */
	@Id(autoIncrement = false)
	int id;
	/**
	 * 配置自定义的列名
	 */
	@Column(column = "name")
	String kkkk;
	/**
	 * 检查check条件
	 */
	// sql怎么写check 表达式，这边就怎么写
	@Check(value = "age>0")
	int age;
	/**
	 * 该字段的值唯一
	 */
	@Unique
	int cardId;
	/**
	 * 不能为空
	 */
	@NotNull
	String ddd;
	// @Transient，transient ，static声明的字段不会在数据库表中生成列
	@Transient
	private String MMMMMM;
	private transient String UUUUUUU;
	private static String LLLLL;

	public QQQQ(int id, String kkkk, int age, int cardId, String ddd) {
		super();
		this.id = id;
		this.kkkk = kkkk;
		this.age = age;
		this.cardId = cardId;
		this.ddd = ddd;
	}

}
