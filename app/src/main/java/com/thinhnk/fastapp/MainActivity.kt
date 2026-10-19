package com.thinhnk.fastapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.thinhnk.fastapp.ui.AppRoot
import com.thinhnk.fastapp.ui.theme.FastTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FastTheme {
                AppRoot()
            }
        }
    }
}
