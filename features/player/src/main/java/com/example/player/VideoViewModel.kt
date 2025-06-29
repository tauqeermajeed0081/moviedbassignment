package com.example.player

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor() : ViewModel() {
    var playbackPosition: Long = 0L
    var playWhenReady: Boolean = true
}
