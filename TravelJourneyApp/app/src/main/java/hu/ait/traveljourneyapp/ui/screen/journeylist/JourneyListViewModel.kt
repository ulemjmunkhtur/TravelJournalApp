package hu.ait.traveljourneyapp.ui.screen.journeylist

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import hu.ait.traveljourneyapp.data.Journey
import hu.ait.traveljourneyapp.data.JourneyWithId
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.util.Locale

class JourneyListViewModel : ViewModel() {

    fun journeysList() = callbackFlow {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid == null) {
            trySend(JourneyUIState.Error("User not logged in"))
            close()
            return@callbackFlow
        }

        val listener = FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("journeys")
            .orderBy("startDate", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                val response = if (snapshot != null) {
                    val journeyList = snapshot.toObjects(Journey::class.java)
                    val journeysWithId = journeyList.mapIndexed { index, journey ->
                        JourneyWithId(snapshot.documents[index].id, journey)
                    }
                    JourneyUIState.Success(journeysWithId)
                } else {
                    JourneyUIState.Error(e?.localizedMessage)
                }

                trySend(response)
            }

        awaitClose {
            listener.remove()
        }
    }

    fun deleteJourney(journeyId: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("journeys")
            .document(journeyId)
            .delete()
    }
    fun saveNewJourney(journey: Journey) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("journeys")
            .add(journey)
    }








}

sealed interface JourneyUIState {
    object Init : JourneyUIState
    object Loading : JourneyUIState
    data class Success(val journeyList: List<JourneyWithId>) : JourneyUIState
    data class Error(val error: String?) : JourneyUIState
}
