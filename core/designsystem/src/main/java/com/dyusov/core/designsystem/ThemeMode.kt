package com.dyusov.core.designsystem

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM;

    val labelRes: Int
        get() = when (this) {
            LIGHT  -> R.string.theme_light
            DARK   -> R.string.theme_dark
            SYSTEM -> R.string.theme_system
        }
}
