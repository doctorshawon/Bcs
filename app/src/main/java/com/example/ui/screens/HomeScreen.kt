package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Language
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BcsSeedData
import com.example.data.McqQuestion
import com.example.ui.BcsViewModel
import com.example.ui.Screen
import com.example.ui.theme.BcsEmeraldDark
import com.example.ui.theme.BcsEmeraldPrimary
import com.example.ui.theme.BcsGoldAccent
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.WrongRed

data class SubjectInfo(
    val name: String,
    val bcsMarks: Int,
    val icon: ImageVector,
    val color: Color
)

val BcsSubjectList = listOf(
    SubjectInfo("মেডিকেল সায়েন্স (Special BCS)", 100, Icons.Default.MedicalServices, Color(0xFFD32F2F)),
    SubjectInfo("বাংলা ভাষা ও সাহিত্য", 35, Icons.Default.MenuBook, Color(0xFF2E7D32)),
    SubjectInfo("English Language & Literature", 35, Icons.Default.Language, Color(0xFF1565C0)),
    SubjectInfo("বাংলাদেশ বিষয়াবলি", 30, Icons.Default.Public, Color(0xFFC62828)),
    SubjectInfo("আন্তর্জাতিক বিষয়াবলি", 20, Icons.Default.Public, Color(0xFF6A1B9A)),
    SubjectInfo("ভূগোল ও পরিবেশ", 10, Icons.Default.Terrain, Color(0xFF00838F)),
    SubjectInfo("সাধারণ বিজ্ঞান", 15, Icons.Default.Science, Color(0xFFE65100)),
    SubjectInfo("কম্পিউটার ও তথ্যপ্রযুক্তি", 15, Icons.Default.Computer, Color(0xFF283593)),
    SubjectInfo("গাণিতিক যুক্তি", 15, Icons.Default.Calculate, Color(0xFFAD1457)),
    SubjectInfo("মানসিক দক্ষতা", 15, Icons.Default.Psychology, Color(0xFF00695C)),
    SubjectInfo("নৈতিকতা ও সুশাসন", 10, Icons.Default.Security, Color(0xFF4E342E))
)

