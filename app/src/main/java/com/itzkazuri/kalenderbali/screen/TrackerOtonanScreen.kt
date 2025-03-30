package com.itzkazuri.kalenderbali.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TrackerOtonanScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Tambahkan scroll agar konten tidak tertutup footer
            .padding(16.dp)
    ) {
        Text(text = "🔍 Tracker Otonan", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Bagi gen z yang malas atau lupa terhadap otonan mereka ya itu pun mereka lupain otonannya kapan hanya inget ulang tahunnya jadi nya cooming soon ya fitur ini :)", style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTrackerOtonanScreen() {
    TrackerOtonanScreen()
}
