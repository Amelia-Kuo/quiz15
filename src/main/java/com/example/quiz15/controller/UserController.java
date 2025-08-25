package com.example.quiz15.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.quiz15.vo.AddInfoReq;
import com.example.quiz15.service.ifs.UserService;
import com.example.quiz15.vo.BasicRes;
import com.example.quiz15.vo.LoginReq;

import jakarta.validation.Valid;

/**
 * 雖然前端專案與後端專案都在同一裝置內,但前端呼叫後端API時,會被認為是跨域請求
 * 所以需要加上 @CrossQrigin
 */
@CrossOrigin // 可提供跨域資源共享的請求
@RestController
public class UserController {

	@Autowired
	private UserService userService;
	
	@PostMapping(value = "user/addInfo")
	public BasicRes addInfo(@Valid @RequestBody AddInfoReq addInfoReq) {
		return userService.addInfo(addInfoReq);
	}
	
	@PostMapping(value = "user/login")
	public BasicRes login(@Valid @RequestBody LoginReq loginReq) {
		return userService.login(loginReq);
		
	}
}
