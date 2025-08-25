package com.example.quiz15.vo;

import java.util.List;

public class QuestionsRes extends BasicRes {

	private List<QuestionVo> qustionVoList;

	public QuestionsRes() {
		super();

	}

	public QuestionsRes(int statuscode, String massage) {
		super(statuscode, massage);

	}

	public QuestionsRes(int statuscode, String massage, List<QuestionVo> qustionVoList) {
		super(statuscode, massage);
		this.qustionVoList = qustionVoList;
	}

	public List<QuestionVo> getQustionVoList() {
		return qustionVoList;
	}

	public void setQustionVoList(List<QuestionVo> qustionVoList) {
		this.qustionVoList = qustionVoList;
	}

}
