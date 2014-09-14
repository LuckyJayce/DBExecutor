package com.shizhefei.db.sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.shizhefei.db.converters.DBType;
import com.shizhefei.db.exception.DbException;
import com.shizhefei.db.sql.function.IFunction;
import com.shizhefei.db.table.Column;
import com.shizhefei.db.table.ColumnUtils;
import com.shizhefei.db.table.Table;
import com.shizhefei.db.table.TableFactory;

/**
 * sql工厂，有兴趣的朋友可以通过这个类研究怎么拼接sql语句的
 * 
 * @author 试着飞 </br> Date: 13-11-18
 */
public class SqlFactory {
	/**
	 * 根据id查找数据
	 * 
	 * @param tableClass
	 * @param idValue
	 * @return sql
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static Sql findById(Class<?> tableClass, Object idValue) {
		Table table = TableFactory.getTable(tableClass);
		Column id = table.getId();
		String idColumnName = id.getName();
		return find(tableClass).where(idColumnName, "=", idValue);
	}

	public static FindSql find(Class<?> tableClass) {
		return new FindSql(tableClass);
	}

	/**
	 * (tableClass)表示<br/>
	 * "select * from table";<br/>
	 * <br/>
	 * (tableClass,"age","name")表示<br/>
	 * "select age,name from table";<br/>
	 * <br/>
	 * (tableClass,"count(*) as num")表示<br/>
	 * "select count(*) as num from table"; <br/>
	 * <br>
	 * 例子 ：SqlFactory.find(Person.class, "name", "age").where("name", "=",
	 * "试着飞").or("age", "=", 1).orderBy("age", true) 表示<br>
	 * select name,age from Person where name = ? OR age = ? ORDER BY age DESC
	 * 
	 * @param tableClass
	 * @param columnNames
	 * @return FindSql
	 */
	public static FindSql find(Class<?> tableClass, String... columnNames) {
		return new FindSql(tableClass).select(columnNames);
	}

	public static FindSql find(Class<?> tableClass, IFunction... functions) {
		return new FindSql(tableClass).select(functions);
	}

	/**
	 * 通过id把entry的数据更新到数据库中
	 * 
	 * @param entry
	 * @return sql
	 * @throws Exception
	 */
	public static Sql updateById(Object entry) throws Exception {
		return updateById(entry, new String[]{});
	}

	/**
	 * 通过 id进行 更新，如果不存在这个id的记录，那么就做插入操作
	 * 
	 * @param entry
	 * @return sql
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static Sql updateOrInsertById(Object entry) throws IllegalArgumentException, IllegalAccessException {
		Table table = TableFactory.getTable(entry.getClass());
		boolean isAppendId = true;
		String sql = getInsertSql(table, isAppendId);
		List<Object> bindValues = getInsertBindValues(table, entry, isAppendId);
		// 将原本的insert into 语句替换为现在的 replace into语句，就实现了更新或插入操作
		sql = " REPLACE " + sql.substring(INSERT.length() - 1, sql.length());
		return Sql.valueOf(table, sql, bindValues);
	}

	/**
	 * 通过id字段更新数据
	 * 
	 * @param entry
	 * @param updateColumnNames
	 * @return sql
	 * @throws Exception
	 */
	public static Sql updateById(Object entry, String... updateColumnNames) throws Exception {
		Table table = TableFactory.getTable(entry.getClass());
		Column id = table.getId();
		String idColumnName = id.getName();
		Object value = id.getColumnField().get(entry);
		return update(entry, updateColumnNames).where(idColumnName, "=", value);
	}

	/**
	 * 更新数据，返回一个可以执行where操作的whereSql对象 <br>
	 * <br>
	 * 
	 * @param entry
	 *            table对应的实例，里面的字段存放着要更新的字段的值
	 * @param updateColumnNames
	 *            要更新的字段 如果为
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static WhereSql update(Object entry, String... updateColumnNames) throws IllegalArgumentException,
			IllegalAccessException {
		Table table = TableFactory.getTable(entry.getClass());
		Map<String, Column> columns;
		if (table.isAutoIncrement()) {
			columns = table.getWhitoutIdColumns();
		} else {
			columns = table.getColumns();
		}
		if (updateColumnNames == null || updateColumnNames.length == 0) {
			updateColumnNames = new String[columns.size()];
			columns.keySet().toArray(updateColumnNames);
		}
		List<Object> bindValues = new ArrayList<Object>();
		for (String updateColumn : updateColumnNames) {
			Object value = columns.get(updateColumn).getColumnField().get(entry);
			bindValues.add(value);
		}
		String sql = getFrontUpdateSql(table, updateColumnNames);
		return new WhereSql(Sql.valueOf(table, sql, bindValues));
	}

	/**
	 * 更新数据，返回一个可以执行where操作的whereSql对象 <br>
	 * <br>
	 * 例子：SqlFactory.update(Person.class, new String[] { "name", "age" }, new
	 * Object[] { "试着飞", 2 }).where( "age", "=", 1) 表示<br>
	 * UPDATE Person SET name='试着飞',age=2 where age = 1<br>
	 * 
	 * @param tableClass
	 * @param updateColumnNames
	 *            要更新的字段
	 * @param values
	 *            对应更新字段的值
	 * @return 返回一个可以执行where操作的whereSql对象
	 */
	public static WhereSql update(Class<?> tableClass, String[] updateColumnNames, Object[] updateValues) {
		Table table = TableFactory.getTable(tableClass);
		String sql = getFrontUpdateSql(table, updateColumnNames);
		return new WhereSql(Sql.valueOf(table, sql, updateValues));
	}

