package com.seduligma.app.domain.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScheduleStateTest {
    @Test
    fun `uncertain execution is not treated as completed`() {
        assertTrue(ScheduleState.COMPLETED.isTerminalSuccess())
        assertFalse(ScheduleState.UNCERTAIN.isTerminalSuccess())
    }
}
