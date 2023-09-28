package com.example.opengl.samples.render

import android.opengl.GLES30
import android.opengl.Matrix
import android.util.Log
import com.example.opengl.samples.SampleApplication
import com.example.opengl.samples.render.base.BaseRenderObj
import com.example.opengl.samples.render.base.ObjType
import com.example.opengl.samples.utils.RenderUtil
import java.nio.FloatBuffer

class TextureBoxRenderObj : BaseRenderObj() {
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
    override val fragmentShaderCode: String
        get() = "#version 300 es \n" +
                "precision mediump float; \n" +
                "uniform sampler2D uTexture; \n" +
                "in vec2 vTextureCoord; \n" +
                "out vec4 fragColor; \n" +
                "void main() { \n" +
                "fragColor = texture(uTexture, vTextureCoord); \n" +
                "} \n"

    private val r = 1.0f
    override var vertexCoords: FloatArray = floatArrayOf(
        //前面
        r, r, r,
        -r, r, r,
        -r, -r, r,
        r, -r, r,
        //后面
        r, r, -r,
        -r, r, -r,
        -r, -r, -r,
        r, -r, -r,
        //上面
        r, r, r,
        r, r, -r,
        -r, r, -r,
        -r, r, r,
        //下面
        r, -r, r,
        r, -r, -r,
        -r, -r, -r,
        -r, -r, r,
        //右面
        r, r, r,
        r, r, -r,
        r, -r, -r,
        r, -r, r,
        //左面
        -r, r, r,
        -r, r, -r,
        -r, -r, -r,
        -r, -r, r
    )

    private val drawOrder = byteArrayOf(
        0, 1, 2, 0, 2, 3,  // 前面
        4, 5, 6, 4, 6, 7,  // 后面
        0, 3, 7, 0, 7, 4,  // 左侧
        1, 2, 6, 1, 6, 5,  // 右侧
        0, 1, 5, 0, 5, 4,  // 顶部
        3, 2, 6, 3, 6, 7   // 底部
    )

    // 纹理坐标(坐标原点在左下角)
    private val textureCoords: FloatArray = floatArrayOf(
        //前面
        0f, 0f,
        1f, 0f,
        1f, 1f,
        0f, 1f,
        //后面
        0f, 0f,
        1f, 0f,
        1f, 1f,
        0f, 1f,
        //上面
        0f, 0f,
        1f, 0f,
        1f, 1f,
        0f, 1f,
        //下面
        0f, 0f,
        1f, 0f,
        1f, 1f,
        0f, 1f,
        //右面
        0f, 0f,
        1f, 0f,
        1f, 1f,
        0f, 1f,
        //左面
        0f, 0f,
        1f, 0f,
        1f, 1f,
        0f, 1f
    )
    // Handles of attrs or uniforms.
    private var positionHandle: Int = -1
    private var matrixHandle: Int = -1
    private var textureHandle: Int = -1
    // textures to render
    private val mTextureRes = listOf(
        "texture_img.png",
        "texture_img2.png",
        "texture_img3.png",
        "texture_img4.png",
        "texture_img5.png",
        "texture_img6.png"
    )
    private lateinit var mTextureIds: IntArray

    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var textureBuffer: FloatBuffer

    private var projectionMatrix = RenderUtil.getIdentityMatrix()
    var mRotateAngle = 0f
    var mRotateAxis = floatArrayOf(0f, 0f, 1f)

    override fun initializeLocationArgs() {
        mTextureIds = RenderUtil.loadMultiTextures(SampleApplication.getInstance(), mTextureRes)
        Log.i(TAG, "loadTextures, ids: ${mTextureIds.toList()}")
        positionHandle = glGetAttribLocation(programId, "aPosition")
        matrixHandle = glGetUniformLocation(programId, "uMVPMatrix")
        textureHandle = glGetAttribLocation(programId, "aTextureCoord")
        vertexBuffer = createFloatBuffer(vertexCoords)
        textureBuffer = createFloatBuffer(textureCoords)
    }

    override val objType: Int
        get() = ObjType.OBJ_TEXTURE_BOX

    override fun draw(mvpMatrix: FloatArray?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
        // 丢弃之前的变换效果，构建新的 mvp 变换矩阵来覆盖
        mvpMatrixTransform()
        GLES30.glEnableVertexAttribArray(positionHandle)
        GLES30.glEnableVertexAttribArray(textureHandle)
        GLES30.glVertexAttribPointer(
            positionHandle,
            COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            0,
            vertexBuffer
        )
        GLES30.glVertexAttribPointer(
            textureHandle,
            COORDS_PER_TEXTURE,
            GLES30.GL_FLOAT,
            false,
            0,
            textureBuffer
        )
        // 激活和绘制纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE)
        renderTextures()

        GLES30.glDisableVertexAttribArray(positionHandle)
        GLES30.glDisableVertexAttribArray(textureHandle)
    }

    private fun renderTextures() {
        for (i in mTextureIds.indices) {
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureIds[i])
            // Draw 6 planes and each has 2 triangles which has 4 vertex.
            GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, i * 4, 4)
        }
//        GLES30.glDrawElements(
//            GLES30.GL_TRIANGLE_FAN,
//            drawOrder.size,
//            GLES30.GL_UNSIGNED_BYTE,
//            createByteBuffer(drawOrder)
//        )
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        super.onSurfaceChanged(width, height)
        val ratio = width.toFloat() / height
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1.0f, 1.0f, 3.0f, 16.0f)
    }

    private fun mvpMatrixTransform() {
        val modelMatrix = RenderUtil.getIdentityMatrix()
        val viewMatrix = RenderUtil.getIdentityMatrix()
        // mRotateAngle = (mRotateAngle + 2) % 360
        // Matrix.rotateM(modelMatrix, 0, mRotateAngle, -1f, -1f, 1f)
        Matrix.rotateM(modelMatrix, 0, mRotateAngle, mRotateAxis[0], mRotateAxis[1], mRotateAxis[2])
        Matrix.setLookAtM(viewMatrix, 0, 0f, 5f, 10f,
            0f, 0f, 0f, 0f, 1f, 0f)
        val mvpMatrix = FloatArray(16)
        val tempMatrix = FloatArray(16)
        Matrix.multiplyMM(tempMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, tempMatrix, 0)
        GLES30.glUniformMatrix4fv(matrixHandle, 1, false, mvpMatrix, 0)
    }

    companion object {
        private const val TAG = "TextureBoxRenderObj"
    }
}