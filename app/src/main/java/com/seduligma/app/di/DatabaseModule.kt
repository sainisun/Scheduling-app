package com.seduligma.app.di

import android.content.Context
import androidx.room.Room
import com.seduligma.app.data.local.ScheduleDao
import com.seduligma.app.data.local.ScheduleEventDao
import com.seduligma.app.data.local.SeduligmaDatabase
import com.seduligma.app.data.local.DatabasePassphraseProvider
import com.seduligma.app.data.repository.RoomScheduleRepository
import com.seduligma.app.data.repository.RoomScheduleEventRepository
import com.seduligma.app.data.scheduling.AndroidScheduleAlarmRegistrar
import com.seduligma.app.data.device.AndroidDeviceHealthEvaluator
import com.seduligma.app.data.notification.AndroidManualConfirmationNotifier
import com.seduligma.app.data.privacy.RoomLocalDataResetter
import com.seduligma.app.domain.device.DeviceHealthEvaluator
import com.seduligma.app.domain.notification.ManualConfirmationNotifier
import com.seduligma.app.domain.privacy.LocalDataResetter
import com.seduligma.app.domain.repository.ScheduleRepository
import com.seduligma.app.domain.repository.ScheduleEventRepository
import com.seduligma.app.domain.scheduling.ScheduleAlarmRegistrar
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindScheduleRepository(repository: RoomScheduleRepository): ScheduleRepository

    @Binds
    @Singleton
    abstract fun bindScheduleEventRepository(repository: RoomScheduleEventRepository): ScheduleEventRepository

    @Binds
    @Singleton
    abstract fun bindScheduleAlarmRegistrar(registrar: AndroidScheduleAlarmRegistrar): ScheduleAlarmRegistrar

    @Binds
    @Singleton
    abstract fun bindDeviceHealthEvaluator(evaluator: AndroidDeviceHealthEvaluator): DeviceHealthEvaluator

    @Binds
    @Singleton
    abstract fun bindManualConfirmationNotifier(notifier: AndroidManualConfirmationNotifier): ManualConfirmationNotifier

    @Binds
    @Singleton
    abstract fun bindLocalDataResetter(resetter: RoomLocalDataResetter): LocalDataResetter
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        passphraseProvider: DatabasePassphraseProvider,
    ): SeduligmaDatabase {
        SQLiteDatabase.loadLibs(context)
        return Room.databaseBuilder(context, SeduligmaDatabase::class.java, "seduligma-secure.db")
            .openHelperFactory(SupportFactory(passphraseProvider.getOrCreate()))
            .addMigrations(SeduligmaDatabase.MIGRATION_1_2)
            .fallbackToDestructiveMigrationOnDowngrade()
            .build()
    }

    @Provides
    fun provideScheduleDao(database: SeduligmaDatabase): ScheduleDao = database.scheduleDao()

    @Provides
    fun provideScheduleEventDao(database: SeduligmaDatabase): ScheduleEventDao = database.scheduleEventDao()
}
