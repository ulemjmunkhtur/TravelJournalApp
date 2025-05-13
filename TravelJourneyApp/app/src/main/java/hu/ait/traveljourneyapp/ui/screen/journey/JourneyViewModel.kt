package hu.ait.traveljourneyapp.ui.screen.journey
// JourneyViewModel.kt


import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hu.ait.traveljourneyapp.data.Journey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class JourneyViewModel : ViewModel() {

    private val _journey = MutableStateFlow<Journey?>(null)
    val journey: StateFlow<Journey?> = _journey

    suspend fun loadJourney(journeyId: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val doc = FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("journeys")
            .document(journeyId)
            .get()
            .await()
        _journey.value = doc.toObject(Journey::class.java)
    }

    fun updateField(field: String, value: Any?) {
        _journey.value = _journey.value?.copy(
            name = (if (field == "name") value as String? else _journey.value?.name).toString(),
            country = (if (field == "country") value as String? else _journey.value?.country).toString(),
            startDate = (if (field == "startDate") value as String? else _journey.value?.startDate).toString(),
            endDate = (if (field == "endDate") value as String? else _journey.value?.endDate).toString(),
            overallRating = if (field == "overallRating") value as Int? else _journey.value?.overallRating,
            publicTransportationRating = if (field == "publicTransportationRating") value as Int? else _journey.value?.publicTransportationRating,
            foodRating = if (field == "foodRating") value as Int? else _journey.value?.foodRating,
            accommodationName = (if (field == "accommodationName") value as String? else _journey.value?.accommodationName).toString(),
            overallThoughts = (if (field == "overallThoughts") value as String? else _journey.value?.overallThoughts).toString(),
            favoriteMemories = (if (field == "favoriteMemories") value as String? else _journey.value?.favoriteMemories).toString(),
            favoriteQuotes = (if (field == "favoriteQuotes") value as String? else _journey.value?.favoriteQuotes).toString(),
            wordsLearned = (if (field == "wordsLearned") value as String? else _journey.value?.wordsLearned).toString(),
            factsLearned = (if (field == "factsLearned") value as String? else _journey.value?.factsLearned).toString(),
            topActivities = if (field == "topActivities") value as List<String>? else _journey.value?.topActivities,
            flagUrl = (if (field == "flagUrl") value as String? else _journey.value?.flagUrl).toString()
        )
    }

    fun saveJourney(journeyId: String?, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val dbRef = FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("journeys")

        val journeyToSave = _journey.value ?: return

        if (journeyId == "new") {
            dbRef.add(journeyToSave)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onError(it.localizedMessage ?: "Error") }
        } else {
            dbRef.document(journeyId.toString())
                .set(journeyToSave)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onError(it.localizedMessage ?: "Error") }
        }
    }
}
