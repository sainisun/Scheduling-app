package com.seduligma.app.domain.device

import org.junit.Assert.assertEquals
import org.junit.Test

class DeviceHealthReportTest {
    @Test
    fun `exact alarm denial takes priority in overall report`() {
        val report = DeviceHealthReport(
            exactAlarm = HealthState.ACTION_REQUIRED,
            notifications = HealthState.READY,
        )

        assertEquals(HealthState.ACTION_REQUIRED, report.overall)
    }

    @Test
    fun `notification denial is limited until user regrants it`() {
        val denied = DeviceHealthReport(
            exactAlarm = HealthState.READY,
            notifications = HealthState.LIMITED,
        )
        val regranted = denied.copy(notifications = HealthState.READY)

        assertEquals(HealthState.LIMITED, denied.overall)
        assertEquals(HealthState.READY, regranted.overall)
    }
}
