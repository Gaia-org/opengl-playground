package com.example.opengl.samples.render

interface IRenderObj {
    val objType: Int

    /**
     * Used for shaders' creating & compiling, and program creating & linking.
     */
    fun initialize()

    /**
     * Use program and apply vertex & fragment shaders, then start render.
     */
    fun draw(mvpMatrix: FloatArray? = null)
}

interface ObjType {
    companion object {
        const val OBJ_UNKNOWN = -1
        const val OBJ_TRIANGLE = 3
        const val OBJ_SQUARE = 4
    }
}