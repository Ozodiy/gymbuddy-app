package com.ozodbek.gymbuddy.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Temporary home screen. In Lab 2 it becomes the workout list. */
@Composable
fun HomeScreen(email: String?, onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
    ) {
        Text("GymBuddy", style = MaterialTheme.typography.headlineLarge)
        Text(
            "Gym-goers forget what they trained and struggle to find training partners, " +
                "so progress stalls and motivation drops.",
            style = MaterialTheme.typography.bodyLarge
        )
        Text("Logged in as ${email ?: "unknown"}")
        Button(onClick = onLogout) { Text("Log out") }
    }
}
