package com.example.quiz15.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.quiz15.entity.User;

import jakarta.transaction.Transactional;

@Repository
public interface UserDao extends JpaRepository<User, String> {

	@Query(value = "select count(email) from user where email = ?1", nativeQuery = true)
	public int getCountByEmail(String email);

	@Query(value = "select * from user where email = ?1", nativeQuery = true)
	public User getByEmail(String email);

	@Transactional
	@Modifying
	@Query(value = "insert into user (name, phone, email, age, password, is_admin) values (?1, ?2, ?3, ?4, ?5, ?6)", nativeQuery = true)
	public void addInfo(String name, String phone, String email, int age, String password, boolean isAdmin);

}
