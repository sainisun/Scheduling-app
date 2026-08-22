package com.seduligma.app.feature.schedule

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.seduligma.app.domain.device.DeviceHealthReport
import com.seduligma.app.domain.device.HealthState
import com.seduligma.app.domain.model.RecurrenceRule
import com.seduligma.app.domain.model.Schedule
import com.seduligma.app.domain.model.ScheduleState
import java.time.Instant
import org.junit.Rule
import org.junit.Test

class SchedulePlannerScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun filteredEmptyStateExplainsThatSchedulesExistButDoNotMatch() {
        composeRule.setContent {
            MaterialTheme {
                SchedulePlannerScreen(
                    schedules = emptyList(),
                    totalScheduleCount = 1,
                    searchQuery = "missing",
                    selectedFilter = ScheduleListFilter.ALL,
                    isLoading = false,
                    deviceHealth = DeviceHealthReport(HealthState.READY, HealthState.READY),
                    onRequestExactAlarm = {},
                    onOpenNotificationSettings = {},
                    onPause = {},
                    onCancel = {},
                    onActivate = {},
                    onEdit = {},
                    onSearchQueryChanged = {},
                    onFilterChanged = {},
                )
            }
        }

        composeRule.onNodeWithText("No schedules match this search or filter.").assertExists()
    }

    @Test
    fun scheduleFilterControlsAndResultCountAreVisible() {
        val schedule = Schedule(
            id = "schedule-1",
            title = "Follow up",
            messagePreview = "Draft",
            scheduledAt = Instant.parse("2030-01-01T09:00:00Z"),
            timezoneId = "UTC",
            recurrence = RecurrenceRule.ONCE,
            state = ScheduleState.DRAFT,
        )
        composeRule.setContent {
            MaterialTheme {
                SchedulePlannerScreen(
                    schedules = listOf(schedule),
                    totalScheduleCount = 2,
                    searchQuery = "follow",
                    selectedFilter = ScheduleListFilter.UPCOMING,
                    isLoading = false,
                    deviceHealth = DeviceHealthReport(HealthState.READY, HealthState.READY),
                    onRequestExactAlarm = {},
                    onOpenNotificationSettings = {},
                    onPause = {},
                    onCancel = {},
                    onActivate = {},
                    onEdit = {},
                    onSearchQueryChanged = {},
                    onFilterChanged = {},
                )
            }
        }

        composeRule.onNodeWithText("Search title or preview").assertExists()
        composeRule.onNodeWithText("✓ Upcoming").assertExists()
        composeRule.onNodeWithText("Showing 1 of 2 schedules").assertExists()
    }
}
