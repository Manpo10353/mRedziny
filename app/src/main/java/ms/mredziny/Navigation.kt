package ms.mredziny

import android.net.Uri
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.maps.model.LatLng
import ms.mredziny.feature.communication.favourite.BusFavouriteScreen
import ms.mredziny.feature.communication.favourite.BusFavouriteViewModel
import ms.mredziny.feature.communication.linemap.BusLineMapScreen
import ms.mredziny.feature.communication.linemap.BusLineMapViewModel
import ms.mredziny.feature.communication.menu.BusScheduleScreen
import ms.mredziny.feature.communication.schedule.BusStopScheduleScreen
import ms.mredziny.feature.communication.schedule.BusStopScheduleViewModel
import ms.mredziny.feature.communication.stops.BusStopScreen
import ms.mredziny.feature.communication.stops.BusStopViewModel
import ms.mredziny.feature.home.HomeScreen
import ms.mredziny.feature.waste.WasteScheduleScreen
import ms.mredziny.feature.waste.WasteScheduleViewModel

// Środek gminy Rędziny, używany gdy mapa nie dostaje punktu startowego.
private val GMINA_CENTER = LatLng(50.857524, 19.211380)

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) },
    ) {
        composable(Screen.Home.route) {
            HomeScreen(onNavigate = { route -> navController.navigate(route) })
        }

        composable(Screen.BusSchedule.route) {
            BusScheduleScreen(navController)
        }

        composable(
            route = "${Screen.BusStops.route}/{direction}",
            arguments = listOf(navArgument("direction") { type = NavType.StringType }),
        ) { entry ->
            BusStopScreen(
                navController,
                hiltViewModel<BusStopViewModel>(),
                entry.arguments?.getString("direction").orEmpty(),
            )
        }

        composable(
            route = "${Screen.BusStopSchedule.route}/{busStopId}/{busStopName}/{direction}",
            arguments = listOf(
                navArgument("busStopId") { type = NavType.LongType },
                navArgument("busStopName") { type = NavType.StringType },
                navArgument("direction") { type = NavType.StringType },
            ),
        ) { entry ->
            val args = entry.arguments
            BusStopScheduleScreen(
                viewModel = hiltViewModel<BusStopScheduleViewModel>(),
                navController = navController,
                busStopName = args?.getString("busStopName").orEmpty(),
                busStopId = args?.getLong("busStopId") ?: 0L,
                direction = args?.getString("direction").orEmpty(),
            )
        }

        composable(
            route = "${Screen.BusFavourite.route}/{direction}",
            arguments = listOf(navArgument("direction") { type = NavType.StringType }),
        ) { entry ->
            BusFavouriteScreen(
                navController,
                hiltViewModel<BusFavouriteViewModel>(),
                entry.arguments?.getString("direction").orEmpty(),
            )
        }

        composable(Screen.WasteCollectionSchedule.route) {
            WasteScheduleScreen(hiltViewModel<WasteScheduleViewModel>())
        }

        composable(
            route = "${Screen.BusLineMap.route}/{viewPoint}",
            arguments = listOf(navArgument("viewPoint") { type = NavType.StringType }),
        ) { entry ->
            val point = parseLatLng(entry.arguments?.getString("viewPoint")) ?: GMINA_CENTER
            BusLineMapScreen(navController, hiltViewModel<BusLineMapViewModel>(), point, 20f)
        }

        composable(Screen.BusLineMap.route) {
            BusLineMapScreen(navController, hiltViewModel<BusLineMapViewModel>(), GMINA_CENTER, 10f)
        }
    }
}

// Zamiast sklejać ścieżkę ręcznie, używaj tej funkcji. Nazwa przystanku może zawierać „/” lub spacje.
fun NavHostController.navigateToBusStopSchedule(busStopId: Long, busStopName: String, direction: String) {
    navigate("${Screen.BusStopSchedule.route}/$busStopId/${Uri.encode(busStopName)}/${Uri.encode(direction)}")
}

private fun parseLatLng(value: String?): LatLng? {
    val parts = value?.split(",") ?: return null
    if (parts.size != 2) return null
    val lat = parts[0].toDoubleOrNull() ?: return null
    val lng = parts[1].toDoubleOrNull() ?: return null
    return LatLng(lat, lng)
}
