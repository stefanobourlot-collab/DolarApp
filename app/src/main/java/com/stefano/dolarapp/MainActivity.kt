package com.stefano.dolarapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stefano.dolarapp.model.TipoCambio
import com.stefano.dolarapp.viewmodel.ConversionDirection
import com.stefano.dolarapp.viewmodel.DolarViewModel
import java.text.NumberFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DolarApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DolarApp(viewModel: DolarViewModel = viewModel()) {
    val cotizaciones by viewModel.cotizaciones.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val amount by viewModel.amount.collectAsState()
    val selectedTipoCambio by viewModel.selectedTipoCambio.collectAsState()
    val conversionDirection by viewModel.conversionDirection.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cotizaciones en Tiempo Real") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Sección Convertidor
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Convertidor Inteligente",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        // Selector del tipo de moneda a usar para convertir
                        var expanded by remember { mutableStateOf(false) }
                        Box {
                            OutlinedButton(
                                onClick = { expanded = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                                    contentDescription = "Cambiar Moneda"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Moneda: ${selectedTipoCambio.displayName}")
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                TipoCambio.values().forEach { tipo ->
                                    DropdownMenuItem(
                                        text = { Text(tipo.displayName) },
                                        onClick = {
                                            viewModel.setSelectedTipoCambio(tipo)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Botón de sentido de conversión
                        Button(
                            onClick = { viewModel.toggleConversionDirection() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val btnText = when (conversionDirection) {
                                ConversionDirection.PESOS_TO_FOREIGN -> "Convertir: Pesos ➔ ${selectedTipoCambio.displayName}"
                                ConversionDirection.FOREIGN_TO_PESOS -> "Convertir: ${selectedTipoCambio.displayName} ➔ Pesos"
                            }
                            Text(btnText)
                        }

                        // Input del monto
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { viewModel.setAmount(it) },
                            label = {
                                Text(
                                    if (conversionDirection == ConversionDirection.PESOS_TO_FOREIGN) "Monto en Pesos ($)"
                                    else "Monto en Moneda Extranjera"
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Pizarra de resultado matemático
                        val calculation = viewModel.calculateConversion()
                        if (calculation.isNotEmpty() && amount.isNotEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Resultado Estimado:", fontWeight = FontWeight.Bold)
                                    Text(
                                        text = if (conversionDirection == ConversionDirection.PESOS_TO_FOREIGN) "$calculation Extranjero" else "$$calculation ARS",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Barra de carga dinámica
            if (isLoading) {
                item { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
            }

            // Cartelera en caso de fallo de red
            error?.let { msg ->
                item {
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(msg, color = MaterialTheme.colorScheme.onErrorContainer)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.fetchCotizaciones() }) { Text("Reintentar") }
                        }
                    }
                }
            }

            // Listado de Monedas en tiempo real limpio y ordenado
            if (cotizaciones.isEmpty()) {
                item {
                    Text(
                        text = "Cargando cotizaciones o servidor no disponible...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                items(cotizaciones) { dolar ->
                    val tituloTarjeta = when (dolar.casa) {
                        "oficial" -> "Dólar Oficial"
                        "blue" -> "Dólar Blue"
                        "bolsa" -> "Dólar MEP (Bolsa)"
                        "ccl" -> "Dólar CCL"
                        "cripto" -> "Dólar Cripto"
                        "euro" -> "Euro Oficial"
                        "euro_blue" -> "Euro Blue"
                        else -> dolar.nombre
                    }

                    MonedaCard(
                        title = tituloTarjeta,
                        compra = dolar.compra,
                        venta = dolar.venta,
                        fecha = "Actualizado"
                    )
                }
            }
        }
    }
}

@Composable
fun MonedaCard(title: String, compra: Double, venta: Double, fecha: String) {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "AR"))
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Compra", style = MaterialTheme.typography.bodySmall)
                    Text(format.format(compra), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Venta", style = MaterialTheme.typography.bodySmall)
                    Text(format.format(venta), color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("Actualizado: $fecha", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
        }
    }
}