package com.example.opengl.samples.render

object RenderObjDispatcher {
    private val sRenderObjs = mapOf<Int, IRenderObj>(
        ObjType.OBJ_TRIANGLE to TriangleRenderObj()
    )

    fun initialize() {
        sRenderObjs.entries.forEach {
            it.value.initialize()
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