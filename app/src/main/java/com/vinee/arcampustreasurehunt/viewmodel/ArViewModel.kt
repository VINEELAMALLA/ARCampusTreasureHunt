package com.vinee.arcampustreasurehunt.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ArViewModel : ViewModel() {
    var currentBlockIndex by mutableStateOf(0)
        private set

    var gameState by mutableStateOf("clue")
        private set // "clue", "reached", "video", "completed"

    fun reset() {
        currentBlockIndex = 0
        gameState = "clue"
    }

    fun toReached() { gameState = "reached" }
    fun toVideo() { gameState = "video" }
    fun toClue() { gameState = "clue" }

    fun nextClue(totalBlocks: Int) {
        if (currentBlockIndex < totalBlocks - 1) {
            currentBlockIndex += 1
            gameState = "clue"
        } else {
            gameState = "completed"
        }
    }
}
