package hu.ait.traveljourneyapp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import hu.ait.traveljourneyapp.ui.screen.journey.JourneyScreen
import hu.ait.traveljourneyapp.ui.screen.journeylist.JourneyListScreen
import hu.ait.traveljourneyapp.ui.screen.login.LoginScreen
import hu.ait.traveljourneyapp.ui.theme.TravelJourneyAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TravelJourneyAppTheme {
                val navController = rememberNavController() // FIXED: initialize controller here
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TravelJournalNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun TravelJournalNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("journeyList") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("journeyList") {
            JourneyListScreen(
                onJourneySelected = { journeyId ->
                    navController.navigate("journeyDetail/$journeyId")
                },
                onAddNewJourney = {
                    navController.navigate("journeyDetail/new")
                }
            )
        }
        composable(
            "journeyDetail/{journeyId}",
            arguments = listOf(navArgument("journeyId") { type = NavType.StringType })
        ) { backStackEntry ->
            val journeyId = backStackEntry.arguments?.getString("journeyId") ?: "new"
            JourneyScreen(journeyId = journeyId, navBack = { navController.popBackStack() })
        }
    }
}
