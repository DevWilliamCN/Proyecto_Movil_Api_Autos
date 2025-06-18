@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.example.desarollomovilapi

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.desarollomovilapi.ui.theme.DesarolloMovilApiTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DesarolloMovilApiTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AutoListScreen()
                }
            }
        }
    }
}

@Composable
fun AutoListScreen() {
    val scope = rememberCoroutineScope()
    var autos by remember { mutableStateOf<List<Auto>>(emptyList()) }
    var filteredAutos by remember { mutableStateOf<List<Auto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchText by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                autos = ApiService.getAutos()
                filteredAutos = autos
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🚗 Catálogo de Autos",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Text(
                    "© 2025 Desarrollado por William",
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Text("+")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    filteredAutos = autos.filter { auto ->
                        auto.marca.contains(it, true) ||
                                auto.modelo.contains(it, true) ||
                                auto.categoria.contains(it, true)
                    }
                },
                label = { Text("Buscar por marca, modelo o categoría") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredAutos) { auto ->
                        AutoCard(auto)
                    }
                }
            }
        }

        if (showDialog) {
            AddAutoDialog(
                onAdd = { nuevoAuto ->
                    autos = autos + nuevoAuto
                    filteredAutos = autos
                    showDialog = false
                },
                onCancel = { showDialog = false }
            )
        }
    }
}

@Composable
fun AutoCard(auto: Auto) {
    val context = LocalContext.current
    val imageName = auto.marca.lowercase().replace(" ", "")
    val imageResId = remember(imageName) {
        val resId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
        if (resId != 0) resId else {
            Log.w("AutoCard", "Imagen no encontrada para $imageName, usando default_car")
            R.drawable.default_car
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Imagen del auto",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )

            Column(modifier = Modifier.padding(12.dp)) {
                Text("${auto.marca} ${auto.modelo}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("₡${auto.precio}", fontWeight = FontWeight.SemiBold)
                Text("Año: ${auto.anio}")
                Text("Rendimiento: ${auto.rendimientoKmL} km/l")
                Text("Categoría: ${auto.categoria}")
                Text("Ranking: ${auto.ranking}")
            }
        }
    }
}

@Composable
fun AddAutoDialog(onAdd: (Auto) -> Unit, onCancel: () -> Unit) {
    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var anio by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var rendimiento by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var ranking by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancel,
        confirmButton = {
            TextButton(onClick = {
                val auto = Auto(
                    marca = marca,
                    modelo = modelo,
                    anio = anio.toIntOrNull() ?: 0,
                    precio = precio.toIntOrNull() ?: 0,
                    rendimientoKmL = rendimiento.toDoubleOrNull() ?: 0.0,
                    categoria = categoria,
                    ranking = ranking.toDoubleOrNull() ?: 0.0
                )
                onAdd(auto)
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text("Cancelar") }
        },
        title = { Text("Agregar Auto") },
        text = {
            Column {
                OutlinedTextField(value = marca, onValueChange = { marca = it }, label = { Text("Marca") })
                OutlinedTextField(value = modelo, onValueChange = { modelo = it }, label = { Text("Modelo") })
                OutlinedTextField(value = anio, onValueChange = { anio = it }, label = { Text("Año") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = precio, onValueChange = { precio = it }, label = { Text("Precio") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = rendimiento, onValueChange = { rendimiento = it }, label = { Text("Rendimiento km/l") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = categoria, onValueChange = { categoria = it }, label = { Text("Categoría") })
                OutlinedTextField(value = ranking, onValueChange = { ranking = it }, label = { Text("Ranking") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
        }
    )
}
