package com.ozodbek.gymbuddy.data.model

import java.util.Date

/** What the form sends to the repository. Owner id and timestamps are added by the repository. */
data class WorkoutInput(
    val title: String,
    val typeId: String,
    val typeName: String,
    val startAt: Date,
    val endAt: Date,
    val notes: String
)
