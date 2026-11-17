package com.ozodbek.gymbuddy.ui.workouts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozodbek.gymbuddy.data.AppException
import com.ozodbek.gymbuddy.data.WorkoutRepository
import com.ozodbek.gymbuddy.data.model.Workout
import com.ozodbek.gymbuddy.domain.WorkoutFilters
import com.ozodbek.gymbuddy.domain.WorkoutStats
import com.ozodbek.gymbuddy.domain.WorkoutSummary
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WorkoutListUiState(
    val isLoading: Boolean = true,
    val workouts: List<Workout> = emptyList(),
    val query: String = "",
    val error: String? = null,
    /** One-time text for the snackbar (for example after a delete). */
    val message: String? = null
) {
    /** The workouts that match the search text (all of them when the search is empty). */
    val visible: List<Workout> get() = WorkoutFilters.search(workouts, query)

    /** E7: numbers for the header, computed from all of the user's workouts. */
    val summary: WorkoutSummary get() = WorkoutStats.summarize(workouts)
}

class WorkoutListViewModel(private val repository: WorkoutRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutListUiState())
    val uiState: StateFlow<WorkoutListUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null

    init {
        startObserving()
    }

    fun retry() = startObserving()

    /**
     * Collects the live query. The job lives in viewModelScope, so the live-query listener is
     * removed (awaitClose in the repository) when this screen is gone, for example after logout.
     */
    private fun startObserving() {
        observeJob?.cancel()
        _uiState.update { it.copy(isLoading = true, error = null) }
        observeJob = viewModelScope.launch {
            repository.observeMyWorkouts()
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = (e as? AppException)?.message ?: "Could not load your workouts"
                        )
                    }
                }
                .collect { list ->
                    _uiState.update {
                        // Sorting happens here, not in the query, so no composite index is needed.
                        it.copy(
                            isLoading = false,
                            error = null,
                            workouts = list.sortedByDescending { w -> w.startAt?.time ?: 0L }
                        )
                    }
                }
        }
    }

    fun delete(workout: Workout) {
        viewModelScope.launch {
            repository.deleteWorkout(workout.id).fold(
                onSuccess = { _uiState.update { it.copy(message = "Deleted “${workout.title}”") } },
                onFailure = { e ->
                    _uiState.update { it.copy(message = e.message ?: "Could not delete the workout") }
                }
            )
        }
    }

    fun onQueryChange(value: String) = _uiState.update { it.copy(query = value) }

    fun messageShown() = _uiState.update { it.copy(message = null) }
}
