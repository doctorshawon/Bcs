package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.McqQuestion
import com.example.ui.BcsViewModel
import com.example.ui.Screen
import com.example.ui.theme.BcsEmeraldPrimary
import com.example.ui.theme.BcsGoldAccent
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.CorrectGreenContainer
import com.example.ui.theme.WrongRed
import com.example.ui.theme.WrongRedContainer

@Composable
fun PracticeScreen(
    viewModel: BcsViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.practiceState.collectAsState()

    if (state.questions.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("কোনো প্রশ্ন পাওয়া যায়নি", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { viewModel.navigateTo(Screen.HOME) }) {
                    Text("হোমে ফিরে যান")
                }
            }
        }
        return
    }

    val currentQ = state.questions[state.currentIndex]
    val progress = (state.currentIndex + 1).toFloat() / state.questions.size

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Bar
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.HOME) },
                    modifier = Modifier.testTag("practice_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = state.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1
                    )
                    Text(
                        text = "প্রশ্ন ${state.currentIndex + 1} / ${state.questions.size}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = { viewModel.toggleBookmark(currentQ) },
                    modifier = Modifier.testTag("practice_bookmark_btn")
                ) {
                    Icon(
                        imageVector = if (currentQ.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (currentQ.isBookmarked) BcsGoldAccent else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Progress Indicator
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = BcsEmeraldPrimary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        // Question Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("practice_question_card"),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Meta tags
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = currentQ.subject,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        if (currentQ.bcsTag.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = currentQ.bcsTag,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        if (currentQ.isUserCreated) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = BcsGoldAccent.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "Make MCQ",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = currentQ.question,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp
                    )
                }
            }
        }

        // 4 Options
        items(4) { optionIndex ->
            val optionText = when (optionIndex) {
                0 -> currentQ.optionA
                1 -> currentQ.optionB
                2 -> currentQ.optionC
                else -> currentQ.optionD
            }
            val prefix = when (optionIndex) {
                0 -> "ক"
                1 -> "খ"
                2 -> "গ"
                else -> "ঘ"
            }

            val isSelected = state.selectedOption == optionIndex
            val isCorrect = optionIndex == currentQ.correctOption

            val containerColor by animateColorAsState(
                targetValue = when {
                    state.isRevealed && isCorrect -> CorrectGreenContainer
                    state.isRevealed && isSelected && !isCorrect -> WrongRedContainer
                    isSelected -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.surface
                },
                label = "opt_bg"
            )

            val borderColor = when {
                state.isRevealed && isCorrect -> CorrectGreen
                state.isRevealed && isSelected && !isCorrect -> WrongRed
                isSelected -> BcsEmeraldPrimary
                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            }

            Card(
                onClick = {
                    if (!state.isRevealed) {
                        viewModel.selectPracticeOption(optionIndex)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("practice_option_$optionIndex"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = containerColor),
                border = BorderStroke(if (isSelected || (state.isRevealed && isCorrect)) 2.dp else 1.dp, borderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = when {
                            state.isRevealed && isCorrect -> CorrectGreen
                            state.isRevealed && isSelected && !isCorrect -> WrongRed
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (state.isRevealed && isCorrect) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Correct",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else if (state.isRevealed && isSelected && !isCorrect) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Wrong",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Text(
                                    text = prefix,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = optionText,
                        fontSize = 15.sp,
                        fontWeight = if (isSelected || (state.isRevealed && isCorrect)) FontWeight.Bold else FontWeight.Normal,
                        color = when {
                            state.isRevealed && isCorrect -> CorrectGreen
                            state.isRevealed && isSelected && !isCorrect -> WrongRed
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Explanation (Shows when answered)
        item {
            AnimatedVisibility(visible = state.isRevealed) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("practice_explanation_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (state.selectedOption == currentQ.correctOption)
                            CorrectGreenContainer.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "সঠিক উত্তর: " + when (currentQ.correctOption) {
                                    0 -> "ক (${currentQ.optionA})"
                                    1 -> "খ (${currentQ.optionB})"
                                    2 -> "গ (${currentQ.optionC})"
                                    else -> "ঘ (${currentQ.optionD})"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }

                        if (currentQ.explanation.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentQ.explanation,
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
            }
        }

        // Navigation & Score Tally
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { viewModel.prevPracticeQuestion() },
                    enabled = state.currentIndex > 0,
                    modifier = Modifier.testTag("practice_prev_btn")
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("পূর্ববর্তী")
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CorrectGreenContainer
                    ) {
                        Text(
                            text = "সঠিক: ${state.correctCount}",
                            color = CorrectGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = WrongRedContainer
                    ) {
                        Text(
                            text = "ভুল: ${state.wrongCount}",
                            color = WrongRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Button(
                    onClick = {
                        if (state.currentIndex + 1 < state.questions.size) {
                            viewModel.nextPracticeQuestion()
                        } else {
                            viewModel.navigateTo(Screen.HOME)
                        }
                    },
                    modifier = Modifier.testTag("practice_next_btn")
                ) {
                    Text(if (state.currentIndex + 1 < state.questions.size) "পরবর্তী" else "সম্পন্ন")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
