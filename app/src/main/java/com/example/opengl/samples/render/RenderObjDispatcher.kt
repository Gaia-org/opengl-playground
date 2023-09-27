package com.example.opengl.samples.render

object RenderObjDispatcher {
    private val sRenderObjs = mapOf<Int, IRenderObj>(
        ObjType.OBJ_TRIANGLE to TriangleRenderObj(),
        ObjType.OBJ_SQUARE to SquareRenderObj(),
        ObjType.OBJ_CIRCLE to CircleRenderObj(),
        ObjType.OBJ_CUBE to CubeRenderObj(),
        ObjType.OBJ_SIMPLE_TEXTURE to SimpleTextureRenderObj(),
    )

    fun initializeAll() {
        sRenderObjs.entries.forEach {
            it.value.initialize()
        }
    }

    fun initialize(type: Int) {
        if (sRenderObjs.containsKey(type)) {
            sRenderObjs[type]?.initialize()
        }
    }

    fun onSurfaceChanged(width: Int, height: Int) {
        sRenderObjs.entries.forEach {
            it.value.onSurfaceChanged(width, height)
        }
    }

    fun renderObj(objType: Int, mvpMatrix: FloatArray? = null) {
        if (sRenderObjs.containsKey(objType)) {
            sRenderObjs[objType]?.draw(mvpMatrix)
        }
    }

    fun renderAll(mvpMatrix: FloatArray?) {
        sRenderObjs.entries.forEach {
            renderObj(it.value, mvpMatrix)
        }
    }

    private fun renderObj(renderObj: IRenderObj, mvpMatrix: FloatArray? = null) {
        renderObj.draw(mvpMatrix)
    }

    fun getAvailableTypes(): List<Int> = sRenderObjs.keys.toList()

    fun getAvailableRenderObjects(): List<IRenderObj> = sRenderObjs.values.toList()
}