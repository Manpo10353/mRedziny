package ms.mredziny

sealed class Screen (val route: String){
    data object Home: Screen("home")
    data object BusSchedule: Screen("bus_schedule")
    data object BusStops: Screen("bus_stops")
    data object BusStopSchedule: Screen("bus_stop_schedule")
    data object BusFavourite: Screen("bus_favourite")
    data object WasteCollectionSchedule: Screen("waste_collection_schedule")
    data object BusLineMap: Screen("bus_line_map")
}