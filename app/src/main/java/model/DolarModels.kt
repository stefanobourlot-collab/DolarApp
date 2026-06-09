package com.stefano.dolarapp.model

// Modelo para mapear la respuesta individual de la lista de la API
data class DolarResponse(
    val casa: String,
    val nombre: String,
    val compra: Double,
    val venta: Double
)

// CORRECCIÓN: Le agregamos el campo 'displayName' al Enum para que MainActivity lo lea sin errores
enum class TipoCambio(val displayName: String) {
    OFICIAL("Oficial"),
    BLUE("Blue"),
    MEP("Mep"),
    CCL("CCL"),
    CRIPTO("Cripto"),
    EURO("Euro"),
    EURO_BLUE("Euro Blue")
}