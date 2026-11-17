package com.ozodbek.gymbuddy

import com.ozodbek.gymbuddy.data.AuthRepository
import com.ozodbek.gymbuddy.data.FirebaseAuthRepository
import com.ozodbek.gymbuddy.data.FirestoreWorkoutRepository
import com.ozodbek.gymbuddy.data.WorkoutRepository

/**
 * The one place that chooses the real data layer. State holders get a repository through
 * their constructor, so tests can pass a fake instead.
 */
class AppContainer {
    val authRepository: AuthRepository by lazy { FirebaseAuthRepository() }
    val workoutRepository: WorkoutRepository by lazy { FirestoreWorkoutRepository() }
}
