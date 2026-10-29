package ms.mredziny

import android.util.Log
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.model.LatLng
import ms.mredziny.ui.comunication.busSchedule.BusSchedule
import ms.mredziny.ui.comunication.busSchedule.busStopSchedule.BusStopScheduleView
import ms.mredziny.ui.comunication.busSchedule.busStops.BusStopScreen
import ms.mredziny.ui.MainScreen
import ms.mredziny.ui.comunication.busLineMap.BusLineMapScreen
import ms.mredziny.viewModel.BusLineMapViewModel
import ms.mredziny.ui.comunication.busSchedule.busFavourite.BusFavouriteScreen
import ms.mredziny.viewModel.BusFavouriteViewModel
import ms.mredziny.viewModel.BusStopScheduleViewModel
import ms.mredziny.viewModel.BusStopViewModel
import ms.mredziny.ui.wasteManagment.wasteCollectionSchedule.WasteCollectionScheduleScreen
import ms.mredziny.viewModel.WasteCollectionScheduleViewModel

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        enterTransition = {
            slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }, animationSpec = tween(300))
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth }, animationSpec = tween(300))
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }, animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }, animationSpec = tween(300))
        }
    ) {
        composable(route = Screen.Home.route) {
            MainScreen(navController)
        }
        composable(Screen.BusSchedule.route) {
            BusSchedule(navController)
        }
        composable(Screen.BusStops.route+"/{direction}") { backStackEntry ->
            val direction = backStackEntry.arguments?.getString("direction")
            val busStopViewModel = hiltViewModel<BusStopViewModel>()
            BusStopScreen(navController, busStopViewModel, direction!!)
        }
        composable(Screen.BusStopSchedule.route+"/{busStopId}/{busStopName}/{direction}") { backStackEntry ->
            val busStopId = backStackEntry.arguments?.getString("busStopId")?.toInt()
            val busStopName = backStackEntry.arguments?.getString("busStopName")
            val direction = backStackEntry.arguments?.getString("direction")
            Log.d("BusStopSchedule", "busStopId: $busStopId, busStopName: $busStopName, direction: $direction")
            val busStopScheduleViewModel = hiltViewModel<BusStopScheduleViewModel>()
            BusStopScheduleView(busStopScheduleViewModel, navController,busStopName!!,busStopId!!,direction!!)
        }
        composable(Screen.BusFavourite.route+"/{direction}"){ backStackEntry ->
            val direction = backStackEntry.arguments?.getString("direction")
            val busFavouriteViewModel = hiltViewModel<BusFavouriteViewModel>()
            BusFavouriteScreen(navController,busFavouriteViewModel,direction!!)
        }
        composable(Screen.WasteCollectionSchedule.route) {
            val wasteCollectionScheduleViewModel = hiltViewModel<WasteCollectionScheduleViewModel>()
            WasteCollectionScheduleScreen(navController, wasteCollectionScheduleViewModel)
        }
        composable(Screen.BusLineMap.route + "/{viewPoint}") { backStackEntry ->
            val viewPointString = backStackEntry.arguments?.getString("viewPoint")
            val latLngParts = viewPointString?.split(",")
            val viewPoint = LatLng(latLngParts!![0].toDouble(), latLngParts[1].toDouble())
            val busLineMapViewModel = hiltViewModel<BusLineMapViewModel>()
            BusLineMapScreen(navController, busLineMapViewModel, viewPoint,20f)
        }

        composable(Screen.BusLineMap.route) {
            val viewPoint = LatLng(50.857524,19.211380)
            val busLineMapViewModel = hiltViewModel<BusLineMapViewModel>()
            BusLineMapScreen(navController, busLineMapViewModel,viewPoint,10f)
        }
    }
}