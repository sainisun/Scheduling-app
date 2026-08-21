package com.seduligma.app.domain.device

enum class HealthState { READY, ACTION_REQUIRED, LIMITED }

data class DeviceHealthReport(
    val exactAlarm: HealthState,
    val notifications: HealthState,
) {
    val overall: HealthState
        get() = when {
            exactAlarm == HealthState.ACTION_REQUIRED -> HealthState.ACTION_REQUIRED
            notifications != HealthState.READY -> HealthState.LIMITED
            else -> HealthState.READY
        }
}

interface DeviceHealthEvaluator {
    fun evaluate(): DeviceHealthReport
}
