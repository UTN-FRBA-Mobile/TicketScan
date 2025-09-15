package com.example.ticketscan.domain.audio

import java.io.File

interface AudioRecorderRepository {
    fun startRecording(): Result<Unit>
    fun stopRecording(): Result<File>
    fun isRecording(): Boolean
}

