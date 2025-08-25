package com.example.quiz15.vo;

import java.util.List;

import com.example.quiz15.entity.Quiz;

public class SearchRes extends BasicRes{
	
	private List<Quiz> quizList;

	public SearchRes() {
		super();
	}

	public SearchRes(int statuscode, String massage) {
		super(statuscode, massage);
	}

	public SearchRes(int statuscode, String massage, List<Quiz> quizList) {
		super(statuscode, massage);
		this.quizList = quizList;
	}

	public List<Quiz> getQuizList() {
		return quizList;
	}

	public void setQuizList(List<Quiz> quizList) {
		this.quizList = quizList;
	}
	
	
}
