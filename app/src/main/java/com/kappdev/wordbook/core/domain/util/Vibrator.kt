package com.kappdev.wordbook.core.domain.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log

fun Context.vibrateFor(durationMillis: Long) {
    val vibrator = getVibratorIfSupported() ?: return

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val effect = VibrationEffect.createOneShot(durationMillis, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(effect)
    } else {
        vibrator.vibrate(durationMillis)
    }
}

private fun Context.getVibratorIfSupported(): Vibrator? {
    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (vibrator.hasVibrator()) {
        return vibrator
    }
    Log.d("Vibration", "Device does not support vibration")
    return null
}