@Composable
fun HomeScreen(
    viewModel: BcsViewModel,
    modifier: Modifier = Modifier
) {
    val allQuestions by viewModel.allQuestions.collectAsState()
    val userCreatedCount by viewModel.userCreatedCount.collectAsState()
    val userCreatedQuestions by viewModel.userCreatedQuestions.collectAsState()
    val bookmarkedQuestions by viewModel.bookmarkedQuestions.collectAsState()
    val mistakeQuestions by viewModel.mistakeQuestions.collectAsState()
    val quizAttempts by viewModel.quizAttempts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    var isSearchActive by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_banner_card"),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(BcsEmeraldPrimary, BcsEmeraldDark)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "BCS MCQ PREP MASTER",
                                    color = BcsGoldAccent,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "বিসিএস প্রিলিমিনারি প্রস্তুতি",
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "২০০ নম্বরের পূর্ণাঙ্গ প্রস্তুতি ও প্রশ্ন নির্মাণ",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 13.sp
                                )
                            }
                            Surface(
                                shape = CircleShape,
                                color = BcsGoldAccent.copy(alpha = 0.2f),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.MenuBook,
                                        contentDescription = null,
                                        tint = BcsGoldAccent,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Stats Grid in Hero
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatBox(
                                title = "প্রশ্ন ভাণ্ডার",
                                value = "${allQuestions.size}",
                                subtitle = "মোট MCQ",
                                modifier = Modifier.weight(1f)
                            )
                            StatBox(
                                title = "স্বনির্মিত MCQ",
                                value = "$userCreatedCount",
                                subtitle = "Make MCQ",
                                modifier = Modifier.weight(1f)
                            )
                            StatBox(
                                title = "মডেল টেস্ট",
                                value = "${quizAttempts.size}",
                                subtitle = "দেওয়া হয়েছে",
                                modifier = Modifier.weight(1f)
                            )
                            StatBox(
                                title = "রিভিশন ব্যাংক",
                                value = "${bookmarkedQuestions.size + mistakeQuestions.size}",
                                subtitle = "সংরক্ষিত/ভুল",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Primary Call to Action: MAKE MCQ (Highlight User's Core Request)
        item {
            ElevatedCard(
                onClick = { viewModel.navigateTo(Screen.MAKE_MCQ) },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("make_mcq_card_button")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Make MCQ • নিজের প্রশ্ন তৈরি করুন",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "অপশন, ব্যাখ্যা, বিষয় ও ট্যাগ সহ প্রশ্ন যোগ করুন ($userCreatedCount টি তৈরি করা হয়েছে)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // AI Auto Question Generator Highlight Card
        item {
            Card(
                onClick = { viewModel.navigateTo(Screen.MAKE_MCQ) },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_auto_mcq_card_btn")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onTertiary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "AI অটো প্রশ্ন তৈরি করুন",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = BcsGoldAccent
                            ) {
                                Text(
                                    text = "Gemini",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "যেকোনো বিষয় বা টপিক থেকে নির্ভুল ৪টি অপশন ও বিশদ ব্যাখ্যাসহ MCQ জেনারেট করুন",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.85f)
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Go",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }

        // Quick Actions Row (Model Test, Practice User MCQs, Mistakes)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Timed Model Test Button
                Card(
                    onClick = { viewModel.startModelTest("BCS Quick Model Test", 20) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("start_quick_model_test_btn"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Quiz,
                                contentDescription = null,
                                tint = BcsEmeraldPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "মডেল টেস্ট",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "২০ প্রশ্ন • সময় ১২ মি. • -০.৫ নেগেটিভ মার্কিং",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Practice My Created MCQs Button
                Card(
                    onClick = {
                        if (userCreatedQuestions.isNotEmpty()) {
                            viewModel.startPractice("My Created MCQs Drill", userCreatedQuestions)
                        } else {
                            viewModel.navigateTo(Screen.MAKE_MCQ)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("practice_my_mcqs_btn"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color(0xFF1565C0),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "আমার প্রশ্ন ড্রিল",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (userCreatedCount > 0) "$userCreatedCount টি প্রশ্ন প্র্যাকটিস করুন" else "নতুন প্রশ্ন তৈরি করুন",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 50th Special BCS Health Cadre 200 Marks Premier Zone
        item {
            val medCount = allQuestions.count { it.subject.contains("মেডিকেল") || it.bcsTag.contains("Special BCS") }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("special_bcs_health_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF2F2)),
                border = BorderStroke(1.5.dp, Color(0xFFFFCDD2)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFD32F2F),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.MedicalServices,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "৫০তম বিশেষ বিসিএস (স্বাস্থ্য ক্যাডার)",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    color = Color(0xFFB71C1C)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFD32F2F)
                                ) {
                                    Text(
                                        text = "২০০ নম্বর",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "১০০ মেডিকেল (Bailey & Love, Davidson, Park, Katzung) + ১০০ জেনারেল (সৌমিত্র শেখর, সংবিধান)",
                                fontSize = 11.sp,
                                color = Color(0xFF5D4037),
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Buttons Row: 200 Marks Test + Syllabus Guide
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.start50thSpecialBcsModelTest(200) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("home_50th_bcs_200m_test_btn")
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("২০০ মার্কস টেস্ট", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.navigateTo(Screen.SYLLABUS_GUIDE) },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFD32F2F)),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("home_50th_bcs_syllabus_btn")
                        ) {
                            Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("সিলেবাস ও বই", fontSize = 12.sp, color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Search Bar for Questions
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    viewModel.setSearchQuery(it)
                    isSearchActive = it.isNotBlank()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_mcq_input"),
                placeholder = { Text("প্রশ্ন, বিষয় বা ট্যাগ খুঁজুন (যেমন: চর্যাপদ, 44th BCS, সংবিধান)...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )
        }

        // If search query is entered, show results
        if (searchQuery.isNotBlank()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "অনুসন্ধানের ফলাফল (${searchResults.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    if (searchResults.isNotEmpty()) {
                        Button(
                            onClick = { viewModel.startPractice("Search Results Drill", searchResults) },
                            modifier = Modifier.testTag("practice_search_results_btn")
                        ) {
                            Text("প্র্যাকটিস শুরু")
                        }
                    }
                }
            }

            items(searchResults.take(10)) { q ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.startPractice(q.subject, listOf(q)) },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = q.subject,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (q.bcsTag.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Text(
                                        text = q.bcsTag,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = q.question,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "সঠিক উত্তর: ${q.getCorrectAnswerText()}",
                            color = CorrectGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Section: Official 10 BCS Subjects Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "বিসিএস প্রিলিমিনারি সিলেবাস বিষয়াবলি",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "১০টি মূল বিষয়ের প্রশ্ন ভাণ্ডার ও প্র্যাকটিস",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                OutlinedButton(
                    onClick = { viewModel.navigateTo(Screen.SYLLABUS_GUIDE) },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("syllabus_guide_btn")
                ) {
                    Text("সিলেবাস গাইড", fontSize = 12.sp)
                }
            }
        }

        // Subject Cards
        items(BcsSubjectList) { subject ->
            val count = allQuestions.count { it.subject == subject.name }
            Card(
                onClick = {
                    val subjectQuestions = allQuestions.filter { it.subject == subject.name }
                    viewModel.startPractice(subject.name, subjectQuestions)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("subject_card_${subject.name}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = subject.color.copy(alpha = 0.15f),
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = subject.icon,
                                contentDescription = null,
                                tint = subject.color,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = subject.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "পরীক্ষার নম্বর: ${subject.bcsMarks}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "•",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$count টি প্রশ্ন প্রস্তুত",
                                fontSize = 12.sp,
                                color = BcsEmeraldPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Practice",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Recent Model Tests & Analytics (if any)
        if (quizAttempts.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "সাম্প্রতিক মডেল টেস্ট ফলাফল",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            items(quizAttempts.take(3)) { attempt ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = attempt.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "সঠিক: ${attempt.correctCount}  |  ভুল: ${attempt.wrongCount}  |  অনুত্তরিত: ${attempt.unansweredCount}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (attempt.score >= attempt.totalQuestions * 0.6) CorrectGreen.copy(alpha = 0.15f)
                            else WrongRed.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = String.format("%.1f", attempt.score) + " / " + attempt.totalQuestions,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = if (attempt.score >= attempt.totalQuestions * 0.6) CorrectGreen else WrongRed,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBox(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.12f)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                color = BcsGoldAccent,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = title,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 9.sp,
                maxLines = 1
            )
        }
    }
}
