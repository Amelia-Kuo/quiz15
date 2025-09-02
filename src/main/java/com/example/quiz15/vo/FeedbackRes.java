package com.example.quiz15.vo;

import java.util.List;

public class FeedbackRes extends BasicRes {

	private String title;

	private String description;

	private String userName;

	private String phone;

	private String email;

	private int age;

	private List<QuestionAnswerVo> questionAnswerVoList;

	public FeedbackRes() {
		super();

	}

	public FeedbackRes(int statuscode, String massage) {
		super(statuscode, massage);

	}

	public FeedbackRes(int statuscode, String massage, String title, String description, String userName, String phone,
			String email, int age, List<QuestionAnswerVo> questionAnswerVoList) {
		super(statuscode, massage);
		this.title = title;
		this.description = description;
		this.userName = userName;
		this.phone = phone;
		this.email = email;
		this.age = age;
		this.questionAnswerVoList = questionAnswerVoList;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public List<QuestionAnswerVo> getQuestionAnswerVoList() {
		return questionAnswerVoList;
	}

	public void setQuestionAnswerVoList(List<QuestionAnswerVo> questionAnswerVoList) {
		this.questionAnswerVoList = questionAnswerVoList;
	}

}