	/**
	 * 创建插入Sql
	 * 
	 * @param entry
	 *            table对应的实例，里面包含要插入的值
	 * @return sql
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws Exception
	 */
	public static Sql insert(Object entry) throws IllegalArgumentException, IllegalAccessException {
		Table table = TableFactory.getTable(entry.getClass());
		boolean isAppendId = !table.isAutoIncrement();
		String sql = getInsertSql(table, isAppendId);
		List<Object> bindValues = getInsertBindValues(table, entry, isAppendId);
		return Sql.valueOf(table, sql, bindValues);
	}

	public static List<Sql> insertAll(Collection<?> entrys) throws IllegalArgumentException, IllegalAccessException {
		// 一般会调用这个方法，说明还会有可能添加sql进去。一般情况下都是添加 delte语句等，所有 size+2
		List<Sql> sqls = new ArrayList<Sql>(entrys.size() + 2);
		for (Object object : entrys) {
			sqls.add(insert(object));
		}
		return sqls;
	}

	/**
	 * 创建删除Sql，根据Id进行删除
	 * 
	 * @param tableClass
	 * @param idValues
	 *            id的值
	 * @return
	 * @throws DbException
	 */
	public static Sql deleteById(Class<?> tableClass, Object idValues) throws DbException {
		Table table = TableFactory.getTable(tableClass);
		Column id = table.getId();
		return delete(tableClass).where(id.getName(), "in", idValues);
	}

	/**
	 * 创建删除Sql，根据where语句进行删除 <br>
	 * <br>
	 * 例子：SqlFactory.delete(Person.class).where("name", "=", "试着飞") 表示<br>
	 * delete from Person where name = '试着飞'<br>
	 * 
	 * @param tableClass
	 * @return
	 */
	public static WhereSql delete(Class<?> tableClass) {
		Table table = TableFactory.getTable(tableClass);
		String sql = getDeleteSql(table.getTableName());
		Sql frontSql = Sql.valueOf(table, sql);
		return new WhereSql(frontSql);
	}

	/**
	 * 删除tableClass表中的全部数据
	 * 
	 * @param tableClass
	 * @return sql
	 */
	public static Sql deleteAll(Class<?> tableClass) {
		Table table = TableFactory.getTable(tableClass);
		return Sql.valueOf(tableClass, getDeleteSql(table.getTableName()));
	}

	/**
	 * 检查表是否存在的Sql语句
	 * 
	 * @param tableName
	 * @return 检查表是否存在的Sql语句
	 */
	public static String checkTableExist(String tableName) {
		return "SELECT COUNT(*) FROM sqlite_master where type='table' and name='" + tableName + "'";
	}

	/**
	 * 删除某个表
	 * 
	 * @param tableClass
	 * @return 删除某个表的sql
	 */
	public static String dropTable(Class<?> tableClass) {
		String tableName = TableFactory.getTableName(tableClass);
		return "Drop Table " + tableName;
	}

