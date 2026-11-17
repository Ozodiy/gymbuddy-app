package com.ozodbek.gymbuddy

import com.ozodbek.gymbuddy.domain.WorkoutForm
import com.ozodbek.gymbuddy.domain.WorkoutValidator
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkoutValidatorTest {
    private val zone = ZoneId.of("UTC")
    private val valid = WorkoutForm(
        title = "Push day",
        typeId = "strength",
        start = "2026-11-17 18:00",
        end = "2026-11-17 19:00",
        notes = "Bench 4x8"
    )

    @Test fun validFormHasNoErrors() = assertFalse(WorkoutValidator.validate(valid, zone).hasErrors)

    @Test fun blankTitleFails() = assertEquals("Title is required", WorkoutValidator.validateTitle("  "))
    @Test fun longTitleFails() = assertNotNull(WorkoutValidator.validateTitle("x".repeat(61)))
    @Test fun sixtyCharTitlePasses() = assertNull(WorkoutValidator.validateTitle("x".repeat(60)))

    @Test fun missingTypeFails() = assertNotNull(WorkoutValidator.validate(valid.copy(typeId = ""), zone).type)

    @Test fun longNotesFail() = assertNotNull(WorkoutValidator.validateNotes("n".repeat(201)))
    @Test fun twoHundredCharNotesPass() = assertNull(WorkoutValidator.validateNotes("n".repeat(200)))

    @Test fun badDateFormatFails() =
        assertNotNull(WorkoutValidator.validate(valid.copy(start = "17.11.2026 18:00"), zone).start)

    @Test fun endBeforeStartFails() =
        assertEquals("End must be after start", WorkoutValidator.validate(valid.copy(end = "2026-11-17 17:00"), zone).end)

    @Test fun endEqualToStartFails() =
        assertNotNull(WorkoutValidator.validate(valid.copy(end = valid.start), zone).end)

    @Test fun parseAndFormatRoundTrip() {
        val date = WorkoutValidator.parseDateTime("2026-11-17 18:30", zone)
        assertNotNull(date)
        assertEquals("2026-11-17 18:30", WorkoutValidator.formatDateTime(date!!, zone))
    }

    @Test fun durationIsInMinutes() {
        val start = WorkoutValidator.parseDateTime("2026-11-17 18:00", zone)!!
        val end = WorkoutValidator.parseDateTime("2026-11-17 19:15", zone)!!
        assertEquals(75L, WorkoutValidator.durationMinutes(start, end))
        assertEquals(0L, WorkoutValidator.durationMinutes(end, start))
    }

    @Test fun errorsFlagIsTrueWhenAnyFieldFails() =
        assertTrue(WorkoutValidator.validate(valid.copy(title = ""), zone).hasErrors)
}
