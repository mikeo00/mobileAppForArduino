package com.example.daizcode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.daizcode.ui.screens.DashboardScreen
import com.example.daizcode.ui.theme.DaizCodeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DaizCodeTheme {
                DashboardScreen()
            }
        }
    }
}