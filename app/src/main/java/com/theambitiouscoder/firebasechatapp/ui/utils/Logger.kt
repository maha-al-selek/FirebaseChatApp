package com.theambitiouscoder.firebasechatapp.ui.utils

import android.util.Log

/**
 * Logger - Logs to Logcat
 * used for debugging purpose
 */
object Logger {

    private const val TAG = "SimpleChatAppTag"

    fun log(message: String?, tagToUse: String = TAG) {
        message?.let {
            Log.d(tagToUse, message)
        }
    }

}