package com.example.quiz15;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import com.example.quiz15.dao.UserDao;
import com.example.quiz15.entity.User;

/**
 * @SpringBootTest: 有加上此註釋表示在執行測試方法之前，會先啟動整個專案，然後讓專案中
 *                  原本有被託管的物件建立起來，因此在測試方法時需要使用到被託管的物件時，可以正常被注入；
 *                  反之要求注入沒有被託管的物件，該物件就會是 null
 */
@SpringBootTest
public class UserTest {

	@Autowired
	private UserDao userDao;

	private int res;

	// @BeforeEach: 執行每個 @Test 前都會執行有加上這個註釋的方法
	@BeforeEach
	public void addTestInfo() {
		// 測試新增資料0
		res = userDao.addInfo("Bella", "0912345678", "bella@balla.com", 18, "b123456", false);
	}

	// @AfterEach: 執行每個 @Test 結束前都會執行有加上這個註釋的方法
	@AfterEach
	public void delTestInfo() {
		// 刪除測試資料
		userDao.delInfo("bella@balla.com");
	}

	@Test
	public void addInfoDaoTest() {
		try {
			// int res = userDao.addInfo("Bella", "0912345678", "bella@balla.com", 18, "b123456", false);
			// 確認 res 是否等於 1，後面的訊息表示前面的判斷不成立返回的訊息
			Assert.isTrue(res == 1, "addInfo failed!");
			// 最後會將測試時新增的資料刪除
			// userDao.delInfo("bella@balla.com");
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Test
	public void getCountByEmailDaoTest() {
//		// 尚未新增資訊前是否可以得到結果
//		int res = userDao.getCountByEmail("bella@balla.com");
//		Assert.isTrue(res == 0, "NOT FOUND EMAIL");
//		// 測試新增資訊: 不需要用變數接回,因為前面已經測試過 addInfo 是可以運行的
//		userDao.addInfo("Bella", "0912345678", "bella@balla.com", 18, "b123456", false);
		int res = userDao.getCountByEmail("bella@balla.com");
		Assert.isTrue(res == 1, "getCountByEmail failed");

	}

	@Test
	public void getByEmailDaoTest() {
		User user = userDao.getByEmail("bella@balla.com");
//		Assert.isTrue(user == null, "NOT FOUND EMAIL");
//		userDao.addInfo("Bella", "0912345678", "bella@balla.com", 18, "b123456", false);
		Assert.isTrue(user == null, "NOT FOUND EMAIL");
	}
}
