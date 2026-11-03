package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.OutlinedFlag
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BcsSeedData
import com.example.ui.BcsViewModel
import com.example.ui.Screen
import com.example.ui.theme.BcsEmeraldDark
import com.example.ui.theme.BcsEmeraldPrimary
import com.example.ui.theme.BcsGoldAccent
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.CorrectGreenContainer
import com.example.ui.theme.WrongRed
import com.example.ui.theme.WrongRedContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelTestScreen(
    viewModel: BcsViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.modelTestState.collectAsState()
    val allQuestions by viewModel.allQuestions.collectAsState()

    var showSubmitDialog by remember { mutableStateOf(false) }
    var selectedQuestionCount by remember { mutableIntStateOf(20) }
    var selectedSubjectFilter by remember { mutableStateOf<String?>(null) }
    var isReviewingAnswers by remember { mutableStateOf(false) }

    var testModeTab by remember { mutableIntStateOf(0) }
    var specialBcsQuestionCount by remember { mutableIntStateOf(200) }

    if (!state.isRunning && !state.isSubmitted) {
        // SETUP SCREEN
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.HOME) },
                        modifier = Modifier.testTag("model_test_back_btn")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = "বিসিএস প্রিলিমিনারি মডেল টেস্ট",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Dual Mode Tabs: 50th Special BCS Health vs General BCS
            item {
                SecondaryTabRow(
                    selectedTabIndex = testModeTab,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = testModeTab == 0,
                        onClick = { testModeTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.MedicalServices,
                                    contentDescription = null,
                                    tint = if (testModeTab == 0) Color(0xFFD32F2F) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "৫০তম বিশেষ বিসিএস (স্বাস্থ্য)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    )
                    Tab(
                        selected = testModeTab == 1,
                        onClick = { testModeTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = if (testModeTab == 1) BcsEmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "সাধারণ বিসিএস",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    )
                }
            }

            if (testModeTab == 0) {
                // 50TH SPECIAL BCS HEALTH CADRE 200 MARKS TEST SECTION
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("50th_special_bcs_setup_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF2F2)),
                        border = BorderStroke(1.5.dp, Color(0xFFFFCDD2))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFD32F2F),
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.MedicalServices,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "৫০তম বিশেষ বিসিএস (স্বাস্থ্য ক্যাডার)",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp,
                                        color = Color(0xFFB71C1C)
                                    )
                                    Text(
                                        text = "মেডিকেল অফিসার পদের জন্য অফিশিয়াল সিলেবাস অনুযায়ী",
                                        fontSize = 11.sp,
                                        color = Color(0xFF5D4037)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 50-50 Split Badge Box
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFFFCDD2)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "📊 ৫০তম বিসিএস ২০০ নম্বরের সুনির্দিষ্ট বিভাজন:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFFB71C1C)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "১. মেডিকেল সেক্টর (১০০ মার্কস): এনাটমি, ফিজিওলজি, ফার্মাকোলজি, প্যাথলজি, মাইক্রোবায়োলজি, কমিউনিটি মেডিসিন ও EPI, মেডিসিন ও পেডিয়াট্রিক্স, সার্জারি, গাইনি ও ফরেনসিক মেডিসিন। (রেফারেন্স: Bailey & Love, Davidson, Robbins, Guyton, Katzung, Park's PSM, DC Dutta)",
                                        fontSize = 11.sp,
                                        lineHeight = 16.sp,
                                        color = Color(0xFF424242)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "২. জেনারেল পার্ট (১০০ মার্কস): বাংলা ২০ (ড. সৌমিত্র শেখর), ইংরেজি ২০ (Wren & Martin), বাংলাদেশ ২০ (সংবিধান), আন্তর্জাতিক ২০ (অধ্যাপক তারেক শামসুর রেহমান), গাণিতিক যুক্তি ও মানসিক দক্ষতা ২০।",
                                        fontSize = 11.sp,
                                        lineHeight = 16.sp,
                                        color = Color(0xFF424242)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Test Duration and rules info
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = "মোট নম্বর", fontSize = 11.sp, color = Color(0xFF5D4037))
                                    Text(text = "$specialBcsQuestionCount নম্বর", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color(0xFFB71C1C))
                                }
                                Column {
                                    Text(text = "পরীক্ষার সময়", fontSize = 11.sp, color = Color(0xFF5D4037))
                                    Text(
                                        text = if (specialBcsQuestionCount >= 200) "১২০ মিনিট (২ ঘণ্টা)" else if (specialBcsQuestionCount >= 50) "৩০ মিনিট" else "১২ মিনিট",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp,
                                        color = Color(0xFFB71C1C)
                                    )
                                }
                                Column {
                                    Text(text = "নেগেটিভ মার্ক", fontSize = 11.sp, color = Color(0xFF5D4037))
                                    Text(text = "-০.৫ প্রতি ভুল", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = WrongRed)
                                }
                            }
                        }
                    }
                }

                // Question Count presets for 50th Special BCS
                item {
                    Text(
                        text = "টেস্টের পরিধি নির্বাচন করুন",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple(200, "২০০ প্রশ্ন (১২০ মি.)", "১০০ মেডিকেল + ১০০ জেনারেল"),
                            Triple(50, "৫০ প্রশ্ন (৩০ মি.)", "২৫ মেডিকেল + ২৫ জেনারেল"),
                            Triple(20, "২০ প্রশ্ন (১২ মি.)", "১০ মেডিকেল + ১০ জেনারেল")
                        ).forEach { (count, label, sub) ->
                            FilterChip(
                                selected = specialBcsQuestionCount == count,
                                onClick = { specialBcsQuestionCount = count },
                                label = {
                                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                        Text(label, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text(sub, fontSize = 9.sp)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Launch 50th BCS Model Test Button
                item {
                    Button(
                        onClick = { viewModel.start50thSpecialBcsModelTest(specialBcsQuestionCount) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("start_50th_special_bcs_test_btn"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "৫০তম বিশেষ বিসিএস AI ($specialBcsQuestionCount মার্কস) টেস্ট শুরু করুন",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                // GENERAL BCS MODEL TEST
                // 200 Marks AI Full Syllabus Model Test Hero Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("general_bcs_200_hero_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.5.dp, BcsEmeraldPrimary.copy(alpha = 0.6f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = BcsEmeraldPrimary.copy(alpha = 0.15f),
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = BcsEmeraldPrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "সাধারণ বিসিএস ২০০ মার্কস AI মডেল টেস্ট",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp,
                                        color = BcsEmeraldDark
                                    )
                                    Text(
                                        text = "বিপিএসসি'র অফিশিয়াল সিলেবাস অনুযায়ী ১০টি বিষয়ের পূর্ণাঙ্গ টেস্ট",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "📋 ১০টি বিষয়ের অফিশিয়াল নম্বর বণ্টন (২০০ প্রশ্ন):",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = BcsEmeraldDark
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "বাংলা ৩৫ • ইংরেজি ৩৫ • বাংলাদেশ ৩০ • আন্তর্জাতিক ২০ • ভূগোল ১০ • বিজ্ঞান ১৫ • কম্পিউটার ১৫ • গণিত ১৫ • মানসিক দক্ষতা ১৫ • নৈতিকতা ১০",
                                        fontSize = 11.sp,
                                        lineHeight = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "⏱️ মোট সময়: ১২০ মিনিট (২ ঘণ্টা) | নেগেটিভ মার্কিং: -০.৫ নম্বর প্রতি ভুল",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFC2185B)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { viewModel.startGeneralBcs200ModelTest() },
                                colors = ButtonDefaults.buttonColors(containerColor = BcsEmeraldPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("start_general_bcs_200_btn")
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "সাধারণ বিসিএস AI ২০০ মার্কস টেস্ট শুরু করুন",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Custom / Practice Model Test Header
                item {
                    Text(
                        text = "কাস্টম মডেল টেস্ট ও বিষয়ভিত্তিক অনুশীলন",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                // Select Questions Count
                item {
                    Text(
                        text = "প্রশ্নের সংখ্যা নির্বাচন করুন",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(10, 20, 50, 100, 200).forEach { count ->
                            FilterChip(
                                selected = selectedQuestionCount == count,
                                onClick = { selectedQuestionCount = count },
                                label = { Text("$count টি", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Select Subject
                item {
                    Text(
                        text = "বিষয় নির্বাচন করুন",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = selectedSubjectFilter == null,
                            onClick = { selectedSubjectFilter = null },
                            label = { Text("সকল বিষয় (সম্পূর্ণ সিলেবাস)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        BcsSeedData.subjects.filter { !it.contains("মেডিকেল") }.take(6).forEach { subj ->
                            FilterChip(
                                selected = selectedSubjectFilter == subj,
                                onClick = { selectedSubjectFilter = subj },
                                label = { Text(subj) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Start Button
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            if (selectedQuestionCount == 200 && selectedSubjectFilter == null) {
                                viewModel.startGeneralBcs200ModelTest()
                            } else {
                                val title = selectedSubjectFilter ?: "BCS Custom Model Test"
                                viewModel.startModelTest(title, selectedQuestionCount, selectedSubjectFilter)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("start_model_test_btn"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "মডেল টেস্ট শুরু করুন ($selectedQuestionCount টি প্রশ্ন)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    } else if (state.isRunning) {
        // ACTIVE RUNNING TEST
        val currentQ = state.questions[state.currentIndex]
        val minutes = state.timeRemainingSeconds / 60
        val seconds = state.timeRemainingSeconds % 60
        val timeFormatted = String.format("%02d:%02d", minutes, seconds)
        val isLowTime = state.timeRemainingSeconds <= 60
        val isFlagged = state.flaggedForReview.contains(state.currentIndex)

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Timer & Status Bar
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Timer Chip
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isLowTime) WrongRedContainer else MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = if (isLowTime) WrongRed else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = timeFormatted,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = if (isLowTime) WrongRed else MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Flag button
                    IconButton(
                        onClick = { viewModel.toggleFlagQuestion(state.currentIndex) },
                        modifier = Modifier.testTag("flag_question_btn")
                    ) {
                        Icon(
                            imageVector = if (isFlagged) Icons.Default.Flag else Icons.Default.OutlinedFlag,
                            contentDescription = "Flag for review",
                            tint = if (isFlagged) BcsGoldAccent else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Submit Test Button
                    Button(
                        onClick = { showSubmitDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BcsEmeraldPrimary
                        ),
                        modifier = Modifier.testTag("submit_test_btn")
                    ) {
                        Text("জমা দিন (Submit)", fontSize = 12.sp)
                    }
                }
            }

            // Question Navigator Strip
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(state.questions.size) { idx ->
                        val isAnswered = state.userAnswers.containsKey(idx)
                        val isCurrent = idx == state.currentIndex
                        val isItemFlagged = state.flaggedForReview.contains(idx)

                        Surface(
                            onClick = { viewModel.setTestCurrentIndex(idx) },
                            shape = CircleShape,
                            color = when {
                                isCurrent -> MaterialTheme.colorScheme.primary
                                isItemFlagged -> BcsGoldAccent
                                isAnswered -> CorrectGreen
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${idx + 1}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        isCurrent || isAnswered -> Color.White
                                        isItemFlagged -> Color.Black
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Question Statement Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "প্রশ্ন ${state.currentIndex + 1} / ${state.questions.size}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (state.is50thSpecialBcs) {
                                val isMed = state.currentIndex < state.medicalTotal
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isMed) Color(0xFFD32F2F) else BcsEmeraldPrimary
                                ) {
                                    Text(
                                        text = if (isMed) "🩺 মেডিকেল সেক্টর (১-১০০)" else "📚 জেনারেল পার্ট (১০১-২০০)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                    )
                                }
                            } else {
                                Text(
                                    text = currentQ.subject,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = currentQ.question,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 24.sp
                        )
                    }
                }
            }

            // 4 Options (Tap to select / change)
            items(4) { optIdx ->
                val optText = when (optIdx) {
                    0 -> currentQ.optionA
                    1 -> currentQ.optionB
                    2 -> currentQ.optionC
                    else -> currentQ.optionD
                }
                val prefix = when (optIdx) {
                    0 -> "ক"
                    1 -> "খ"
                    2 -> "গ"
                    else -> "ঘ"
                }

                val isSelected = state.userAnswers[state.currentIndex] == optIdx

                Card(
                    onClick = { viewModel.selectTestAnswer(state.currentIndex, optIdx) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("test_option_$optIdx"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) BcsEmeraldPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) BcsEmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = prefix,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = optText,
                            fontSize = 15.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Navigation Row
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { viewModel.setTestCurrentIndex(state.currentIndex - 1) },
                        enabled = state.currentIndex > 0
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("পূর্ববর্তী")
                    }

                    Button(
                        onClick = {
                            if (state.currentIndex + 1 < state.questions.size) {
                                viewModel.setTestCurrentIndex(state.currentIndex + 1)
                            } else {
                                showSubmitDialog = true
                            }
                        }
                    ) {
                        Text(if (state.currentIndex + 1 < state.questions.size) "পরবর্তী" else "জমা দিন")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Submit Dialog
        if (showSubmitDialog) {
            val answeredCount = state.userAnswers.size
            val unansweredCount = state.questions.size - answeredCount

            AlertDialog(
                onDismissRequest = { showSubmitDialog = false },
                title = { Text("মডেল টেস্ট জমা দিতে চান?") },
                text = {
                    Text(
                        "মোট প্রশ্ন: ${state.questions.size} টি\n" +
                        "উত্তর দিয়েছেন: $answeredCount টি\n" +
                        "অনুত্তরিত: $unansweredCount টি\n\n" +
                        "ভুল উত্তরের জন্য -০.৫ নম্বর কাটা যাবে।"
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showSubmitDialog = false
                            viewModel.submitModelTest()
                        }
                    ) {
                        Text("হ্যাঁ, জমা দিন")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSubmitDialog = false }) {
                        Text("পুনরায় পরীক্ষা করুন")
                    }
                }
            )
        }
    } else {
        // RESULTS SCORECARD SCREEN
        val isPassed = state.score >= (state.questions.size * 0.6)

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { viewModel.navigateTo(Screen.HOME) }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = "মডেল টেস্ট ফলাফল ও বিশ্লেষণ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Result Score Hero Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isPassed) CorrectGreenContainer else WrongRedContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isPassed) CorrectGreen else WrongRed,
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isPassed) Icons.Default.Check else Icons.Default.Close,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isPassed) "প্রিলিমিনারি পাস কাট-অফ অর্জিত! 🎉" else "আরো অনুশীলনের প্রয়োজন",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isPassed) CorrectGreen else WrongRed
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = String.format("%.2f", state.score) + " / " + state.questions.size + ".0",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "প্রাপ্ত মোট নম্বর (নেগেটিভ মার্কিং সহ)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats Summary Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ResultStat(
                                label = "সঠিক (+১)",
                                value = "${state.correctCount}",
                                color = CorrectGreen
                            )
                            ResultStat(
                                label = "ভুল (-০.৫)",
                                value = "${state.wrongCount} (-${state.wrongCount * 0.5})",
                                color = WrongRed
                            )
                            ResultStat(
                                label = "অনুত্তরিত (০)",
                                value = "${state.unansweredCount}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // 50th Special BCS Sectional Breakdown
            if (state.is50thSpecialBcs) {
                item {
                    Text(
                        text = "৫০তম বিশেষ বিসিএস সেকশনভিত্তিক স্কোর বিশ্লেষণ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF2F2)),
                            border = BorderStroke(1.dp, Color(0xFFFFCDD2))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "🩺 মেডিকেল সেক্টর",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFFB71C1C)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${String.format("%.1f", state.medicalScore)} / ${state.medicalTotal}.0",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFB71C1C)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "সঠিক: ${state.medicalCorrect} | ভুল: ${state.medicalWrong} (-${state.medicalWrong * 0.5})",
                                    fontSize = 10.sp,
                                    color = Color(0xFF5D4037)
                                )
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "📚 জেনারেল পার্ট",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${String.format("%.1f", state.generalScore)} / ${state.generalTotal}.0",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "সঠিক: ${state.generalCorrect} | ভুল: ${state.generalWrong} (-${state.generalWrong * 0.5})",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Action Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.retakeCurrentTest() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("পুনরায় দিন")
                    }

                    Button(
                        onClick = { isReviewingAnswers = !isReviewingAnswers },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isReviewingAnswers) "ব্যাখ্যা লুকান" else "উত্তর ও ব্যাখ্যা দেখুন")
                    }
                }
            }

            // Detailed Solution Review
            if (isReviewingAnswers) {
                item {
                    Text(
                        text = "প্রশ্নোত্তর ও বিস্তারিত ব্যাখ্যা",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                itemsIndexed(state.questions) { idx, q ->
                    val userAns = state.userAnswers[idx]
                    val isCorrect = userAns == q.correctOption

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "প্রশ্ন ${idx + 1}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    if (state.is50thSpecialBcs) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        val isMed = idx < state.medicalTotal
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (isMed) Color(0xFFFFF2F2) else MaterialTheme.colorScheme.secondaryContainer
                                        ) {
                                            Text(
                                                text = if (isMed) "মেডিকেল" else "জেনারেল",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isMed) Color(0xFFB71C1C) else MaterialTheme.colorScheme.onSecondaryContainer,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = when {
                                        userAns == null -> MaterialTheme.colorScheme.surfaceVariant
                                        isCorrect -> CorrectGreenContainer
                                        else -> WrongRedContainer
                                    }
                                ) {
                                    Text(
                                        text = when {
                                            userAns == null -> "ছেড়ে দেওয়া হয়েছে"
                                            isCorrect -> "সঠিক (+১.০)"
                                            else -> "ভুল (-০.৫)"
                                        },
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when {
                                            userAns == null -> MaterialTheme.colorScheme.onSurfaceVariant
                                            isCorrect -> CorrectGreen
                                            else -> WrongRed
                                        },
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = q.question, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "সঠিক উত্তর: ${q.getCorrectAnswerText()}",
                                color = CorrectGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )

                            if (q.explanation.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                val hasCitation = q.explanation.contains("📚 রেফারেন্স:")
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (hasCitation) BcsGoldAccent.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = if (hasCitation) BorderStroke(1.dp, BcsGoldAccent.copy(alpha = 0.4f)) else null,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "ব্যাখ্যা: ${q.explanation}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ResultStat(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
