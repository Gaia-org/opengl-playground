package com.example.opengl.samples.utils

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import kotlin.math.pow
import kotlin.math.sqrt

object UiDisplayUtils {
    const val INCH_TO_MM = 25.399999618530273

    fun getScreenWidth(context: Context): Int {
        val dm = DisplayMetrics()
        val windowManager = context.getSystemService("window") as WindowManager
        windowManager.defaultDisplay.getMetrics(dm)
        return dm.widthPixels
    }

    fun getScreenHeight(context: Context): Int {
        val dm = DisplayMetrics()
        val windowManager = context.getSystemService("window") as WindowManager
        windowManager.defaultDisplay.getMetrics(dm)
        return dm.heightPixels
    }

    fun getScreenDiagonalPixels(context: Context): Double {
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels
        return sqrt(screenWidth.toDouble().pow(2.0) + screenHeight.toDouble().pow(2.0))
    }

    fun getScreenSizeInInches(context: Context): Double {
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels
        val xdpi = displayMetrics.xdpi
        val ydpi = displayMetrics.ydpi
        val screenWidthInInches = screenWidth / xdpi
        val screenHeightInInches = screenHeight / ydpi
        return sqrt(
            screenWidthInInches.toDouble().pow(2.0) + screenHeightInInches.toDouble().pow(2.0)
        )
    }

    fun getScreenSizeInMm(context: Context): Triple<Double, Double, Double> {
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels
        val xdpi = displayMetrics.xdpi
        val ydpi = displayMetrics.ydpi
        val screenWidthInMms = screenWidth / xdpi * INCH_TO_MM
        val screenHeightInMms = screenHeight / ydpi * INCH_TO_MM
        val diagonalInMms = sqrt(
            screenWidthInMms.pow(2.0) + screenHeightInMms.pow(2.0)
        )
        return Triple(screenWidthInMms, screenHeightInMms, diagonalInMms)
    }

    fun getPixelsPerMm(context: Context): Double {
        val screenSizeInches = getScreenSizeInInches(context)
        val screenPixels = getScreenDiagonalPixels(context)
        return screenPixels / (screenSizeInches * INCH_TO_MM)
    }

    fun getMmPerPixel(context: Context): Double {
        val screenSizeInches = getScreenSizeInInches(context)
        val screenPixels = getScreenDiagonalPixels(context)
        return screenSizeInches * INCH_TO_MM / screenPixels
    }

    fun dip2px(context: Context, dpValue: Float): Int {
        val scale: Float = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun px2dip(context: Context, pxValue: Float): Int {
        val scale: Float = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }
}