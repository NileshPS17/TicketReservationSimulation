package com.alphaworks.wrappers;

public class Station {
	private int ID;
	private String code, name;
	public Station(int iD, String code, String name) {
		super();
		ID = iD;
		this.code = code;
		this.name = name;
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return code + " - " + name;
	}
	
	@Override
	public boolean equals(Object arg) {
		if ( arg!= null && arg instanceof Station) {
			return ((Station)arg).getID() == this.ID;
		}
		
		return false;
	}
	
}
