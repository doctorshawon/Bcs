package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.McqRepository
import com.example.ui.BcsViewModel
import com.example.ui.BcsViewModelFactory
import com.example.ui.Screen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MakeMcqScreen
import com.example.ui.screens.ModelTestScreen
import com.example.ui.screens.PracticeScreen
import com.example.ui.screens.RevisionVaultScreen
import com.example.ui.screens.SyllabusGuideScreen
import com.example.ui.theme.MyApplicationTheme

@androidx.compose.material3.ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.example.util.configureAppCheck(this, intent)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = McqRepository(database.mcqDao())

        setContent {
            MyApplicationTheme {
                val bcsViewModel: BcsViewModel = viewModel(
                    factory = BcsViewModelFactory(repository)
                )

                BcsApp(viewModel = bcsViewModel)
            }
        }
    }
}

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun BcsApp(viewModel: BcsViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val userCreatedCount by viewModel.userCreatedCount.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    // Handle back button
    BackHandler(enabled = currentScreen != Screen.HOME) {
        viewModel.navigateTo(Screen.HOME)
    }

    // Hide bottom navigation during active timed model test or practice session for maximum focus
    val modelTestState by viewModel.modelTestState.collectAsState()
    val showBottomBar = !modelTestState.isRunning

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    modifier = Modifier.testTag("main_bottom_nav")
                ) {
                    NavigationBarItem(
                        selected = currentScreen == Screen.HOME,
                        onClick = { viewModel.navigateTo(Screen.HOME) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == Screen.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("হোম") },
                        modifier = Modifier.testTag("nav_home")
                    )

                    NavigationBarItem(
                        selected = currentScreen == Screen.MAKE_MCQ,
                        onClick = { viewModel.navigateTo(Screen.MAKE_MCQ) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (userCreatedCount > 0) {
                                        Badge { Text("$userCreatedCount") }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (currentScreen == Screen.MAKE_MCQ) Icons.Filled.EditNote else Icons.Outlined.EditNote,
                                    contentDescription = "Make MCQ"
                                )
                            }
                        },
                        label = { Text("Make MCQ") },
                        modifier = Modifier.testTag("nav_make_mcq")
                    )

                    NavigationBarItem(
                        selected = currentScreen == Screen.MODEL_TEST,
                        onClick = { viewModel.navigateTo(Screen.MODEL_TEST) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == Screen.MODEL_TEST) Icons.Filled.Quiz else Icons.Outlined.Quiz,
                                contentDescription = "Model Test"
                            )
                        },
                        label = { Text("মডেল টেস্ট") },
                        modifier = Modifier.testTag("nav_model_test")
                    )

                    NavigationBarItem(
                        selected = currentScreen == Screen.REVISION_VAULT,
                        onClick = { viewModel.navigateTo(Screen.REVISION_VAULT) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == Screen.REVISION_VAULT) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Vault"
                            )
                        },
                        label = { Text("রিভিশন") },
                        modifier = Modifier.testTag("nav_vault")
                    )

                    NavigationBarItem(
                        selected = currentScreen == Screen.SYLLABUS_GUIDE,
                        onClick = { viewModel.navigateTo(Screen.SYLLABUS_GUIDE) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == Screen.SYLLABUS_GUIDE) Icons.Filled.MenuBook else Icons.Outlined.MenuBook,
                                contentDescription = "Syllabus"
                            )
                        },
                        label = { Text("সিলেবাস") },
                        modifier = Modifier.testTag("nav_syllabus")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentScreen, label = "screen_fade") { screen ->
                when (screen) {
                    Screen.HOME -> HomeScreen(viewModel = viewModel)
                    Screen.MAKE_MCQ -> MakeMcqScreen(viewModel = viewModel)
                    Screen.PRACTICE -> PracticeScreen(viewModel = viewModel)
                    Screen.MODEL_TEST -> ModelTestScreen(viewModel = viewModel)
                    Screen.REVISION_VAULT -> RevisionVaultScreen(viewModel = viewModel)
                    Screen.SYLLABUS_GUIDE -> SyllabusGuideScreen(viewModel = viewModel)
                }
            }
        }
    }
}
