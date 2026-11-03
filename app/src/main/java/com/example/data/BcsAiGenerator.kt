package com.example.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val TAG = "BcsAiGenerator"

data class GeneratedMcqItem(
    val question: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOption: Int,
    val explanation: String,
    val subject: String,
    val topic: String,
    val difficulty: String,
    val bcsTag: String = "50th Special BCS",
    val referenceBook: String = ""
) {
    fun toMcqQuestion(): McqQuestion {
        val finalExplanation = buildString {
            append(explanation)
            if (referenceBook.isNotBlank() && !explanation.contains(referenceBook, ignoreCase = true)) {
                append("\n📚 রেফারেন্স: ")
                append(referenceBook)
            }
        }

        return McqQuestion(
            question = question,
            optionA = optionA,
            optionB = optionB,
            optionC = optionC,
            optionD = optionD,
            correctOption = correctOption.coerceIn(0, 3),
            explanation = finalExplanation,
            subject = subject,
            topic = topic,
            bcsTag = bcsTag,
            difficulty = difficulty,
            isUserCreated = true
        )
    }
}

class BcsAiGenerator {

    private val mcqItemSchema = Schema.obj(
        properties = mapOf(
            "question" to Schema.string(description = "The MCQ question statement in Bengali or English"),
            "optionA" to Schema.string(description = "Option A choice"),
            "optionB" to Schema.string(description = "Option B choice"),
            "optionC" to Schema.string(description = "Option C choice"),
            "optionD" to Schema.string(description = "Option D choice"),
            "correctOption" to Schema.integer(description = "0 for optionA, 1 for optionB, 2 for optionC, 3 for optionD"),
            "explanation" to Schema.string(description = "Detailed factual rationale explaining why the answer is correct"),
            "topic" to Schema.string(description = "Specific topic or sub-discipline"),
            "difficulty" to Schema.string(description = "Easy, Medium, or Hard"),
            "referenceBook" to Schema.string(
                description = "Standard textbook reference e.g. Bailey & Love, Davidson, Robbins, Guyton, Katzung, Park's PSM, DC Dutta, সৌমিত্র শেখর, Wren & Martin, বাংলাদেশ সংবিধান",
                nullable = true
            )
        )
    )

    private val mcqListSchema = Schema.obj(
        properties = mapOf(
            "questions" to Schema.array(
                items = mcqItemSchema,
                description = "List of generated BCS exam questions"
            )
        )
    )

    private val config = generationConfig {
        responseMimeType = "application/json"
        responseSchema = mcqListSchema
    }

