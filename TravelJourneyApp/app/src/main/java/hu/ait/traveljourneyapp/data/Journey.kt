package hu.ait.traveljourneyapp.data

data class Journey(
    var uid: String = "",
    var name: String = "",
    var country: String = "",
    var startDate: String = "",
    var endDate: String = "",
    var overallRating: Int? = 0,
    var publicTransportationRating: Int? = 0,
    var foodRating: Int? = 0,
    var accommodationName: String = "",
    var accommodationRating: Int = 0,
    var overallThoughts: String = "",
    var favoriteMemories: String = "",
    var favoriteQuotes: String = "",
    var wordsLearned: String = "",
    var factsLearned: String = "",
    var topActivities: List<String>? = emptyList(),
    var galleryImageUrls: List<String> = emptyList(),  // Store image URLs instead of Image objects
    var flagUrl: String = ""  // Store a URL to the flag image
)

data class JourneyWithId(
    var journeyId: String = "",
    var journey: Journey
)