package com.dyusov.core.ui.utils

import androidx.core.graphics.ColorUtils

/**
 * Converts a color to a pastel version by reducing saturation and increasing lightness
 * @param saturationFactor Factor to reduce saturation (0.0 to 1.0, default 0.5 = 50% saturation)
 * @param lightnessFactor Factor to increase lightness (0.0 to 1.0, default 0.8 = 80% lightness)
 * @return Pastel version of the color
 */
fun Int.toPastel(
    saturationFactor: Float = 0.9f,
    lightnessFactor: Float = 0.75f
): Int {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(this, hsl)

    hsl[1] *= saturationFactor
    hsl[2] = hsl[2] + (1 - hsl[2]) * lightnessFactor

    return ColorUtils.HSLToColor(hsl)
}