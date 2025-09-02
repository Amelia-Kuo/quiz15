package com.example.quiz15.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.quiz15.entity.Fillin;
import com.example.quiz15.entity.FillinId;
import com.example.quiz15.vo.QuestionAnswerDto;
import com.example.quiz15.vo.UserVo;

import jakarta.transaction.Transactional;

@Repository
public interface FillinDao extends JpaRepository<Fillin, FillinId> {

	@Transactional
	@Modifying
	@Query(value = "insert into fillin (quiz_id, question_id, email, answer, fillin_date) values (?1, ?2, ?3, ?4, ?5)", nativeQuery = true)
	public void insert(int quizId, int questionId, String email, String answer, LocalDateTime now);

	@Query(value = "select count(email) from fillin where quizId = ?1 and email = ?2", nativeQuery = true)
	public int selectCountByQuizIdAndEmail(int quizId, String Email);

	/**
	 * 1. 當 select 的欄位無法只用特定的一張表(或是裝載資料的 Entity) 來裝載資料時，nativeQuery 要變成 false </br>
	 * 2. nativeQuery = false 時，SQL語法中 <br>
	 * 2.1. 無法用 * 表示所有欄位，只能羅列全部<br>
	 * 2.1.1 select 的欄位名稱會變成各個 Entity class 中的屬性變數名稱 <br>
	 * 2.2. on 後面的欄位名稱是 Entity class 中的屬性變數名稱<br>
	 * 2.3. 表的名稱會變成 Entity class 名稱 <br>
	 * 2.4. select 後面的欄位要透過 new 建構方法的方式來塞值，UserVo 中也要有對應的建構方法<br>
	 * 2.5. UserVo 要給定完整的路徑: com.example.quiz15.vo.UserVo 3. selcet distinct 表示會把
	 * selcet 中所有的欄位值有重複的值刪除
	 */
	@Query(value = "select distinct new com.example.quiz15.vo.UserVo(U.name, U.email, F.fillinDate)"//
			+ " from User as U join Fillin as F on U.email = F.email where F.quizId = ?1")
	public List<UserVo> selcetUserVoList(int quizId);

	@Query(value = "select new com.example.quiz15.vo.QuestionAnswerDto(Qu.questionId, Qu.question, Qu.type, Qu.required, F.answer)"//
			+ " from Question as Qu left join Fillin as F on Qu.questionId = F.questionId where Qu.quizId = ?1 and F.email = ?2")
	public List<QuestionAnswerDto> selectQuestionAnswerDtoList(int quizId, String email);

//	public void selectFeedbackByQuizId(int quizId);
}
