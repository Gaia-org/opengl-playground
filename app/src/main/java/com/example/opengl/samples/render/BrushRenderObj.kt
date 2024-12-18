package com.example.opengl.samples.render

import android.opengl.GLES30
import android.util.Log
import com.example.opengl.samples.SampleApplication
import com.example.opengl.samples.render.base.BaseRenderObj
import com.example.opengl.samples.render.base.ObjType
import com.example.opengl.samples.utils.RenderUtil

/**
 * Simple pencil type brush renderer.
 */
class BrushRenderObj : BaseRenderObj() {
    override val vertexShaderCode: String
        get() = "uniform float rectW; \n" +
                "attribute vec4 position; \n" +
                "attribute float texIndex; \n" +
                "attribute float iAngle; \n" +
                "uniform vec4 aColor; \n" +
                "uniform mat3 uMVPMatrix;\n" +
                "varying vec4 colorFactor;\n" +
                "varying float angle;\n" +
                "varying float texId;\n" +
                "void main()\n" +
                "{\n" +
                "     vec3 translatePos = uMVPMatrix * vec3(position.x, position.y, 1.0);\n" +
                "     gl_Position = vec4(translatePos.x,translatePos.y,0.0,1.0);\n" +
                "    colorFactor = vec4(aColor.rgb,position.w * aColor.a);\n" + // 获取画笔颜色
                "    angle = iAngle/360.0 * 6.2831852;\n" + // 获取弧度值
                "    texId = texIndex;\n" +
                "    gl_PointSize = position.z;\n" +
                "}"
    override val fragmentShaderCode: String
        get() = "precision highp float;\n" +
                "uniform sampler2D texOne;\n" +
                "uniform sampler2D texTwo;\n" +
                "uniform sampler2D texThree;\n" +
                "uniform sampler2D texFour;\n" +
                "uniform sampler2D texFive;\n" +
                "uniform sampler2D texSix;\n" +
                "uniform sampler2D texSeven;\n" +
                "uniform sampler2D texEight;\n" +
                "uniform sampler2D texNine;\n" +
                "uniform sampler2D texTen;\n" +
                "varying float angle;\n" +
                "varying float texId;\n" +
                "varying vec4 colorFactor;\n" +
                "void main() {\n" +
                "   vec2 texCoo = vec2(gl_PointCoord.x,gl_PointCoord.y) - vec2(0.5);\n" +
                "   vec2 texCoord = vec2(texCoo.x * cos(angle) - texCoo.y * sin(angle) ,  texCoo.y * cos(angle) + texCoo.x * sin(angle)) + vec2(0.5);\n" +
                "   vec4 texColor = texture2D(texOne, texCoord);\n" +
                "   if(texId == 1.0)\n" +
                "       texColor = texture2D(texOne, texCoord);\n" +
                "   if(texId == 2.0)\n" +
                "       texColor = texture2D(texTwo, texCoord);\n" +
                "   if(texId == 3.0)\n" +
                "       texColor = texture2D(texThree, texCoord);\n" +
                "   if(texId == 4.0)\n" +
                "       texColor = texture2D(texFour, texCoord);\n" +
                "   if(texId == 5.0)\n" +
                "       texColor = texture2D(texFive, texCoord);\n" +
                "   if(texId == 6.0)\n" +
                "       texColor = texture2D(texSix, texCoord);\n" +
                "   if(texId == 7.0)\n" +
                "       texColor = texture2D(texSeven, texCoord);\n" +
                "   if(texId == 8.0)\n" +
                "       texColor = texture2D(texEight, texCoord);\n" +
                "   if(texId == 9.0)\n" +
                "       texColor = texture2D(texNine, texCoord);\n" +
                "   if(texId == 10.0)\n" +
                "       texColor = texture2D(texTen, texCoord);\n" +
                "    gl_FragColor = vec4(colorFactor.r,colorFactor.g, colorFactor.b, texColor.a * colorFactor.a);\n" +
                "}"
    override var vertexCoords: FloatArray
        get() = TODO("Not yet implemented")
        set(value) {}

    private var positionHandle = -1
    private var matrixHandle = -1
    private var colorHandle = -1
    private var angleHandle = -1
    private var texIndexHandle = -1
    private lateinit var mTextureIds: IntArray

    override fun initializeLocationArgs() {
        mTextureIds = RenderUtil.loadMultiTextures(SampleApplication.getInstance(), texResources.asList())
        Log.i(TAG, "loadTextures, ids: ${mTextureIds.toList()}")
        positionHandle = GLES30.glGetAttribLocation(programId, "position")
        matrixHandle = GLES30.glGetUniformLocation(programId, "uMVPMatrix")
        colorHandle = GLES30.glGetUniformLocation(programId, "aColor")
        angleHandle = GLES30.glGetAttribLocation(programId, "iAngle")
        texIndexHandle = GLES30.glGetAttribLocation(programId, "texIndex")
    }

    override val objType: Int
        get() = ObjType.OBJ_BRUSH

    override fun draw(mvpMatrix: FloatArray?) {
        TODO("Not yet implemented")
    }

    companion object {
        private const val TAG = "BrushRenderObj"

        private val texResources = arrayOf(
            "/assets/pencilTiltBig/P01.png",
            "/assets/pencilTiltBig/P02.png",
            "/assets/pencilTiltBig/P03.png",
            "/assets/pencilTiltBig/P04.png",
            "/assets/pencilTiltBig/P05.png",
            "/assets/pencilTiltBig/P06.png",
            "/assets/pencilTiltBig/P07.png",
            "/assets/pencilTiltBig/P08.png",
            "/assets/pencilTiltBig/P09.png",
            "/assets/pencilTiltBig/P10.png"
        )
    }
}