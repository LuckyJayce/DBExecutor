package com.shizhefei.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.shizhefei.db.converters.ColumnConverterFactory;
import com.shizhefei.db.converters.IColumnConverter;
import com.shizhefei.db.database.DBHelper;
import com.shizhefei.db.database.NameDBHelper;
import com.shizhefei.db.exception.DbException;
import com.shizhefei.db.sql.Sql;
import com.shizhefei.db.sql.SqlFactory;
import com.shizhefei.db.sql.function.LastInsertIdFunction;
import com.shizhefei.db.table.Column;
import com.shizhefei.db.table.DbModel;
import com.shizhefei.db.table.Table;
import com.shizhefei.db.table.TableFactory;
import com.shizhefei.db.utils.LogUtils;
import com.shizhefei.db.utils.ObjectFactory;

/**
 * 数据库操作类，负责执行Sql({@link om.shizhefei.db.sql.Sql}) (线程安全)
 * 
 * @see com.shizhefei.db.sql.Sql
 * @see com.shizhefei.db.sql.SqlFactory
 * @see com.shizhefei.db.database.DBHelper
 * 
 * @author 试着飞</br> Date: 13-11-18
 */
public class DBExecutor {
	//错误的tag
	private final String errorCotent = "数据库操作失败";
	private final DBHelper dbHelper;
	private static final Map<String, DBExecutor> dbExecutors = new ConcurrentHashMap<String, DBExecutor>();
	// 创建一个锁对象
	protected final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	protected final Lock readLock = readWriteLock.readLock();
	protected final Lock writeLock = readWriteLock.writeLock();
	private final Map<Class<?>, Boolean> hasCheckTable = new ConcurrentHashMap<Class<?>, Boolean>();
	private Object hasCheckTableLock = new Object();
	public static final String DEFAULT_DBNAME = "MY_DB.db";
	public static final int DEFAULT_DBVERION = 1;

	/**
	 * 默认使用数据库的名字为{@link DBExecutor#DEFAULT_DBNAME}<br/>
	 * 默认使用数据库的版本为{@link DBExecutor#DEFAULT_DBVERION}<br/>
	 * 如果需要修改数据版本，监听数据库的变化请用{@link DBExecutor#getInstance(DBHelper)}
	 * 
	 * @param context
	 * @return
	 */
	public static DBExecutor getInstance(Context context) {
		return getInstance(new NameDBHelper(context, DEFAULT_DBNAME, DEFAULT_DBVERION));
	}

	/**
	 * 通过DBHelper获取DBExecutor实例<br>
	 * 使用者通过实现DBHelper({@link com.shizhefei.db.database.DBHelper}
	 * )，或者使用已经存在的DBHelper的子类（ {@link com.shizhefei.db.database.NameDBHelper}）
	 * 
	 * @param dbHelper
	 * @return DBExecutor
	 */
	public synchronized static DBExecutor getInstance(DBHelper dbHelper) {
		String path = dbHelper.getDatabasePath();
		DBExecutor dbExecutor = dbExecutors.get(path);
		if (dbExecutor == null) {
			dbExecutor = new DBExecutor(dbHelper);
			dbExecutors.put(path, dbExecutor);
		}
		return dbExecutor;
	}

	private DBExecutor(DBHelper dbHelper) {
		super();
		this.dbHelper = dbHelper;
	}

	/**
	 * 插入数据
	 * 
	 * @param entry
	 * @return 是否插入成功
	 */
	public boolean insert(Object entry) {
		try {
			return execute(SqlFactory.insert(entry));
		} catch (Exception e) {
			LogUtils.e(errorCotent, e);
		}
		return false;
	}

	/**
	 * 执行插入操作，并且返回插入的id
	 * 
	 * @param entry
	 * @return id
	 */
	public long insertGetId(Object entry) {
		long id = -1;
		writeLock.lock();
		try {
			if (execute(false, SqlFactory.insert(entry))) {// 执行插入语句，如果插入成功
				// 查询出最后一次插入的id
				Sql sql = SqlFactory.find(entry.getClass(), new LastInsertIdFunction("lastId"));
				List<DbModel> models = executeQueryGetDBModels(false, sql);
				if (models != null && !models.isEmpty()) {
					id = models.get(0).getLong("lastId");
				}
			}
		} catch (Exception e) {
			LogUtils.e(errorCotent, e);
		} finally {
			writeLock.unlock();
		}
		return id;
	}

