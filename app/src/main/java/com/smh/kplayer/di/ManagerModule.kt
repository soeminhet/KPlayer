package com.smh.kplayer.di

import android.content.Context
import com.smh.player.manager.FFMPEGManager
import com.smh.player.manager.FileManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ManagerModule {
    @Provides
    @Singleton
    fun provideFileManger(
        @ApplicationContext context: Context,
    ): FileManager {
        return FileManager(context)
    }

    @Provides
    @Singleton
    fun provideFFMPEGManger(
        @ApplicationContext context: Context,
    ): FFMPEGManager {
        return FFMPEGManager(context)
    }
}