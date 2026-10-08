package com.streamrelay.app

import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.jiangdg.ausbc.MultiCameraClient
import com.jiangdg.ausbc.base.CameraFragment
import com.jiangdg.ausbc.callback.ICameraStateCallBack
import com.jiangdg.ausbc.callback.IPreviewDataCallBack
import com.jiangdg.ausbc.camera.bean.CameraRequest
import com.jiangdg.ausbc.widget.AspectRatioTextureView
import com.jiangdg.ausbc.widget.IAspectRatio

class TxFragment : CameraFragment() {
    private var frame: FrameLayout? = null
    private var texture: AspectRatioTextureView? = null
    private var statusView: TextView? = null
    private val ui = Handler(Looper.getMainLooper())

    private var state = "Waiting for capture card"
    private var frames = 0
    private var lastTick = System.currentTimeMillis()
    private var fps = 0
    private var w = 0
    private var h = 0

    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View? {
        val ctx = requireContext()
        val f = FrameLayout(ctx)
        val t = AspectRatioTextureView(ctx)
        f.addView(t, FrameLayout.LayoutParams(-1, -1))
        val s = TextView(ctx).apply {
            textSize = 18f
            setTextColor(0xFFFFFFFF.toInt())
            setBackgroundColor(0xAA000000.toInt())
            setPadding(32, 24, 32, 24)
            text = state
        }
        f.addView(s, FrameLayout.LayoutParams(-2, -2, Gravity.TOP or Gravity.START))
        frame = f
        texture = t
        statusView = s
        return f
    }

    override fun getCameraView(): IAspectRatio? = texture

    override fun getCameraViewContainer(): ViewGroup? = frame

    override fun getCameraRequest(): CameraRequest {
        return CameraRequest.Builder()
            .setPreviewWidth(1280)
            .setPreviewHeight(720)
            .setPreviewFormat(CameraRequest.PreviewFormat.FORMAT_MJPEG)
            .create()
    }

    private fun refresh() {
        val extra = if (w > 0) "  " + w + "x" + h + " @ " + fps + " fps" else ""
        statusView?.text = state + extra
    }

    override fun onCameraState(
        self: MultiCameraClient.ICamera,
        code: ICameraStateCallBack.State,
        msg: String?
    ) {
        when (code) {
            ICameraStateCallBack.State.OPENED -> {
                state = "Card OK"
                startStats()
            }
            ICameraStateCallBack.State.CLOSED -> state = "Card closed"
            ICameraStateCallBack.State.ERROR -> state = "Card error: " + msg
        }
        ui.post { refresh() }
    }

    private fun startStats() {
        setPreviewDataCallBack(object : IPreviewDataCallBack {
            override fun onPreviewData(
                data: ByteArray?,
                width: Int,
                height: Int,
                format: IPreviewDataCallBack.DataFormat
            ) {
                frames++
                w = width
                h = height
                val now = System.currentTimeMillis()
                if (now - lastTick >= 1000) {
                    fps = (frames * 1000L / (now - lastTick)).toInt()
                    frames = 0
                    lastTick = now
                    ui.post { refresh() }
                }
            }
        })
    }
}
