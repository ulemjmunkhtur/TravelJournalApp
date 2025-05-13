package hu.ait.traveljourneyapp.ui.screen.journey

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import hu.ait.traveljourneyapp.data.Journey

@Composable
fun JourneyScreen(
    journeyId: String,
    viewModel: JourneyViewModel = viewModel(),
    navBack: () -> Unit = {}
) {
    val journey by viewModel.journey.collectAsState()

    LaunchedEffect(journeyId) {
        viewModel.loadJourney(journeyId)
    }

    journey?.let { trip ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Edit", style = MaterialTheme.typography.titleMedium)
                trip.flagUrl?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = "Flag",
                        modifier = Modifier.size(64.dp)
                    )
                }
            }

            Text("Name: ${trip.name ?: "-"}")
            Text("Country: ${trip.country ?: "-"}")
            Text("Dates: ${trip.startDate ?: "-"} to ${trip.endDate ?: "-"}")

            RatingRow("Overall", trip.overallRating ?: 0) { viewModel.updateField("overallRating", it) }
            RatingRow("Public Transport", trip.publicTransportationRating ?: 0) { viewModel.updateField("publicTransportationRating", it) }
            RatingRow("Food", trip.foodRating ?: 0) { viewModel.updateField("foodRating", it) }

            EditableSection("Overall Thoughts", trip.overallThoughts) {
                viewModel.updateField("overallThoughts", it)
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                EditableSection("Favorite Memory", trip.favoriteMemories) {
                    viewModel.updateField("favoriteMemories", it)
                }
                EditableSection("Favorite Quotes", trip.favoriteQuotes) {
                    viewModel.updateField("favoriteQuotes", it)
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                EditableSection("Words Learned", trip.wordsLearned) {
                    viewModel.updateField("wordsLearned", it)
                }
                EditableSection("Facts Learned", trip.factsLearned) {
                    viewModel.updateField("factsLearned", it)
                }
            }

            Text("Top Activities:", style = MaterialTheme.typography.titleSmall)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(trip.topActivities?.size ?: 0) { idx ->
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color.LightGray, RoundedCornerShape(8.dp))
                    ) {
                        Text(trip.topActivities?.get(idx) ?: "")
                    }
                }
            }
        }
    }
}

@Composable
fun RatingRow(label: String, rating: Int, onRatingChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("$label:", modifier = Modifier.width(140.dp))
        repeat(5) { index ->
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = if (index < rating) Color.Yellow else Color.Gray,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onRatingChange(index + 1) }
            )
        }
    }
}

@Composable
fun EditableSection(label: String, content: String?, onUpdate: (String) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var tempText by remember { mutableStateOf(content ?: "") }

    Column(
        modifier = Modifier
            .width(160.dp)
            .clickable { showDialog = true }
            .padding(8.dp)
            .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
    ) {
        Text(text = "$label:", style = MaterialTheme.typography.bodySmall)
        Text(text = content ?: "Tap to edit", modifier = Modifier.padding(4.dp))
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Edit $label") },
            text = {
                TextField(
                    value = tempText,
                    onValueChange = { tempText = it },
                    label = { Text(label) }
                )
            },
            confirmButton = {
                Button(onClick = {
                    onUpdate(tempText)
                    showDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
