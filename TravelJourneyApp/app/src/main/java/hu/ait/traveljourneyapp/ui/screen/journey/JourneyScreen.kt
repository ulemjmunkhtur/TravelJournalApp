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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
            TextButton(onClick = navBack) {
                Text("< Back")
            }

            Text(
                text = "${trip.name ?: "-"}, ${trip.country ?: "-"}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                trip.flagUrl?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = "Flag",
                        modifier = Modifier
                            .height(100.dp)
                            .width(100.dp)
                    )
                }
                Column(horizontalAlignment = Alignment.Start) {
                    Text("Dates: ${trip.startDate ?: "-"} to ${trip.endDate ?: "-"}")
                    RatingRow("Overall", trip.overallRating ?: 0) {
                        viewModel.updateField("overallRating", it)
                        viewModel.saveJourney(journeyId, {}, {})
                    }
                    RatingRow("Public Transport", trip.publicTransportationRating ?: 0) {
                        viewModel.updateField("publicTransportationRating", it)
                        viewModel.saveJourney(journeyId, {}, {})
                    }
                    RatingRow("Food", trip.foodRating ?: 0) {
                        viewModel.updateField("foodRating", it)
                        viewModel.saveJourney(journeyId, {}, {})
                    }
                }
            }

            EditableSectionFullWidth("Overall Thoughts", trip.overallThoughts ?: "") {
                viewModel.updateField("overallThoughts", it)
                viewModel.saveJourney(journeyId, {}, {})
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                EditableSection("Favorite Memory", trip.favoriteMemories ?: "") {
                    viewModel.updateField("favoriteMemories", it)
                    viewModel.saveJourney(journeyId, {}, {})
                }
                EditableSection("Favorite Quotes", trip.favoriteQuotes ?: "") {
                    viewModel.updateField("favoriteQuotes", it)
                    viewModel.saveJourney(journeyId, {}, {})
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                EditableSection("Words Learned", trip.wordsLearned ?: "") {
                    viewModel.updateField("wordsLearned", it)
                    viewModel.saveJourney(journeyId, {}, {})
                }
                EditableSection("Facts Learned", trip.factsLearned ?: "") {
                    viewModel.updateField("factsLearned", it)
                    viewModel.saveJourney(journeyId, {}, {})
                }
            }

            Text("Top Activities:", style = MaterialTheme.typography.titleSmall)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(trip.topActivities?.size ?: 0) { idx ->
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color(0xFFFFD1DC), RoundedCornerShape(4.dp))
                    ) {
                        Text(
                            trip.topActivities?.get(idx) ?: "",
                            modifier = Modifier.padding(4.dp)
                        )
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
fun EditableSection(label: String, content: String, onUpdate: (String) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var tempText by remember { mutableStateOf(content) }

    Column(
        modifier = Modifier
            .width(180.dp)
            .height(120.dp)
            .clickable { showDialog = true }
            .padding(8.dp)
            .background(Color(0xFFEBDCFB), RoundedCornerShape(12.dp))
    ) {
        Text(text = "$label:", style = MaterialTheme.typography.bodyMedium)
        Text(
            text = content,
            modifier = Modifier.padding(4.dp),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                color = Color(0xFFFFF8DC),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Edit $label", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    TextField(
                        value = tempText,
                        onValueChange = { tempText = it },
                        label = { Text(label) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        singleLine = false,
                        maxLines = Int.MAX_VALUE
                    )
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = { showDialog = false }) { Text("Cancel") }
                        Button(onClick = {
                            onUpdate(tempText)
                            showDialog = false
                        }) { Text("Save") }
                    }
                }
            }
        }
    }
}

@Composable
fun EditableSectionFullWidth(label: String, content: String, onUpdate: (String) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var tempText by remember { mutableStateOf(content) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { showDialog = true }
            .padding(8.dp)
            .background(Color(0xFFEBDCFB), RoundedCornerShape(12.dp))
    ) {
        Text(text = "$label:", style = MaterialTheme.typography.bodySmall)
        Text(
            text = content,
            modifier = Modifier.padding(4.dp),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                color = Color(0xFFFFF8DC),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Edit $label", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    TextField(
                        value = tempText,
                        onValueChange = { tempText = it },
                        label = { Text(label) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        singleLine = false,
                        maxLines = Int.MAX_VALUE
                    )
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = { showDialog = false }) { Text("Cancel") }
                        Button(onClick = {
                            onUpdate(tempText)
                            showDialog = false
                        }) { Text("Save") }
                    }
                }
            }
        }
    }
}
