package com.example.quiz15.vo;

public class BasicRes {

	private int statuscode;

	private String massage;

	public BasicRes() {
		super();
	}

	public BasicRes(int statuscode, String massage) {
		super();
		this.statuscode = statuscode;
		this.massage = massage;
	}

	public int getStatuscode() {
		return statuscode;
	}

	public void setStatuscode(int statuscode) {
		this.statuscode = statuscode;
	}

	public String getMassage() {
		return massage;
	}

	public void setMassage(String massage) {
		this.massage = massage;
	}

}
