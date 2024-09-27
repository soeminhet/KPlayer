package com.smh.kplayer.di

import com.smh.kplayer.repository.GlobalFileRepository
import com.smh.kplayer.repository.GlobalFileRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindFileRepository(
        globalFileRepositoryImpl: GlobalFileRepositoryImpl
    ): GlobalFileRepository
}