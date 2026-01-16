package com.rhinepereira.versetrack.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rhinepereira.versetrack.data.BibleData
import com.rhinepereira.versetrack.data.Note
import com.rhinepereira.versetrack.data.NoteWithVerses
import com.rhinepereira.versetrack.data.Verse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerseScreen(viewModel: VerseViewModel = viewModel()) {
    var selectedNoteWithVerses by remember { mutableStateOf<NoteWithVerses?>(null) }
    var showAddNoteDialog by remember { mutableStateOf(false) }
    var showAddVerseDialog by remember { mutableStateOf(false) }

    val notesWithVerses by viewModel.allNotesWithVerses.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedNoteWithVerses?.note?.theme ?: "Verse Track") },
                navigationIcon = {
                    if (selectedNoteWithVerses != null) {
                        IconButton(onClick = { selectedNoteWithVerses = null }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (selectedNoteWithVerses == null) showAddNoteDialog = true else showAddVerseDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (selectedNoteWithVerses == null) {
                // Themes Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notesWithVerses) { noteWithVerses ->
                        ThemeCard(
                            noteWithVerses = noteWithVerses,
                            onClick = { selectedNoteWithVerses = noteWithVerses },
                            onDelete = { viewModel.deleteNote(noteWithVerses.note) }
                        )
                    }
                }
            } else {
                // Verses List for selected Theme
                val verses by viewModel.getVersesForNote(selectedNoteWithVerses!!.note.id).collectAsState(initial = emptyList())
                BackHandler { selectedNoteWithVerses = null }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(verses) { verse ->
                        VerseItem(
                            verse = verse,
                            onDelete = { viewModel.deleteVerse(verse) }
                        )
                    }
                }
            }
        }

        if (showAddNoteDialog) {
            AddNoteDialog(
                onDismiss = { showAddNoteDialog = false },
                onConfirm = { theme ->
                    viewModel.addNote(theme)
                    showAddNoteDialog = false
                }
            )
        }

        if (showAddVerseDialog && selectedNoteWithVerses != null) {
            AddVerseDialog(
                onDismiss = { showAddVerseDialog = false },
                onConfirm = { reference, content ->
                    viewModel.addVerse(selectedNoteWithVerses!!.note.id, reference, content)
                    showAddVerseDialog = false
                }
            )
        }
    }
}

@Composable
fun ThemeCard(noteWithVerses: NoteWithVerses, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = noteWithVerses.note.theme,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete Theme",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            val recentVerses = noteWithVerses.verses.sortedByDescending { it.createdAt }.take(2)
            if (recentVerses.isEmpty()) {
                Text(
                    "No verses yet",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            } else {
                recentVerses.forEach { verse ->
                    Column(modifier = Modifier.padding(bottom = 4.dp)) {
                        Text(
                            text = verse.reference,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = verse.content,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VerseItem(verse: Verse, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = verse.reference,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Verse")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = verse.content,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun AddNoteDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var theme by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Theme") },
        text = {
            TextField(value = theme, onValueChange = { theme = it }, label = { Text("Theme (e.g. Faith, Hope)") })
        },
        confirmButton = {
            Button(onClick = { if (theme.isNotBlank()) onConfirm(theme) }) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVerseDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedBook by remember { mutableStateOf(BibleData.catholicBooks[0]) }
    var chapter by remember { mutableStateOf("") }
    var verseStart by remember { mutableStateOf("") }
    var verseEnd by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Bible Verse") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Book Selection
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = selectedBook,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Book") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        BibleData.catholicBooks.forEach { book ->
                            DropdownMenuItem(
                                text = { Text(book) },
                                onClick = {
                                    selectedBook = book
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextField(
                        value = chapter,
                        onValueChange = { if (it.all { char -> char.isDigit() }) chapter = it },
                        label = { Text("Ch.") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    TextField(
                        value = verseStart,
                        onValueChange = { if (it.all { char -> char.isDigit() }) verseStart = it },
                        label = { Text("Ver.") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    TextField(
                        value = verseEnd,
                        onValueChange = { if (it.all { char -> char.isDigit() }) verseEnd = it },
                        label = { Text("End") },
                        placeholder = { Text("-") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                TextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Verse Content") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (chapter.isNotBlank() && verseStart.isNotBlank() && content.isNotBlank()) {
                    val ref = if (verseEnd.isBlank()) {
                        "$selectedBook $chapter:$verseStart"
                    } else {
                        "$selectedBook $chapter:$verseStart-$verseEnd"
                    }
                    onConfirm(ref, content)
                }
            }) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
