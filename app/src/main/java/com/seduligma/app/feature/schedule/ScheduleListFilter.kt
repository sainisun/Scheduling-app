package com.seduligma.app.feature.schedule

import com.seduligma.app.domain.model.Schedule
import com.seduligma.app.domain.model.ScheduleState

enum class ScheduleListFilter(val label: String) {
    ALL("All"),
    UPCOMING("Upcoming"),
    ACTION_REQUIRED("Needs review"),
    PAUSED("Paused"),
    COMPLETED("Completed"),
}

object ScheduleListFilters {
    fun apply(
        schedules: List<Schedule>,
        query: String,
        filter: ScheduleListFilter,
    ): List<Schedule> {
        val normalizedQuery = query.trim().lowercase()
        return schedules
            .asSequence()
            .filter { schedule -> matchesFilter(schedule, filter) }
            .filter { schedule ->
                normalizedQuery.isEmpty() ||
                    schedule.title.lowercase().contains(normalizedQuery) ||
                    schedule.messagePreview.lowercase().contains(normalizedQuery)
            }
            .sortedWith(compareBy<Schedule> { it.scheduledAt }.thenBy { it.title.lowercase() })
            .toList()
    }

    private fun matchesFilter(schedule: Schedule, filter: ScheduleListFilter): Boolean = when (filter) {
        ScheduleListFilter.ALL -> true
        ScheduleListFilter.UPCOMING -> schedule.state in setOf(
            ScheduleState.DRAFT,
            ScheduleState.NEEDS_PERMISSION,
            ScheduleState.WAITING,
            ScheduleState.BLOCKED,
        )
        ScheduleListFilter.ACTION_REQUIRED -> schedule.state in setOf(
            ScheduleState.ATTEMPTING,
            ScheduleState.NEEDS_PERMISSION,
            ScheduleState.BLOCKED,
            ScheduleState.FAILED,
            ScheduleState.UNCERTAIN,
        )
        ScheduleListFilter.PAUSED -> schedule.state == ScheduleState.PAUSED
        ScheduleListFilter.COMPLETED -> schedule.state == ScheduleState.COMPLETED
    }
}
