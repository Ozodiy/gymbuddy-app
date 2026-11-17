package com.ozodbek.gymbuddy.domain

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Date

/** The raw text of the workout form. */
data class WorkoutForm(
    val title: String = "",
    val typeId: String = "",
    val start: String = "",
    val end: String = "",
    val notes: String = ""
)

/** One error message (or null) per field. */
data class WorkoutFormErrors(
    val title: String? = null,
    val type: String? = null,
    val start: String? = null,
    val end: String? = null,
    val notes: String? = null
) {
    val hasErrors: Boolean get() = listOf(title, type, start, end, notes).any { it != null }
}

/**
 * Pure validation and date helpers: no UI, no Android, no backend imports,
 * so it can be unit-tested on the JVM. The server rules in firestore.rules mirror
 * the title, notes and end-after-start rules.
 */
object WorkoutValidator {
    const val MAX_TITLE = 60
    const val MAX_NOTES = 200
    const val DATE_PATTERN = "yyyy-MM-dd HH:mm"

    private val formatter = DateTimeFormatter.ofPattern(DATE_PATTERN)

    fun validateTitle(title: String): String? = when {
        title.isBlank() -> "Title is required"
        title.trim().length > MAX_TITLE -> "Maximum $MAX_TITLE characters"
        else -> null
    }

    fun validateType(typeId: String): String? =
        if (typeId.isBlank()) "Choose a workout type" else null

    fun validateNotes(notes: String): String? =
        if (notes.length > MAX_NOTES) "Maximum $MAX_NOTES characters" else null

    fun validateDateTime(text: String, zone: ZoneId = ZoneId.systemDefault()): String? =
        if (parseDateTime(text, zone) == null) "Use the format $DATE_PATTERN" else null

    /** Cross-field rule: a workout must end after it starts. */
    fun validateEndAfterStart(start: Date?, end: Date?): String? =
        if (start != null && end != null && !end.after(start)) "End must be after start" else null

    fun validate(form: WorkoutForm, zone: ZoneId = ZoneId.systemDefault()): WorkoutFormErrors {
        val start = parseDateTime(form.start, zone)
        val end = parseDateTime(form.end, zone)
        return WorkoutFormErrors(
            title = validateTitle(form.title),
            type = validateType(form.typeId),
            start = validateDateTime(form.start, zone),
            end = validateDateTime(form.end, zone) ?: validateEndAfterStart(start, end),
            notes = validateNotes(form.notes)
        )
    }

    /** Returns null when the text is not a valid "yyyy-MM-dd HH:mm" date and time. */
    fun parseDateTime(text: String, zone: ZoneId = ZoneId.systemDefault()): Date? =
        try {
            Date.from(LocalDateTime.parse(text.trim(), formatter).atZone(zone).toInstant())
        } catch (e: DateTimeParseException) {
            null
        }

    fun formatDateTime(date: Date, zone: ZoneId = ZoneId.systemDefault()): String =
        formatter.format(date.toInstant().atZone(zone))

    /** Whole minutes between start and end, never negative. */
    fun durationMinutes(start: Date, end: Date): Long =
        maxOf(0L, (end.time - start.time) / 60_000L)
}
