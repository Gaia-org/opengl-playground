package com.example.opengl.samples.render.base

import android.opengl.GLES30
import com.example.opengl.samples.utils.RenderUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

abstract class BaseRenderObj : IRenderObj {
    protected abstract val vertexShaderCode: String

    protected abstract val fragmentShaderCode: String

    protected abstract var vertexCoords: FloatArray

    protected val vertexCount: Int by lazy {
        vertexCoords.size / COORDS_PER_VERTEX
    }

    protected val vertexStride = COORDS_PER_VERTEX * Float.SIZE_BYTES // 4 bytes per vertex

    protected var programId: Int = -1

    override fun initialize() {
        val vertexShader = createVertexShader()
        val fragmentShader = createFragmentShader()
        programId = RenderUtil.createProgram(vertexShader, fragmentShader)
        GLES30.glUseProgram(programId)
        initializeLocationArgs()
    }

    // 初始化顶点、片段着色器的顶点属性和uniform变量
    abstract fun initializeLocationArgs()

    override fun onSurfaceChanged(width: Int, height: Int) {

    }

    protected fun createFloatBuffer(floatArray: FloatArray): FloatBuffer = ByteBuffer
        .allocateDirect(floatArray.size * Float.SIZE_BYTES)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .apply {
            put(floatArray)
            position(0)
        }

    protected fun createShortBuffer(shortArray: ShortArray): ShortBuffer = ByteBuffer
        .allocateDirect(shortArray.size * Short.SIZE_BYTES)
        .order(ByteOrder.nativeOrder())
        .asShortBuffer()
        .apply {
            put(shortArray)
            position(0)
        }

    protected fun createByteBuffer(byteArray: ByteArray): ByteBuffer = ByteBuffer
        .allocateDirect(byteArray.size * Byte.SIZE_BYTES)
        .order(ByteOrder.nativeOrder())
        .apply {
            put(byteArray)
            position(0)
        }

    protected fun createVertexShader(): Int = RenderUtil.loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode)

    protected fun createFragmentShader(): Int = RenderUtil.loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode)

    protected fun glGetAttribLocation(program: Int, name: String): Int =
        GLES30.glGetAttribLocation(program, name)

    protected fun glGetUniformLocation(program: Int, name: String): Int =
        GLES30.glGetUniformLocation(program, name)

    companion object {
        const val COORDS_PER_VERTEX = 3
        const val VALUE_PER_COLOR = 4
        const val COORDS_PER_TEXTURE = 2
    }

}