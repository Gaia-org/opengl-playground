package com.example.opengl.samples.render

import android.opengl.GLES30
import android.util.Log
import com.example.opengl.samples.SampleApplication
import com.example.opengl.samples.utils.RenderUtil
import java.nio.FloatBuffer

class SimpleTextureRenderObj : BaseRenderObj() {
    override val vertexShaderCode: String
        get() = "#version 300 es \n" +
                "in vec4 aPosition; \n" +
                "in vec2 aTextureCoord; \n" +
                "uniform mat4 uMVPMatrix; \n" +
                "out vec2 vTextureCoord; \n" +
                "void main() { \n" +
                    "gl_Position = uMVPMatrix * aPosition; \n" +
                    "vTextureCoord = aTextureCoord; \n" +
                "} \n"
    // // OpenGL ES 2.0 code
//    override val vertexShaderCode: String
//        get() = "attribute vec4 aPosition;\n" +
//                "attribute vec2 aTextureCoord;\n" +
//                "uniform mat4 mvpMatrix;\n" +
//                "varying vec2 vTexCoord;\n" +
//                "void main() {\n" +
//                "     gl_Position  = mvpMatrix * aPosition;\n" +
//                "     vTexCoord = aTextureCoord;\n" +
//                "}\n"
    override val fragmentShaderCode: String
        get() = "#version 300 es \n" +
                "precision mediump float; \n" +
                "uniform sampler2D uTexture; \n" +
                "in vec2 vTextureCoord; \n" +
                "out vec4 fragColor; \n" +
                "void main() { \n" +
                    "fragColor = texture(uTexture, vTextureCoord); \n" +
                "} \n"


    // OpenGL ES 2.0 code
//    override val fragmentShaderCode: String
//        get() = "precision mediump float;\n" +
//                "    uniform sampler2D uTextureUnit;\n" +
//                "    varying vec2 vTexCoord;\n" +
//                "    void main() {\n" +
//                "        gl_FragColor = texture2D(uTextureUnit, vTexCoord);\n" +
//                "    }"

    // 顶点坐标(坐标原点在中心)
    override var vertexCoords: FloatArray = floatArrayOf(
        1f, 1f, 0f, // 0
        -1f, 1f, 0f, // 1
        -1f ,-1f, 0f, // 2
        1f, -1f, 0f // 3
    )
    // 纹理坐标(坐标原点在左下角)
    private val textureCoords: FloatArray = floatArrayOf(
        1f, 0f,
        0f, 0f,
        0f, 1f,
        1f, 1f
    )
    // Handles of attrs or uniforms.
    private var positionHandle: Int = -1
    private var matrixHandle: Int = -1
    private var textureHandle: Int = -1

    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var textureBuffer: FloatBuffer
    private var mTextureId: Int = -1

    override fun initializeLocationArgs() {
        positionHandle = glGetAttribLocation(programId, "vPosition")
        matrixHandle = glGetUniformLocation(programId, "uMVPMatrix")
        textureHandle = glGetAttribLocation(programId, "aTextureCoord")
        vertexBuffer = createFloatBuffer(vertexCoords)
        textureBuffer = createFloatBuffer(textureCoords)
        mTextureId = RenderUtil.loadTextureFromAssets(SampleApplication.getInstance(), "texture_img.png")
        Log.i(TAG, "loadTexture, id: $mTextureId")
    }

    override val objType: Int
        get() = ObjType.OBJ_SIMPLE_TEXTURE

    override fun draw(mvpMatrix: FloatArray?) {
        mvpMatrix?.let {
            GLES30.glUniformMatrix4fv(matrixHandle, 1, false, it, 0)
        }
        GLES30.glEnableVertexAttribArray(positionHandle)
        GLES30.glVertexAttribPointer(
            positionHandle,
            vertexCount,
            GLES30.GL_FLOAT,
            false,
            0,
            vertexBuffer
        )
        GLES30.glEnableVertexAttribArray(textureHandle)
        GLES30.glVertexAttribPointer(
            textureHandle,
            textureCoords.size / 2,
            GLES30.GL_FLOAT,
            false,
            0,
            textureBuffer
        )
        // 激活和绑定纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureId)
        // 绘制纹理贴图
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexCount)
        GLES30.glDisableVertexAttribArray(positionHandle)
        GLES30.glDisableVertexAttribArray(textureHandle)
    }

    companion object {
        private const val TAG = "SimpleTextureRenderObj"
    }
}