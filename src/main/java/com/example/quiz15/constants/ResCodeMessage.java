package com.example.quiz15.constants;

public enum ResCodeMessage {
	SUCCESS(200, "Successful!"), //
	PASSWORD_ERROR(400, "Password Error!"), //
	EMAIL_EXISTS(400, "Email Exists!"), //
	ADD_INFO_ERROR(400,"Add Info Error!"), //
	PASSWORD_MISMATCH(400,"Password Mismatch!"), //
	NOT_FOUND(404,"NOT FOUND."), //
	QUIZ_CREATE_ERROR(400,"Quiz Ccreate Error!"),//
	DATE_FORMATE_ERROR(400,"Date Formate Error!"),//
	QUIZ_UPDATE_FAILED(400,"Quiz Update Failed!"),
	QUESTION_TYPE_ERROR(400,"Question Type Error!"),//
	OPTIONS_INSUFFICIENT(400,"Options Insufficient!"),//
	TEXT_HAS_OPTIONS_ERROR(400,"Text Has Options Error!"),//
	OPTIONS_TRANSFER_ERROR(400,"Options Transfer Error!"),
	QUIZ_ID_ERROR(400,"Quiz ID Error!");

	private int statuscode;

	private String massage;

	// Enum 沒有預設的建構方法
	// 所以帶有參數的建構方法一定要有
	private ResCodeMessage(int statuscode, String massage) {
		this.statuscode = statuscode;
		this.massage = massage;
	}

	public int getStatuscode() {
		return statuscode;
	}

	public String getMassage() {
		return massage;
	}

}
