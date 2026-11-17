package com.ozodbek.gymbuddy.data

import com.ozodbek.gymbuddy.data.model.Workout
import com.ozodbek.gymbuddy.data.model.WorkoutInput
import com.ozodbek.gymbuddy.data.model.WorkoutType
import kotlinx.coroutines.flow.Flow

/** The only place the app talks to the cloud database for workouts. */
interface WorkoutRepository {
    /** Live list of the signed-in user's workouts. The listener is removed when the collector stops. */
    fun observeMyWorkouts(): Flow<List<Workout>>
    suspend fun getWorkout(id: String): Result<Workout>
    suspend fun getWorkoutTypes(): Result<List<WorkoutType>>
    suspend fun addWorkout(input: WorkoutInput): Result<String>
    suspend fun updateWorkout(id: String, input: WorkoutInput): Result<Unit>
    suspend fun deleteWorkout(id: String): Result<Unit>
}