	/**
	 * 插入多条数据
	 * 
	 * @param entrys
	 * @return 是否插入成功
	 */
	public boolean insertAll(List<?> entrys) {
		try {
			Sql[] sqls = new Sql[entrys.size()];
			for (int i = 0; i < sqls.length; i++) {
				sqls[i] = SqlFactory.insert(entrys.get(i));
			}
			return execute(sqls);
		} catch (Exception e) {
			LogUtils.e(errorCotent, e);
		}
		return false;
	}

	/**
	 * 根据id执行更新操作，如果之前没有这个id的记录那么就会自动执行插入操作
	 * 
	 * @param entry
	 * @return 是否插入或者更新成功
	 */
	public boolean updateOrInsertById(Object entry) {
		try {
			return execute(SqlFactory.updateOrInsertById(entry));
		} catch (Exception e) {
			LogUtils.e(errorCotent, e);
		}
		return false;
	}

	/**
	 * 通过id更新多条数据
	 * 
	 * @param entrys
	 * @return 是否更新或插入成功
	 */
	public boolean updateAllOrInsertById(List<?> entrys) {
		Sql[] sqls = new Sql[entrys.size()];
		try {
			for (int i = 0; i < sqls.length; i++) {
				sqls[i] = SqlFactory.updateOrInsertById(entrys.get(i));
			}
			return execute(sqls);
		} catch (Exception e) {
			LogUtils.e(errorCotent, e);
		}
		return false;
	}

	/**
	 * 通过id更新数据
	 * 
	 * @param entry
	 * @return 是否更新成功
	 */
	public boolean updateById(Object entry) {
		try {
			return execute(SqlFactory.updateById(entry));
		} catch (Exception e) {
			LogUtils.e(errorCotent, e);
		}
		return false;
	}

	/**
	 * 通过id更新多条数据
	 * 
	 * @param entrys
	 * @return 是否更新成功
	 */
	public boolean updateAllById(List<?> entrys) {
		Sql[] sqls = new Sql[entrys.size()];
		try {
			for (int i = 0; i < sqls.length; i++) {
				sqls[i] = SqlFactory.updateById(entrys.get(i));
			}
			return execute(sqls);
		} catch (Exception e) {
			LogUtils.e(errorCotent, e);
		}
		return false;
	}

	/**
	 * 通过id删除数据
	 * 
	 * @param tableClass
	 * @param idValues
	 * @return 是否删除成功
	 */
	public boolean deleteById(Class<?> tableClass, Object idValues) {
		try {
			Sql sql = SqlFactory.deleteById(tableClass, idValues);
			return execute(sql);
		} catch (Exception e) {
			LogUtils.e(errorCotent, e);
		}
		return false;
	}

	/**
	 * 通过id查找数据
	 * 
	 * @param <T>
	 * @param tableClass
	 * @param idValue
	 * @return 如果出现异常返回null ，正常情况返回实体对象
	 */
	public <T> T findById(Class<T> tableClass, Object idValue) {
		try {
			Sql sql = SqlFactory.findById(tableClass, idValue);
			return executeQueryGetFirstEntry(sql);
		} catch (Exception e) {
			LogUtils.e(errorCotent, e);
		}
		return null;
	}

	/**
	 * 查找出tableClass表中所有的记录
	 * 
	 * @param <T>
	 * @param tableClass
	 * @return 表中所有的记录
	 */
	public <T> List<T> findAll(Class<T> tableClass) {
		try {
			Sql sql = SqlFactory.find(tableClass);
			return executeQuery(sql);
		} catch (Exception e) {
			LogUtils.e(errorCotent, e);
		}
		return null;
	}

