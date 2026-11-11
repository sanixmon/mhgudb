package com.sanix.mhgucodex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sanix.mhgucodex.core.designsystem.theme.MhguTheme
import com.sanix.mhgucodex.core.navigation.MhguApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MhguTheme {
                MhguApp()
            }
        }
    }
}
