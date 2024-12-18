package com.example.opengl.samples.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.opengl.GLES30
import android.opengl.GLUtils
import android.opengl.Matrix
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

object RenderUtil {
    private const val TAG = "RenderUtil"

    @JvmStatic
    fun loadShader(type: Int, shaderCode: String): Int {
        // 创建着色器对象
        val shader = GLES30.glCreateShader(type)

        // 将着色器源码加载到着色器对象中
        GLES30.glShaderSource(shader, shaderCode)

        // 编译着色器
        GLES30.glCompileShader(shader)

        // 检查编译状态
        val compileStatus = IntArray(1)
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] == 0) {
            // 编译失败，打印错误日志并删除着色器对象
            val errorMsg = GLES30.glGetShaderInfoLog(shader)
            Log.e(TAG, "Shader compilation failed: $errorMsg, shader code: $shaderCode")
            GLES30.glDeleteShader(shader)
        }

        return shader
    }

    @JvmStatic
    fun createProgram(vertexShader: Int, fragmentShader: Int): Int {
        // 创建 OpenGL ES 程序对象
        val program = GLES30.glCreateProgram()

        // 添加顶点着色器和片段着色器到程序对象中
        GLES30.glAttachShader(program, vertexShader)
        GLES30.glAttachShader(program, fragmentShader)

        // 链接程序对象
        GLES30.glLinkProgram(program)

        // 检查链接状态
        val linkStatus = IntArray(1)
        GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == 0) {
            // 链接失败，打印错误日志并删除程序对象
            val errorMsg = GLES30.glGetProgramInfoLog(program)
            GLES30.glDeleteProgram(program)
            throw RuntimeException("Program link failed: $errorMsg")
        } else {
            Log.i(TAG, "createProgram, programId: $program")
        }

        return program
    }

    @JvmStatic
    fun createVertexBuffer(floatAttrs: FloatArray): FloatBuffer = ByteBuffer
        .allocateDirect(floatAttrs.size * Float.SIZE_BYTES)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .apply {
            put(floatAttrs)
            position(0)
        }

    fun getIdentityMatrix(size: Int = 16, offset: Int = 0): FloatArray {
        return FloatArray(size).also {
            Matrix.setIdentityM(it, offset)
        }
    }

    fun loadTextureFromAssets(context: Context, fileName: String, sizeRect: Rect? = null): Int {
        val bitmap = ResourceUtils.getBitmapFromAssets(context, fileName) ?: return -1
        sizeRect?.set(0, 0, bitmap.width, bitmap.height)
        return loadTexture(bitmap)
    }

    fun loadTexture(bitmap: Bitmap): Int {
        val textureIds = IntArray(1)
        GLES30.glGenTextures(1, textureIds, 0)
        if (textureIds[0] == 0) {
            throw RuntimeException("Failed to generate texture ID")
        }

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIds[0])
//        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
//        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST.toFloat())
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR.toFloat())
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE.toFloat())
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE.toFloat())
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0)
        // 生成MIP贴图
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D)
        // Or use this code as same implementation.
        /*val b = ByteBuffer.allocate(bitmap.width * bitmap.height * 4)
        bitmap.copyPixelsToBuffer(b)
        b.position(0)
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, bitmap.width, bitmap.height,
            0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, b)*/
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
        bitmap.recycle()

        return textureIds[0]
    }

    fun loadMultiTextures(context: Context, resArray: List<String>): IntArray {
        val bitmaps = Array(resArray.size) { index ->
            ResourceUtils.getBitmapFromAssets(context, resArray[index])
        }
        val textureIds = IntArray(resArray.size)
        GLES30.glGenTextures(resArray.size, textureIds, 0)
        for (i in resArray.indices) {
            val bitmap = bitmaps[i]
            val textureId = textureIds[i]
            if (bitmap == null) {
                Log.e(TAG, "loadMultiTextures, bitmap get null: $textureId")
            }
            // Bind texture to GL
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId)
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR_MIPMAP_LINEAR)
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
            // Load bitmap into texture
            GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0)
            // 纹理缩小模式设置 mipmap 技术来提高渲染性能
            GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D)
            // Unbind texture.
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
            bitmap?.recycle()
        }
        return textureIds
    }

    fun createTextureAndBindFramebuffer(width: Int, height: Int): Int {
        val iArr = intArrayOf(0)
        GLES30.glGenTextures(1, iArr, 0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, iArr[0])
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, 10240, 9728)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, 10241, 9728)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, 10242, 10497)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, 10243, 10497)
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, 6408, width, height, 0, 6408, 5121, null)
        GLES30.glBindFramebuffer(36160, iArr[0])
        GLES30.glFramebufferTexture2D(36160, 36064, 3553, iArr[0], 0)
        return iArr[0]
    }

}