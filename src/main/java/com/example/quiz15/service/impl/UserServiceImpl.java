package com.example.quiz15.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.quiz15.constants.ResCodeMessage;
import com.example.quiz15.dao.UserDao;
import com.example.quiz15.entity.User;
import com.example.quiz15.service.ifs.UserService;
import com.example.quiz15.vo.AddInfoReq;
import com.example.quiz15.vo.BasicRes;
import com.example.quiz15.vo.LoginReq;

@Service
public class UserServiceImpl implements UserService {

	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	
	@Autowired
	private UserDao userDao;
	
	@Override
	public BasicRes addInfo(AddInfoReq addInfoReq) {
		// req的參數檢查已經在 User 類別中透過 Valid 驗證
		// 1.檢查帳號是否已存在
		int count = userDao.getCountByEmail(addInfoReq.getEmail());
		// email是 PK值 不能重複,因此回傳值若為 0,表示這個帳號不存在;反之若 count == 1 即表示帳號存在
		if (count == 1) {
			return new BasicRes(//
					ResCodeMessage.EMAIL_EXISTS.getStatuscode(),//
					ResCodeMessage.EMAIL_EXISTS.getMassage());
					
		}
		// 2.新增資訊
		try {
			userDao.addInfo(addInfoReq.getName(), addInfoReq.getPhone(), addInfoReq.getEmail(), addInfoReq.getAge()//
					, encoder.encode(addInfoReq.getPassword()),addInfoReq.isAdmin());
			return new BasicRes(//
					ResCodeMessage.SUCCESS.getStatuscode(),//
					ResCodeMessage.SUCCESS.getMassage());
		}catch(Exception e) {
			return new BasicRes(//
					ResCodeMessage.ADD_INFO_ERROR.getStatuscode(),//
					ResCodeMessage.ADD_INFO_ERROR.getMassage());
		}
		
	}

	@Override
	public BasicRes login(LoginReq loginReq) {
		// 確認Email是否存在資料庫
		User user = userDao.getByEmail(loginReq.getEmail());
		// 透過 email 取得一筆資料, email 不存在會得到 null
		if (user == null) {
			return new BasicRes(//
					ResCodeMessage.NOT_FOUND.getStatuscode(), //
					ResCodeMessage.NOT_FOUND.getMassage());
		}
		// 比對密碼
		// if條件式前有驚嘆好,等同於encoder.matches(loginreq.getPassword(), user.getPassword()) == false
		if (!encoder.matches(loginReq.getPassword(), user.getPassword())) {
			return new BasicRes(//
					ResCodeMessage.PASSWORD_MISMATCH.getStatuscode(), //
					ResCodeMessage.PASSWORD_MISMATCH.getMassage());
		}
		return new BasicRes(//
				ResCodeMessage.SUCCESS.getStatuscode(), //
				ResCodeMessage.SUCCESS.getMassage());
	
	}

}