    private val model by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = config
        )
    }

    suspend fun generateQuestions(
        subject: String,
        topic: String,
        count: Int,
        difficulty: String,
        is50thSpecialBcs: Boolean = true
    ): Result<List<GeneratedMcqItem>> = withContext(Dispatchers.IO) {
        val safeCount = count.coerceIn(1, 40)
        if (safeCount <= 20) {
            generateSingleBatch(subject, topic, safeCount, difficulty, is50thSpecialBcs)
        } else {
            // For 21-40 questions, split into two batches to ensure high quality and prevent token limits
            val batch1 = safeCount / 2
            val batch2 = safeCount - batch1
            val res1 = generateSingleBatch(subject, topic, batch1, difficulty, is50thSpecialBcs)
            if (res1.isFailure) return@withContext res1

            val list1 = res1.getOrNull() ?: emptyList()
            val res2 = generateSingleBatch(
                subject = subject,
                topic = if (topic.isNotBlank()) "$topic (Part 2 / Advanced topics)" else "Advanced high-yield syllabus topics",
                count = batch2,
                difficulty = difficulty,
                is50thSpecialBcs = is50thSpecialBcs
            )
            val list2 = res2.getOrNull() ?: emptyList()
            val combined = (list1 + list2).distinctBy { it.question }
            if (combined.isNotEmpty()) {
                Result.success(combined)
            } else {
                res1
            }
        }
    }

    private suspend fun generateSingleBatch(
        subject: String,
        topic: String,
        count: Int,
        difficulty: String,
        is50thSpecialBcs: Boolean
    ): Result<List<GeneratedMcqItem>> = withContext(Dispatchers.IO) {
        val isMedical = subject.contains("মেডিকেল") || subject.contains("Medical")

        val standardGuide = if (is50thSpecialBcs || isMedical) {
            """
            This question generation is strictly tailored for the 50th Special BCS Health Cadre (৫০তম বিশেষ বিসিএস স্বাস্থ্য ক্যাডার) examination following the official BPSC syllabus.
            The exam structure has two equal pillars:
            - 100 Marks Medical Sector: Basic & clinical sciences. Reference textbooks taught in Bangladesh:
              * Surgery: Bailey & Love's Short Practice of Surgery (28th Ed)
              * Medicine: Davidson's Principles and Practice of Medicine, Kumar & Clark
              * Pathology: Robbins & Cotran Pathologic Basis of Disease
              * Physiology: Guyton & Hall Medical Physiology, Ganong
              * Pharmacology: Bertram Katzung Basic & Clinical Pharmacology, KD Tripathi
              * Community Medicine & Public Health: Park's PSM, Bangladesh DGHS National EPI Guidelines & Schedules
              * Obstetrics & Gynecology: DC Dutta's Textbook of Obstetrics & Gynecology
              * Anatomy: Vishram Singh, A.K. Datta, Langman's Embryology
              * Forensic Medicine: Parikh's Textbook of Medical Jurisprudence, Reddy
            - 100 Marks General Part:
              * Bengali (20 Marks): Dr. Soumitra Shekhar ('বাংলা ভাষা ও সাহিত্য জিজ্ঞাসা'), Dr. Hayat Mahmud, NCTB Board Grammar
              * English (20 Marks): Cliff's TOEFL, Wren & Martin High School English Grammar, Michael Swan, William J. Long
              * Bangladesh Affairs (20 Marks): The Constitution of Bangladesh, Bangladesh Economic Review, Liberation War history
              * International Affairs (20 Marks): Global politics, diplomacy, Professor Tareque Shamsur Rehman
              * Mathematical Reasoning & Mental Ability (20 Marks): NCTB Secondary Math, Khairul's Basic Math
            """.trimIndent()
        } else {
            """
            This is for the official Bangladesh Civil Service (BCS) preliminary examination syllabus published by BPSC.
            Standard reference books:
            - Bengali: Dr. Soumitra Shekhar, Dr. Hayat Mahmud, NCTB 9-10 Grammar
            - English: Wren & Martin, Cliff's TOEFL, Michael Swan
            - Bangladesh Affairs: Bangladesh Constitution, Banglapedia, Economic Survey
            - International Affairs: Prof. Tareque Shamsur Rehman
            """.trimIndent()
        }

        val prompt = """
            You are a senior Bangladesh Public Service Commission (BPSC) Question Setter for the 50th Special BCS Health Cadre and BCS Preliminary examinations.
            
            $standardGuide
            
            Target parameters:
            - Subject: '$subject'
            - Topic focus: '${if (topic.isNotBlank()) topic else "High-yield topics according to BPSC 50th BCS syllabus"}'
            - Target Question Count: $count
            - Difficulty Level: $difficulty
            - Exam Tag: '${if (is50thSpecialBcs) "50th Special BCS" else "BCS Prelims"}'
            
            Strict instructions:
            1. Formulate exactly $count high-yield multiple-choice questions matching actual BPSC standard.
            2. Provide 4 clear options (optionA, optionB, optionC, optionD).
            3. 'correctOption' MUST be an integer: 0 for A, 1 for B, 2 for C, or 3 for D.
            4. In 'referenceBook', cite the exact author/book taught in Bangladesh (e.g., 'Bailey & Love's Surgery 28th Ed, Ch. 12', 'Davidson's Medicine, Ch. 18', 'Park's PSM / Bangladesh EPI Guidelines', 'Katzung Pharmacology', 'DC Dutta Gynecology', 'ড. সৌমিত্র শেখর, বাংলা ভাষা ও সাহিত্য', 'Wren & Martin Grammar', 'বাংলাদেশ সংবিধান অনুচ্ছেদ ২৭').
            5. In 'explanation', provide an in-depth factual explanation with specific rules/clinical evidence.
            6. For Medical Sector or English, write questions in English/medical terminology. For Bengali, Bangladesh Affairs, etc., write in Bengali.
        """.trimIndent()

        try {
            val response = withTimeout(75_000L) {
                model.generateContent(prompt)
            }

            val jsonString = response.text
            if (jsonString.isNullOrBlank()) {
                return@withContext Result.failure(Exception("AI returned an empty response. Please try again."))
            }

            val json = JSONObject(jsonString)
            val questionsArray = json.getJSONArray("questions")
            val resultList = mutableListOf<GeneratedMcqItem>()

            for (i in 0 until questionsArray.length()) {
                val qObj = questionsArray.getJSONObject(i)
                val refBook = qObj.optString("referenceBook", "").trim()
                val item = GeneratedMcqItem(
                    question = qObj.getString("question").trim(),
                    optionA = qObj.getString("optionA").trim(),
                    optionB = qObj.getString("optionB").trim(),
                    optionC = qObj.getString("optionC").trim(),
                    optionD = qObj.getString("optionD").trim(),
                    correctOption = qObj.optInt("correctOption", 0).coerceIn(0, 3),
                    explanation = qObj.optString("explanation", "").trim(),
                    subject = subject,
                    topic = qObj.optString("topic", if (topic.isNotBlank()) topic else subject).trim(),
                    difficulty = qObj.optString("difficulty", difficulty),
                    bcsTag = if (is50thSpecialBcs) "50th Special BCS" else "AI Generated",
                    referenceBook = refBook
                )
                resultList.add(item)
            }

            if (resultList.isEmpty()) {
                return@withContext Result.failure(Exception("No questions were generated."))
            }

            Result.success(resultList)
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "AI question generation timed out", e)
            Result.failure(Exception("AI generation timed out. Please check connection and try again."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "AI question generation failed", e)
            Result.failure(Exception(e.localizedMessage ?: "AI question generation failed."))
        }
    }
}
