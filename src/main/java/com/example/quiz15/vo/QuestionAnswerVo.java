package com.example.quiz15.vo;

import java.util.ArrayList;
import java.util.List;

import com.example.quiz15.constants.ConstantsMessage;

import jakarta.validation.constraints.Min;

// 此 class 將問題編號與作答綑綁
public class QuestionAnswerVo {

	@Min(value = 1, message = ConstantsMessage.QUESTION_ID_ERROR)
	private int questionId;

	// 給定預設值將 answerList 的預設值從 null 變成 空的 List
	private List<String> answerList = new ArrayList<>();

	public int getQuestionId() {
		return questionId;
	}

	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}

	public List<String> getAnswerList() {
		return answerList;
	}

	public void setAnswerList(List<String> answerList) {
		this.answerList = answerList;
	}
	
	
}
