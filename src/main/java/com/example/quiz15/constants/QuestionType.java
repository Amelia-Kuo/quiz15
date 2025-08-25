package com.example.quiz15.constants;

public enum QuestionType {
	SINGLE_CHOICE("S"),//
	MULTI_CHOICE("M"),
	TEXT_QUESTION("T");
	

	private String type;

	private QuestionType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
