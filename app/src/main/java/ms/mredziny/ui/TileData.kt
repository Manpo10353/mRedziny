package ms.mredziny.ui

import ms.mredziny.R
import ms.mredziny.Screen

data class TileData(
    val title: String,
    val icon: Int,
    val route: String? = null,
)

data class TileList(
    val title: String,
    val tiles: List<TileData>,
)

val communicationTilesList = (
    TileList(
        title = "Komunikacja",
        tiles = listOf(
            TileData("Rozkład jazdy", R.drawable.departure_board_48px, Screen.BusSchedule.route),
            TileData("Mapa linii", R.drawable.map_48px,Screen.BusLineMap.route),
            TileData("Wyszukiwanie połączeń", R.drawable.route_48px),
            //TileData("Rozkład przystanku", R.drawable.schedule_48px),
            //TileData("Rozkład przystanku", R.drawable.schedule_48px),

        )
    )
)

val wasteManagementTilesList = (
    TileList(
        title = "Gospodarka odpadami",
        tiles = listOf(
            TileData("Harmonogram odbioru odpadów", R.drawable.delete_48px,Screen.WasteCollectionSchedule.route),
            TileData("Zasady segregacji odpadów", R.drawable.recycling_48px),
            TileData("Punkt selektywnej zbiórki odpadów", R.drawable.delete_48px),
            //TileData("Zbiórki odpadów", R.drawable.delete_48px),
        )
    )
)

val mCommuneOfficeTilesList = (
    TileList(
        title = "mUrząd Gminy",
        tiles = listOf(
            TileData("Umów wizytę", R.drawable.calendar_add_on_48px),
            TileData("Twój radny", R.drawable.groups_48px),
        )
    )
)

val tourismTilesList = (
    TileList(
        title = "Turystyka",
        tiles = listOf(
            TileData("Mapa atrakcji", R.drawable.map_48px),
            TileData("Rowerem po gminie", R.drawable.directions_bike_48px),
            TileData("Punkty widokowe", R.drawable.map_48px),
            //TileData("Punkty gastronomiczne", R.drawable.map_48px),
        )
    )
)



