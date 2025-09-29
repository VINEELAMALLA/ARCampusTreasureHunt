package com.vinee.arcampustreasurehunt.ar

import android.content.Context
import com.google.ar.core.ArCoreApk

object ArSupport {
    fun isArSupported(context: Context): Boolean {
        val availability = ArCoreApk.getInstance().checkAvailability(context)
        return availability.isSupported && !availability.isUnknown
    }
}
