package com.ozodbek.gymbuddy.ui.workouts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ozodbek.gymbuddy.data.model.Workout
import com.ozodbek.gymbuddy.domain.WorkoutValidator

/** The list of the signed-in user's workouts. It only shows state and forwards user actions. */
@Composable
fun WorkoutListScreen(
    viewModel: WorkoutListViewModel,
    userEmail: String?,
    onAdd: () -> Unit,
    onOpen: (String) -> Unit,
    onLogout: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingDelete by remember { mutableStateOf<Workout?>(null) }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.messageShown()
        }
    }

    pendingDelete?.let { workout ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Delete workout?") },
            text = { Text("“${workout.title}” will be deleted. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(workout)
                    pendingDelete = null
                }) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { pendingDelete = null }) { Text("Cancel") } }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = { FloatingActionButton(onClick = onAdd) { Text("+") } }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("My workouts", style = MaterialTheme.typography.headlineSmall)
                    if (userEmail != null) {
                        Text("Logged in as $userEmail", style = MaterialTheme.typography.bodySmall)
                    }
                }
                TextButton(onClick = onLogout) { Text("Log out") }
            }

            OutlinedTextField(
                value = state.query,
                onValueChange = viewModel::onQueryChange,
                label = { Text("Search workouts") },
                singleLine = true,
                trailingIcon = {
                    if (state.query.isNotEmpty()) {
                        TextButton(onClick = { viewModel.onQueryChange("") }) { Text("Clear") }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            Box(modifier = Modifier.fillMaxSize().padding(top = 8.dp)) {
                when {
                    state.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                    state.error != null -> Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(state.error.orEmpty(), color = MaterialTheme.colorScheme.error)
                        Button(onClick = viewModel::retry) { Text("Try again") }
                    }

                    // E3: an empty list invites the user to add the first workout ...
                    state.workouts.isEmpty() -> EmptyMessage(
                        title = "No workouts yet",
                        hint = "Tap + to add your first workout.",
                        modifier = Modifier.align(Alignment.Center)
                    )

                    // ... while a search without hits says something different.
                    state.visible.isEmpty() -> EmptyMessage(
                        title = "No workouts match “${state.query.trim()}”",
                        hint = "Try another word or tap Clear.",
                        modifier = Modifier.align(Alignment.Center)
                    )

                    else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.visible, key = { it.id }) { workout ->
                            WorkoutRow(
                                workout = workout,
                                onOpen = { onOpen(workout.id) },
                                onDelete = { pendingDelete = workout }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkoutRow(workout: Workout, onOpen: () -> Unit, onDelete: () -> Unit) {
    val start = workout.startAt
    val end = workout.endAt
    val minutes = if (start != null && end != null) WorkoutValidator.durationMinutes(start, end) else 0L

    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onOpen)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(workout.title, style = MaterialTheme.typography.titleMedium)
                Text("${workout.typeName} · $minutes min", style = MaterialTheme.typography.bodyMedium)
                if (start != null) {
                    Text(WorkoutValidator.formatDateTime(start), style = MaterialTheme.typography.bodySmall)
                }
            }
            TextButton(onClick = onDelete) { Text("Delete") }
        }
    }
}

@Composable
private fun EmptyMessage(title: String, hint: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(hint, style = MaterialTheme.typography.bodyMedium)
    }
}
