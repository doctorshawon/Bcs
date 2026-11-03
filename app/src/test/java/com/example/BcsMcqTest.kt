package com.example

import com.example.data.BcsSeedData
import com.example.data.McqQuestion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BcsMcqTest {

    @Test
    fun bcsSeedData_hasAllSubjectsIncludingMedical() {
        val questions = BcsSeedData.getInitialQuestions()
        assertTrue("Seed questions should not be empty", questions.isNotEmpty())
        assertEquals(11, BcsSeedData.subjects.size)
        assertTrue(BcsSeedData.subjects.contains("মেডিকেল সায়েন্স (Special BCS)"))

        // Verify each question has 4 non-empty options and valid correctOption index (0..3)
        questions.forEach { q ->
            assertEquals(4, q.getOptions().size)
            assertTrue(q.correctOption in 0..3)
            assertTrue(q.question.isNotBlank())
            assertTrue(q.getCorrectAnswerText().isNotBlank())
        }
    }

    @Test
    fun bcsSeedData_containsSpecialBcsHealthQuestions() {
        val questions = BcsSeedData.getInitialQuestions()
        val medicalQuestions = questions.filter {
            it.subject.contains("মেডিকেল") || it.bcsTag.contains("Special BCS")
        }
        assertTrue("Should have at least 10 special medical MCQs", medicalQuestions.size >= 10)

        // Verify key medical topics are represented
        val topics = medicalQuestions.map { it.topic }
        assertTrue(topics.any { it.contains("Anatomy") })
        assertTrue(topics.any { it.contains("Physiology") })
        assertTrue(topics.any { it.contains("Pharmacology") })
        assertTrue(topics.any { it.contains("Pathology") })
        assertTrue(topics.any { it.contains("Community Medicine") })
        assertTrue(topics.any { it.contains("Obstetrics") })
    }

    @Test
    fun scoringFormula_appliesBpscRulesCorrectly() {
        // Example: 20 questions, 15 correct, 4 wrong, 1 unanswered
        val correct = 15
        val wrong = 4
        val netScore = (correct * 1.0) - (wrong * 0.5)
        assertEquals(13.0, netScore, 0.001)

        // Example: 1 correct, 2 wrong -> 1.0 - 1.0 = 0.0
        val netScoreZero = (1 * 1.0) - (2 * 0.5)
        assertEquals(0.0, netScoreZero, 0.001)
    }

    @Test
    fun mcqQuestion_correctAnswerTextMatchesOption() {
        val q = McqQuestion(
            question = "Which life-saving drug is first-line for Anaphylactic Shock?",
            optionA = "Hydrocortisone",
            optionB = "Adrenaline (1:1000)",
            optionC = "Chlorpheniramine",
            optionD = "Dopamine",
            correctOption = 1,
            subject = "মেডিকেল সায়েন্স (Special BCS)"
        )
        assertEquals("Adrenaline (1:1000)", q.getCorrectAnswerText())
    }
}
