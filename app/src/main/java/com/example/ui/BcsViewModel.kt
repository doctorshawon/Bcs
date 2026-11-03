package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.BcsAiGenerator
import com.example.data.BcsSeedData
import com.example.data.GeneratedMcqItem
import com.example.data.McqQuestion
import com.example.data.McqRepository
import com.example.data.QuizAttempt
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Screen {
    HOME,
    MAKE_MCQ,
    PRACTICE,
    MODEL_TEST,
    REVISION_VAULT,
    SYLLABUS_GUIDE
}

data class PracticeState(
    val title: String = "Practice Mode",
    val questions: List<McqQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOption: Int? = null,
    val isRevealed: Boolean = false,
    val correctCount: Int = 0,
    val wrongCount: Int = 0
)

data class ModelTestState(
    val title: String = "BCS Model Test",
    val questions: List<McqQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val userAnswers: Map<Int, Int> = emptyMap(), // questionIndex -> selectedOption (0..3)
    val flaggedForReview: Set<Int> = emptySet(),
    val timeRemainingSeconds: Int = 600, // default 10 minutes
    val totalTimeSeconds: Int = 600,
    val isRunning: Boolean = false,
    val isSubmitted: Boolean = false,
    val score: Double = 0.0,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val unansweredCount: Int = 0,
    val is50thSpecialBcs: Boolean = false,
    val medicalTotal: Int = 0,
    val medicalCorrect: Int = 0,
    val medicalWrong: Int = 0,
    val medicalScore: Double = 0.0,
    val generalTotal: Int = 0,
    val generalCorrect: Int = 0,
    val generalWrong: Int = 0,
    val generalScore: Double = 0.0
)

sealed interface AiGenerationState {
    data object Idle : AiGenerationState
    data class Generating(val statusMessage: String) : AiGenerationState
    data class Success(val questions: List<GeneratedMcqItem>) : AiGenerationState
    data class Error(val message: String) : AiGenerationState
}

class BcsViewModel(private val repository: McqRepository) : ViewModel() {

    // Current screen navigation
    private val _currentScreen = MutableStateFlow(Screen.HOME)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Database Flows
    val allQuestions: StateFlow<List<McqQuestion>> = repository.allQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userCreatedQuestions: StateFlow<List<McqQuestion>> = repository.userCreatedQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarkedQuestions: StateFlow<List<McqQuestion>> = repository.bookmarkedQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mistakeQuestions: StateFlow<List<McqQuestion>> = repository.mistakeQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userCreatedCount: StateFlow<Int> = repository.userCreatedCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val quizAttempts: StateFlow<List<QuizAttempt>> = repository.quizAttempts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults: StateFlow<List<McqQuestion>> = combine(allQuestions, _searchQuery) { list, query ->
        if (query.isBlank()) list
        else list.filter {
            it.question.contains(query, ignoreCase = true) ||
            it.topic.contains(query, ignoreCase = true) ||
            it.bcsTag.contains(query, ignoreCase = true) ||
            it.subject.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Practice Session State
    private val _practiceState = MutableStateFlow(PracticeState())
    val practiceState: StateFlow<PracticeState> = _practiceState.asStateFlow()

    // Model Test Session State
    private val _modelTestState = MutableStateFlow(ModelTestState())
    val modelTestState: StateFlow<ModelTestState> = _modelTestState.asStateFlow()

    // Editing MCQ state
    private val _editingQuestion = MutableStateFlow<McqQuestion?>(null)
    val editingQuestion: StateFlow<McqQuestion?> = _editingQuestion.asStateFlow()

    // Banner message for UI alerts (e.g. "Question created successfully!")
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // AI Question Generator
    private val aiGenerator = BcsAiGenerator()
    private val _aiGenerationState = MutableStateFlow<AiGenerationState>(AiGenerationState.Idle)
    val aiGenerationState: StateFlow<AiGenerationState> = _aiGenerationState.asStateFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            repository.checkAndSeed()
        }
    }

