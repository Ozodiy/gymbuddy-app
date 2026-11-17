package com.ozodbek.gymbuddy.domain

import com.ozodbek.gymbuddy.data.model.Workout

/** Pure filtering logic, kept out of the UI so it can be unit-tested on the JVM. */
object WorkoutFilters {

    /**
     * Keeps the workouts whose title, type name or notes contain [query].
     * Letter case is ignored; a blank query returns the list unchanged.
     */
    fun search(workouts: List<Workout>, query: String): List<Workout> {
        val text = query.trim()
        if (text.isEmpty()) return workouts
        return workouts.filter { workout ->
            listOf(workout.title, workout.typeName, workout.notes).any { it.contains(text, ignoreCase = true) }
        }
    }
}
