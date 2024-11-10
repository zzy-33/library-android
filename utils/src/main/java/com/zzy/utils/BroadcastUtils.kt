package com.zzy.utils

import android.content.Context
import android.content.Intent

/**
 *    author : Jordan
 *    time   : 2024/04/17
 *    desc   :
 */
class BroadcastUtils {

    /**
     * 开机自动启动
     */
    fun <T> onBootCompleted(context: Context, intent: Intent, cls: Class<T>) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val intent = Intent(context, cls)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

}