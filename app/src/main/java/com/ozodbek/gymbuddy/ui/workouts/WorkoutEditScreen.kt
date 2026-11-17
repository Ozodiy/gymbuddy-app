package com.ozodbek.gymbuddy.ui.workouts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.ozodbek.gymbuddy.data.model.WorkoutType
import com.ozodbek.gymbuddy.domain.WorkoutValidator

/** One screen for both actions: create (no id) and edit (with id). */
@Composable
fun WorkoutEditScreen(viewModel: WorkoutEditViewModel, onDone: () -> Unit) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.saved) {
        if (state.saved) onDone()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            if (viewModel.isEditing) "Edit workout" else "New workout",
            style = MaterialTheme.typography.headlineSmall
        )

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Column
        }

        OutlinedTextField(
            value = state.form.title,
            onValueChange = viewModel::onTitleChange,
            label = { Text("Title") },
            singleLine = true,
            isError = state.errors.title != null,
            supportingText = { state.errors.title?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        TypePicker(
            types = state.types,
            selectedId = state.form.typeId,
            error = state.errors.type,
            onSelect = viewModel::onTypeChange
        )

        OutlinedTextField(
            value = state.form.start,
            onValueChange = viewModel::onStartChange,
            label = { Text("Start (${WorkoutValidator.DATE_PATTERN})") },
            singleLine = true,
            isError = state.errors.start != null,
            supportingText = { state.errors.start?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.form.end,
            onValueChange = viewModel::onEndChange,
            label = { Text("End (${WorkoutValidator.DATE_PATTERN})") },
            singleLine = true,
            isError = state.errors.end != null,
            supportingText = { state.errors.end?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.form.notes,
            onValueChange = viewModel::onNotesChange,
            label = { Text("Notes") },
            minLines = 3,
            isError = state.errors.notes != null,
            supportingText = {
                Text(state.errors.notes ?: "${state.form.notes.length}/${WorkoutValidator.MAX_NOTES}")
            },
            modifier = Modifier.fillMaxWidth()
        )

        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Button(
            onClick = viewModel::save,
            enabled = !state.isSaving && state.types.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            if (state.isSaving) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
            else Text("Save")
        }
        TextButton(onClick = onDone, modifier = Modifier.fillMaxWidth()) { Text("Cancel") }
    }
}

@Composable
private fun TypePicker(
    types: List<WorkoutType>,
    selectedId: String,
    error: String?,
    onSelect: (String) -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }
    val selected = types.firstOrNull { it.id == selectedId }

    Column {
        Box {
            OutlinedButton(onClick = { menuOpen = true }, modifier = Modifier.fillMaxWidth()) {
                Text(selected?.name ?: "Choose a workout type")
            }
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                types.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.name) },
                        onClick = {
                            onSelect(type.id)
                            menuOpen = false
                        }
                    )
                }
            }
        }
        if (error != null) {
            Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
    }
}
