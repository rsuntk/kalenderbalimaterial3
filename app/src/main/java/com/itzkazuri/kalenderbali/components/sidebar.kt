package com.itzkazuri.kalenderbali.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SidebarMenu() {
    var drawerState = remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = DrawerState(if (drawerState.value) DrawerValue.Open else DrawerValue.Closed),
        drawerContent = {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Menu", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Kalender")
                Text("Pengaturan")
            }
        }
    ) {
        Button(onClick = { drawerState.value = !drawerState.value }) {
            Text("Buka Sidebar")
        }
    }
}