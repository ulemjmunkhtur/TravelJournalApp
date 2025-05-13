package hu.ait.traveljourneyapp.ui.screen.journeylist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import hu.ait.traveljourneyapp.data.JourneyWithId
import hu.ait.traveljourneyapp.data.Journey
import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.ui.platform.LocalContext
import hu.ait.traveljourneyapp.ui.screen.newjourney.AddJourneyDialog
import hu.ait.traveljourneyapp.ui.screen.newjourney.AddJourneyViewModel
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JourneyListScreen(
    viewModel: JourneyListViewModel = viewModel(),
    onJourneySelected: (String) -> Unit,
    onAddNewJourney: () -> Unit
) {
    val journeyListState = viewModel.journeysList().collectAsState(initial = JourneyUIState.Init)
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Journeys") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                actions = {
                    IconButton(onClick = { /* TODO: Add Info behavior */ }) {
                        Icon(Icons.Filled.Info, contentDescription = "Info")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->

        Column(modifier = Modifier.padding(paddingValues)) {
            when (val state = journeyListState.value) {
                is JourneyUIState.Init -> {
                    Text("Initializing...")
                }
                is JourneyUIState.Loading -> {
                    CircularProgressIndicator()
                }
                is JourneyUIState.Error -> {
                    Text("Error: ${state.error}")
                }
                is JourneyUIState.Success -> {
                    LazyColumn {
                        items(state.journeyList) { journeyWithId ->
                            JourneyCard(
                                journey = journeyWithId,
                                onClick = { onJourneySelected(journeyWithId.journeyId) },
                                onRemoveItem = {
                                    viewModel.deleteJourney(journeyWithId.journeyId)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    val addJourneyViewModel: AddJourneyViewModel = viewModel()
    if (showDialog) {
        AddJourneyDialog(
            onDismiss = { showDialog = false },
            onSave = { journey->
                addJourneyViewModel.saveNewJourney(journey)
                showDialog = false
            },
            viewModel = addJourneyViewModel
        )
    }
}

@Composable
fun JourneyCard(
    journey: JourneyWithId,
    onClick: () -> Unit,
    onRemoveItem: () -> Unit
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = journey.journey.name, style = MaterialTheme.typography.titleMedium)
                    Text(text = "Country: ${journey.journey.country}")
                    Text(text = "Dates: ${journey.journey.startDate} – ${journey.journey.endDate}")
                    Text(text = "Rating: ${journey.journey.overallRating}/10")
                }
                if (journey.journey.uid == currentUserId) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clickable { onRemoveItem() }
                    )
                }
            }
        }
    }
}
