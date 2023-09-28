package com.example.opengl.samples.render

import android.graphics.Rect
import android.opengl.GLES30
import android.opengl.Matrix
import android.util.Log
import com.example.opengl.samples.SampleApplication
import com.example.opengl.samples.render.base.BaseRenderObj
import com.example.opengl.samples.render.base.ObjType
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
//                "uniform mat4 uMVPMatrix;\n" +
//                "varying vec2 vTexCoord;\n" +
//                "void main() {\n" +
//                "     gl_Position  = uMVPMatrix * aPosition;\n" +
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

    private var renderWidth: Int = 0
    private var renderHeight: Int = 0
    private var transformMatrix: FloatArray = FloatArray(16)
    private var renderRect: Rect = Rect()

    override fun initializeLocationArgs() {
        mTextureId = RenderUtil.loadTextureFromAssets(SampleApplication.getInstance(), "texture_img.png", renderRect)
        Log.i(TAG, "loadTexture, id: $mTextureId")
        positionHandle = glGetAttribLocation(programId, "aPosition")
        matrixHandle = glGetUniformLocation(programId, "uMVPMatrix")
        textureHandle = glGetAttribLocation(programId, "aTextureCoord")
        val uTextureLocation = GLES30.glGetAttribLocation(programId, "uTexture")
        GLES30.glUniform1i(uTextureLocation, 0)
        vertexBuffer = createFloatBuffer(vertexCoords)
        textureBuffer = createFloatBuffer(textureCoords)
    }

    override val objType: Int
        get() = ObjType.OBJ_SIMPLE_TEXTURE

    override fun draw(mvpMatrix: FloatArray?) {
        // 此处也可通过按照比例设定viewPort尺寸进而限制显示范围(二者选其一，建议在开发过程中调整viewPort尺寸)
        GLES30.glUniformMatrix4fv(matrixHandle, 1, false, transformMatrix, 0)
        GLES30.glEnableVertexAttribArray(positionHandle)
        GLES30.glVertexAttribPointer(
            positionHandle,
            COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )
        GLES30.glEnableVertexAttribArray(textureHandle)
        GLES30.glVertexAttribPointer(
            textureHandle,
            COORDS_PER_TEXTURE,
            GLES30.GL_FLOAT,
            false,
            0,
            textureBuffer
        )
        // 激活和绑定纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureId)
        // 绘制纹理贴图
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, vertexCount)
        GLES30.glDisableVertexAttribArray(positionHandle)
        GLES30.glDisableVertexAttribArray(textureHandle)
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        super.onSurfaceChanged(width, height)
        this.renderWidth = width
        this.renderHeight = height
        transformSize(renderWidth, renderHeight, transformMatrix)
    }

    fun transformSize(width: Int, height: Int, matrix: FloatArray) {
        val mapWidth = 800f
        val mapHeight = 450f
        val mapRatio = mapWidth / mapHeight
        val ratio = width.toFloat() / height
        var w = 1.0f
        var h = 1.0f
        if (mapRatio > ratio) { //宽度最大，高度适配
            h = mapRatio / ratio
        } else { //高度最大，宽度适配
            w = ratio / mapRatio
        }
        Matrix.orthoM(matrix, 0, -w, w, -h, h, -1f, 1f)
    }

    fun getTextureId(): Int = mTextureId

    fun getRenderRect(): Rect = renderRect

    companion object {
        private const val TAG = "SimpleTextureRenderObj"
    }
}