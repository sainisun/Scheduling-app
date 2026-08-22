package com.seduligma.app.feature.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seduligma.app.domain.device.DeviceHealthEvaluator
import com.seduligma.app.domain.device.DeviceHealthReport
import com.seduligma.app.domain.device.HealthState
import com.seduligma.app.domain.model.RecurrenceRule
import com.seduligma.app.domain.model.LocalEvidence
import com.seduligma.app.domain.model.Schedule
import com.seduligma.app.domain.model.ScheduleEvent
import com.seduligma.app.domain.model.ScheduleEventType
import com.seduligma.app.domain.model.ScheduleReasonCode
import com.seduligma.app.domain.model.ScheduleState
import com.seduligma.app.domain.repository.ScheduleEventRepository
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ScheduleListUiState(
    val schedules: List<Schedule> = emptyList(),
    val recentEvents: List<ScheduleEvent> = emptyList(),
    val totalScheduleCount: Int = 0,
    val searchQuery: String = "",
    val selectedFilter: ScheduleListFilter = ScheduleListFilter.ALL,
    val isLoading: Boolean = true,
    val deviceHealth: DeviceHealthReport = DeviceHealthReport(
        exactAlarm = HealthState.LIMITED,
        notifications = HealthState.LIMITED,
    ),
)

@HiltViewModel
class ScheduleListViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val scheduleEventRepository: ScheduleEventRepository,
    private val scheduleAlarmRegistrar: ScheduleAlarmRegistrar,
    private val deviceHealthEvaluator: DeviceHealthEvaluator,
) : ViewModel() {
    private val controls = MutableStateFlow(ScheduleListControls())

    val uiState: StateFlow<ScheduleListUiState> = combine(
        scheduleRepository.observeSchedules(),
        scheduleEventRepository.observeEvents(),
        controls,
    ) { schedules, events, currentControls ->
            ScheduleListUiState(
                schedules = ScheduleListFilters.apply(
                    schedules = schedules,
                    query = currentControls.searchQuery,
                    filter = currentControls.selectedFilter,
                ),
                totalScheduleCount = schedules.size,
                recentEvents = events,
                searchQuery = currentControls.searchQuery,
                selectedFilter = currentControls.selectedFilter,
                isLoading = false,
                deviceHealth = deviceHealthEvaluator.evaluate(),
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ScheduleListUiState(),
        )

    fun setSearchQuery(query: String) {
        controls.value = controls.value.copy(searchQuery = query)
    }

    fun setFilter(filter: ScheduleListFilter) {
        controls.value = controls.value.copy(selectedFilter = filter)
    }

    fun refreshDeviceHealth() {
        controls.value = controls.value.copy(healthRevision = controls.value.healthRevision + 1)
    }

    fun createDraft(
        title: String,
        messagePreview: String,
        scheduledAt: Instant,
        timezoneId: String,
        recurrence: RecurrenceRule,
    ) {
        viewModelScope.launch {
            val schedule = Schedule(
                id = UUID.randomUUID().toString(),
                title = title.trim(),
                messagePreview = messagePreview.trim(),
                scheduledAt = scheduledAt,
                timezoneId = timezoneId,
                recurrence = recurrence,
                state = ScheduleState.DRAFT,
            )
            scheduleRepository.createDraft(schedule)
            recordEvent(schedule.id, ScheduleEventType.CREATED, LocalEvidence.NONE)
        }
    }

    fun createDraft() = createDraft(
        title = "New schedule",
        messagePreview = "Draft saved locally.",
        scheduledAt = Instant.now().plusSeconds(60 * 60),
        timezoneId = ZoneId.systemDefault().id,
        recurrence = RecurrenceRule.ONCE,
    )

    fun updateDraft(
        existing: Schedule,
        title: String,
        messagePreview: String,
        scheduledAt: Instant,
        timezoneId: String,
        recurrence: RecurrenceRule,
    ) {
        viewModelScope.launch {
            scheduleAlarmRegistrar.cancel(existing.id)
            scheduleRepository.updateSchedule(
                existing.copy(
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

    fun pauseSchedule(scheduleId: String) {
        viewModelScope.launch {
            scheduleAlarmRegistrar.cancel(scheduleId)
            scheduleRepository.updateState(scheduleId, ScheduleState.PAUSED)
            recordEvent(scheduleId, ScheduleEventType.PAUSED, LocalEvidence.NONE)
        }
    }

    fun cancelSchedule(scheduleId: String) {
        viewModelScope.launch {
            scheduleAlarmRegistrar.cancel(scheduleId)
            scheduleRepository.updateState(scheduleId, ScheduleState.CANCELLED)
            recordEvent(
                scheduleId,
                ScheduleEventType.CANCELLED,
                LocalEvidence.NONE,
                ScheduleReasonCode.USER_CANCELLED_CONFIRMATION,
            )
        }
    }

    fun activateSchedule(schedule: Schedule) {
        viewModelScope.launch {
            when (scheduleAlarmRegistrar.register(schedule)) {
                AlarmRegistrationResult.REGISTERED -> {
                    scheduleRepository.updateState(schedule.id, ScheduleState.WAITING)
                    recordEvent(schedule.id, ScheduleEventType.ACTIVATED, LocalEvidence.ALARM_REGISTERED)
                }
                AlarmRegistrationResult.EXACT_ALARM_PERMISSION_REQUIRED -> {
                    scheduleRepository.updateState(schedule.id, ScheduleState.NEEDS_PERMISSION)
                    recordEvent(
                        schedule.id,
                        ScheduleEventType.OUTCOME_RECORDED,
                        LocalEvidence.NONE,
                        ScheduleReasonCode.EXACT_ALARM_DENIED,
                    )
                }
            }
        }
    }

    private suspend fun recordEvent(
        scheduleId: String,
        eventType: ScheduleEventType,
        evidence: LocalEvidence,
        reasonCode: ScheduleReasonCode? = null,
    ) {
        scheduleEventRepository.recordEvent(
            ScheduleEvent(
                id = UUID.randomUUID().toString(),
                scheduleId = scheduleId,
                eventType = eventType,
                localEvidence = evidence,
                reasonCode = reasonCode,
                occurredAt = Instant.now(),
            ),
        )
    }
}

private data class ScheduleListControls(
    val searchQuery: String = "",
    val selectedFilter: ScheduleListFilter = ScheduleListFilter.ALL,
    val healthRevision: Int = 0,
)
