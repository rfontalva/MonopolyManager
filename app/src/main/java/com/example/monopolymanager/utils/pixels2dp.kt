package com.example.monopolymanager.utils

import android.content.Context
import android.util.DisplayMetrics

fun convertPixelsToDp(px: Float, context: Context): Float {
    val value = px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    return value
}