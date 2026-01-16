package com.rhinepereira.versetrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.rhinepereira.versetrack.ui.VerseScreen
import com.rhinepereira.versetrack.ui.theme.VerseTrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VerseTrackTheme {
                VerseScreen()
            }
        }
    }
}
