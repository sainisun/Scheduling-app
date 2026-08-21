package com.seduligma.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.seduligma.app.feature.schedule.SchedulePlannerApp
import com.seduligma.app.ui.theme.SeduligmaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SeduligmaTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    SchedulePlannerApp()
                }
            }
        }
    }
}
