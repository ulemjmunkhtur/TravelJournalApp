package hu.ait.traveljourneyapp.ui.screen.newjourney

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hu.ait.traveljourneyapp.data.Journey
import java.util.Locale

class AddJourneyViewModel: ViewModel() {

    fun getFlagUrl(countryName: String): String? {
        val countryCode = getCountryCode(countryName)
        return countryCode?.let { "https://flagcdn.com/w320/$it.png" }
    }

    fun getCountryCode(countryName: String): String? {
        val locales = Locale.getISOCountries().map { code ->
            Locale("", code)
        }
        return locales.find { it.displayCountry.equals(countryName, ignoreCase = true) }?.country?.lowercase()
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