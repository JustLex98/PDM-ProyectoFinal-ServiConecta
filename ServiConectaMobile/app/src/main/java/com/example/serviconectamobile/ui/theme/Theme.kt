package com.example.serviconectamobile.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = ServiOrange,      // El naranja manda en los botones
    secondary = ServiOrange,
    background = ServiBlack,    // Fondo negro
    surface = ServiDarkGrey,    // Superficies de tarjetas/campos
    onPrimary = ServiBlack,     // Texto negro sobre botones naranja
    onSecondary = ServiBlack,
    onBackground = ServiWhite,  // Texto blanco sobre fondo negro
    onSurface = ServiWhite      // Texto blanco sobre superficies
)

@Composable
fun ServiConectaMobileTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
