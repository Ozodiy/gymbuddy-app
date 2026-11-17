package com.ozodbek.gymbuddy.ui.workouts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozodbek.gymbuddy.data.WorkoutRepository
import com.ozodbek.gymbuddy.data.model.WorkoutInput
import com.ozodbek.gymbuddy.data.model.WorkoutType
import com.ozodbek.gymbuddy.domain.WorkoutForm
import com.ozodbek.gymbuddy.domain.WorkoutFormErrors
import com.ozodbek.gymbuddy.domain.WorkoutValidator
import java.util.Date
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WorkoutEditUiState(
    val isLoading: Boolean = true,
    val form: WorkoutForm = WorkoutForm(),
    val errors: WorkoutFormErrors = WorkoutFormErrors(),
    val types: List<WorkoutType> = emptyList(),
    val isSaving: Boolean = false,
    /** A readable message shown above the Save button (load or save failed). */
    val error: String? = null,
    /** Set after a successful save; the screen then returns to the list. */
    val saved: Boolean = false
)

/** State holder for the one form that creates (workoutId == null) or edits a workout. */
class WorkoutEditViewModel(
    private val repository: WorkoutRepository,
    private val workoutId: String?
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutEditUiState())
    val uiState: StateFlow<WorkoutEditUiState> = _uiState.asStateFlow()

    val isEditing: Boolean get() = workoutId != null

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val typesResult = repository.getWorkoutTypes()
            val types = typesResult.getOrNull()
            if (types == null) {
                fail(typesResult.exceptionOrNull()?.message)
                return@launch
            }
            if (types.isEmpty()) {
                fail("No workout types are available yet")
                return@launch
            }

            if (workoutId == null) {
                val now = Date()
                val form = WorkoutForm(
                    typeId = types.first().id,
                    start = WorkoutValidator.formatDateTime(now),
                    end = WorkoutValidator.formatDateTime(Date(now.time + ONE_HOUR_MS))
                )
                _uiState.update { it.copy(isLoading = false, types = types, form = form) }
            } else {
                repository.getWorkout(workoutId).fold(
                    onSuccess = { w ->
                        val form = WorkoutForm(
                            title = w.title,
                            typeId = w.typeId,
                            start = w.startAt?.let { WorkoutValidator.formatDateTime(it) }.orEmpty(),
                            end = w.endAt?.let { WorkoutValidator.formatDateTime(it) }.orEmpty(),
                            notes = w.notes
                        )
                        _uiState.update { it.copy(isLoading = false, types = types, form = form) }
                    },
                    onFailure = { e -> fail(e.message) }
                )
            }
        }
    }

    private fun fail(message: String?) {
        _uiState.update { it.copy(isLoading = false, error = message ?: "Could not load the form") }
    }

    fun onTitleChange(value: String) = changeForm({ it.copy(title = value) }) { it.copy(title = null) }
    fun onTypeChange(value: String) = changeForm({ it.copy(typeId = value) }) { it.copy(type = null) }
    fun onStartChange(value: String) = changeForm({ it.copy(start = value) }) { it.copy(start = null) }
    fun onEndChange(value: String) = changeForm({ it.copy(end = value) }) { it.copy(end = null) }
    fun onNotesChange(value: String) = changeForm({ it.copy(notes = value) }) { it.copy(notes = null) }

    private fun changeForm(
        form: (WorkoutForm) -> WorkoutForm,
        errors: (WorkoutFormErrors) -> WorkoutFormErrors
    ) {
        _uiState.update { it.copy(form = form(it.form), errors = errors(it.errors), error = null) }
    }

    /** Validates first; invalid input never reaches the repository. */
    fun save() {
        val state = _uiState.value
        if (state.isSaving) return

        val errors = WorkoutValidator.validate(state.form)
        val type = state.types.firstOrNull { it.id == state.form.typeId }
        val start = WorkoutValidator.parseDateTime(state.form.start)
        val end = WorkoutValidator.parseDateTime(state.form.end)
        if (errors.hasErrors || type == null || start == null || end == null) {
            _uiState.update { it.copy(errors = errors) }
            return
        }

        val input = WorkoutInput(
            title = state.form.title.trim(),
            typeId = type.id,
            typeName = type.name,
            startAt = start,
            endAt = end,
            notes = state.form.notes.trim()
        )
        _uiState.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            val result = if (workoutId == null) {
                repository.addWorkout(input).map { }
            } else {
                repository.updateWorkout(workoutId, input)
            }
            result.fold(
                onSuccess = { _uiState.update { it.copy(isSaving = false, saved = true) } },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isSaving = false, error = e.message ?: "Could not save the workout")
                    }
                }
            )
        }
    }

    private companion object {
        const val ONE_HOUR_MS = 60L * 60L * 1000L
    }
}