    fun generateQuestionsWithAi(
        subject: String,
        topic: String,
        count: Int = 5,
        difficulty: String = "Medium"
    ) {
        val safeCount = count.coerceIn(1, 40)
        viewModelScope.launch {
            _aiGenerationState.value = AiGenerationState.Generating(
                "AI ৫০তম বিসিএস সিলেবাস ও প্রামাণ্য গ্রন্থ বিশ্লেষণ করে $safeCount টি প্রশ্ন তৈরি করছে..."
            )
            val is50th = subject.contains("মেডিকেল", ignoreCase = true) || subject.contains("Medical", ignoreCase = true) || topic.contains("50", ignoreCase = true) || topic.contains("৫০", ignoreCase = true)
            val result = aiGenerator.generateQuestions(
                subject = subject,
                topic = topic,
                count = safeCount,
                difficulty = difficulty,
                is50thSpecialBcs = is50th
            )
            result.fold(
                onSuccess = { generatedList ->
                    _aiGenerationState.value = AiGenerationState.Success(generatedList)
                },
                onFailure = { error ->
                    _aiGenerationState.value = AiGenerationState.Error(error.localizedMessage ?: "AI প্রশ্ন তৈরিতে সমস্যা হয়েছে।")
                }
            )
        }
    }

    fun saveAllAiGeneratedQuestions() {
        val current = _aiGenerationState.value
        if (current is AiGenerationState.Success) {
            viewModelScope.launch {
                val questionsToInsert = current.questions.map { it.toMcqQuestion() }
                repository.insertQuestions(questionsToInsert)
                _snackbarMessage.value = "${questionsToInsert.size} টি AI প্রশ্ন আপনার প্রশ্ন ব্যাংকে সংরক্ষণ করা হয়েছে!"
                _aiGenerationState.value = AiGenerationState.Idle
            }
        }
    }

    fun startPracticeOnAiQuestions() {
        val current = _aiGenerationState.value
        if (current is AiGenerationState.Success && current.questions.isNotEmpty()) {
            val mcqList = current.questions.map { it.toMcqQuestion() }
            startPractice("AI Generated Quiz Drill", mcqList)
        }
    }

