package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AddThoughtScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ThoughtViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ThoughtViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val onboardingDone by viewModel.onboardingCompleted.collectAsStateWithLifecycle()
                    var currentScreen by remember { mutableStateOf("home") }

                    if (!onboardingDone) {
                        OnboardingScreen(
                            viewModel = viewModel,
                            onFinished = { currentScreen = "home" }
                        )
                    } else {
                        when (currentScreen) {
                            "add_thought" -> {
                                AddThoughtScreen(
                                    viewModel = viewModel,
                                    onBack = { currentScreen = "home" }
                                )
                            }
                            else -> {
                                HomeScreen(
                                    viewModel = viewModel,
                                    onNavigateToAddThought = { currentScreen = "add_thought" }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
