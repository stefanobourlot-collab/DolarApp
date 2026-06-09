package com.stefano.dolarapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefano.dolarapp.model.DolarResponse
import com.stefano.dolarapp.model.TipoCambio
import com.stefano.dolarapp.network.DolarApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ConversionDirection {
    PESOS_TO_FOREIGN, FOREIGN_TO_PESOS
}

class DolarViewModel : ViewModel() {

    private val apiService = DolarApiService.create()

    private val _cotizaciones = MutableStateFlow<List<DolarResponse>>(emptyList())
    val cotizaciones: StateFlow<List<DolarResponse>> = _cotizaciones.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount

    private val _selectedTipoCambio = MutableStateFlow(TipoCambio.BLUE)
    val selectedTipoCambio: StateFlow<TipoCambio> = _selectedTipoCambio

    private val _conversionDirection = MutableStateFlow(ConversionDirection.PESOS_TO_FOREIGN)
    val conversionDirection: StateFlow<ConversionDirection> = _conversionDirection

    init {
        fetchCotizaciones()
    }

    fun fetchCotizaciones() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _cotizaciones.value = apiService.getCotizaciones()
            } catch (e: Exception) {
                _error.value = "Error al conectar con el servidor. Revisá tu conexión."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setAmount(value: String) {
        _amount.value = value
    }

    fun setSelectedTipoCambio(tipo: TipoCambio) {
        _selectedTipoCambio.value = tipo
    }

    fun toggleConversionDirection() {
        _conversionDirection.value = if (_conversionDirection.value == ConversionDirection.PESOS_TO_FOREIGN) {
            ConversionDirection.FOREIGN_TO_PESOS
        } else {
            ConversionDirection.PESOS_TO_FOREIGN
        }
    }

    fun calculateConversion(): String {
        val currentAmount = _amount.value.toDoubleOrNull() ?: return ""
        val listaDolares = _cotizaciones.value
        if (listaDolares.isEmpty()) return ""

        // Filtramos usando el nombre que devuelve la API
        val nombreBuscado = when (_selectedTipoCambio.value) {
            TipoCambio.OFICIAL -> "Oficial"
            TipoCambio.BLUE -> "Blue"
            TipoCambio.MEP -> "Bolsa"
            TipoCambio.CCL -> "Contado con liqui"
            TipoCambio.CRIPTO -> "Cripto"
            TipoCambio.EURO -> "Euro"
            TipoCambio.EURO_BLUE -> "Euro Blue"
        }

        val dolarSeleccionado = listaDolares.find { it.nombre.equals(nombreBuscado, ignoreCase = true) }
            ?: return "Cotización no disponible"

        val precioRef = dolarSeleccionado.venta
        if (precioRef == 0.0) return "0.00"

        return if (_conversionDirection.value == ConversionDirection.PESOS_TO_FOREIGN) {
            String.format(java.util.Locale.US, "%.2f", currentAmount / precioRef)
        } else {
            String.format(java.util.Locale.US, "%.2f", currentAmount * precioRef)
        }
    }
}