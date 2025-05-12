package hu.ait.traveljourneyapp.ui.screen.journey

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import hu.ait.traveljourneyapp.data.Journey


@Composable
fun JourneyScreen(journeyId: String, viewModel: JourneyViewModel = viewModel(), navBack: () -> Unit) {
    val journeyState by viewModel.journey.collectAsState()

    var name by rememberSaveable { mutableStateOf("") }
    var country by rememberSaveable { mutableStateOf("") }

    // Load journey if it's not new
    LaunchedEffect(journeyId) {
        if (journeyId != "new") {
            viewModel.loadJourney(journeyId)
        }
    }

    LaunchedEffect(journeyState) {
        journeyState?.let {
            name = it.name
            country = it.country
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Trip Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = country,
            onValueChange = { country = it },
            label = { Text("Country") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val journey = Journey(
                    uid = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                    name = name,
                    country = country
                    // Add other fields as needed
                )
                viewModel.saveJourney(journeyId, journey,
                    onSuccess = { navBack() },
                    onError = { /* Show snackbar or Toast */ }
                )
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save")
        }
    }
}
