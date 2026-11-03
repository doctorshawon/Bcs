package com.example

import com.example.data.BcsSeedData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testSeedDataValidation() {
        val questions = BcsSeedData.getInitialQuestions()
        assertTrue("Seed questions should not be empty", questions.isNotEmpty())

        val medicalQuestions = questions.filter {
            it.subject.contains("মেডিকেল") || it.bcsTag.contains("Special BCS")
        }
        val generalQuestions = questions.filter {
            !it.subject.contains("মেডিকেল") && !it.bcsTag.contains("Special BCS")
        }

        assertTrue("Should have medical science questions for 50th Special BCS", medicalQuestions.isNotEmpty())
        assertTrue("Should have general part questions", generalQuestions.isNotEmpty())

        // Verify valid options and correctOption in range 0..3
        for (q in questions) {
            assertTrue("Question must not be blank", q.question.isNotBlank())
            assertTrue("Option A must not be blank", q.optionA.isNotBlank())
            assertTrue("Option B must not be blank", q.optionB.isNotBlank())
            assertTrue("Option C must not be blank", q.optionC.isNotBlank())
            assertTrue("Option D must not be blank", q.optionD.isNotBlank())
            assertTrue("correctOption must be 0..3", q.correctOption in 0..3)
            assertTrue("Explanation should be provided", q.explanation.isNotBlank())
        }
    }

    @Test
    fun testSyllabusSubjectsCompleteness() {
        val subjects = BcsSeedData.subjects
        assertTrue("Must include Medical Science", subjects.any { it.contains("মেডিকেল") })
        assertTrue("Must include Bangla", subjects.any { it.contains("বাংলা") })
        assertTrue("Must include English", subjects.any { it.contains("English") })
        assertTrue("Must include Bangladesh Affairs", subjects.any { it.contains("বাংলাদেশ") })
        assertTrue("Must include International Affairs", subjects.any { it.contains("আন্তর্জাতিক") })
        assertTrue("Must include Science", subjects.any { it.contains("বিজ্ঞান") })
        assertTrue("Must include ICT", subjects.any { it.contains("কম্পিউটার") })
        assertTrue("Must include Math", subjects.any { it.contains("গণিত") })
        assertTrue("Must include Mental Ability", subjects.any { it.contains("মানসিক") })
        assertTrue("Must include Ethics", subjects.any { it.contains("নৈতিকতা") })
    }
}
