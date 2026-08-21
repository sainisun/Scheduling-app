package com.seduligma.app.di

import android.content.Context
import androidx.room.Room
import com.seduligma.app.data.local.ScheduleDao
import com.seduligma.app.data.local.SeduligmaDatabase
import com.seduligma.app.data.repository.RoomScheduleRepository
import com.seduligma.app.domain.repository.ScheduleRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindScheduleRepository(repository: RoomScheduleRepository): ScheduleRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SeduligmaDatabase =
        Room.databaseBuilder(context, SeduligmaDatabase::class.java, "seduligma.db")
            .fallbackToDestructiveMigrationOnDowngrade()
            .build()

    @Provides
    fun provideScheduleDao(database: SeduligmaDatabase): ScheduleDao = database.scheduleDao()
}
