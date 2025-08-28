package com.example.quiz15.vo;

import java.util.List;

import com.example.quiz15.constants.ConstantsMessage;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class FillinReq {

	@Min(value = 1, message = ConstantsMessage.QUIZ_ID_ERROR)
	private int quizId;

	@NotBlank(message = ConstantsMessage.EMAIL_ERROR)
	private String email;
	
	@Valid // 有檢查 Vo 中的 QuestionId 所以要加上 @Valid 使其生效
	private List<QuestionAnswerVo> QuestionAnswerVoList;

	public int getQuizId() {
		return quizId;
	}

	public void setQuizId(int quizId) {
		this.quizId = quizId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<QuestionAnswerVo> getQuestionAnswerVoList() {
		return QuestionAnswerVoList;
	}

	public void setQuestionAnswerVoList(List<QuestionAnswerVo> questionAnswerVoList) {
		QuestionAnswerVoList = questionAnswerVoList;
	}


}
