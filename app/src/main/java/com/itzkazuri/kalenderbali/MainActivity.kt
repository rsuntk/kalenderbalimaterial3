package com.itzkazuri.kalenderbali

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.itzkazuri.kalenderbali.ui.screen.CalendarScreen
import com.itzkazuri.kalenderbali.screen.TrackerOtonanScreen
import com.itzkazuri.kalenderbali.screen.SettingsScreen
import com.itzkazuri.kalenderbali.screen.ReminderScreen
import com.itzkazuri.kalenderbali.ui.theme.KalenderBaliTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Minta izin akses kalender
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (!isGranted) {
                    // Izin tidak diberikan, bisa tambahkan logika peringatan di sini
                }
            }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.READ_CALENDAR)
        }

        setContent {
            KalenderBaliTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    var selectedItem by remember { mutableStateOf("kalender") }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController, selectedItem) { selectedItem = it }
        }
    ) { paddingValues ->
        NavigationHost(navController, paddingValues)
    }
}

@Composable
fun NavigationHost(navController: NavHostController, paddingValues: PaddingValues) {
    NavHost(navController, startDestination = "kalender") {
        composable("kalender") {
            CalendarScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                onAddReminderClick = {
                    navController.navigate("reminder") // Navigasi ke halaman reminder
                },
                onDateClick = { day, month, year ->
                    // Tambahkan navigasi ke detail tanggal jika diperlukan
                    // Contoh: navController.navigate("detail/$day/$month/$year")
                }
            )
        }
        composable("tracker") {
            TrackerOtonanScreen(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        }
        composable("settings") {
            SettingsScreen(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        }
        composable("reminder") {
            ReminderScreen()
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController, selectedItem: String, onItemSelected: (String) -> Unit) {
    NavigationBar(
        modifier = Modifier.height(80.dp)
    ) {
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.ic_home), contentDescription = "Kalender") },
            label = { Text("Kalender", style = MaterialTheme.typography.labelSmall) },
            selected = selectedItem == "kalender",
            onClick = {
                navController.navigate("kalender")
                onItemSelected("kalender")
            }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.ic_tracker), contentDescription = "Tracker Otonan") },
            label = { Text("Tracker", style = MaterialTheme.typography.labelSmall) },
            selected = selectedItem == "tracker",
            onClick = {
                navController.navigate("tracker")
                onItemSelected("tracker")
            }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.ic_settings), contentDescription = "Settings") },
            label = { Text("Settings", style = MaterialTheme.typography.labelSmall) },
            selected = selectedItem == "settings",
            onClick = {
                navController.navigate("settings")
                onItemSelected("settings")
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    KalenderBaliTheme {
        MainScreen()
    }
}
