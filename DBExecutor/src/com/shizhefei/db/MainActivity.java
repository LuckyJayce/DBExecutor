package com.shizhefei.db;

import android.app.Activity;
import android.os.Bundle;

import com.shizhefei.db.annotations.Id;
import com.shizhefei.db.sql.Sql;
import com.shizhefei.db.sql.SqlFactory;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		System.out.println(SqlFactory.find(Person.class));
		System.out.println(SqlFactory.find(Person.class, "age"));
		System.out.println(SqlFactory.find(Person.class, "age", "name"));
		System.out.println(SqlFactory.find(Person.class, "age", "name", "id"));

		System.out.println(SqlFactory.find(Person.class).where("age =? and ( name = ? or name = ? )", new Object[] { 1, "zsy", "uu" }));

		Sql sql = SqlFactory.find(Person.class).where("age", "=", "1").orderBy("age", true);
		System.out.println(sql);

		sql = SqlFactory.update(Person.class, new String[] { "age" }, new Object[] { 1 });
		System.out.println(sql);

		Person person = new Person("zzz", "1", 2);
		try {
			sql = SqlFactory.update(person, "age", "name");
			System.out.println(sql);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}

class Person {
	String name;
	@Id
	String id;
	int age;

	public Person(String name, String id, int age) {
		super();
		this.name = name;
		this.id = id;
		this.age = age;
	}

	public Person() {
		super();
	}

}
