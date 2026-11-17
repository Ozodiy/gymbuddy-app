package com.ozodbek.gymbuddy.domain

import com.ozodbek.gymbuddy.data.model.Workout
import java.time.DayOfWeek
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import java.util.Date

/** The numbers shown in the statistics header. */
data class WorkoutSummary(
    val thisWeekCount: Int,
    val thisWeekMinutes: Long
) {
    /** For example "3 this week · 165 min trained". */
    fun headline(): String = "$thisWeekCount this week · $thisWeekMinutes min trained"
}

/** Pure statistics logic (no UI), so it can be unit-tested on the JVM. */
object WorkoutStats {

    /** Counts the workouts that started in the current week (Monday to Sunday) and adds up their minutes. */
    fun summarize(
        workouts: List<Workout>,
        now: Date = Date(),
        zone: ZoneId = ZoneId.systemDefault()
    ): WorkoutSummary {
        val today = now.toInstant().atZone(zone).toLocalDate()
        val weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val from = weekStart.atStartOfDay(zone).toInstant().toEpochMilli()
        val until = weekStart.plusDays(7).atStartOfDay(zone).toInstant().toEpochMilli()

        val thisWeek = workouts.filter { w ->
            val start = w.startAt?.time
            start != null && start >= from && start < until
        }
        val minutes = thisWeek.sumOf { w ->
            val start = w.startAt
            val end = w.endAt
            if (start != null && end != null) WorkoutValidator.durationMinutes(start, end) else 0L
        }
        return WorkoutSummary(thisWeekCount = thisWeek.size, thisWeekMinutes = minutes)
    }
}
