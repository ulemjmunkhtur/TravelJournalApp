package hu.ait.traveljourneyapp.ui.screen.newjourney

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import hu.ait.traveljourneyapp.data.Journey
import java.util.Calendar


@Composable
fun AddJourneyDialog(
    onDismiss: () -> Unit,
    onSave: (Journey) -> Unit,
    viewModel: AddJourneyViewModel
) {
    var name by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    fun showDatePicker(onDateSelected: (String) -> Unit) {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                val formatted = String.format("%04d-%02d-%02d", year, month + 1, day)
                onDateSelected(formatted)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Journey") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Trip Name") },
                    singleLine = true
                )
                TextField(
                    value = country,
                    onValueChange = { country = it },
                    label = { Text("Country") },
                    singleLine = true
                )
                OutlinedButton(onClick = {
                    showDatePicker { selectedDate -> startDate = selectedDate }
                }) {
                    Text(text = if (startDate.isBlank()) "Select Start Date" else "Start: $startDate")
                }
                OutlinedButton(onClick = {
                    showDatePicker { selectedDate -> endDate = selectedDate }
                }) {
                    Text(text = if (endDate.isBlank()) "Select End Date" else "End: $endDate")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
                val flagUrl = viewModel.getFlagUrl(country)  // "France" → https://flagcdn.com/w320/fr.png
                val journey = Journey(
                    uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty(),
                    name = name.takeIf { it.isNotBlank() }.toString(),
                    country = country.takeIf { it.isNotBlank() }.toString(),
                    startDate = startDate.takeIf { it.isNotBlank() }.toString(),
                    endDate = endDate.takeIf { it.isNotBlank() }.toString(),
                    flagUrl = flagUrl.toString()
                )
                onSave(journey)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
