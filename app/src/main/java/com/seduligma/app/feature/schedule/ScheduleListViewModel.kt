package com.seduligma.app.feature.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seduligma.app.domain.model.RecurrenceRule
import com.seduligma.app.domain.model.Schedule
import com.seduligma.app.domain.model.ScheduleState
import com.seduligma.app.domain.repository.ScheduleRepository
import com.seduligma.app.domain.scheduling.AlarmRegistrationResult
import com.seduligma.app.domain.scheduling.ScheduleAlarmRegistrar
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ScheduleListUiState(
    val schedules: List<Schedule> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class ScheduleListViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val scheduleAlarmRegistrar: ScheduleAlarmRegistrar,
) : ViewModel() {
    val uiState: StateFlow<ScheduleListUiState> = scheduleRepository.observeSchedules()
        .map { schedules -> ScheduleListUiState(schedules = schedules, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ScheduleListUiState(),
        )

    fun createDraft(
        title: String,
        messagePreview: String,
        scheduledAt: Instant,
        timezoneId: String,
        recurrence: RecurrenceRule,
    ) {
        viewModelScope.launch {
            scheduleRepository.createDraft(
                Schedule(
                    id = UUID.randomUUID().toString(),
                    title = title.trim(),
                    messagePreview = messagePreview.trim(),
                    scheduledAt = scheduledAt,
                    timezoneId = timezoneId,
                    recurrence = recurrence,
                    state = ScheduleState.DRAFT,
                ),
            )
        }
    }

    fun createDraft() = createDraft(
        title = "New schedule",
        messagePreview = "Draft saved locally.",
        scheduledAt = Instant.now().plusSeconds(60 * 60),
        timezoneId = ZoneId.systemDefault().id,
        recurrence = RecurrenceRule.ONCE,
    )

    fun pauseSchedule(scheduleId: String) {
        viewModelScope.launch {
            scheduleAlarmRegistrar.cancel(scheduleId)
            scheduleRepository.updateState(scheduleId, ScheduleState.PAUSED)
        }
    }

    fun cancelSchedule(scheduleId: String) {
        viewModelScope.launch {
            scheduleAlarmRegistrar.cancel(scheduleId)
            scheduleRepository.updateState(scheduleId, ScheduleState.CANCELLED)
        }
    }

    fun activateSchedule(schedule: Schedule) {
        viewModelScope.launch {
            val state = when (scheduleAlarmRegistrar.register(schedule)) {
                AlarmRegistrationResult.REGISTERED -> ScheduleState.WAITING
                AlarmRegistrationResult.EXACT_ALARM_PERMISSION_REQUIRED -> ScheduleState.NEEDS_PERMISSION
            }
            scheduleRepository.updateState(schedule.id, state)
        }
    }
}
