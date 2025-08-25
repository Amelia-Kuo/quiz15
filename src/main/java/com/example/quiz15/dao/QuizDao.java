package com.example.quiz15.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.quiz15.entity.Quiz;

import jakarta.transaction.Transactional;

@Repository
public interface QuizDao extends JpaRepository<Quiz, Integer>{
	
	@Transactional
	@Modifying
	@Query(value = "insert into quiz (title, description, start_date, end_date, is_published) values (?1, ?2, ?3, ?4, ?5)",nativeQuery = true)
	public void insert(String title, String description,LocalDateTime startDate, LocalDateTime endDate, boolean published);
	
	@Query(value = "select max(id) from quiz  ",nativeQuery = true)
	public int getMaxQuizId();
	
	@Query(value = "select count(id) from quiz where id = ?1",nativeQuery = true)
	public int getCountByQuizId(int quizId);
	
	/**
	 * 回傳值的資料型態設定成 int 主要是用來判斷資料是否有正確更新成功，
	 * int = 1 表示有資料被更新；0則表示無資料被更新
	 */
	@Transactional
	@Modifying
	@Query(value = "update quiz set title = ?2, description = ?3, start_date = ?4, end_date = ?5, is_published = ?6 where id = ?1",nativeQuery = true)
	public int update(int id, String title, String description,LocalDateTime startDate, LocalDateTime endDate, boolean published);
	
	@Query(value = "select * from quiz",nativeQuery = true)
	public List<Quiz> getAll();
	
	@Query(value = "select * from quiz where title like %?1% and start_Date >= ?2 and end_Date <= ?3",nativeQuery = true)
	public List<Quiz> getAll(String title, LocalDateTime startDate, LocalDateTime endDate); 
}
