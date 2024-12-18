package com.example.opengl.samples.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

object ResourceUtils {
    fun getBitmapFromAssets(context: Context, path: String): Bitmap? {
        val options = BitmapFactory.Options().apply {
            inScaled = false // 禁用缩放
        }
        var inputStream: InputStream? = null
        try {
            inputStream = context.assets.open(path)
            return BitmapFactory.decodeStream(inputStream, null, options)
        } catch (e: IOException) {
            throw RuntimeException("Failed to get bitmap from assets: $path", e)
        } finally {
            inputStream?.close()
        }
    }

    fun getBitmapFromAssets(path: String): Bitmap? {
        val options = BitmapFactory.Options().apply {
            inScaled = false // 禁用缩放
        }
        var inputStream: InputStream? = null
        try {
            // 获取较慢，而且第一次可能获取不到https://juejin.cn/post/6844903429983174669
            inputStream = javaClass.classLoader?.getResourceAsStream(path)
            if (inputStream == null) {
                inputStream =  javaClass.classLoader?.getResourceAsStream(path)
            }
            return BitmapFactory.decodeStream(inputStream, null, options)
        } catch (e: IOException) {
            throw RuntimeException("Failed to load bitmap from assets by `getResourceAsStream`: $path", e)
        } finally {
            inputStream?.close()
        }
    }

    fun readShaderFromResource(context: Context, shaderId: Int): String {
        val inputStream: InputStream = context.resources.openRawResource(shaderId)
        val br = BufferedReader(InputStreamReader(inputStream))
        var line: String?
        val sb = StringBuilder()
        try {
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
                sb.append("\n")
            }
            br.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return sb.toString()
    }
}