	/**
	 * 检查表是否存在，如果不存在就创建
	 * 
	 * @param database
	 * @param table
	 * @throws DbException
	 */
	public void createTableIfNotExist(SQLiteDatabase database, Table table) throws DbException {
		// 表是否检查过 是否存在
		if (!hasCheckTable.containsKey(table.getTableClass())) {
			synchronized (hasCheckTableLock) {
				if (!hasCheckTable.containsKey(table.getTableClass())) {
					// 如果表不存在 就创建
					if (!tableIsExist(database, table))
						database.execSQL(SqlFactory.createTable(table));
					// 能到达这步说明表创建成功了，否则在上面创建表的时候就报错了。所以设置标志为检查过，避免重复检查
					hasCheckTable.put(table.getTableClass(), Boolean.TRUE);
				}
			}
		}
	}

	/**
	 * 统计表中数据总的条数
	 * 
	 * @param tableClass
	 * @return 数据总的条数
	 */
	public long count(Class<?> tableClass) {
		Sql sql = SqlFactory.find(tableClass, "count(*) as num");
		DbModel model = executeQueryGetFirstDBModel(sql);
		if (model != null) {
			return model.getInt("num");
		}
		return 0;
	}

	/**
	 * 通过sql查询结果，从查询的结果获取到第一个DbModel返回出来
	 * 
	 * @param sql
	 * @return 返回查询的结果的第一个DbModel
	 */
	public DbModel executeQueryGetFirstDBModel(Sql sql) {
		List<DbModel> models = executeQueryGetDBModels(sql);
		if (models != null && !models.isEmpty())
			return models.get(0);
		return null;
	}

	/**
	 * 通过sql查询结果，从查询的结果获取到第一个table对应的实例
	 * 
	 * @param sql
	 * @return 返回查询的结果的第一个table对应的实例
	 */
	public <T> T executeQueryGetFirstEntry(Sql sql) {
		List<T> list = executeQuery(sql);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 检查表是否存在
	 * 
	 * @param database
	 * @param table
	 * @return 是否存在
	 * @throws DbException
	 */
	private boolean tableIsExist(SQLiteDatabase database, Table table) throws DbException {
		boolean exist = false;
		if (database.isOpen()) {
			Cursor cursor = null;
			String checkSql = SqlFactory.checkTableExist(table.getTableName());
			try {
				cursor = database.rawQuery(checkSql, null);
				if (cursor.moveToNext()) {
					int count = cursor.getInt(0);
					if (count > 0) {
						exist = true;
					}
				}
				cursor.close();
			} catch (Exception e) {
				LogUtils.e(errorCotent, e);
			}
		}
		return exist;
	}

	/**
	 * 检查表是否存在
	 * 
	 * @param tableClass
	 * @return 是否存在
	 * @throws DbException
	 */
	public boolean tableIsExist(Class<?> tableClass) {
		try {
			return tableIsExist(dbHelper.getDatabase(), TableFactory.getTable(tableClass));
		} catch (DbException e) {
			e.printStackTrace();
		}
		return false;
	}

	private String[] convertToStringValues(List<Object> binds) {
		/**
		 * 把绑定的值转化为 字符串形式的绑定的值，但是必须先进行转化器转化一下
		 */
		String[] bindArg = new String[binds.size()];
		for (int i = 0; i < bindArg.length; i++) {
			Object value = binds.get(i);
			// 得到对应类型的转化器
			IColumnConverter converter = ColumnConverterFactory.getColumnConverter(value);
			// 从java类型的数据转化成sql保存的数据类�?
			value = converter.toSqlValue(value);
			// 转化为字符串
			String vv = String.valueOf(value);
			bindArg[i] = vv;
		}
		return bindArg;
	}

	/**
	 * 获取cursor 中查询到的值，用于 select count(*) from 查找的东西不是表的字段的情况
	 * 
	 * @param <T>
	 * @param table
	 * @param cursor
	 * @param factory
	 * @return
	 * @throws Exception
	 */
	private DbModel getDBModel(Cursor cursor) {
		DbModel result = new DbModel();
		int columnCount = cursor.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			result.add(cursor.getColumnName(i), cursor.getString(i));
		}
		return result;
	}