	/**
	 * 创建表的Sql
	 * 
	 * @param table
	 * @return 创建表的Sql
	 */
	public static String createTable(Table table) {
		Column id = table.getId();
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE IF NOT EXISTS ");
		sql.append(table.getTableName());
		sql.append(" ( ");
		if (table.isAutoIncrement()) {
			sql.append("\"").append(id.getName()).append("\"  ").append("INTEGER PRIMARY KEY AUTOINCREMENT,");
		} else {
			sql.append("\"").append(id.getName()).append("\"  ").append(id.getColumnConverter().getDBType())
					.append(" PRIMARY KEY,");
		}

		Collection<Column> columns = table.getColumns().values();
		for (Column column : columns) {
			if (column == id) {
				continue;
			}
			DBType dbType = column.getColumnConverter().getDBType();
			sql.append("\"").append(column.getName()).append("\"  ");
			sql.append(dbType.name());
			if (column.getDefalutValue() != null) {
				sql.append(" defalut ");
				if (DBType.TEXT.equals(dbType)) {
					sql.append('\'').append(column.getDefalutValue()).append('\'');
				} else {
					sql.append(column.getDefalutValue());
				}
				sql.append(" ");
			}
			if (ColumnUtils.isUnique(column.getColumnField())) {
				sql.append(" UNIQUE");
			}
			if (ColumnUtils.isNotNull(column.getColumnField())) {
				sql.append(" NOT NULL");
			}
			String check = ColumnUtils.getCheck(column.getColumnField());
			if (check != null) {
				sql.append(" CHECK(").append(check).append(")");
			}
			sql.append(",");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(" )");
		return sql.toString();
	}

	/**
	 * 得到插入语句绑定的值
	 * 
	 * @param table
	 * @param entry
	 * @param isAppendId
	 *            是否添加id字段上去
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private static List<Object> getInsertBindValues(Table table, Object entry, boolean isAppendId)
			throws IllegalArgumentException, IllegalAccessException {
		Collection<Column> columns = table.getColumns().values();
		List<Object> bindValues = new ArrayList<Object>(columns.size());
		Column id = table.getId();
		for (Column column : columns) {
			if (column == id && !isAppendId)
				continue;
			bindValues.add(column.getColumnField().get(entry));
		}
		return bindValues;
	}

	/**
	 * 得到插入语句的sql
	 * 
	 * @param table
	 * @param isAppendId
	 *            是否添加id字段上去
	 * @return
	 */
	private static synchronized String getInsertSql(Table table, boolean isAppendId) {
		Class<?> c = table.getTableClass();
		String s = sqlCache.getInsertSql(c, isAppendId);
		if (s == null) {
			Collection<Column> columns = table.getColumns().values();
			List<Object> bindValues = new ArrayList<Object>(columns.size());
			StringBuilder sql = new StringBuilder();
			bindValues.clear();
			sql.append(INSERT);
			sql.append(" INTO ");
			sql.append(table.getTableName());
			sql.append(" (");
			StringBuilder values = new StringBuilder(30);
			Column id = table.getId();
			for (Column column : columns) {
				if (column == id && !isAppendId)
					continue;
				sql.append(column.getName()).append(",");
				values.append('?');
				values.append(",");
			}
			sql.deleteCharAt(sql.length() - 1);
			values.deleteCharAt(values.length() - 1);
			sql.append(')');
			sql.append(" VALUES(");
			sql.append(values);
			sql.append(");");
			s = sql.toString();
			sqlCache.putInsertSql(c, sql.toString(), isAppendId);
		}
		return s;
	}

	private static final String INSERT = "INSERT ";

	/**
	 * 获取update 语句的前面部分没有where 语句, update tableName set name=?...
	 * 
	 * @param table
	 *            更新的表
	 * @param updateColumnNames
	 *            更新的字段名
	 * @return
	 */
	private synchronized static String getFrontUpdateSql(Table table, String[] updateColumnNames) {
		Class<?> c = table.getTableClass();
		String s = sqlCache.getUpdateSql(c, updateColumnNames);
		if (s == null) {
			StringBuilder sql = new StringBuilder(40);
			sql.append(" UPDATE ");
			sql.append(table.getTableName());
			sql.append(" SET ");
			for (String columnName : updateColumnNames) {
				sql.append(columnName).append("=?,");
			}
			sql.deleteCharAt(sql.length() - 1);
			s = sql.toString();
			sqlCache.putUpdateSql(c, updateColumnNames, s);
		}
		return s;
	}

	/**
	 * 创建sql
	 * 
	 * @param tableClass
	 * @param sql
	 * @return sql
	 */
	public static Sql makeSql(Class<?> tableClass, String sql) {
		return Sql.valueOf(tableClass, sql);
	}

	/**
	 * 创建sql
	 * 
	 * @param tableClass
	 * @param sql
	 * @param bindValues
	 * @return sql
	 */
	public static Sql makeSql(Class<?> tableClass, String sql, List<Object> bindValues) {
		Table table = TableFactory.getTable(tableClass);
		return Sql.valueOf(table, sql, bindValues);
	}

	/**
	 * 创建sql
	 * 
	 * @param tableClass
	 * @param sql
	 * @param bindValues
	 * @return sql
	 */
	public static Sql makeSql(Class<?> tableClass, String sql, Object[] bindValues) {
		return Sql.valueOf(tableClass, sql, bindValues);
	}

	private static String getDeleteSql(String tableName) {
		return "delete from " + tableName;
	}

	// sql 语句的缓存，如果相同，就不用重新拼接sql语句，直接引用就好了
	// 目前只缓存 insert 和 update的语句
	private static SqlCache sqlCache = new SqlCache();

}
