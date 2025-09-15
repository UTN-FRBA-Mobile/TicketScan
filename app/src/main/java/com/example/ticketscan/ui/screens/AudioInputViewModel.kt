package com.example.ticketscan.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketscan.domain.audio.RecordAudioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

@HiltViewModel
class AudioInputViewModel @Inject constructor(
    private val recordAudioUseCase: RecordAudioUseCase
) : ViewModel() {
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    private val _audioFile = MutableStateFlow<File?>(null)
    val audioFile: StateFlow<File?> = _audioFile

    fun startRecording() {
        viewModelScope.launch {
            val result = recordAudioUseCase.startRecording()
            if (result.isSuccess) {
                _isRecording.value = true
            }
        }
    }

    fun stopRecording() {
        viewModelScope.launch {
            val result = recordAudioUseCase.stopRecording()
            if (result.isSuccess) {
                _isRecording.value = false
                _audioFile.value = result.getOrNull()
            }
        }
    }
}
