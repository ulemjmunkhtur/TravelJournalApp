package hu.ait.traveljourneyapp.ui.screen.journey

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

    fun saveJourney(journeyId: String?, journey: Journey, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("journeys")

        if (journeyId == "new") {
            db.add(journey)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onError(it.localizedMessage ?: "Error") }
        } else {
            db.document(journeyId.toString())
                .set(journey)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onError(it.localizedMessage ?: "Error") }
        }
    }
}
