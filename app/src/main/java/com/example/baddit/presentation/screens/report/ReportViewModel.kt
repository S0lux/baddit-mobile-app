package com.example.baddit.presentation.screens.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baddit.domain.model.report.ReportResponseDTO
import com.example.baddit.domain.repository.AuthRepository
import com.example.baddit.domain.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.report.ReportType

@HiltViewModel
class ReportViewModel @Inject constructor(
    val reportRepository: ReportRepository,
    val authRepository: AuthRepository
) : ViewModel() {
    private val _reports = MutableStateFlow<List<ReportResponseDTO>>(emptyList())
    val reports: StateFlow<List<ReportResponseDTO>> = _reports.asStateFlow()

    private val _selectedType = MutableStateFlow<ReportType?>(null)
    val selectedType: StateFlow<ReportType?> = _selectedType.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun loadReports() {
        viewModelScope.launch {
            _isLoading.value = true
            _isRefreshing.value = true
            _error.value = null
            try {
                when (val result = reportRepository.getAllReports()) {
                    is Result.Success -> {
                        _reports.value = result.data
                    }

                    is Result.Error -> {
                        _error.value = result.error.name
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load reports"
            } finally {
                _isLoading.value = false
                _isRefreshing.value = false
            }
        }
    }

    fun resolveReport(reportId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = reportRepository.resolveReports(reportId)) {
                is Result.Success -> {
                    loadReports()
                }

                is Result.Error -> {
                    _error.value = result.error.name
                }
            }
            _isLoading.value = false
        }
    }

    fun createReport(
        type: ReportType,
        content: String,
        targetUserId: String? = null,
        targetPostId: String? = null
    ) {
        viewModelScope.launch {
            _error.value = null
            when (val result = reportRepository.createReport(
                type.toString(),
                content,
                targetUserId,
                targetPostId
            )) {
                is Result.Success -> {

                }

                is Result.Error -> {
                    _error.value = result.error.name
                }
            }
        }
    }


    fun setSelectedType(type: String?) {
        _selectedType.value = if (type == null) null else try {
            ReportType.valueOf(type)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    fun getFilteredReports(): List<ReportResponseDTO> {
        return _reports.value.filter { report ->
            selectedType.value?.let { report.type == it } ?: true
        }
    }

}