    fun resetAiGenerationState() {
        _aiGenerationState.value = AiGenerationState.Idle
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    fun clearMessage() {
        _snackbarMessage.value = null
    }

    // --- MCQ CREATION & MANAGEMENT ---
    fun saveNewQuestion(
        questionText: String,
        optionA: String,
        optionB: String,
        optionC: String,
        optionD: String,
        correctOption: Int,
        subject: String,
        topic: String,
        bcsTag: String,
        difficulty: String,
        explanation: String
    ) {
        viewModelScope.launch {
            val existing = _editingQuestion.value
            if (existing != null) {
                // Update
                val updated = existing.copy(
                    question = questionText.trim(),
                    optionA = optionA.trim(),
                    optionB = optionB.trim(),
                    optionC = optionC.trim(),
                    optionD = optionD.trim(),
                    correctOption = correctOption,
                    subject = subject,
                    topic = topic.trim(),
                    bcsTag = if (bcsTag.isBlank()) "Self Made" else bcsTag.trim(),
                    difficulty = difficulty,
                    explanation = explanation.trim()
                )
                repository.updateQuestion(updated)
                _editingQuestion.value = null
                _snackbarMessage.value = "MCQ updated successfully!"
            } else {
                // Create
                val newQuestion = McqQuestion(
                    question = questionText.trim(),
                    optionA = optionA.trim(),
                    optionB = optionB.trim(),
                    optionC = optionC.trim(),
                    optionD = optionD.trim(),
                    correctOption = correctOption,
                    subject = subject,
                    topic = topic.trim(),
                    bcsTag = if (bcsTag.isBlank()) "Self Made" else bcsTag.trim(),
                    difficulty = difficulty,
                    explanation = explanation.trim(),
                    isUserCreated = true
                )
                repository.insertQuestion(newQuestion)
                _snackbarMessage.value = "MCQ saved to question bank!"
            }
        }
    }

    fun startEditing(question: McqQuestion) {
        _editingQuestion.value = question
        _currentScreen.value = Screen.MAKE_MCQ
    }

    fun cancelEditing() {
        _editingQuestion.value = null
    }

    fun deleteQuestion(question: McqQuestion) {
        viewModelScope.launch {
            repository.deleteQuestion(question)
            _snackbarMessage.value = "Question deleted."
        }
    }

    fun toggleBookmark(question: McqQuestion) {
        viewModelScope.launch {
            repository.toggleBookmark(question.id, question.isBookmarked)
            // also update in practice mode if currently open
            val currentPractice = _practiceState.value
            if (currentPractice.questions.isNotEmpty() && currentPractice.currentIndex in currentPractice.questions.indices) {
                val currentQ = currentPractice.questions[currentPractice.currentIndex]
                if (currentQ.id == question.id) {
                    val updatedList = currentPractice.questions.toMutableList()
                    updatedList[currentPractice.currentIndex] = currentQ.copy(isBookmarked = !question.isBookmarked)
                    _practiceState.value = currentPractice.copy(questions = updatedList)
                }
            }
        }
    }

    // --- PRACTICE MODE ---
    fun startPractice(title: String, questions: List<McqQuestion>) {
        if (questions.isEmpty()) {
            _snackbarMessage.value = "No questions available in this category yet."
            return
        }
        val shuffled = questions.shuffled()
        _practiceState.value = PracticeState(
            title = title,
            questions = shuffled,
            currentIndex = 0,
            selectedOption = null,
            isRevealed = false,
            correctCount = 0,
            wrongCount = 0
        )
        _currentScreen.value = Screen.PRACTICE
    }

    fun selectPracticeOption(optionIndex: Int) {
        val state = _practiceState.value
        if (state.isRevealed) return // already answered
        val currentQ = state.questions.getOrNull(state.currentIndex) ?: return
        val isCorrect = optionIndex == currentQ.correctOption

        viewModelScope.launch {
            repository.recordAttempt(currentQ.id, isCorrect)
        }

        _practiceState.value = state.copy(
            selectedOption = optionIndex,
            isRevealed = true,
            correctCount = if (isCorrect) state.correctCount + 1 else state.correctCount,
            wrongCount = if (!isCorrect) state.wrongCount + 1 else state.wrongCount
        )
    }

    fun nextPracticeQuestion() {
        val state = _practiceState.value
        if (state.currentIndex + 1 < state.questions.size) {
            _practiceState.value = state.copy(
                currentIndex = state.currentIndex + 1,
                selectedOption = null,
                isRevealed = false
            )
        }
    }

    fun prevPracticeQuestion() {
        val state = _practiceState.value
        if (state.currentIndex > 0) {
            _practiceState.value = state.copy(
                currentIndex = state.currentIndex - 1,
                selectedOption = null,
                isRevealed = false
            )
        }
    }

    // --- MODEL TEST / TIMED EXAM SIMULATION ---
    fun isMedicalQuestion(q: McqQuestion): Boolean {
        return q.subject.contains("মেডিকেল", ignoreCase = true) ||
               q.subject.contains("Medical", ignoreCase = true) ||
               q.bcsTag.contains("Health", ignoreCase = true) ||
               q.bcsTag.contains("Special BCS", ignoreCase = true) ||
               q.topic.contains("Surgery", ignoreCase = true) ||
               q.topic.contains("Medicine", ignoreCase = true) ||
               q.topic.contains("Anatomy", ignoreCase = true) ||
               q.topic.contains("Physiology", ignoreCase = true) ||
               q.topic.contains("Pharmacology", ignoreCase = true) ||
               q.topic.contains("Pathology", ignoreCase = true) ||
               q.topic.contains("EPI", ignoreCase = true) ||
               q.topic.contains("Pediatrics", ignoreCase = true) ||
               q.topic.contains("Obstetrics", ignoreCase = true) ||
               q.topic.contains("Gynecology", ignoreCase = true) ||
               q.topic.contains("Forensic", ignoreCase = true)
    }

    fun start50thSpecialBcsModelTest(count: Int = 200) {
        val all = allQuestions.value
        if (all.isEmpty()) {
            _snackbarMessage.value = "প্রশ্ন ব্যাংকে পর্যাপ্ত প্রশ্ন নেই।"
            return
        }

        val medicalPool = all.filter { isMedicalQuestion(it) }.shuffled()
        val generalPool = all.filter { !isMedicalQuestion(it) }.shuffled()

        val halfCount = count / 2
        // Ensure 50-50 split between Medical Sector and General Part
        val selectedMedical = if (medicalPool.isNotEmpty()) {
            val list = mutableListOf<McqQuestion>()
            while (list.size < halfCount) {
                list.addAll(medicalPool.shuffled())
            }
            list.take(halfCount)
        } else {
            all.take(halfCount)
        }

        val selectedGeneral = if (generalPool.isNotEmpty()) {
            val list = mutableListOf<McqQuestion>()
            while (list.size < halfCount) {
                list.addAll(generalPool.shuffled())
            }
            list.take(halfCount)
        } else {
            all.take(halfCount)
        }

        val combinedQuestions = selectedMedical + selectedGeneral

        // Official 50th BCS: 200 questions = 120 minutes (7200s); 50 = 30 mins; 20 = 12 mins
        val timeInSeconds = when {
            count >= 200 -> 7200 // 120 mins
            count >= 50 -> 1800 // 30 mins
            else -> count * 36
        }

        timerJob?.cancel()
        _modelTestState.value = ModelTestState(
            title = "৫০তম বিশেষ বিসিএস (স্বাস্থ্য) ২০০ মার্কস পূর্ণাঙ্গ টেস্ট",
            questions = combinedQuestions,
            currentIndex = 0,
            userAnswers = emptyMap(),
            flaggedForReview = emptySet(),
            timeRemainingSeconds = timeInSeconds,
            totalTimeSeconds = timeInSeconds,
            isRunning = true,
            isSubmitted = false,
            is50thSpecialBcs = true,
            medicalTotal = halfCount,
            generalTotal = halfCount
        )
        _currentScreen.value = Screen.MODEL_TEST

        // Start countdown
        timerJob = viewModelScope.launch {
            while (_modelTestState.value.isRunning && _modelTestState.value.timeRemainingSeconds > 0) {
                delay(1000)
                val current = _modelTestState.value
                if (current.timeRemainingSeconds <= 1) {
                    submitModelTest()
                    break
                } else {
                    _modelTestState.value = current.copy(
                        timeRemainingSeconds = current.timeRemainingSeconds - 1
                    )
                }
            }
        }
    }

    fun startGeneralBcs200ModelTest() {
        val all = allQuestions.value
        if (all.isEmpty()) {
            _snackbarMessage.value = "প্রশ্ন ব্যাংকে পর্যাপ্ত প্রশ্ন নেই।"
            return
        }

        // BPSC Official Syllabus for General BCS Preliminary (200 Marks):
        // 1. বাংলা ভাষা ও সাহিত্য: ৩৫
        // 2. English Language & Literature: ৩৫
        // 3. বাংলাদেশ বিষয়াবলি: ৩০
        // 4. আন্তর্জাতিক বিষয়াবলি: ২০
        // 5. ভূগোল (বাংলাদেশ ও বিশ্ব), পরিবেশ ও দুর্যোগ ব্যবস্থাপনা: ১০
        // 6. সাধারণ বিজ্ঞান: ১৫
        // 7. কম্পিউটার ও তথ্যপ্রযুক্তি: ১৫
        // 8. গাণিতিক যুক্তি: ১৫
        // 9. মানসিক দক্ষতা: ১৫
        // 10. নৈতিকতা, মূল্যবোধ ও সুশাসন: ১০
        val subjectDistribution = listOf(
            "বাংলা ভাষা ও সাহিত্য" to 35,
            "English Language & Literature" to 35,
            "বাংলাদেশ বিষয়াবলি" to 30,
            "আন্তর্জাতিক বিষয়াবলি" to 20,
            "ভূগোল ও পরিবেশ" to 10,
            "সাধারণ বিজ্ঞান" to 15,
            "কম্পিউটার ও তথ্যপ্রযুক্তি" to 15,
            "গাণিতিক যুক্তি" to 15,
            "মানসিক দক্ষতা" to 15,
            "নৈতিকতা ও সুশাসন" to 10
        )

        val fullQuestionList = mutableListOf<McqQuestion>()
        subjectDistribution.forEach { (subj, quota) ->
            val pool = all.filter { it.subject.contains(subj, ignoreCase = true) }.shuffled()
            if (pool.isNotEmpty()) {
                var added = 0
                while (added < quota) {
                    val batch = pool.shuffled()
                    for (item in batch) {
                        fullQuestionList.add(item)
                        added++
                        if (added >= quota) break
                    }
                }
            } else {
                // Fallback to general pool questions
                val fallbackPool = all.filter { !isMedicalQuestion(it) }.shuffled()
                var added = 0
                while (added < quota && fallbackPool.isNotEmpty()) {
                    for (item in fallbackPool) {
                        fullQuestionList.add(item)
                        added++
                        if (added >= quota) break
                    }
                }
            }
        }

        val totalTimeSeconds = 7200 // 120 minutes (2 hours)
        timerJob?.cancel()
        _modelTestState.value = ModelTestState(
            title = "সাধারণ বিসিএস ২০০ মার্কস AI মডেল টেস্ট (১০টি বিষয়)",
            questions = fullQuestionList,
            currentIndex = 0,
            userAnswers = emptyMap(),
            flaggedForReview = emptySet(),
            timeRemainingSeconds = totalTimeSeconds,
            totalTimeSeconds = totalTimeSeconds,
            isRunning = true,
            isSubmitted = false,
            is50thSpecialBcs = false
        )
        _currentScreen.value = Screen.MODEL_TEST

        // Start countdown
        timerJob = viewModelScope.launch {
            while (_modelTestState.value.isRunning && _modelTestState.value.timeRemainingSeconds > 0) {
                delay(1000)
                val current = _modelTestState.value
                if (current.timeRemainingSeconds <= 1) {
                    submitModelTest()
                    break
                } else {
                    _modelTestState.value = current.copy(
                        timeRemainingSeconds = current.timeRemainingSeconds - 1
                    )
                }
            }
        }
    }

    fun startModelTest(title: String, count: Int = 20, subjectFilter: String? = null) {
        val available = if (subjectFilter != null) {
            allQuestions.value.filter { it.subject == subjectFilter }
        } else {
            allQuestions.value
        }

        if (available.isEmpty()) {
            _snackbarMessage.value = "Not enough questions for a model test."
            return
        }

        val testQuestions = available.shuffled().take(count)
        val timeInSeconds = when {
            count <= 10 -> 300 // 5 mins
            count <= 20 -> 720 // 12 mins (36s per question as in official BCS!)
            else -> count * 36
        }

        timerJob?.cancel()
        _modelTestState.value = ModelTestState(
            title = title,
            questions = testQuestions,
            currentIndex = 0,
            userAnswers = emptyMap(),
            flaggedForReview = emptySet(),
            timeRemainingSeconds = timeInSeconds,
            totalTimeSeconds = timeInSeconds,
            isRunning = true,
            isSubmitted = false,
            is50thSpecialBcs = false
        )
        _currentScreen.value = Screen.MODEL_TEST

        // Start countdown
        timerJob = viewModelScope.launch {
            while (_modelTestState.value.isRunning && _modelTestState.value.timeRemainingSeconds > 0) {
                delay(1000)
                val current = _modelTestState.value
                if (current.timeRemainingSeconds <= 1) {
                    submitModelTest()
                    break
                } else {
                    _modelTestState.value = current.copy(
                        timeRemainingSeconds = current.timeRemainingSeconds - 1
                    )
                }
            }
        }
    }

    fun selectTestAnswer(questionIndex: Int, optionIndex: Int) {
        val current = _modelTestState.value
        if (current.isSubmitted) return
        val currentAnswers = current.userAnswers.toMutableMap()
        if (currentAnswers[questionIndex] == optionIndex) {
            // Deselect if already selected
            currentAnswers.remove(questionIndex)
        } else {
            currentAnswers[questionIndex] = optionIndex
        }
        _modelTestState.value = current.copy(userAnswers = currentAnswers)
    }

    fun toggleFlagQuestion(questionIndex: Int) {
        val current = _modelTestState.value
        val flags = current.flaggedForReview.toMutableSet()
        if (flags.contains(questionIndex)) {
            flags.remove(questionIndex)
        } else {
            flags.add(questionIndex)
        }
        _modelTestState.value = current.copy(flaggedForReview = flags)
    }

    fun setTestCurrentIndex(index: Int) {
        val current = _modelTestState.value
        if (index in current.questions.indices) {
            _modelTestState.value = current.copy(currentIndex = index)
        }
    }

    fun submitModelTest() {
        timerJob?.cancel()
        val current = _modelTestState.value
        if (current.isSubmitted) return

        var correct = 0
        var wrong = 0
        var unanswered = 0

        var medCorrect = 0
        var medWrong = 0
        var genCorrect = 0
        var genWrong = 0

        current.questions.forEachIndexed { index, q ->
            val userAns = current.userAnswers[index]
            val isMed = if (current.is50thSpecialBcs) {
                index < current.medicalTotal
            } else {
                isMedicalQuestion(q)
            }

            if (userAns == null) {
                unanswered++
            } else if (userAns == q.correctOption) {
                correct++
                if (isMed) medCorrect++ else genCorrect++
                viewModelScope.launch { repository.recordAttempt(q.id, true) }
            } else {
                wrong++
                if (isMed) medWrong++ else genWrong++
                viewModelScope.launch { repository.recordAttempt(q.id, false) }
            }
        }

        // BPSC rules: +1.0 for correct, -0.5 for wrong answer
        val netScore = (correct * 1.0) - (wrong * 0.5)
        val medNet = (medCorrect * 1.0) - (medWrong * 0.5)
        val genNet = (genCorrect * 1.0) - (genWrong * 0.5)
        val timeSpent = current.totalTimeSeconds - current.timeRemainingSeconds

        val attempt = QuizAttempt(
            title = current.title,
            subject = if (current.is50thSpecialBcs) "৫০তম বিশেষ বিসিএস স্বাস্থ্য"
                      else if (current.title.contains("Subject")) "Subject Drill"
                      else "Full BCS Model Test",
            totalQuestions = current.questions.size,
            correctCount = correct,
            wrongCount = wrong,
            unansweredCount = unanswered,
            score = if (netScore < 0.0) 0.0 else netScore,
            timeTakenSeconds = timeSpent
        )

        viewModelScope.launch {
            repository.saveQuizAttempt(attempt)
        }

        _modelTestState.value = current.copy(
            isRunning = false,
            isSubmitted = true,
            score = if (netScore < 0.0) 0.0 else netScore,
            correctCount = correct,
            wrongCount = wrong,
            unansweredCount = unanswered,
            medicalCorrect = medCorrect,
            medicalWrong = medWrong,
            medicalScore = if (medNet < 0.0) 0.0 else medNet,
            generalCorrect = genCorrect,
            generalWrong = genWrong,
            generalScore = if (genNet < 0.0) 0.0 else genNet
        )
    }

    fun retakeCurrentTest() {
        val current = _modelTestState.value
        if (current.is50thSpecialBcs) {
            start50thSpecialBcsModelTest(current.questions.size)
        } else {
            startModelTest(current.title, current.questions.size)
        }
    }
}

class BcsViewModelFactory(private val repository: McqRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BcsViewModel::class.java)) {
            return BcsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
