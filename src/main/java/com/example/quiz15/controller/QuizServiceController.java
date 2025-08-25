package com.example.quiz15.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.quiz15.service.ifs.QuizService;
import com.example.quiz15.vo.BasicRes;
import com.example.quiz15.vo.QuestionsRes;
import com.example.quiz15.vo.QuizCreateReq;
import com.example.quiz15.vo.QuizUpdateReq;
import com.example.quiz15.vo.SearchReq;
import com.example.quiz15.vo.SearchRes;

import jakarta.validation.Valid;

@CrossOrigin
@RestController
public class QuizServiceController {
	@Autowired
	QuizService quizService;

	@PostMapping(value = "quiz/create")
	public BasicRes create(@Valid @RequestBody QuizCreateReq quizCreateReq) throws Exception {
		return quizService.create(quizCreateReq);
	}

	@PostMapping(value = "quiz/update")
	public BasicRes update(@Valid @RequestBody QuizUpdateReq quizUpdateReq) throws Exception {
		return quizService.create(quizUpdateReq);
	}

	@GetMapping(value = "quiz/get_Quizs")
	public SearchRes getAllQuizs() {
		return quizService.getAllQuizs();
	}

	// API 的路徑: http://localhost:8080/quiz/get_Questions?quizId=1
	// ?後面的 quizId 必須要和 @RequestParam 括號中的字串一樣
	@PostMapping(value = "quiz/get_Questions")
	public QuestionsRes getQuizsByQuizId(@RequestParam("quizId") int quizId) {
		return quizService.getQuizsByQuizId(quizId);
	}

	@PostMapping(value = "quiz/search")
	public SearchRes search(@RequestBody SearchReq searchReq) {
		return quizService.search(searchReq);
	}
	
	@PostMapping(value = "quiz/delete")
	public BasicRes delete(@RequestParam("quizId") int quizId) throws Exception{
		return quizService.delete(quizId);
	}
}
