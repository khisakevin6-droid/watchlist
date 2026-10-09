package com.kevv.watchlist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.kevv.watchlist.ui.KevvTheme
import com.kevv.watchlist.ui.WatchlistScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KevvTheme {
                Surface(color = MaterialTheme.colorScheme.background) { WatchlistScreen() }
            }
        }
    }
}
