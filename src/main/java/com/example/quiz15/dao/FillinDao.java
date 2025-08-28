package com.example.quiz15.dao;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.quiz15.entity.Fillin;
import com.example.quiz15.entity.FillinId;

import jakarta.transaction.Transactional;

@Repository
public interface FillinDao extends JpaRepository<Fillin, FillinId>{
	

	@Transactional
	@Modifying
	@Query(value = "insert into fillin (quiz_id, question_id, email, answer, fillin_date) values (?1, ?2, ?3, ?4, ?5)",nativeQuery = true)
	public void insert(int quizId, int questionId, String email, String answer, LocalDateTime now);
}
