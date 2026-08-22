package com.seduligma.app.privacy

import android.content.pm.ApplicationInfo
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test

class BackupExclusionTest {
    @Test
    fun applicationDisablesAndroidBackupForEncryptedLocalSchedulerData() {
        val applicationInfo = InstrumentationRegistry.getInstrumentation()
            .targetContext
            .applicationInfo

        assertEquals(0, applicationInfo.flags and ApplicationInfo.FLAG_ALLOW_BACKUP)
    }
}
