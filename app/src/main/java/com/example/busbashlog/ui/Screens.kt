package com.example.busbashlog.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.busbashlog.data.LogRepository
import com.example.busbashlog.model.LogEntry
import com.example.busbashlog.model.Vehicle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class LogViewModel(private val repo: LogRepository) : ViewModel() {
    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()
    private val _results = MutableStateFlow<List<Vehicle>>(emptyList())
    val results = _results.asStateFlow()
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()
    val entries = repo.entries

    fun onQuery(q: String) { _query.value = q }

    fun search() {
        viewModelScope.launch {
            _loading.value = true
            repo.lookup(_query.value)
                .onSuccess { _results.value = it }
                .onFailure { _results.value = emptyList() }
            _loading.value = false
        }
    }

    fun log(v: Vehicle?, notes: String = "") {
        viewModelScope.launch { repo.add(v, notes) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val vm: LogViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                LogViewModel(LogRepository(context.applicationContext)) as T
        }
    )

    val query by vm.query.collectAsState()
    val results by vm.results.collectAsState()
    val loading by vm.loading.collectAsState()
    val entries by vm.entries.collectAsState(initial = emptyList())
    var notes by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Bus Bash Log") }) },
        floatingActionButton = {
            // Simple share of latest entry as example
            if (entries.isNotEmpty()) {
                FloatingActionButton(onClick = {
                    val e = entries.first()
                    val text = "Seen ${e.fleetCode ?: ""} ${e.reg ?: ""} (${e.operatorName ?: ""}) – ${e.notes}"
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, text)
                    }
                    context.startActivity(Intent.createChooser(intent, "Share sighting"))
                }) {
                    Icon(Icons.Default.Share, contentDescription = "Share")
                }
            }
        }
    ) { padding ->
        Column(Modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = vm::onQuery,
                label = { Text("Fleet or registration") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            Button(onClick = { vm.search() }, enabled = !loading) {
                Text(if (loading) "Looking up…" else "Look up")
            }

            results.firstOrNull()?.let { v ->
                Card(Modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text(v.display, fontWeight = FontWeight.Bold)
                        Text("${v.operator?.name ?: ""} • ${v.vehicleType?.name ?: ""}")
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notes") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(onClick = {
                            vm.log(v, notes)
                            notes = ""
                        }) {
                            Icon(Icons.Default.Add, null)
                            Spacer(Modifier.width(4.dp))
                            Text("Add to log")
                        }
                    }
                }
            }

            Text("Your log", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
            LazyColumn {
                items(entries) { e ->
                    LogCard(e)
                }
            }
        }
    }
}

@Composable
fun LogCard(e: LogEntry) {
    val sdf = remember { SimpleDateFormat("dd MMM yyyy HH:mm", Locale.UK) }
    Card(Modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text(
                listOfNotNull(e.fleetCode, e.reg).joinToString(" • "),
                fontWeight = FontWeight.Bold
            )
            Text("${e.operatorName ?: ""} • ${e.typeName ?: ""}")
            if (e.notes.isNotBlank()) Text(e.notes)
            Text(sdf.format(Date(e.timestamp)), style = MaterialTheme.typography.bodySmall)
        }
    }
}
