package com.seduligma.app.domain.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScheduleStateTest {
    @Test
    fun `uncertain execution is not treated as completed`() {
        val terminalSuccess = ScheduleState.COMPLETED
        val uncertain = ScheduleState.UNCERTAIN

        assertTrue(terminalSuccess == ScheduleState.COMPLETED)
        assertFalse(uncertain == ScheduleState.COMPLETED)
    }
}
