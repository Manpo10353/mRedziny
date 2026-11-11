package ms.mredziny.feature.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.AltRoute
import androidx.compose.material.icons.automirrored.outlined.DirectionsBike
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DepartureBoard
import androidx.compose.material.icons.outlined.EditCalendar
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Recycling
import androidx.compose.ui.graphics.vector.ImageVector
import ms.mredziny.Screen

data class TileData(
    val title: String,
    val icon: ImageVector,
    val route: String? = null,
)

data class TileSection(
    val title: String,
    val tiles: List<TileData>,
)

val homeSections: List<TileSection> = listOf(
    TileSection(
        title = "Komunikacja",
        tiles = listOf(
            TileData("Rozkład jazdy", Icons.Outlined.DepartureBoard, Screen.BusSchedule.route),
            TileData("Mapa linii", Icons.Outlined.Map, Screen.BusLineMap.route),
          //  TileData("Wyszukiwanie połączeń", Icons.AutoMirrored.Outlined.AltRoute),
        ),
    ),
    TileSection(
        title = "Gospodarka odpadami",
        tiles = listOf(
            TileData("Harmonogram odbioru odpadów", Icons.Outlined.Delete, Screen.WasteCollectionSchedule.route),
            //TileData("Zasady segregacji odpadów", Icons.Outlined.Recycling),
            TileData("Punkt selektywnej zbiórki odpadów", Icons.Outlined.Delete),
        ),
    ),
    /*TileSection(
        title = "mUrząd Gminy",
        tiles = listOf(
            TileData("Umów wizytę", Icons.Outlined.EditCalendar),
            TileData("Twój radny", Icons.Outlined.Groups),
        ),
    ),
    TileSection(
        title = "Turystyka",
        tiles = listOf(
            TileData("Mapa atrakcji", Icons.Outlined.Map),
            TileData("Rowerem po gminie", Icons.AutoMirrored.Outlined.DirectionsBike),
            TileData("Punkty widokowe", Icons.Outlined.Explore),
        ),
    ),*/
)
