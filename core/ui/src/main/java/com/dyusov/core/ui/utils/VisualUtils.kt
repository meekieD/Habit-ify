package com.dyusov.core.ui.utils

import androidx.core.graphics.ColorUtils

/**
 * Converts a color to a pastel/muted version adapted for the current theme.
 *
 * Light theme: reduces saturation + pushes lightness toward white (pastel).
 * Dark theme:  reduces saturation + pushes lightness toward a mid-dark tone,
 *              so cards don't look washed-out on a dark background.
 *
 * @param darkTheme       Whether the app is currently in dark mode.
 * @param saturationFactor Factor to reduce saturation (0.0–1.0).
 * @param lightnessFactor  Light theme: target lightness fraction (default 0.75 = 75%).
 * @param darkLightness    Dark theme: absolute lightness to clamp to (default 0.22).
 */
fun Int.toPastel(
    darkTheme: Boolean = false,
    saturationFactor: Float = 0.9f,
    lightnessFactor: Float = 0.75f,
    darkLightness: Float = 0.22f
): Int {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(this, hsl)

    hsl[1] *= saturationFactor

    hsl[2] = if (darkTheme) {
        darkLightness
    } else {
        hsl[2] + (1 - hsl[2]) * lightnessFactor
    }

    return ColorUtils.HSLToColor(hsl)
}