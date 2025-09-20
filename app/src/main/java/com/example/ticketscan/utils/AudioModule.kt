package com.example.ticketscan.utils

import android.content.Context
import com.example.ticketscan.domain.audio.AudioRecorderRepository
import com.example.ticketscan.domain.audio.RecordAudioUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton


@Module
@InstallIn(ViewModelComponent::class)
object AudioModule {
    @Provides
    fun provideAudioRecorderRepository(
        @ApplicationContext context: Context
    ): AudioRecorderRepository = AudioRecorderRepositoryImpl(context)

    @Provides
    fun provideRecordAudioUseCase(
        audioRecorderRepository: AudioRecorderRepository
    ): RecordAudioUseCase = RecordAudioUseCase(audioRecorderRepository)
}

