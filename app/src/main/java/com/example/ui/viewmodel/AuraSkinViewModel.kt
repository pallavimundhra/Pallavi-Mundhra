package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.network.SkinAnalysisEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class AnalysisUiState {
    object Idle : AnalysisUiState()
    object Analyzing : AnalysisUiState()
    data class Success(val report: SkinReport) : AnalysisUiState()
    data class Error(val message: String) : AnalysisUiState()
}

class AuraSkinViewModel(
    application: Application,
    private val repository: SkinAnalysisRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<AnalysisUiState>(AnalysisUiState.Idle)
    val uiState: StateFlow<AnalysisUiState> = _uiState.asStateFlow()

    private val _activeReport = MutableStateFlow<SkinReport?>(null)
    val activeReport: StateFlow<SkinReport?> = _activeReport.asStateFlow()

    // Routine checkmark states (StepNumber -> Boolean)
    private val _completedMorningSteps = MutableStateFlow<Set<Int>>(emptySet())
    val completedMorningSteps: StateFlow<Set<Int>> = _completedMorningSteps.asStateFlow()

    private val _completedEveningSteps = MutableStateFlow<Set<Int>>(emptySet())
    val completedEveningSteps: StateFlow<Set<Int>> = _completedEveningSteps.asStateFlow()

    private val _completedWeeklySteps = MutableStateFlow<Set<Int>>(emptySet())
    val completedWeeklySteps: StateFlow<Set<Int>> = _completedWeeklySteps.asStateFlow()

    // Dark Mode Support
    private val _isDarkMode = MutableStateFlow<Boolean?>(null)
    val isDarkMode: StateFlow<Boolean?> = _isDarkMode.asStateFlow()

    fun toggleDarkMode(currentSystemDark: Boolean) {
        val current = _isDarkMode.value ?: currentSystemDark
        _isDarkMode.value = !current
    }

    // History from Room database
    val analysesHistory: StateFlow<List<SkinAnalysisEntity>> = repository.allItems
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Populate standard default report so the app loads with a beautiful empty/initial dashboard immediately
        loadDefaultReport()
    }

    private fun loadDefaultReport() {
        val initial = SkinAnalysisEngine.getSimulatedReport("Normal / Healthy Standard")
        _activeReport.value = initial
        _uiState.value = AnalysisUiState.Success(initial)
    }

    fun triggerSkinAnalysis(bitmap: Bitmap?, faceTypeLabel: String) {
        viewModelScope.launch {
            _uiState.value = AnalysisUiState.Analyzing
            _completedMorningSteps.value = emptySet()
            _completedEveningSteps.value = emptySet()
            _completedWeeklySteps.value = emptySet()
            try {
                // Perform the analysis (Live Gemini API or robust simulation fallback)
                val report = SkinAnalysisEngine.analyzeSkin(bitmap, faceTypeLabel)
                
                // Save report to Room local database
                val entity = SkinAnalysisEntity(
                    imageSourceUri = faceTypeLabel,
                    report = report
                )
                repository.insertAnalysis(entity)

                // Update active views
                _activeReport.value = report
                _uiState.value = AnalysisUiState.Success(report)
            } catch (e: Exception) {
                Log.e("AuraSkinViewModel", "Analysis failed", e)
                _uiState.value = AnalysisUiState.Error(e.localizedMessage ?: "Unknown analysis error")
            }
        }
    }

    fun selectHistoryReport(report: SkinReport) {
        _activeReport.value = report
        _uiState.value = AnalysisUiState.Success(report)
        // Reset routine checklist for new report
        _completedMorningSteps.value = emptySet()
        _completedEveningSteps.value = emptySet()
        _completedWeeklySteps.value = emptySet()
    }

    fun toggleMorningStep(stepNumber: Int) {
        val current = _completedMorningSteps.value
        _completedMorningSteps.value = if (current.contains(stepNumber)) {
            current - stepNumber
        } else {
            current + stepNumber
        }
    }

    fun toggleEveningStep(stepNumber: Int) {
        val current = _completedEveningSteps.value
        _completedEveningSteps.value = if (current.contains(stepNumber)) {
            current - stepNumber
        } else {
            current + stepNumber
        }
    }

    fun toggleWeeklyStep(stepNumber: Int) {
        val current = _completedWeeklySteps.value
        _completedWeeklySteps.value = if (current.contains(stepNumber)) {
            current - stepNumber
        } else {
            current + stepNumber
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteAnalysis(id)
        }
    }

    fun updateAnalysisNotes(id: Long, notes: String) {
        viewModelScope.launch {
            repository.getAnalysisById(id)?.let { entity ->
                val updated = entity.copy(notes = notes)
                repository.insertAnalysis(updated)
            }
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    // Factory Class
    class Factory(
        private val application: Application,
        private val repository: SkinAnalysisRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuraSkinViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AuraSkinViewModel(application, repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

// Extension to map Flow to allItems
private val SkinAnalysisRepository.allItems: kotlinx.coroutines.flow.Flow<List<SkinAnalysisEntity>>
    get() = this.allAnalyses
