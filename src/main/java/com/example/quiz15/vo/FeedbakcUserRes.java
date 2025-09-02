package com.example.quiz15.vo;

import java.util.List;

public class FeedbakcUserRes extends BasicRes {

	private int quizId;

	// 單張問卷有多人填寫
	private List<UserVo> userVoList;

	public FeedbakcUserRes() {
		super();
	}

	public FeedbakcUserRes(int statuscode, String massage) {
		super(statuscode, massage);
	}

	public FeedbakcUserRes(int statuscode, String massage, int quizId, List<UserVo> userVoList) {
		super(statuscode, massage);
		this.quizId = quizId;
		this.userVoList = userVoList;
	}

	public int getQuizId() {
		return quizId;
	}

	public void setQuizId(int quizId) {
		this.quizId = quizId;
	}

	public List<UserVo> getUserVoList() {
		return userVoList;
	}

	public void setUserVoList(List<UserVo> userVoList) {
		this.userVoList = userVoList;
	}

}
