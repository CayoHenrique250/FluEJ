package com.example.fluej

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.fluej.navigation.AppNavigation
import com.example.fluej.ui.theme.FluEJTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FluEJTheme {
                AppNavigation()
            }
        }
    }
}