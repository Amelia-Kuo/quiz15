package com.example.quiz15.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.quiz15.constants.QuestionType;
import com.example.quiz15.constants.ResCodeMessage;
import com.example.quiz15.dao.FillinDao;
import com.example.quiz15.dao.QuestionDao;
import com.example.quiz15.dao.QuizDao;
import com.example.quiz15.entity.Question;
import com.example.quiz15.entity.Quiz;
import com.example.quiz15.service.ifs.QuizService;
import com.example.quiz15.vo.BasicRes;
import com.example.quiz15.vo.FillinReq;
import com.example.quiz15.vo.QuestionAnswerVo;
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
	
	@Autowired
	private FillinDao fillinDao;

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
	}

	private BasicRes checkStatus(LocalDateTime startDate, boolean isPublished) {
		// 問卷允許修改的狀態 1.尚未發佈 2.已發佈+尚未開始
		if (!isPublished || startDate.isAfter(LocalDateTime.now())) {
			// 返回成功表示問卷允許被修改
			return new BasicRes(ResCodeMessage.SUCCESS.getStatuscode(), //
					ResCodeMessage.SUCCESS.getMassage());
		}
		return new BasicRes(ResCodeMessage.QUIZ_CANNOT_BE_EDITED.getStatuscode(), //
				ResCodeMessage.QUIZ_CANNOT_BE_EDITED.getMassage());
	}

