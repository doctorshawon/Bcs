package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mcq_questions")
data class McqQuestion(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val question: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOption: Int, // 0 for A, 1 for B, 2 for C, 3 for D
    val explanation: String = "",
    val subject: String, // One of the 10 official BCS categories
    val topic: String = "",
    val bcsTag: String = "", // e.g. "45th BCS", "44th BCS", "Self Made"
    val difficulty: String = "Medium", // Easy, Medium, Hard
    val isUserCreated: Boolean = false,
    val isBookmarked: Boolean = false,
    val timesAttempted: Int = 0,
    val timesCorrect: Int = 0,
    val lastAttemptCorrect: Boolean? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getOptions(): List<String> = listOf(optionA, optionB, optionC, optionD)
    
    fun getCorrectAnswerText(): String = when (correctOption) {
        0 -> optionA
        1 -> optionB
        2 -> optionC
        3 -> optionD
        else -> optionA
    }
}
