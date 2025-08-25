package com.example.quiz15.service.ifs;

import com.example.quiz15.vo.BasicRes;
import com.example.quiz15.vo.QuestionsRes;
import com.example.quiz15.vo.QuizCreateReq;
import com.example.quiz15.vo.QuizUpdateReq;
import com.example.quiz15.vo.SearchReq;
import com.example.quiz15.vo.SearchRes;

public interface QuizService {
	
	public BasicRes create(QuizCreateReq quizCreateReq) throws Exception;
	
	public BasicRes update(QuizUpdateReq quizUpdateReq) throws Exception;
	
	public SearchRes getAllQuizs();
	
	public QuestionsRes getQuizsByQuizId(int quizId);
	
	public SearchRes search(SearchReq searchReq);
	
	public BasicRes delete(int quizId) throws Exception;
}
