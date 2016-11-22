package com.alphaworks.wrappers;

/**
 * Wrapper class for holding the details of a traveller.
 * @author nilesh
 *
 */
public class Person {
	private String Id, name;
	private int age;
	private int seatNo;
		
	public Person(String id, String name, int age, int seatNo) {
		super();
		if( age <= 0 || seatNo <= 0)
			throw new IllegalArgumentException("Invalid details![ age | seatNo ]");
		Id = id;
		this.name = name;
		this.age = age;
		this.seatNo = seatNo;
	}

	public Person(String id, String name, int age ) {
		this.Id = id;
		this.name = name;
		this.age = age;
	}
	

	public Person(String id, String name) {
		this.Id = id;
		this.name = name;
	}

	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public int getSeatNo() {
		return seatNo;
	}
	public void setSeatNo(int seatNo) {
		this.seatNo = seatNo;
	}
	
	
}
