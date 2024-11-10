package com.zzy.utils

import android.util.Base64

/**
 *    author : Jordan
 *    time   : 2024/04/17
 *    desc   : base64转换工具类
 */
object Base64Utils {
    fun encodeBase64(bytes: ByteArray): ByteArray {
        return Base64.encode(bytes, Base64.DEFAULT);
    }

    fun decodeBase64(key: String): ByteArray {
        return Base64.decode(key, Base64.DEFAULT);
    }

    fun enCodeString(bytes: ByteArray): String {
        return Base64.encodeToString(bytes, android.util.Base64.DEFAULT);
    }
}