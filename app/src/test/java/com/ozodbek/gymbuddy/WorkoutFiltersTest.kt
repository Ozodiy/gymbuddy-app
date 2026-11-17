package com.ozodbek.gymbuddy

import com.ozodbek.gymbuddy.data.model.Workout
import com.ozodbek.gymbuddy.domain.WorkoutFilters
import org.junit.Assert.assertEquals
import org.junit.Test

class WorkoutFiltersTest {
    private val push = Workout(id = "1", title = "Push day", typeName = "Strength", notes = "Bench 4x8")
    private val run = Workout(id = "2", title = "Morning run", typeName = "Cardio", notes = "5 km, easy")
    private val yoga = Workout(id = "3", title = "Yoga flow", typeName = "Mobility", notes = "")
    private val all = listOf(push, run, yoga)

    @Test fun blankQueryReturnsEverything() = assertEquals(all, WorkoutFilters.search(all, "   "))

    @Test fun matchesTitle() = assertEquals(listOf(push), WorkoutFilters.search(all, "push"))

    @Test fun matchesTypeName() = assertEquals(listOf(run), WorkoutFilters.search(all, "cardio"))

    @Test fun matchesNotes() = assertEquals(listOf(run), WorkoutFilters.search(all, "5 km"))

    @Test fun ignoresLetterCase() = assertEquals(listOf(yoga), WorkoutFilters.search(all, "YOGA"))

    @Test fun noHitsReturnsEmptyList() = assertEquals(emptyList<Workout>(), WorkoutFilters.search(all, "swim"))
}
