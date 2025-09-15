package com.example.ticketscan.utils

import android.content.Context
import android.media.MediaRecorder
import com.example.ticketscan.domain.audio.AudioRecorderRepository
import java.io.File

class AudioRecorderRepositoryImpl(private val context: Context) : AudioRecorderRepository {
    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: File? = null
    private var recording = false

    override fun startRecording(): Result<Unit> {
        return try {
            val fileName = "audio_${System.currentTimeMillis()}.m4a"
            outputFile = File(context.cacheDir, fileName)
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputFile!!.absolutePath)
                prepare()
                start()
            }
            recording = true
            Result.success(Unit)
        } catch (e: Exception) {
            recording = false
            Result.failure(e)
        }
    }

    override fun stopRecording(): Result<File> {
        return try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            recording = false
            outputFile?.let { Result.success(it) } ?: Result.failure(Exception("No se encontró archivo de audio"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun isRecording(): Boolean = recording
}

