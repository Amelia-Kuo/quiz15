package com.example.quiz15.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.quiz15.constants.QuestionType;
import com.example.quiz15.constants.ResCodeMessage;
import com.example.quiz15.dao.QuestionDao;
import com.example.quiz15.dao.QuizDao;
import com.example.quiz15.entity.Question;
import com.example.quiz15.entity.Quiz;
import com.example.quiz15.service.ifs.QuizService;
import com.example.quiz15.vo.BasicRes;
import com.example.quiz15.vo.QuestionVo;
import com.example.quiz15.vo.QuestionsRes;
import com.example.quiz15.vo.QuizCreateReq;
import com.example.quiz15.vo.QuizUpdateReq;
import com.example.quiz15.vo.SearchReq;
import com.example.quiz15.vo.SearchRes;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@Service
public class QuizServiceImpl implements QuizService {

	// 提供 類別(或 JSON 格式)與 物件 之間的轉換
	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private QuizDao quizDao;

	@Autowired
	private QuestionDao questionDao;

	/**
	 * @throws Exception
	 * @Transactional<br>
	 *                    執行過程有錯誤時,整筆資料都會失敗(無法寫入資料庫)<br>
	 *                    1.當一個方法中執行多個 Dao 時(跨表或單一張表寫入多筆),所有資料應該都要算是同一次交易<br>
	 *                    執行過程有錯誤時,整筆資料都會失敗(無法寫入資料庫)<br>
	 *                    2. @Transactional
	 *                    預設有效回朔異常是RuntimeException的這個子類別,若發生異常不再此類別中則資料無法回朔,<br>
	 *                    因此如若要在任一異常產生時都資料都能回朔,則需要將有效範圍提升到Exception的父類別
	 */
	@Transactional(rollbackOn = Exception.class)
	@Override
	public BasicRes create(QuizCreateReq quizCreateReq) throws Exception {
		// 參數檢查已經透過 @Valid 驗證
		// 新增問卷
		try {
			// 使用排除法,檢查日期
			BasicRes checkRes = checkDate(quizCreateReq.getStartDate(), quizCreateReq.getEndDate());
			if (checkRes != null) {
				return checkRes;
			}
			// 新增問卷
			quizDao.insert(quizCreateReq.getTitle(), quizCreateReq.getDescription(), quizCreateReq.getStartDate(), //
					quizCreateReq.getEndDate(), quizCreateReq.isPublished());
			// 新增問卷後要取得問卷流水號
			// 雖然因為 @Transactional 尚未將資料提交(commit)進資料庫，但實際上SQL語法已經執行完畢，
			// 依然可以取得對應的值
			int quizId = quizDao.getMaxQuizId();
			// 新增問題
			// 取出所有問題
			List<QuestionVo> questionVoList = quizCreateReq.getQuestionList();

			for (QuestionVo vo : questionVoList) {
				// 檢查題目類型和選項
				checkRes = checkQuestionType(vo);
				// 呼叫方法 checkQuestionType 得到 Res 是 null 表示檢查沒有錯誤,
				// 方法中檢查全部都沒問題回傳null
				if (checkRes != null) {
					// return checkRes;
					throw new Exception(checkRes.getMassage());
				}
				// My SQL 沒有 List 的資料格式,需要把 options 資料格式 從 List<String> 轉成 String
				List<String> optionsList = vo.getOptions();
				String str = mapper.writeValueAsString(optionsList);
				// 記得設定QuizId
				questionDao.insert(quizId, vo.getQuestionId(), vo.getQuestion(), vo.getType(), vo.isRequired(), str);
			}
			return new BasicRes(//
					ResCodeMessage.SUCCESS.getStatuscode(), //
					ResCodeMessage.SUCCESS.getMassage());
		} catch (Exception e) {
			// 不能 return BasicRes 而是要將發生的異常拋出去，這樣 @Transaction 才會生效
			throw e;
		}

	}

