package com.ozodbek.gymbuddy.data.model

import com.google.firebase.firestore.DocumentId

/** Read-only reference record in the `workoutTypes` collection (Strength, Cardio, ...). */
data class WorkoutType(
    @DocumentId val id: String = "",
    val name: String = "",
    val sortOrder: Long = 0
)
