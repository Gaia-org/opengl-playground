package com.example.opengl.samples

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import com.example.opengl.samples.view.SimpleGLSurfaceView
import com.example.opengl.samples.view.SimpleRenderView

class MainActivity : AppCompatActivity() {
    private val mGLSurfaceView: SimpleGLSurfaceView by lazy { SimpleGLSurfaceView(this) }
    private val mCustomRenderView: SimpleRenderView by lazy { SimpleRenderView(this) }
    private lateinit var m: SimpleGLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        val containerView = findViewById<FrameLayout>(R.id.container)
//        containerView.addView(mGLSurfaceView)
        containerView.addView(mCustomRenderView)
    }

    override fun onResume() {
        super.onResume()
        mGLSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mGLSurfaceView.onPause()
    }
}