	// GPT版
	private BasicRes checkQuestionType(QuestionVo vo) {
		String type = vo.getType();

		if (type == null) {
			return new BasicRes(ResCodeMessage.QUESTION_TYPE_ERROR.getStatuscode(),
					ResCodeMessage.QUESTION_TYPE_ERROR.getMassage());
		}
		// 單選或多選題
		if (type.equalsIgnoreCase(QuestionType.SINGLE_CHOICE.getType())
				|| type.equalsIgnoreCase(QuestionType.MULTI_CHOICE.getType())) {

			if (vo.getOptions() == null || vo.getOptions().size() < 2) {
				return new BasicRes(ResCodeMessage.OPTIONS_INSUFFICIENT.getStatuscode(),
						ResCodeMessage.OPTIONS_INSUFFICIENT.getMassage());
			}

		}
		// 簡答題
		if (type.equalsIgnoreCase(QuestionType.TEXT_QUESTION.getType())) {
			if (vo.getOptions() != null && vo.getOptions().size() > 0) {
				return new BasicRes(ResCodeMessage.TEXT_HAS_OPTIONS_ERROR.getStatuscode(),
						ResCodeMessage.TEXT_HAS_OPTIONS_ERROR.getMassage());
			}
		}
		// 都正確就回 null
		return null;
	}

	private BasicRes checkDate(LocalDateTime startDate, LocalDateTime endDate) {
		// 1.開始日期不能比結束時間晚 2.開始時間不能比創建時間早
		// 判斷式:假設 開始日期比結束時間晚 或者 開始日期比創建時間早 => 回覆錯誤訊息
		// LocalDateTime.now() => 取得當前日期
		if (startDate.isAfter(startDate) || startDate.isBefore(startDate)) {
			return new BasicRes(//
					ResCodeMessage.DATE_FORMATE_ERROR.getStatuscode(), //
					ResCodeMessage.DATE_FORMATE_ERROR.getMassage());
		}
		return null;
//		return null => 檢查沒有問題回傳 null 等同於 檢查沒有問題回傳下面成功的BasicRes
//		return new BasicRes(//
//				ResCodeMessage.SUCCESS.getStatuscode(), //
//				ResCodeMessage.SUCCESS.getMassage());
	};

// 修改問卷
	@Transactional(rollbackOn = Exception.class)
	@Override
	public BasicRes update(QuizUpdateReq quizUpdateReq) throws Exception {
		// 參數檢查已經透過 @Valid 驗證

		// 更新是對已存在的問卷進行修改
		try {
			// 1. 檢查QuizId是否存在
			int count = quizDao.getCountByQuizId(quizUpdateReq.getQuizId());
			int quizId = quizUpdateReq.getQuizId();
			if (count != 1) {
				return new BasicRes(ResCodeMessage.NOT_FOUND.getStatuscode(), //
						ResCodeMessage.NOT_FOUND.getMassage());
			}
			// 2. 檢查日期
			BasicRes checkRes = checkDate(quizUpdateReq.getStartDate(), quizUpdateReq.getEndDate());
			if (checkRes != null) {
				return checkRes;
			}
			// 3. 更新問卷
			int updateRes = quizDao.update(quizId, quizUpdateReq.getTitle(), quizUpdateReq.getDescription(), //
					quizUpdateReq.getStartDate(), quizUpdateReq.getEndDate(), quizUpdateReq.isPublished());
			if (updateRes != 1) {
				// 表示資料沒有更新成功
				return new BasicRes(ResCodeMessage.QUIZ_UPDATE_FAILED.getStatuscode(), //
						ResCodeMessage.QUIZ_UPDATE_FAILED.getMassage());
			}
			// 4. 刪除同一張問卷的所有問題
			questionDao.deleteByQuizID(quizId);
			// 5. 檢查問題
			List<QuestionVo> questionVoList = quizUpdateReq.getQuestionList();
			for (QuestionVo vo : questionVoList) {
				checkRes = checkQuestionType(vo);
				// 方法中檢查全部都沒問題回傳null
				if (checkRes != null) {
					throw new Exception(checkRes.getMassage());
				} // My SQL 沒有 List 的資料格式,需要把 options 資料格式 從 List<String> 轉成 String
				List<String> optionsList = vo.getOptions();
				String str = mapper.writeValueAsString(optionsList);
				// 記得設定QuizId
				questionDao.insert(quizId, vo.getQuestionId(), vo.getQuestion(), vo.getType(), vo.isRequired(), str);

			}
			return new BasicRes(//
					ResCodeMessage.SUCCESS.getStatuscode(), //
					ResCodeMessage.SUCCESS.getMassage());
		} catch (Exception e) {
			// 不能 return BasicRes 而是要將發生的異常拋出去，這樣 @Transaction 才會生效
			throw e;
		}
	}

