package ms.mredziny.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Blue,
    onPrimary = Color.White,
    primaryContainer = Navy,
    onPrimaryContainer = Color.White,
    secondary = Gold,
    onSecondary = Color(0xFF1A1400),
    tertiary = Teal,
    onTertiary = Color.White,
    background = Color(0xFF0F1419),
    onBackground = Color(0xFFE8EDF2),
    surface = Color(0xFF1B232C),
    onSurface = Color(0xFFE8EDF2),
    surfaceVariant = Color(0xFF232E39),
    onSurfaceVariant = Color(0xFFA9B4C0),
    outline = Color(0xFF3A4553),
)

private val LightColors = lightColorScheme(
    primary = Blue,
    onPrimary = Color.White,
    primaryContainer = Navy,
    onPrimaryContainer = Color.White,
    secondary = GoldDark,
    onSecondary = Color.White,
    tertiary = Teal,
    onTertiary = Color.White,
    background = Color(0xFFF5F7FA),
    onBackground = Color(0xFF14202B),
    surface = Color.White,
    onSurface = Color(0xFF14202B),
    surfaceVariant = Color(0xFFE8EEF4),
    onSurfaceVariant = Color(0xFF4F5B66),
    outline = Color(0xFFC5CED8),
)

data class WasteColors(
    val mixed: Color,
    val paper: Color,
    val metal: Color,
    val glass: Color,
    val bio: Color,
    val pszok: Color,
)

private val LightWaste = WasteColors(
    mixed = WasteMixedL,
    paper = WastePaperL,
    metal = WasteMetalL,
    glass = WasteGlassL,
    bio = WasteBioL,
    pszok = WastePszokL,
)

private val DarkWaste = WasteColors(
    mixed = WasteMixedD,
    paper = WastePaperD,
    metal = WasteMetalD,
    glass = WasteGlassD,
    bio = WasteBioD,
    pszok = WastePszokD,
)
val LocalWasteColors = staticCompositionLocalOf { LightWaste }

data class OperatorColors(val container: Color, val content: Color)

val OperatorGzkColors = OperatorColors(
    container = OperatorGzkLightBg,
    content = OperatorGzkLightText,
)

@Composable
fun MredzinyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalWasteColors provides if (darkTheme) DarkWaste else LightWaste,

        ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            content = content,
        )
    }
}
