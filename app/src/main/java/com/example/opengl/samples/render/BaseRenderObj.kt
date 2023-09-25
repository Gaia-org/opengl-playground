package com.example.opengl.samples.render

import android.opengl.GLES30
import com.example.opengl.samples.utils.RenderUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

abstract class BaseRenderObj : IRenderObj {
    protected abstract var vertexShaderCode: String

    protected abstract var fragmentShaderCode: String

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
        initializeLocationArgs()
        GLES30.glUseProgram(programId)
    }

    // 初始化顶点、片段着色器的顶点属性和uniform变量
    abstract fun initializeLocationArgs()

    protected fun createFloatBuffer(floatArray: FloatArray): FloatBuffer = ByteBuffer
        .allocateDirect(floatArray.size * Float.SIZE_BYTES)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .apply {
            put(floatArray)
            position(0)
        }

    protected fun createVertexShader(): Int = RenderUtil.loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode)

    protected fun createFragmentShader(): Int = RenderUtil.loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode)

    companion object {
        const val COORDS_PER_VERTEX = 3
    }

}