	@Override
	public SearchRes getAllQuizs() {
		List<Quiz> list = quizDao.getAll();
		return new SearchRes(ResCodeMessage.SUCCESS.getStatuscode(), //
				ResCodeMessage.SUCCESS.getMassage(), list);
	}

	@Override
	public QuestionsRes getQuizsByQuizId(int quizId) {
		if (quizId <= 1) {
			return new QuestionsRes(ResCodeMessage.QUIZ_ID_ERROR.getStatuscode(), //
					ResCodeMessage.QUIZ_ID_ERROR.getMassage());
		}

		List<QuestionVo> questionVoList = new ArrayList<>();
		List<Question> list = questionDao.getQuestionsByQuizId(quizId);
		// 把選項的資料型態從 String 轉換成 List<String>
		for (Question item : list) {
			String str = item.getOptions();
			try {
				List<String> optionsList = mapper.readValue(str, new TypeReference<>() {
				});
				// 將從DB取得的每一筆資料(Question item) 的每個欄位值放到 QuestionVo 中，以便返回給使用者
				// Question 和 QuestionsVo 差別在於選項的資料型態
				QuestionVo vo = new QuestionVo(item.getQuizId(), item.getQuestionId(), item.getQuestion(),
						item.getType(), item.isRequired(), optionsList);
				// 把每個 vo 放到 questionVoList 當中
				questionVoList.add(vo);
			} catch (Exception e) {
				// 這邊不寫 throw e 是因為次方法中沒有使用 @Transactional，不影響返回結果
				return new QuestionsRes(ResCodeMessage.OPTIONS_TRANSFER_ERROR.getStatuscode(), //
						ResCodeMessage.OPTIONS_TRANSFER_ERROR.getMassage());
			}
		}
		return new QuestionsRes(ResCodeMessage.OPTIONS_TRANSFER_ERROR.getStatuscode(), //
				ResCodeMessage.OPTIONS_TRANSFER_ERROR.getMassage(), questionVoList);
	}

	@Override
	public SearchRes Search(SearchReq searchReq) {
		// 轉換 searchReq 的值
		// 若 quizName 是 null ,轉成空字串
		String quizName = searchReq.getQuizName();
		if (quizName == null) {
			quizName = "";
		} else { // else是多餘的不需要寫,可以轉換成下列的三元運算子
			quizName = quizName;
		}

//		// 三元運算子
//		// 格式: 變數名稱 = 條件判斷式 ? 判斷結果為true時賦予的值 : 判斷結果為false時賦予的值
//		quizName = quizName == null ? "" : quizName;
//		// 上面if else 條件式可轉換成一行
//		String quizName = searchReq.getQuizName() == null ? "" : searchReq.getQuizName();
		// =============================================================================//
		// 轉換開始時間 => 若沒有開始日期就給定一個最早的時間
		LocalDateTime startDate = searchReq.getStartDate() == null ? LocalDateTime.of(1970, 1, 1, 0, 0)
				: searchReq.getStartDate();
		// 轉換結束時間 => 若沒有結束日期就給定一個最晚的時間
		LocalDateTime endDate = searchReq.getStartDate() == null ? LocalDateTime.of(9999, 12, 31, 23, 59)
				: searchReq.getStartDate();
		List<Quiz> list = quizDao.getAll(quizName, startDate, endDate);
		return new SearchRes(ResCodeMessage.SUCCESS.getStatuscode(), //
					ResCodeMessage.SUCCESS.getMassage(),list);
	}

}
