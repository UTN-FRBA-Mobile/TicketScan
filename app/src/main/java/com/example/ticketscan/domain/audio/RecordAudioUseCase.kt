package com.example.ticketscan.domain.audio

import java.io.File

class RecordAudioUseCase(private val audioRecorderRepository: AudioRecorderRepository) {
    fun startRecording(): Result<Unit> = audioRecorderRepository.startRecording()
    fun stopRecording(): Result<File> = audioRecorderRepository.stopRecording()
    fun isRecording(): Boolean = audioRecorderRepository.isRecording()
}

