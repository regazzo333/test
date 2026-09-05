package com.nudge.app.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.nudge.app.audio.NudgeAudioPlayer
import com.nudge.app.haptics.NudgeHaptics
import com.nudge.app.ui.screens.NudgeOverlayScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NudgeOverlayService : Service() {
    private var windowManager: WindowManager? = null
    private var overlayView: ComposeView? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val title = intent?.getStringExtra("TITLE") ?: "نغزة سريعة!"
        val soundType = intent?.getStringExtra("SOUND_TYPE") ?: "dinner_bell"

        // Trigger Audio & Waveform Haptics
        scope.launch {
            NudgeAudioPlayer.playNudgeSound(soundType)
        }
        NudgeHaptics.trigger(this, longArrayOf(0, 250, 80, 250, 80, 500))

        showOverlay(title)
        return START_NOT_STICKY
    }

    private fun showOverlay(title: String) {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }

        overlayView = ComposeView(this).apply {
            setContent {
                NudgeOverlayScreen(
                    title = title,
                    onDismiss = { stopSelf() }
                )
            }
        }

        windowManager?.addView(overlayView, params)

        // Auto dismiss overlay after 5 seconds
        scope.launch {
            delay(5000)
            stopSelf()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayView?.let { windowManager?.removeView(it) }
    }
}