package com.example.submarinehunt.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.submarinehunt.audio.AudioHapticFeedbackHelper
import com.example.submarinehunt.data.AppThemeSetting
import com.example.submarinehunt.data.GameSettings
import com.example.submarinehunt.data.GameStatistics
import com.example.submarinehunt.data.PreferencesManager
import com.example.submarinehunt.engine.SearchAttemptResult
import com.example.submarinehunt.engine.SubmarineHuntEngine
import com.example.submarinehunt.model.Coordinate
import com.example.submarinehunt.model.GamePhase
import com.example.submarinehunt.model.HuntGameState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Navigation and one-time UI events.
 */
sealed class GameUiEvent {
    object NavigateToResult : GameUiEvent()
    data class ShowMessage(val message: String) : GameUiEvent()
}

/**
 * ViewModel managing Submarine Hunt state, preferences, audio/haptic feedback,
 * and game loop lifecycle.
 */
class SubmarineHuntViewModel @JvmOverloads constructor(
    application: Application,
    private val preferencesManager: PreferencesManager = PreferencesManager(application),
    private val audioHapticHelper: AudioHapticFeedbackHelper = AudioHapticFeedbackHelper(application)
) : AndroidViewModel(application) {

    private val engine = SubmarineHuntEngine()

    private val _gameState = MutableStateFlow(engine.state)
    val gameState: StateFlow<HuntGameState> = _gameState.asStateFlow()

    val settings: StateFlow<GameSettings> = preferencesManager.settingsFlow
    val statistics: StateFlow<GameStatistics> = preferencesManager.statisticsFlow

    private val _uiEvents = MutableSharedFlow<GameUiEvent>()
    val uiEvents: SharedFlow<GameUiEvent> = _uiEvents.asSharedFlow()

    // Rapid tap protection: true when input should be temporarily locked
    private val _isInputLocked = MutableStateFlow(false)
    val isInputLocked: StateFlow<Boolean> = _isInputLocked.asStateFlow()

    // Confirmation dialog for restarting a game
    private val _showRestartDialog = MutableStateFlow(false)
    val showRestartDialog: StateFlow<Boolean> = _showRestartDialog.asStateFlow()

    private var revealJob: Job? = null

    /**
     * Handles tapping a grid cell.
     */
    fun onCellTapped(coordinate: Coordinate) {
        if (_isInputLocked.value || _gameState.value.isFound) {
            return
        }

        when (val attempt = engine.search(coordinate)) {
            is SearchAttemptResult.AlreadySearched -> {
                // Repeated search: no penalty, do not consume turn, emit message
                viewModelScope.launch {
                    _uiEvents.emit(GameUiEvent.ShowMessage("Already searched."))
                }
            }

            is SearchAttemptResult.Success -> {
                val currentSettings = settings.value
                _gameState.value = attempt.newState

                // Trigger audio and haptic effects
                audioHapticHelper.playFeedbackSound(
                    feedback = attempt.result.feedback,
                    soundEnabled = currentSettings.soundEnabled
                )
                audioHapticHelper.performHaptic(
                    feedback = attempt.result.feedback,
                    hapticEnabled = currentSettings.hapticEnabled
                )

                if (attempt.result.found) {
                    // Win condition met
                    _isInputLocked.value = true

                    // Persist win stats
                    preferencesManager.recordWin(
                        searches = attempt.newState.searches,
                        score = attempt.newState.score
                    )

                    // Reveal animation delay (600-900ms) before navigating to Result screen
                    revealJob?.cancel()
                    revealJob = viewModelScope.launch {
                        val delayMs = if (currentSettings.animationsEnabled) 850L else 150L
                        delay(delayMs)
                        engine.transitionToResult()
                        _gameState.value = engine.state
                        _uiEvents.emit(GameUiEvent.NavigateToResult)
                        _isInputLocked.value = false
                    }
                }
            }

            is SearchAttemptResult.GameOver,
            is SearchAttemptResult.InvalidCoordinate -> {
                // Ignored
            }
        }
    }

    /**
     * Requests game restart, honoring confirm setting.
     */
    fun requestRestart() {
        if (settings.value.confirmNewHunt && _gameState.value.searches > 0 && !_gameState.value.isFound) {
            _showRestartDialog.value = true
        } else {
            startNewHunt()
        }
    }

    fun dismissRestartDialog() {
        _showRestartDialog.value = false
    }

    /**
     * Starts a completely new hunt round with a fresh random submarine location.
     */
    fun startNewHunt() {
        revealJob?.cancel()
        _showRestartDialog.value = false
        _isInputLocked.value = false
        engine.reset()
        _gameState.value = engine.state
    }

    // Settings actions
    fun setTheme(theme: AppThemeSetting) = preferencesManager.updateTheme(theme)
    fun setDynamicColor(enabled: Boolean) = preferencesManager.updateDynamicColor(enabled)
    fun setSound(enabled: Boolean) = preferencesManager.updateSound(enabled)
    fun setHaptics(enabled: Boolean) = preferencesManager.updateHaptic(enabled)
    fun setAnimations(enabled: Boolean) = preferencesManager.updateAnimations(enabled)
    fun setConfirmNewHunt(enabled: Boolean) = preferencesManager.updateConfirmNewHunt(enabled)
    fun setShowCoordinateLabels(enabled: Boolean) = preferencesManager.updateShowCoordinateLabels(enabled)
    fun resetStatistics() = preferencesManager.resetStatistics()
    fun resetSettings() = preferencesManager.resetSettings()

    override fun onCleared() {
        super.onCleared()
        audioHapticHelper.release()
    }
}
