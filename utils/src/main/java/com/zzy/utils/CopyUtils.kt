package com.zzy.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

/**
 *    author : Jordan
 *    time   : 2024/04/17
 *    desc   :
 */
class CopyUtils {
    /**
     * 复制文本到剪贴板
     * @param context 上下文
     * @param label 标签
     * @param text 文本内容
     */
    fun copyText(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
    }

    /**
     * 从剪贴板中获取文本
     * @param context 上下文
     * @return 剪贴板中的文本，如果剪贴板为空或不包含文本，则返回 null
     */
    fun getText(context: Context): String? {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = clipboard.primaryClip
        if (clip != null && clip.itemCount > 0) {
            val text = clip.getItemAt(0).text
            return text.toString()
        }
        return null
    }
}