package dev.dariostrm.groshare.groceries

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

@Composable
fun UsernameAvatar(
    username: String,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = false,
    size: Dp = 40.dp
) {
    // remember() caches the color math so it only recalculates if the username or theme toggles
    val colors = remember(username, isDarkMode) {
        val cleanName = username.trim().ifEmpty { "?" }
        val hash = cleanName.hashCode()
        val hue = (((hash % 360) + 360) % 360).toFloat()

        val saturation = if (isDarkMode) 0.53f else 0.73f
        val textSaturation = if (isDarkMode) 0.79f else 0.52f
        val bgLightness = if (isDarkMode) 0.26f else 0.79f
        val textLightness = if (isDarkMode) 0.80f else 0.36f

        Pair(
            hslToComposeColor(hue, saturation, bgLightness),
            hslToComposeColor(hue, saturation, textLightness)
        )
    }

    val displayLetter = remember(username) {
        username.trim().take(1).uppercase()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(colors.first)
    ) {
        Text(
            text = displayLetter,
            color = colors.second,
            fontWeight = FontWeight.Medium,
            // Dynamically size the font to fill exactly 50% of the circle's diameter
            fontSize = (size.value * 0.5f).sp
        )
    }
}


fun hslToComposeColor(hue: Float, saturation: Float, lightness: Float): Color {
    val c = (1f - abs(2f * lightness - 1f)) * saturation
    val x = c * (1f - abs((hue / 60f) % 2f - 1f))
    val m = lightness - c / 2f

    val (r, g, b) = when ((hue / 60f).toInt()) {
        0 -> Triple(c, x, 0f)
        1 -> Triple(x, c, 0f)
        2 -> Triple(0f, c, x)
        3 -> Triple(0f, x, c)
        4 -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }

    return Color(
        red = (r + m).coerceIn(0f, 1f),
        green = (g + m).coerceIn(0f, 1f),
        blue = (b + m).coerceIn(0f, 1f)
    )
}