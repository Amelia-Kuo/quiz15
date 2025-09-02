package com.example.quiz15.vo;

import java.time.LocalDateTime;

public class UserVo {

	private String name;

	private String email;

	private LocalDateTime fillinDate;

	public UserVo() {
		super();
	}

	public UserVo(String name, String email, LocalDateTime fillinDate) {
		super();
		this.name = name;
		this.email = email;
		this.fillinDate = fillinDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public LocalDateTime getFillinDate() {
		return fillinDate;
	}

	public void setFillinDate(LocalDateTime fillinDate) {
		this.fillinDate = fillinDate;
	}

}
