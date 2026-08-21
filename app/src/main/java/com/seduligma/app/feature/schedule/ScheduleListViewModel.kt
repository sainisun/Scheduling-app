package com.seduligma.app.feature.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seduligma.app.domain.model.RecurrenceRule
import com.seduligma.app.domain.model.Schedule
import com.seduligma.app.domain.model.ScheduleState
import com.seduligma.app.domain.repository.ScheduleRepository
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
) : ViewModel() {
    val uiState: StateFlow<ScheduleListUiState> = scheduleRepository.observeSchedules()
        .map { schedules -> ScheduleListUiState(schedules = schedules, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ScheduleListUiState(),
        )

    fun createDraft() {
        viewModelScope.launch {
            scheduleRepository.createDraft(
                Schedule(
                    id = UUID.randomUUID().toString(),
                    title = "New schedule",
                    messagePreview = "Draft saved locally. The editor is the next feature slice.",
                    scheduledAt = Instant.now().plusSeconds(60 * 60),
                    timezoneId = ZoneId.systemDefault().id,
                    recurrence = RecurrenceRule.ONCE,
                    state = ScheduleState.DRAFT,
                ),
            )
        }
    }

    fun pauseSchedule(scheduleId: String) {
        viewModelScope.launch {
            scheduleRepository.updateState(scheduleId, ScheduleState.PAUSED)
        }
    }

    fun cancelSchedule(scheduleId: String) {
        viewModelScope.launch {
            scheduleRepository.updateState(scheduleId, ScheduleState.CANCELLED)
        }
    }
}
