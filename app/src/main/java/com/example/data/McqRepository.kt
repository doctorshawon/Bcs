package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class McqRepository(private val mcqDao: McqDao) {

    val allQuestions: Flow<List<McqQuestion>> = mcqDao.getAllQuestions()
    val userCreatedQuestions: Flow<List<McqQuestion>> = mcqDao.getUserCreatedQuestions()
    val bookmarkedQuestions: Flow<List<McqQuestion>> = mcqDao.getBookmarkedQuestions()
    val mistakeQuestions: Flow<List<McqQuestion>> = mcqDao.getMistakeQuestions()
    val userCreatedCount: Flow<Int> = mcqDao.getUserCreatedCount()
    val quizAttempts: Flow<List<QuizAttempt>> = mcqDao.getAllQuizAttempts()

    fun getQuestionsBySubject(subject: String): Flow<List<McqQuestion>> =
        mcqDao.getQuestionsBySubject(subject)

    fun searchQuestions(query: String): Flow<List<McqQuestion>> =
        mcqDao.searchQuestions(query)

    suspend fun checkAndSeed() = withContext(Dispatchers.IO) {
        val totalCount = mcqDao.getQuestionCount()
        if (totalCount == 0) {
            mcqDao.insertQuestions(BcsSeedData.getInitialQuestions())
        } else {
            val existing = mcqDao.getAllQuestionsSync().map { it.question }.toSet()
            val initial = BcsSeedData.getInitialQuestions()
            val missing = initial.filter { it.question !in existing }
            if (missing.isNotEmpty()) {
                mcqDao.insertQuestions(missing)
            }
        }
    }

    suspend fun insertQuestion(question: McqQuestion): Long = withContext(Dispatchers.IO) {
        mcqDao.insertQuestion(question)
    }

    suspend fun insertQuestions(questions: List<McqQuestion>) = withContext(Dispatchers.IO) {
        mcqDao.insertQuestions(questions)
    }

    suspend fun updateQuestion(question: McqQuestion) = withContext(Dispatchers.IO) {
        mcqDao.updateQuestion(question)
    }

    suspend fun deleteQuestion(question: McqQuestion) = withContext(Dispatchers.IO) {
        mcqDao.deleteQuestion(question)
    }

    suspend fun deleteQuestionById(id: Long) = withContext(Dispatchers.IO) {
        mcqDao.deleteQuestionById(id)
    }

    suspend fun toggleBookmark(id: Long, currentStatus: Boolean) = withContext(Dispatchers.IO) {
        mcqDao.setBookmark(id, !currentStatus)
    }

    suspend fun recordAttempt(id: Long, isCorrect: Boolean) = withContext(Dispatchers.IO) {
        mcqDao.recordAttempt(id, isCorrect)
    }

    suspend fun saveQuizAttempt(attempt: QuizAttempt): Long = withContext(Dispatchers.IO) {
        mcqDao.insertQuizAttempt(attempt)
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        mcqDao.clearQuizAttempts()
    }
}
