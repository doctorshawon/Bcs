package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface McqDao {

    @Query("SELECT * FROM mcq_questions ORDER BY id DESC")
    fun getAllQuestions(): Flow<List<McqQuestion>>

    @Query("SELECT * FROM mcq_questions")
    suspend fun getAllQuestionsSync(): List<McqQuestion>

    @Query("SELECT * FROM mcq_questions WHERE subject = :subject ORDER BY id DESC")
    fun getQuestionsBySubject(subject: String): Flow<List<McqQuestion>>

    @Query("SELECT * FROM mcq_questions WHERE isUserCreated = 1 ORDER BY id DESC")
    fun getUserCreatedQuestions(): Flow<List<McqQuestion>>

    @Query("SELECT * FROM mcq_questions WHERE isBookmarked = 1 ORDER BY id DESC")
    fun getBookmarkedQuestions(): Flow<List<McqQuestion>>

    @Query("SELECT * FROM mcq_questions WHERE lastAttemptCorrect = 0 ORDER BY id DESC")
    fun getMistakeQuestions(): Flow<List<McqQuestion>>

    @Query("SELECT * FROM mcq_questions WHERE question LIKE '%' || :query || '%' OR topic LIKE '%' || :query || '%' OR bcsTag LIKE '%' || :query || '%' ORDER BY id DESC")
    fun searchQuestions(query: String): Flow<List<McqQuestion>>

    @Query("SELECT * FROM mcq_questions WHERE id = :id LIMIT 1")
    suspend fun getQuestionById(id: Long): McqQuestion?

    @Query("SELECT COUNT(*) FROM mcq_questions")
    suspend fun getQuestionCount(): Int

    @Query("SELECT COUNT(*) FROM mcq_questions WHERE isUserCreated = 1")
    fun getUserCreatedCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: McqQuestion): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<McqQuestion>)

    @Update
    suspend fun updateQuestion(question: McqQuestion)

    @Delete
    suspend fun deleteQuestion(question: McqQuestion)

    @Query("DELETE FROM mcq_questions WHERE id = :id")
    suspend fun deleteQuestionById(id: Long)

    @Query("UPDATE mcq_questions SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun setBookmark(id: Long, isBookmarked: Boolean)

    @Query("UPDATE mcq_questions SET timesAttempted = timesAttempted + 1, timesCorrect = timesCorrect + (CASE WHEN :isCorrect THEN 1 ELSE 0 END), lastAttemptCorrect = :isCorrect WHERE id = :id")
    suspend fun recordAttempt(id: Long, isCorrect: Boolean)

    // Quiz Attempts
    @Query("SELECT * FROM quiz_attempts ORDER BY timestamp DESC")
    fun getAllQuizAttempts(): Flow<List<QuizAttempt>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizAttempt(attempt: QuizAttempt): Long

    @Query("DELETE FROM quiz_attempts")
    suspend fun clearQuizAttempts()
}
