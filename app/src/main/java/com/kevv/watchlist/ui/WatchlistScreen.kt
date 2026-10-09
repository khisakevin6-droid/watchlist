package com.kevv.watchlist.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kevv.watchlist.data.MediaType
import com.kevv.watchlist.data.Status
import com.kevv.watchlist.data.Title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistScreen(vm: WatchViewModel = viewModel()) {
    val list by vm.list.collectAsStateWithLifecycle()
    val query by vm.query.collectAsStateWithLifecycle()
    val filter by vm.filter.collectAsStateWithLifecycle()

    var showForm by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Title?>(null) }
    var deleting by remember { mutableStateOf<Title?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Column {
                    Text("Kevv Watchlist", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "${list.total} saved ${if (list.total == 1) "title" else "titles"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { editing = null; showForm = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add title")
            }
        }
    ) { pad ->
        Column(Modifier.padding(pad).fillMaxSize()) {
            OutlinedTextField(
                value = query,
                onValueChange = vm::setQuery,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                placeholder = { Text("Search titles") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    if (query.isNotEmpty()) IconButton(onClick = { vm.setQuery("") }) {
                        Icon(Icons.Default.Clear, "Clear search")
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { FilterChip(filter == null, { vm.setFilter(null) }, { Text("All") }) }
                items(Status.values().toList()) { s ->
                    FilterChip(filter == s, { vm.setFilter(s) }, { Text(s.label) })
                }
            }
            if (list.items.isEmpty()) {
                EmptyState(hasAny = list.total > 0)
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(list.items, key = { it.id }) { t ->
                        TitleCard(t,
                            onEdit = { editing = t; showForm = true },
                            onDelete = { deleting = t })
                    }
                }
            }
        }
    }

    if (showForm) {
        TitleDialog(
            initial = editing,
            onDismiss = { showForm = false },
            onSave = { vm.save(it); showForm = false }
        )
    }
    deleting?.let { t ->
        AlertDialog(
            onDismissRequest = { deleting = null },
            title = { Text("Delete title?") },
            text = { Text("\"${t.name}\" will be removed from your watchlist.") },
            confirmButton = {
                TextButton(onClick = { vm.delete(t); deleting = null }) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { deleting = null }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun EmptyState(hasAny: Boolean) {
    Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                if (hasAny) "No matches" else "Your watchlist is empty",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            Text(
                if (hasAny) "Try a different search or filter."
                else "Tap + to add your first movie or TV show.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TitleCard(t: Title, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(t.name, style = MaterialTheme.typography.titleMedium,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text("${t.type.label} · ${t.status.label}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (t.type == MediaType.TV) {
                    Text("Season ${t.season} · Episode ${t.episode}",
                        style = MaterialTheme.typography.bodySmall)
                }
                if (t.rating > 0) {
                    Text("Rating: ${t.rating}/10", style = MaterialTheme.typography.bodySmall)
                }
            }
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Edit ${t.name}") }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Delete ${t.name}") }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TitleDialog(initial: Title?, onDismiss: () -> Unit, onSave: (Title) -> Unit) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var type by remember { mutableStateOf(initial?.type ?: MediaType.MOVIE) }
    var status by remember { mutableStateOf(initial?.status ?: Status.WANT) }
    var season by remember { mutableStateOf((initial?.season ?: 1).toString()) }
    var episode by remember { mutableStateOf((initial?.episode ?: 1).toString()) }
    var rating by remember { mutableStateOf((initial?.rating ?: 0).toFloat()) }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Add title" else "Edit title") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name, onValueChange = { name = it; showError = false },
                    label = { Text("Title") }, singleLine = true,
                    isError = showError,
                    supportingText = { if (showError) Text("Enter a title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Type", style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MediaType.values().forEach {
                        FilterChip(type == it, { type = it }, { Text(it.label) })
                    }
                }
                Text("Status", style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Status.values().forEach {
                        FilterChip(status == it, { status = it }, { Text(it.label) })
                    }
                }
                if (type == MediaType.TV) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(season, { season = it.filter(Char::isDigit).take(3) },
                            label = { Text("Season") }, singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f))
                        OutlinedTextField(episode, { episode = it.filter(Char::isDigit).take(4) },
                            label = { Text("Episode") }, singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f))
                    }
                }
                val r = rating.toInt()
                Text(if (r == 0) "Rating: unrated" else "Rating: $r/10",
                    style = MaterialTheme.typography.labelLarge)
                Slider(value = rating, onValueChange = { rating = it },
                    valueRange = 0f..10f, steps = 9)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isBlank()) { showError = true; return@TextButton }
                onSave(
                    (initial ?: Title(name = "")).copy(
                        name = name.trim(), type = type, status = status,
                        season = season.toIntOrNull()?.coerceAtLeast(1) ?: 1,
                        episode = episode.toIntOrNull()?.coerceAtLeast(1) ?: 1,
                        rating = rating.toInt()
                    )
                )
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
