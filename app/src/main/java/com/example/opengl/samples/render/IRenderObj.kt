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

    fun onSurfaceChanged(width: Int, height: Int)
}

interface ObjType {
    companion object {
        const val OBJ_UNKNOWN = -1
        const val OBJ_TRIANGLE = 3
        const val OBJ_SQUARE = 4
        const val OBJ_CIRCLE = 5
        const val OBJ_CUBE = 6
        const val OBJ_SIMPLE_TEXTURE = 12
        const val OBJ_TEXTURE_BOX = 13
    }
}