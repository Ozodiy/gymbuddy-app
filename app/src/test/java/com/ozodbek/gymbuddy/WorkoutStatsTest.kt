package com.ozodbek.gymbuddy

import com.ozodbek.gymbuddy.data.model.Workout
import com.ozodbek.gymbuddy.domain.WorkoutStats
import com.ozodbek.gymbuddy.domain.WorkoutValidator
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Test

class WorkoutStatsTest {
    private val zone = ZoneId.of("UTC")

    // Wednesday 18 November 2026 12:00; the week runs Monday 16 to Sunday 22 November.
    private val now = WorkoutValidator.parseDateTime("2026-11-18 12:00", zone)!!

    private fun workout(start: String, end: String) = Workout(
        title = "w",
        startAt = WorkoutValidator.parseDateTime(start, zone),
        endAt = WorkoutValidator.parseDateTime(end, zone)
    )

    @Test fun emptyListGivesZeros() {
        val summary = WorkoutStats.summarize(emptyList(), now, zone)
        assertEquals(0, summary.thisWeekCount)
        assertEquals(0L, summary.thisWeekMinutes)
    }

    @Test fun countsAndAddsUpOnlyThisWeek() {
        val list = listOf(
            workout("2026-11-16 08:00", "2026-11-16 09:00"), // Monday, 60 min
            workout("2026-11-18 18:00", "2026-11-18 19:45"), // Wednesday, 105 min
            workout("2026-11-22 23:00", "2026-11-22 23:30"), // Sunday, 30 min
            workout("2026-11-15 20:00", "2026-11-15 21:00"), // last Sunday: not counted
            workout("2026-11-23 07:00", "2026-11-23 08:00") // next Monday: not counted
        )
        val summary = WorkoutStats.summarize(list, now, zone)
        assertEquals(3, summary.thisWeekCount)
        assertEquals(195L, summary.thisWeekMinutes)
    }

    @Test fun headlineShowsBothNumbers() {
        val summary = WorkoutStats.summarize(listOf(workout("2026-11-18 18:00", "2026-11-18 19:00")), now, zone)
        assertEquals("1 this week · 60 min trained", summary.headline())
    }
}