// 修改問卷
	@Transactional(rollbackOn = Exception.class)
	@Override
	public BasicRes update(QuizUpdateReq quizUpdateReq) throws Exception {
		// 參數檢查已經透過 @Valid 驗證

		// 更新是對已存在的問卷進行修改
		try {
			// 1. 檢查QuizId是否存在
			// int count = quizDao.getCountByQuizId(quizUpdateReq.getQuizId());
			int quizId = quizUpdateReq.getQuizId();
			Quiz quiz = quizDao.getById(quizId);
			if (quiz == null) {
				return new BasicRes(ResCodeMessage.NOT_FOUND.getStatuscode(), //
						ResCodeMessage.NOT_FOUND.getMassage());
			}
			// 2. 檢查日期
			BasicRes checkRes = checkDate(quizUpdateReq.getStartDate(), quizUpdateReq.getEndDate());
			if (checkRes != null) {
				return checkRes;
			}
			// 3. 檢查問卷狀態是否可以被更新
			checkRes = checkStatus(quizUpdateReq.getStartDate(), quizUpdateReq.isPublished());
			if (checkRes.getStatuscode() != 200) {
				return checkRes;
			}
			// 4. 更新問卷
			int updateRes = quizDao.update(quizId, quizUpdateReq.getTitle(), quizUpdateReq.getDescription(), //
					quizUpdateReq.getStartDate(), quizUpdateReq.getEndDate(), quizUpdateReq.isPublished());
			if (updateRes != 1) {
				// 表示資料沒有更新成功
				return new BasicRes(ResCodeMessage.QUIZ_UPDATE_FAILED.getStatuscode(), //
						ResCodeMessage.QUIZ_UPDATE_FAILED.getMassage());
			}
			// 5. 刪除同一張問卷的所有問題
			questionDao.deleteByQuizID(quizId);
			// 6. 檢查問題
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
	public SearchRes search(SearchReq searchReq) {
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
		List<Quiz> list = new ArrayList<>();
		if (searchReq.isPublished()) {
			list = quizDao.getAllPublished(quizName, startDate, endDate);
		} else {
			list = quizDao.getAll(quizName, startDate, endDate);
		}
		return new SearchRes(ResCodeMessage.SUCCESS.getStatuscode(), //
				ResCodeMessage.SUCCESS.getMassage(), list);
	}

	@Transactional(rollbackOn = Exception.class)
	@Override
	public BasicRes delete(int quizId) throws Exception {
		if (quizId <= 0) {
			return new BasicRes(ResCodeMessage.QUIZ_ID_ERROR.getStatuscode(),
					ResCodeMessage.QUIZ_ID_ERROR.getMassage());
		}
		Quiz quiz = quizDao.getById(quizId);
		if (quiz == null) { // 要判斷是否為null,若不判斷且取得的值是null時,後續使用方法會報錯
			return new BasicRes(ResCodeMessage.NOT_FOUND.getStatuscode(), ResCodeMessage.NOT_FOUND.getMassage());
		}
		// 3. 檢查問卷狀態是否可以被更新
		BasicRes checkRes = checkStatus(quiz.getStartDate(), quiz.isPublished());
		if (checkRes.getStatuscode() != 200) {
			return checkRes;
		}
		try {
			quizDao.deleteById(quizId);
			questionDao.deleteByQuizID(quizId);
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

	@Override
	public BasicRes fillin(FillinReq fillinReq) throws Exception {
		// 檢查填寫的問卷(quiz)
		// 檢查 1.是否已發佈 2.當下填寫的日期是否超過開放時間(當日是否介於開始與結束時間之間)
		int count = quizDao.selectCountById(fillinReq.getQuizId(), LocalDateTime.now());
		if (count != 1) {
			return new BasicRes(ResCodeMessage.QUIZ_ID_ERROR.getStatuscode(), //
					ResCodeMessage.QUIZ_ID_ERROR.getMassage());
		}
		// 檢查題目
		// 檢查 1.必填不得為空白 2.單選不能多個答案 3.答案與選項一致
		// 取得一張問卷的所有題目
		List<Question> questionList = questionDao.getQuestionsByQuizId(fillinReq.getQuizId());
		List<QuestionAnswerVo> QuestionAnswerVoList = fillinReq.getQuestionAnswerVoList();
		// 將問題編號和回答轉換成 Map,就是將 QuestionAnswerVo 中的屬性轉換成 Map
		Map<Integer, List<String>> answerMap = new HashMap<>();
		for (QuestionAnswerVo vo : QuestionAnswerVoList) {
			answerMap.put(vo.getQuestionId(), vo.getAnswerList());
		}
		// 檢查每一題
		for (Question question : questionList) {
			int questionId = question.getQuestionId();
			String type = question.getType();
			boolean required = question.isRequired();
			// 1.檢查必填但沒有答案 => 必填且 answerMap 的 key 中沒有 questionId
			if (required && !answerMap.containsKey(questionId)) {
				return new BasicRes(ResCodeMessage.ANSWER_REQUIRED.getStatuscode(), //
						ResCodeMessage.ANSWER_REQUIRED.getMassage());
			}
			// 2.單選但有多個答案
			if (type.equalsIgnoreCase(QuestionType.SINGLE_CHOICE.getType())) {
				List<String> answerList = answerMap.get(questionId);
				if (answerList.size() > 1) {
					return new BasicRes(ResCodeMessage.QUESTION_TYPE_IS_SINGLE.getStatuscode(), //
							ResCodeMessage.QUESTION_TYPE_IS_SINGLE.getMassage());
				}
			}
			// 簡答題沒有選項,跳過檢查3
			if(type.equalsIgnoreCase(QuestionType.TEXT_QUESTION.getType())) {
				continue;
			}
			// 3.比對該題答案要和題目選項是否一樣(答案必須包含在選項裡)
			String optionsStr = question.getOptions();
			List<String> answerList = answerMap.get(questionId);
			for (String answer : answerList) {
				// 將每個答案比對是否被包含在選項字串中
				if (!optionsStr.contains(answer)) {
					return new BasicRes(ResCodeMessage.OPTION_ANSWER_MISMATCH.getStatuscode(), //
							ResCodeMessage.OPTION_ANSWER_MISMATCH.getMassage());
				}
			}
		}
		// 存資料: 一個題目存成一筆資料
		for(QuestionAnswerVo vo : QuestionAnswerVoList) {
			// 把answerList轉成字串
			try {
				String str = mapper.writeValueAsString(vo.getAnswerList());
				fillinDao.insert(fillinReq.getQuizId(), vo.getQuestionId(), fillinReq.getEmail(), str, LocalDateTime.now());
			} catch (Exception e) {
				throw e;
			}
		}
		return new BasicRes(ResCodeMessage.SUCCESS.getStatuscode(), //
				ResCodeMessage.SUCCESS.getMassage());
	}
	
	
	
	
	

	public BasicRes fillin_Test(FillinReq fillinReq) {
		// 檢查填寫的問卷(quiz)
		// 檢查 1.是否已發佈 2.當下填寫的日期是否超過開放時間(當日是否介於開始與結束時間之間)
		int count = quizDao.selectCountById(fillinReq.getQuizId(), LocalDateTime.now());
		if (count != 1) {
			return new BasicRes(ResCodeMessage.QUIZ_ID_ERROR.getStatuscode(), //
					ResCodeMessage.QUIZ_ID_ERROR.getMassage());
		}
		// 檢查題目
		// 檢查 1.必填不得為空白 2.單選不能多個答案 3.答案與選項一致
		// 取得一張問卷的所有題目
		List<Question> questionList = questionDao.getQuestionsByQuizId(fillinReq.getQuizId());
		List<QuestionAnswerVo> QuestionAnswerVoList = fillinReq.getQuestionAnswerVoList();
		// QuestionAnswerVoList 中非必填可能沒作答,因此 size 可能比 questionList 少
		// 要知道每題是否必填及問題型態,才能拿填寫的答案比對 => 因此 questionList 要做比較的基底放在外層迴圈

		// 先把必填的questionId 放入 List中
		List<Integer> questionIdList = new ArrayList<>();
		for (Question question : questionList) {
			if (question.isRequired()) {
				questionIdList.add(question.getQuestionId());
			}

		}

		for (Question question : questionList) {
			int questionId = question.getQuestionId();
			String type = question.getType();
			boolean required = question.isRequired();
			// 該題必填 => 檢查 QuestionAnswerVoList 中有沒有該筆存在
			for (QuestionAnswerVo vo : QuestionAnswerVoList) {
				int voQuestionId = vo.getQuestionId();
				// 該題必填但題目編號不包含在questionOdList => 回傳錯誤
				if (required && !questionIdList.contains(voQuestionId)) {
					return new BasicRes(ResCodeMessage.ANSWER_REQUIRED.getStatuscode(), //
							ResCodeMessage.ANSWER_REQUIRED.getMassage());
				}

				List<String> answerList = vo.getAnswerList();
				// 檢查相同的 questionId，該題必填且空白 => 回傳錯誤
				// CollectionUtils.isEmpty 有判斷 null
				// QuestionAnswerVo 中 answerList 有給定新的值,沒有 mapping 到會是一個空的List
				if (questionId == voQuestionId && required && answerList.isEmpty()) {
					return new BasicRes(ResCodeMessage.ANSWER_REQUIRED.getStatuscode(), //
							ResCodeMessage.ANSWER_REQUIRED.getMassage());
				}
				// 檢查相同的 questionId，單選但多個答案 => 回傳錯誤
				if (questionId == voQuestionId && type == QuestionType.SINGLE_CHOICE.getType()
						&& answerList.size() > 1) {
					return new BasicRes(ResCodeMessage.QUESTION_TYPE_IS_SINGLE.getStatuscode(), //
							ResCodeMessage.QUESTION_TYPE_IS_SINGLE.getMassage());
				}
			}
		}

		return null;
	}

}
