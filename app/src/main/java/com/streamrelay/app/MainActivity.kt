package com.streamrelay.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var root: FrameLayout
    private lateinit var menu: LinearLayout
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        root = FrameLayout(this).apply { id = View.generateViewId() }
        menu = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(48, 48, 48, 48)
        }
        status = TextView(this).apply {
            text = "Choose a mode"
            textSize = 22f
            gravity = Gravity.CENTER
        }
        val tx = Button(this).apply {
            text = "TX (send)"
            setOnClickListener { startTx() }
        }
        val rx = Button(this).apply {
            text = "RX (receive)"
            setOnClickListener { status.text = "RX not built yet" }
        }
        menu.addView(status)
        menu.addView(tx)
        menu.addView(rx)
        root.addView(menu, FrameLayout.LayoutParams(-1, -1))
        setContentView(root)
    }

    private fun startTx() {
        val needed = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
        val missing = needed.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isEmpty()) showTx()
        else ActivityCompat.requestPermissions(this, missing.toTypedArray(), 1)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) showTx()
        else status.text = "Please allow camera and microphone"
    }

    private fun showTx() {
        menu.visibility = View.GONE
        supportFragmentManager.beginTransaction().add(root.id, TxFragment()).commit()
    }
}
