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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import hu.ait.traveljourneyapp.ui.screen.newjourney.AddJourneyDialog
import hu.ait.traveljourneyapp.ui.screen.newjourney.AddJourneyViewModel
import java.util.*

import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel

import hu.ait.traveljourneyapp.data.navigation.RetrofitInstance
import android.content.Context
import android.widget.Toast
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng


fun getGeoApiKey(context: Context): String {
    val appInfo = context.packageManager.getApplicationInfo(context.packageName, android.content.pm.PackageManager.GET_META_DATA)
    return appInfo.metaData.getString("com.google.android.geo.API_KEY") ?: ""
}


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

        Column(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
        ) {
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
                    JourneyMap(journeys = state.journeyList.map { it.journey })
                    LazyColumn(modifier = Modifier.weight(1f)) {
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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    if (showDialog) {
        AddJourneyDialog(
            onDismiss = { showDialog = false },
            onSave = { journey ->
                scope.launch {
                    try {
                        val apiKey = getGeoApiKey(context)
                        val response = RetrofitInstance.api.getCoordinates(journey.country, apiKey)

                        if (response.results.isNotEmpty()) {
                            addJourneyViewModel.saveNewJourney(journey)
                            showDialog = false
                        } else {
                            Toast.makeText(context, "Invalid country name.", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Geocoding failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
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

@Composable
fun JourneyMap(journeys: List<Journey>) {
    val coroutineScope = rememberCoroutineScope()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(20.0, 0.0), 2f)
    }

    val journeyCoords = remember { mutableStateMapOf<String, LatLng>() }

    LaunchedEffect(journeys) {
        journeys.forEach { journey ->
            if (!journeyCoords.containsKey(journey.name)) {
                coroutineScope.launch {
                    try {
                        val res = RetrofitInstance.api.getCoordinates(
                            journey.country,
                            "YOUR_GEOCODING_API_KEY"
                        )
                        res.results.firstOrNull()?.geometry?.location?.let {
                            journeyCoords[journey.name] = LatLng(it.lat, it.lng)
                        }
                    } catch (e: Exception) {
                        // Handle failure
                    }
                }
            }
        }
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        cameraPositionState = cameraPositionState
    ) {
        journeyCoords.forEach { (name, coords) ->
            Marker(
                state = MarkerState(position = coords),
                title = name
            )
        }
    }
}