	/**
	 * 执行SQL
	 * 
	 * @param needLock
	 *            ，是否要加锁
	 * @param sqls
	 * @return
	 */
	private boolean execute(boolean needLock, Sql... sqls) {
		boolean isOk = false;
		if (needLock) {
			writeLock.lock();
		}
		try {
			SQLiteDatabase database = dbHelper.getDatabase();
			// 检查数据库是否打开
			if (database.isOpen()) {
				try {
					// 遍历检查表是否存在
					for (Sql sql : sqls) {
						if (sql.isCheckTableExit()) {
							createTableIfNotExist(database, sql.getTable());
						}
					}
					// 开始执行事务
					database.beginTransaction();
					// 遍历执行sql
					for (Sql sql : sqls) {
						// 创建SQLiteStatement
						SQLiteStatement statement = database.compileStatement(sql.getSqlText());
						int bindArg = 1;
						// 绑定数据到SQLiteStatement中
						for (Object value : sql.getBindValues()) {
							// 根据数据类型得到对应的转化器
							IColumnConverter converter = ColumnConverterFactory.getColumnConverter(value);
							// 从java类型的数据转化成sql保存的数据类型
							value = converter.toSqlValue(value);
							// 绑定数据到SQLiteStatement中
							converter.getDBType().bindObjectToProgram(statement, bindArg++, value);
						}
						// 执行statement
						statement.execute();
						// 关闭statement
						statement.close();
					}
					// 设置返回的结果为成功
					isOk = true;
					// 设置事务执行成功
					database.setTransactionSuccessful();
				} catch (Exception e) {
					LogUtils.e(errorCotent, e);
				} finally {
					// 结束事务
					database.endTransaction();
					// database.close();
					// 避免多个线程同时读的时候，被关闭，下一个线程就操作失败,这里不关闭，让系统自己关闭
				}
			}
		} finally {
			if (needLock) {
				writeLock.unlock();
			}
		}
		return isOk;
	}

	/**
	 * 执行更新，插入，删除的sql语句，注意里面用到了事务，如果其中一个没有执行成功，那么就会回滚。<br/>
	 * 如果不想放在同一个事务中，建议多次调用execute()执行单个sql
	 * 
	 * @param sqls
	 * @return 是否操作成功
	 */
	public boolean execute(Sql... sqls) {
		return execute(true, sqls);
	}

	/**
	 * 执行查询的sql
	 * 
	 * @param <T>
	 * @param sql
	 * @return 如果出现异常返回null
	 */
	public <T> List<T> executeQuery(Sql sql) {
		List<T> results = null;
		readLock.lock();
		try {
			SQLiteDatabase database = dbHelper.getDatabase();
			Cursor cursor = null;
			// 检查数据库是否打开
			if (database.isOpen()) {
				try {
					if (sql.isCheckTableExit()) {
						// 检查表是否存在，不存在就创建
						createTableIfNotExist(database, sql.getTable());
					}
					// 从sql中得到绑定的值
					List<Object> binds = sql.getBindValues();
					// 将绑定的值装换为字符串类型的值
					String[] bindArg = convertToStringValues(binds);
					// 执行查询操作
					cursor = database.rawQuery(sql.getSqlText(), bindArg);
					results = getEntrys(sql.getTable(), cursor);
				} catch (Exception e) {
					LogUtils.e(errorCotent, e);
				} finally {
					if (cursor != null)
						cursor.close();
					// database.close();
					// 避免多个线程同时读的时候，被关闭，下一个线程就操作失败,这里不关闭，让系统自己关闭
				}
			}
		} finally {
			readLock.unlock();
		}
		return results;
	}

	/**
	 * 从游标中返回 tableClass 对应的数据列表
	 * 
	 * @param tableClass
	 * @param cursor
	 * @return 游标中返回 tableClass 对应的数据列表
	 */
	public static <T> List<T> getEntrys(Class<T> tableClass, Cursor cursor) {
		return getEntrys(TableFactory.getTable(tableClass), cursor);
	}

