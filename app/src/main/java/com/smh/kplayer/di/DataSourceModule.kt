package com.smh.kplayer.di

import com.smh.kplayer.data.MemoryCacheDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    @Provides
    @Singleton
    fun provideMemoryCacheDataSource(
    ): MemoryCacheDataSource {
        return MemoryCacheDataSource()
    }
}