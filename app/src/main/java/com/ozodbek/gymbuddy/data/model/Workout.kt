package com.ozodbek.gymbuddy.data.model

import com.google.firebase.firestore.DocumentId
import java.util.Date

/**
 * A workout stored in the `workouts` collection.
 * ownerId, createdAt and updatedAt are set by the data layer / server, never typed by the user.
 */
data class Workout(
    @DocumentId val id: String = "",
    val ownerId: String = "",
    val typeId: String = "", // id in the workoutTypes reference collection
    val typeName: String = "", // copied for the list screen
    val title: String = "",
    val startAt: Date? = null,
    val endAt: Date? = null,
    val notes: String = "",
    val createdAt: Date? = null, // FieldValue.serverTimestamp()
    val updatedAt: Date? = null // FieldValue.serverTimestamp()
)
