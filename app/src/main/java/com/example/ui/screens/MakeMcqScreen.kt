package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BcsSeedData
import com.example.data.GeneratedMcqItem
import com.example.data.McqQuestion
import com.example.ui.AiGenerationState
import com.example.ui.BcsViewModel
import com.example.ui.theme.BcsEmeraldDark
import com.example.ui.theme.BcsEmeraldPrimary
import com.example.ui.theme.BcsGoldAccent
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.CorrectGreenContainer
import com.example.ui.theme.WrongRed

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MakeMcqScreen(
    viewModel: BcsViewModel,
    modifier: Modifier = Modifier
) {
    val userCreatedQuestions by viewModel.userCreatedQuestions.collectAsState()
    val editingQuestion by viewModel.editingQuestion.collectAsState()
    val aiState by viewModel.aiGenerationState.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }

    // AI Generation form fields
    var aiSubject by remember { mutableStateOf(BcsSeedData.subjects.first()) }
    var aiTopic by remember { mutableStateOf("") }
    var aiQuestionCount by remember { mutableIntStateOf(3) }
    var aiDifficulty by remember { mutableStateOf("Medium") }
    var aiSubjectDropdownExpanded by remember { mutableStateOf(false) }

    // Manual Form fields
    var questionText by remember { mutableStateOf("") }
    var optionA by remember { mutableStateOf("") }
    var optionB by remember { mutableStateOf("") }
    var optionC by remember { mutableStateOf("") }
    var optionD by remember { mutableStateOf("") }
    var correctOption by remember { mutableIntStateOf(0) }
    var selectedSubject by remember { mutableStateOf(BcsSeedData.subjects.first()) }
    var topicText by remember { mutableStateOf("") }
    var bcsTagText by remember { mutableStateOf("Self Made") }
    var selectedDifficulty by remember { mutableStateOf("Medium") }
    var explanationText by remember { mutableStateOf("") }

    var subjectMenuExpanded by remember { mutableStateOf(false) }
    var questionToDelete by remember { mutableStateOf<McqQuestion?>(null) }
    var validationError by remember { mutableStateOf<String?>(null) }

    // When entering edit mode, populate form and switch to manual tab
    LaunchedEffect(editingQuestion) {
        if (editingQuestion != null) {
            val q = editingQuestion!!
            questionText = q.question
            optionA = q.optionA
            optionB = q.optionB
            optionC = q.optionC
            optionD = q.optionD
            correctOption = q.correctOption
            selectedSubject = q.subject
            topicText = q.topic
            bcsTagText = q.bcsTag
            selectedDifficulty = q.difficulty
            explanationText = q.explanation
            selectedTab = 1 // switch to manual edit tab
        }
    }

    fun clearManualForm() {
        questionText = ""
        optionA = ""
        optionB = ""
        optionC = ""
        optionD = ""
        correctOption = 0
        topicText = ""
        bcsTagText = "Self Made"
        selectedDifficulty = "Medium"
        explanationText = ""
        validationError = null
        viewModel.cancelEditing()
    }

    fun fillSampleTemplate() {
        val sampleIndex = (1..4).random()
        when (sampleIndex) {
            1 -> {
                questionText = "মুক্তিযুদ্ধকালে বাংলাদেশকে কয়টি সেক্টরে বিভক্ত করা হয়েছিল?"
                optionA = "৯টি"
                optionB = "১০টি"
                optionC = "১১টি"
                optionD = "১২টি"
                correctOption = 2
                selectedSubject = "বাংলাদেশ বিষয়াবলি"
                topicText = "মুক্তিযুদ্ধ"
                bcsTagText = "45th BCS"
                selectedDifficulty = "Easy"
                explanationText = "১৯৭১ সালের ১০-১৭ জুলাই অনুষ্ঠিত সেক্টর কমান্ডার্স সম্মেলনে সমগ্র বাংলাদেশকে ১১টি সেক্টর ও ৬৪টি সাব-সেক্টরে বিভক্ত করা হয়।"
            }
            2 -> {
                questionText = "What is the meaning of the idiom 'Achilles' heel'?"
                optionA = "A secret weapon"
                optionB = "A vulnerable spot"
                optionC = "A great strength"
                optionD = "A quick solution"
                correctOption = 1
                selectedSubject = "English Language & Literature"
                topicText = "Idioms & Phrases"
                bcsTagText = "43rd BCS"
                selectedDifficulty = "Medium"
                explanationText = "'Achilles' heel' originates from Greek mythology and refers to a weakness or vulnerable point in an otherwise strong situation."
            }
            3 -> {
                questionText = "কম্পিউটারের প্রসেসরের প্রধান তিনটি অংশ কী কী?"
                optionA = "ALU, Control Unit, Registers"
                optionB = "RAM, ROM, Cache"
                optionC = "Hard Disk, SSD, Bus"
                optionD = "Input, Output, Monitor"
                correctOption = 0
                selectedSubject = "কম্পিউটার ও তথ্যপ্রযুক্তি"
                topicText = "কম্পিউটার হার্ডওয়্যার"
                bcsTagText = "44th BCS"
                selectedDifficulty = "Medium"
                explanationText = "সিপিইউ (CPU)-এর তিনটি মৌলিক উপাদান হলো: ১. গাণিতিক যুক্তি ইউনিট (ALU), ২. নিয়ন্ত্রণ ইউনিট (CU) এবং ৩. রেজিস্টার (Registers)।"
            }
            else -> {
                questionText = "Which life-saving drug is the primary first-line choice for Acute Anaphylactic Shock?"
                optionA = "Intravenous Hydrocortisone"
                optionB = "Intramuscular Adrenaline (1:1000)"
                optionC = "Oral Chlorpheniramine"
                optionD = "Intravenous Dopamine"
                correctOption = 1
                selectedSubject = "মেডিকেল সায়েন্স (Special BCS)"
                topicText = "Emergency Pharmacology"
                bcsTagText = "42nd Special BCS"
                selectedDifficulty = "Easy"
                explanationText = "Intramuscular Adrenaline (Epinephrine) 1:1000, 0.5 mg in the anterolateral thigh is the immediate life-saving drug of choice for acute anaphylactic shock."
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // 3-way Tab Header
        SecondaryTabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = BcsGoldAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("AI অটো তৈরি", fontWeight = FontWeight.Bold)
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        text = if (editingQuestion != null) "প্রশ্ন সম্পাদনা" else "ম্যানুয়াল তৈরি",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = {
                    Text(
                        text = "প্রশ্নসমূহ (${userCreatedQuestions.size})",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }

        when (selectedTab) {
            0 -> {
                // TAB 0: AI AUTO QUESTION GENERATOR
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ai_generator_banner"),
                            shape = RoundedCornerShape(18.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(BcsEmeraldPrimary, BcsEmeraldDark)
                                        )
                                    )
                                    .padding(16.dp)
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = CircleShape,
                                            color = BcsGoldAccent.copy(alpha = 0.2f),
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.AutoAwesome,
                                                    contentDescription = null,
                                                    tint = BcsGoldAccent,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "AI বিসিএস প্রশ্ন জেনারেটর (Gemini 3.8)",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            )
                                            Text(
                                                text = "স্বয়ংক্রিয়ভাবে ৪টি অপশন ও বিশদ ব্যাখ্যাসহ প্রশ্ন তৈরি করুন",
                                                color = Color.White.copy(alpha = 0.85f),
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Generator Config Form
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Subject Selector
                                Text(
                                    text = "বিষয় নির্বাচন করুন *",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                ExposedDropdownMenuBox(
                                    expanded = aiSubjectDropdownExpanded,
                                    onExpandedChange = { aiSubjectDropdownExpanded = !aiSubjectDropdownExpanded }
                                ) {
                                    OutlinedTextField(
                                        value = aiSubject,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = aiSubjectDropdownExpanded) },
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth()
                                            .testTag("ai_subject_dropdown"),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    ExposedDropdownMenu(
                                        expanded = aiSubjectDropdownExpanded,
                                        onDismissRequest = { aiSubjectDropdownExpanded = false }
                                    ) {
                                        BcsSeedData.subjects.forEach { subj ->
                                            DropdownMenuItem(
                                                text = { Text(subj) },
                                                onClick = {
                                                    aiSubject = subj
                                                    aiSubjectDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Topic / Keyword Input (Optional)
                                Text(
                                    text = "টপিক বা নির্দিষ্ট অধ্যায় (ঐচ্ছিক)",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = aiTopic,
                                    onValueChange = { aiTopic = it },
                                    placeholder = {
                                        Text(
                                            if (aiSubject.contains("মেডিকেল")) "যেমন: Pharmacology, Anatomy, EPI Schedule, Shock"
                                            else "যেমন: চর্যাপদ, মুক্তিযুদ্ধ, সংবিধান, Shakespeare, ধারা"
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("ai_topic_input"),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // 50th Special BCS High-Yield Topic Presets
                                Text(
                                    text = "৫০তম বিশেষ বিসিএস সিলেবাস ও প্রামাণ্য গ্রন্থ প্রিসেট:",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(
                                        Pair("মেডিকেল সায়েন্স (Special BCS)", "Surgery (Bailey & Love)"),
                                        Pair("মেডিকেল সায়েন্স (Special BCS)", "Medicine & Emergency (Davidson)"),
                                        Pair("মেডিকেল সায়েন্স (Special BCS)", "EPI & Public Health (Park's PSM)"),
                                        Pair("মেডিকেল সায়েন্স (Special BCS)", "Pharmacology & Antidotes (Katzung)"),
                                        Pair("মেডিকেল সায়েন্স (Special BCS)", "Obstetrics & PPH (DC Dutta)"),
                                        Pair("বাংলা ভাষা ও সাহিত্য", "৫০তম বিসিএস বাংলা ব্যাকরণ ও সাহিত্য (ড. সৌমিত্র শেখর)"),
                                        Pair("English Language & Literature", "50th BCS English Grammar (Wren & Martin)"),
                                        Pair("বাংলাদেশ বিষয়াবলি", "৫০তম বিসিএস সংবিধান ও অর্থনৈতিক সমীক্ষা"),
                                        Pair("আন্তর্জাতিক বিষয়াবলি", "আন্তর্জাতিক ভূ-রাজনীতি (তারেক শামসুর রেহমান)")
                                    ).forEach { (presetSubj, presetTopic) ->
                                        Surface(
                                            onClick = {
                                                aiSubject = presetSubj
                                                aiTopic = presetTopic
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (aiTopic == presetTopic) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                            border = BorderStroke(1.dp, if (aiTopic == presetTopic) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                        ) {
                                            Text(
                                                text = presetTopic,
                                                fontSize = 11.sp,
                                                fontWeight = if (aiTopic == presetTopic) FontWeight.Bold else FontWeight.Normal,
                                                color = if (aiTopic == presetTopic) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Question Count Chips (up to 40 questions per topic)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "প্রশ্নের সংখ্যা (টপিকভিত্তিক সর্বোচ্চ ৪০টি)",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "$aiQuestionCount টি",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 13.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(5, 10, 20, 30, 40).forEach { cnt ->
                                        FilterChip(
                                            selected = aiQuestionCount == cnt,
                                            onClick = { aiQuestionCount = cnt },
                                            label = { Text("$cnt টি", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Difficulty Chips
                                Text(
                                    text = "কঠিনতার স্তর",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf("Easy", "Medium", "Hard").forEach { diff ->
                                        FilterChip(
                                            selected = aiDifficulty == diff,
                                            onClick = { aiDifficulty = diff },
                                            label = {
                                                Text(
                                                    when (diff) {
                                                        "Easy" -> "সহজ"
                                                        "Medium" -> "মাঝারি"
                                                        else -> "কঠিন"
                                                    }
                                                )
                                            },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Generate Button
                                Button(
                                    onClick = {
                                        viewModel.generateQuestionsWithAi(
                                            subject = aiSubject,
                                            topic = aiTopic,
                                            count = aiQuestionCount,
                                            difficulty = aiDifficulty
                                        )
                                    },
                                    enabled = aiState !is AiGenerationState.Generating,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("generate_ai_mcq_btn"),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (aiState is AiGenerationState.Generating) "AI তৈরি করছে..." else "AI দিয়ে $aiQuestionCount টি প্রশ্ন তৈরি করুন",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    // DEDICATED 200-QUESTION AI MODEL TEST GENERATOR SECTION
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ai_200_model_test_generator_card"),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.5.dp, BcsGoldAccent.copy(alpha = 0.6f)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = BcsGoldAccent.copy(alpha = 0.2f),
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Quiz,
                                                contentDescription = null,
                                                tint = Color(0xFFC78500),
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "২০০ মার্কস AI পূর্ণাঙ্গ মডেল টেস্ট জেনারেটর",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "পিএসসি'র অফিশিয়াল সিলেবাস অনুযায়ী সম্পূর্ণ ২০০ নম্বরের টেস্ট",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Option 1: 50th Special BCS Health Cadre 200 Marks Model Test
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color(0xFFFFF4F4),
                                    border = BorderStroke(1.dp, Color(0xFFFFCDD2)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.MedicalServices,
                                                contentDescription = null,
                                                tint = Color(0xFFD32F2F),
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "৫০তম বিশেষ বিসিএস (স্বাস্থ্য) ২০০ মার্কস AI টেস্ট",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = Color(0xFFB71C1C)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "• ১০০ মার্কস মেডিকেল সেক্টর: সার্জারি, মেডিসিন, ফার্মাকোলজি, ফিজিওলজি, প্যাথলজি, গাইনি ও EPI (Bailey & Love, Davidson, Park's PSM, Katzung)\n• ১০০ মার্কস জেনারেল পার্ট: বাংলা ২০ (ড. সৌমিত্র শেখর), ইংরেজি ২০ (Wren & Martin), বাংলাদেশ ২০ (সংবিধান), আন্তর্জাতিক ২০, গণিত ও মানসিক ২০\n• মোট সময়: ১২০ মিনিট (২ ঘণ্টা) | নেগেটিভ মার্কিং: -০.৫",
                                            fontSize = 11.sp,
                                            lineHeight = 16.sp,
                                            color = Color(0xFF424242)
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Button(
                                            onClick = { viewModel.start50thSpecialBcsModelTest(200) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("start_50th_bcs_ai_200_btn")
                                        ) {
                                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("৫০তম বিশেষ বিসিএস AI ২০০ মার্কস টেস্ট শুরু করুন", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Option 2: General BCS 200 Marks Model Test
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color(0xFFF1F8F5),
                                    border = BorderStroke(1.dp, BcsEmeraldPrimary.copy(alpha = 0.3f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.MenuBook,
                                                contentDescription = null,
                                                tint = BcsEmeraldPrimary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "সাধারণ বিসিএস ২০০ মার্কস AI টেস্ট (১০টি বিষয়)",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = BcsEmeraldDark
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "• পিএসসি'র অফিশিয়াল সিলেবাস অনুযায়ী পূর্ণাঙ্গ ১০টি বিষয়ের ২০০ প্রশ্ন:\n  বাংলা ৩৫, ইংরেজি ৩৫, বাংলাদেশ ৩০, আন্তর্জাতিক ২০, ভূগোল ১০, সাধারণ বিজ্ঞান ১৫, কম্পিউটার ১৫, গাণিতিক যুক্তি ১৫, মানসিক দক্ষতা ১৫, নৈতিকতা ও সুশাসন ১০\n• মোট সময়: ১২০ মিনিট (২ ঘণ্টা) | নেগেটিভ মার্কিং: -০.৫",
                                            fontSize = 11.sp,
                                            lineHeight = 16.sp,
                                            color = Color(0xFF2E4035)
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Button(
                                            onClick = { viewModel.startGeneralBcs200ModelTest() },
                                            colors = ButtonDefaults.buttonColors(containerColor = BcsEmeraldPrimary),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("start_general_bcs_ai_200_btn")
                                        ) {
                                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("সাধারণ বিসিএস AI ২০০ মার্কস টেস্ট শুরু করুন", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // AI State Handling
                    when (val state = aiState) {
                        is AiGenerationState.Generating -> {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(18.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(32.dp),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(14.dp))
                                        Column {
                                            Text(
                                                text = "AI প্রশ্ন তৈরি করছে...",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            Text(
                                                text = state.statusMessage,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        is AiGenerationState.Error -> {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = WrongRed.copy(alpha = 0.15f))
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = "প্রশ্ন তৈরিতে সমস্যা হয়েছে",
                                            fontWeight = FontWeight.Bold,
                                            color = WrongRed,
                                            fontSize = 14.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = state.message,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Button(
                                            onClick = {
                                                viewModel.generateQuestionsWithAi(
                                                    subject = aiSubject,
                                                    topic = aiTopic,
                                                    count = aiQuestionCount,
                                                    difficulty = aiDifficulty
                                                )
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = WrongRed)
                                        ) {
                                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("পুনরায় চেষ্টা করুন")
                                        }
                                    }
                                }
                            }
                        }

                        is AiGenerationState.Success -> {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = CorrectGreenContainer)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = CorrectGreen,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "✨ ${state.questions.size} টি AI প্রশ্ন তৈরি সম্পন্ন!",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp,
                                                    color = CorrectGreen
                                                )
                                            }

                                            IconButton(onClick = { viewModel.resetAiGenerationState() }) {
                                                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = CorrectGreen)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = { viewModel.saveAllAiGeneratedQuestions() },
                                                colors = ButtonDefaults.buttonColors(containerColor = CorrectGreen),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .testTag("save_all_ai_mcq_btn")
                                            ) {
                                                Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("সব সেভ করুন", fontSize = 12.sp)
                                            }

                                            OutlinedButton(
                                                onClick = { viewModel.startPracticeOnAiQuestions() },
                                                border = BorderStroke(1.dp, CorrectGreen),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .testTag("practice_ai_mcqs_btn")
                                            ) {
                                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = CorrectGreen, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("প্র্যাকটিস করুন", fontSize = 12.sp, color = CorrectGreen)
                                            }
                                        }
                                    }
                                }
                            }

                            // Render generated question preview cards
                            items(state.questions) { genQ ->
                                GeneratedQuestionCard(
                                    item = genQ,
                                    onSaveSingle = {
                                        viewModel.saveNewQuestion(
                                            questionText = genQ.question,
                                            optionA = genQ.optionA,
                                            optionB = genQ.optionB,
                                            optionC = genQ.optionC,
                                            optionD = genQ.optionD,
                                            correctOption = genQ.correctOption,
                                            subject = genQ.subject,
                                            topic = genQ.topic,
                                            bcsTag = genQ.bcsTag,
                                            difficulty = genQ.difficulty,
                                            explanation = genQ.explanation
                                        )
                                    }
                                )
                            }
                        }

                        AiGenerationState.Idle -> {
                            // Idle state
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }

            1 -> {
                // TAB 1: MANUAL MCQ CREATOR FORM
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (editingQuestion != null) "প্রশ্নটি সংশোধন করুন" else "বিসিএস মডেল MCQ তৈরি করুন",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            OutlinedButton(
                                onClick = { fillSampleTemplate() },
                                contentPadding = ButtonDefaults.TextButtonContentPadding,
                                modifier = Modifier.testTag("fill_sample_mcq_btn")
                            ) {
                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("নমুনা ডেমো", fontSize = 12.sp)
                            }
                        }
                    }

                    // Error alert
                    if (validationError != null) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = WrongRed.copy(alpha = 0.15f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = validationError!!,
                                    color = WrongRed,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }

                    // Question Statement Input
                    item {
                        Text(
                            text = "১. মূল প্রশ্ন (Question Statement) *",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = questionText,
                            onValueChange = { questionText = it; validationError = null },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("mcq_question_input"),
                            placeholder = { Text("প্রশ্ন লিখুন (বাংলা বা ইংরেজি)... যেমন: চর্যাপদ কত সালে আবিষ্কৃত হয়?") },
                            minLines = 2,
                            maxLines = 4,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Subject Dropdown
                    item {
                        Text(
                            text = "২. বিসিএস বিষয় (Subject) *",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        ExposedDropdownMenuBox(
                            expanded = subjectMenuExpanded,
                            onExpandedChange = { subjectMenuExpanded = !subjectMenuExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedSubject,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectMenuExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                                    .testTag("subject_dropdown"),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = subjectMenuExpanded,
                                onDismissRequest = { subjectMenuExpanded = false }
                            ) {
                                BcsSeedData.subjects.forEach { subject ->
                                    DropdownMenuItem(
                                        text = { Text(subject) },
                                        onClick = {
                                            selectedSubject = subject
                                            subjectMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // 4 Options (A, B, C, D) with Radio Button Selector for Correct Answer
                    item {
                        Text(
                            text = "৩. অপশনসমূহ এবং সঠিক উত্তর নির্বাচন করুন *",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "যে অপশনটি সঠিক, তার পাশের গোল চিহ্নে ক্লিক করুন।",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OptionInputRow(
                                label = "ক / A",
                                value = optionA,
                                onValueChange = { optionA = it },
                                isSelected = correctOption == 0,
                                onSelect = { correctOption = 0 },
                                testTag = "mcq_option_a_input",
                                radioTestTag = "correct_radio_a"
                            )
                            OptionInputRow(
                                label = "খ / B",
                                value = optionB,
                                onValueChange = { optionB = it },
                                isSelected = correctOption == 1,
                                onSelect = { correctOption = 1 },
                                testTag = "mcq_option_b_input",
                                radioTestTag = "correct_radio_b"
                            )
                            OptionInputRow(
                                label = "গ / C",
                                value = optionC,
                                onValueChange = { optionC = it },
                                isSelected = correctOption == 2,
                                onSelect = { correctOption = 2 },
                                testTag = "mcq_option_c_input",
                                radioTestTag = "correct_radio_c"
                            )
                            OptionInputRow(
                                label = "ঘ / D",
                                value = optionD,
                                onValueChange = { optionD = it },
                                isSelected = correctOption == 3,
                                onSelect = { correctOption = 3 },
                                testTag = "mcq_option_d_input",
                                radioTestTag = "correct_radio_d"
                            )
                        }
                    }

                    // Explanation Input
                    item {
                        Text(
                            text = "৪. ব্যাখ্যা ও শর্টকাট টেকনিক (Explanation) - ঐচ্ছিক",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = explanationText,
                            onValueChange = { explanationText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("mcq_explanation_input"),
                            placeholder = { Text("সঠিক উত্তরের যুক্তি, প্রাসঙ্গিক সাল, সূত্র বা শর্টকাট টেকনিক লিখুন...") },
                            minLines = 2,
                            maxLines = 4,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Topic & BCS Tag Inputs
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "টপিক / অধ্যায়",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = topicText,
                                    onValueChange = { topicText = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("topic_input"),
                                    placeholder = { Text("যেমন: প্রাচীন যুগ") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "বিসিএস ট্যাগ",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = bcsTagText,
                                    onValueChange = { bcsTagText = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("bcs_tag_input"),
                                    placeholder = { Text("45th BCS / Self Made") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }

                    // Difficulty Selector
                    item {
                        Text(
                            text = "কঠিনতার মাত্রা (Difficulty)",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Easy", "Medium", "Hard").forEach { diff ->
                                FilterChip(
                                    selected = selectedDifficulty == diff,
                                    onClick = { selectedDifficulty = diff },
                                    label = {
                                        val banglaDiff = when (diff) {
                                            "Easy" -> "সহজ (Easy)"
                                            "Medium" -> "মাঝারি (Medium)"
                                            else -> "কঠিন (Hard)"
                                        }
                                        Text(banglaDiff)
                                    }
                                )
                            }
                        }
                    }

                    // Submit & Clear Buttons
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (editingQuestion != null) {
                                OutlinedButton(
                                    onClick = { clearManualForm() },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp)
                                ) {
                                    Text("বাতিল")
                                }
                            }

                            Button(
                                onClick = {
                                    if (questionText.isBlank()) {
                                        validationError = "অনুগ্রহ করে প্রশ্নটি লিখুন।"
                                        return@Button
                                    }
                                    if (optionA.isBlank() || optionB.isBlank() || optionC.isBlank() || optionD.isBlank()) {
                                        validationError = "অনুগ্রহ করে ৪টি অপশনই (ক, খ, গ, ঘ) পূরণ করুন।"
                                        return@Button
                                    }

                                    viewModel.saveNewQuestion(
                                        questionText = questionText,
                                        optionA = optionA,
                                        optionB = optionB,
                                        optionC = optionC,
                                        optionD = optionD,
                                        correctOption = correctOption,
                                        subject = selectedSubject,
                                        topic = topicText,
                                        bcsTag = bcsTagText,
                                        difficulty = selectedDifficulty,
                                        explanation = explanationText
                                    )
                                    clearManualForm()
                                    selectedTab = 2 // Switch to list tab to see it!
                                },
                                modifier = Modifier
                                    .weight(if (editingQuestion != null) 2f else 1f)
                                    .height(50.dp)
                                    .testTag("save_mcq_button"),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(
                                    imageVector = if (editingQuestion != null) Icons.Default.Check else Icons.Default.CheckCircle,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (editingQuestion != null) "আপডেট করুন" else "MCQ সংরক্ষণ করুন",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }

            2 -> {
                // TAB 2: MY CREATED MCQs LIST
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "আপনার তৈরি প্রশ্ন ভাণ্ডার",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = "মোট ${userCreatedQuestions.size} টি প্রশ্ন সংরক্ষিত",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (userCreatedQuestions.isNotEmpty()) {
                                Button(
                                    onClick = {
                                        viewModel.startPractice("My MCQs Drill", userCreatedQuestions)
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("practice_my_questions_btn")
                                ) {
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("প্র্যাকটিস")
                                }
                            }
                        }
                    }

                    if (userCreatedQuestions.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "এখনও কোনো প্রশ্ন তৈরি করা হয়নি",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "AI দিয়ে স্বয়ংক্রিয়ভাবে প্রশ্ন তৈরি করুন অথবা নিজে প্রশ্ন লিখুন।",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = { selectedTab = 0 },
                                            modifier = Modifier.testTag("ai_create_tab_btn")
                                        ) {
                                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("AI অটো তৈরি")
                                        }
                                        OutlinedButton(
                                            onClick = { selectedTab = 1 },
                                            modifier = Modifier.testTag("manual_create_tab_btn")
                                        ) {
                                            Text("নিজে লিখুন")
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        items(userCreatedQuestions, key = { it.id }) { q ->
                            CreatedMcqCard(
                                question = q,
                                onEdit = { viewModel.startEditing(q) },
                                onDelete = { questionToDelete = q },
                                onToggleBookmark = { viewModel.toggleBookmark(q) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (questionToDelete != null) {
        AlertDialog(
            onDismissRequest = { questionToDelete = null },
            title = { Text("প্রশ্নটি মুছতে চান?") },
            text = { Text("এই প্রশ্নটি আপনার তালিকা থেকে স্থায়ীভাবে মুছে যাবে।") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteQuestion(questionToDelete!!)
                        questionToDelete = null
                    }
                ) {
                    Text("মুছে ফেলুন", color = WrongRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { questionToDelete = null }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@Composable
private fun GeneratedQuestionCard(
    item: GeneratedMcqItem,
    onSaveSingle: () -> Unit
) {
    var isSaved by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = item.subject,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = BcsGoldAccent.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "✨ AI",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Button(
                    onClick = {
                        if (!isSaved) {
                            onSaveSingle()
                            isSaved = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSaved) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Check else Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isSaved) "সংরক্ষিত" else "সেভ", fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.question,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))
            val options = listOf(item.optionA, item.optionB, item.optionC, item.optionD)
            options.forEachIndexed { idx, opt ->
                val isCorrect = idx == item.correctOption
                val prefix = when (idx) {
                    0 -> "ক"
                    1 -> "খ"
                    2 -> "গ"
                    else -> "ঘ"
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isCorrect) CorrectGreenContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "($prefix)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isCorrect) CorrectGreen else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = opt,
                            fontSize = 13.sp,
                            fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCorrect) CorrectGreen else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        if (isCorrect) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Correct",
                                tint = CorrectGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            if (item.explanation.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "ব্যাখ্যা: ${item.explanation}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            if (item.referenceBook.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BcsGoldAccent.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, BcsGoldAccent.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "📚 প্রামাণ্য গ্রন্থ রেফারেন্স: ${item.referenceBook}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF5D4037),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun OptionInputRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isSelected: Boolean,
    onSelect: () -> Unit,
    testTag: String,
    radioTestTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) BcsEmeraldPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) CorrectGreenContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                modifier = Modifier.testTag(radioTestTag)
            )
            Surface(
                shape = CircleShape,
                color = if (isSelected) BcsEmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .testTag(testTag),
                placeholder = { Text("অপশন লিখুন...") },
                singleLine = true
            )
        }
    }
}

@Composable
private fun CreatedMcqCard(
    question: McqQuestion,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleBookmark: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("created_mcq_card_${question.id}"),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Badges row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = question.subject,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    if (question.bcsTag.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (question.bcsTag.contains("AI")) BcsGoldAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = if (question.bcsTag.contains("AI")) "✨ AI Generated" else question.bcsTag,
                                fontSize = 11.sp,
                                fontWeight = if (question.bcsTag.contains("AI")) FontWeight.Bold else FontWeight.Normal,
                                color = if (question.bcsTag.contains("AI")) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Row {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = WrongRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Question
            Text(
                text = question.question,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Options with correct indicated
            val options = question.getOptions()
            options.forEachIndexed { idx, opt ->
                val isCorrect = idx == question.correctOption
                val prefix = when (idx) {
                    0 -> "ক"
                    1 -> "খ"
                    2 -> "গ"
                    else -> "ঘ"
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isCorrect) CorrectGreenContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "($prefix)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isCorrect) CorrectGreen else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = opt,
                            fontSize = 13.sp,
                            color = if (isCorrect) CorrectGreen else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.weight(1f)
                        )
                        if (isCorrect) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Correct Answer",
                                tint = CorrectGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Explanation Section
            if (question.explanation.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isExpanded = !isExpanded },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isExpanded) "ব্যাখ্যা লুকান" else "ব্যাখ্যা দেখুন",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                AnimatedVisibility(visible = isExpanded) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                    ) {
                        Text(
                            text = question.explanation,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }
    }
}