	private static <T> List<T> getEntrys(Table table, Cursor cursor) {
		@SuppressWarnings("unchecked")
		Class<T> tableClass = (Class<T>) table.getTableClass();
		// 创建可以创建tableclass=实例的ObjectFactory
		ObjectFactory<T> factory = new ObjectFactory<T>(tableClass);
		// 遍历cursor，从cursor中获取查询的数据，并添加到results�?
		try {
			List<T> list = new ArrayList<T>();
			while (cursor.moveToNext()) {
				T object = getEntry(table, cursor, factory);
				list.add(object);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<DbModel> executeQueryGetDBModels(boolean needLock, Sql sql) {
		List<DbModel> results = null;
		if (needLock) {
			readLock.lock();
		}
		try {
			SQLiteDatabase database = dbHelper.getDatabase();
			Cursor cursor = null;
			// 检查数据库是否打开
			if (database.isOpen()) {
				try {
					if (sql.isCheckTableExit()) {
						// 如果表不存在就创建
						createTableIfNotExist(database, sql.getTable());
					}
					// 得到sql绑定的值
					List<Object> binds = sql.getBindValues();
					String[] bindArg = convertToStringValues(binds);
					// 执行查询操作
					cursor = database.rawQuery(sql.getSqlText(), bindArg);
					List<DbModel> list = new ArrayList<DbModel>();
					// 遍历查询的结结果
					while (cursor.moveToNext()) {
						// 创建model，并把查询的数据保存在DbModel里面
						DbModel model = getDBModel(cursor);
						list.add(model);
					}
					results = list;
				} catch (Exception e) {
					LogUtils.e(errorCotent, e);
				} finally {
					if (cursor != null)
						cursor.close();
					// database.close();
					// 避免多个线程同时读的时候，被关闭，下一个线程就操作失败,这里不关闭，让系统自己关闭
				}
			}
		} finally {
			if (needLock) {
				readLock.unlock();
			}
		}
		return results;
	}

	/**
	 * 执行查询sql，返回DbModel的集合
	 * 
	 * @param sql
	 * @return 如果出现异常，则返回null
	 */
	public List<DbModel> executeQueryGetDBModels(Sql sql) {
		return executeQueryGetDBModels(true, sql);
	}

	/**
	 * 获取cursor 中查询到的数值
	 * 
	 * @param <T>
	 * @param table
	 * @param cursor
	 * @param factory
	 * @return 实体类的数据
	 * @throws Exception
	 */
	private static <T> T getEntry(Table table, Cursor cursor, ObjectFactory<T> factory) throws Exception {
		// 创建实例
		T result = factory.newInstance();
		// 获取表中的所有字段，包括id字段
		Map<String, Column> map = table.getColumns();
		// 遍历cursor
		for (int columnIndex = 0, count = cursor.getColumnCount(); columnIndex < count; columnIndex++) {
			String columnName = cursor.getColumnName(columnIndex);
			// 通过字段名字得到对应的java类中的字段
			Column column = map.get(columnName);
			if (column != null) {
				// 得到字段的转化器
				IColumnConverter converter = column.getColumnConverter();
				// 从cursor中得到值
				Object value = converter.getDBType().getValueFromCursor(cursor, columnIndex);
				// 从sql类型的数据转化成java类保存的数据类型
				value = converter.toJavaValue(value);
				// 把value值设置在result实例里
				column.getColumnField().set(result, value);
			}
		}
		return result;
	}

	/**
	 * 删除tableClass 表中所有的记录
	 * 
	 * @param tableClass
	 * @return 是否删除成功
	 */
	public boolean deleteAll(Class<?> tableClass) {
		return execute(SqlFactory.deleteAll(tableClass));
	}

	/**
	 * 得到数据库的路径
	 * 
	 * @return 数据库的路径
	 */
	public String getDatabasePath() {
		return dbHelper.getDatabasePath();
	}

	public DBHelper getDBHelper() {
		return dbHelper;
	}

}
