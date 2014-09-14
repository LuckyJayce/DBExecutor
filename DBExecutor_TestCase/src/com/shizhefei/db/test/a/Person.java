package com.shizhefei.db.test.a;

import java.sql.Date;

import com.shizhefei.db.annotations.Id;
import com.shizhefei.db.annotations.Table;
/**
 * 
 * @author йтве╥и
 *
 */
@Table(name = "MMMMM")
public class Person {
	@Id
	private int id;
	private String name;
	private boolean isStudent;
	private Date date;

	public Person(String name, boolean isStudent, Date date) {
		super();
		this.name = name;
		this.isStudent = isStudent;
		this.date = date;
	}

	public Person(int id, String name, boolean isStudent, Date date) {
		super();
		this.id = id;
		this.name = name;
		this.isStudent = isStudent;
		this.date = date;
	}

	public Person() {
		super();
	}

}
