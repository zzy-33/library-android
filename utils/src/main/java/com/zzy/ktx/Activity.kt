@file:JvmName("ActivityUtils")

package com.zzy.ktx

import android.app.Activity
import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.hideSupportActionBar() = supportActionBar?.hide()

val Activity.decorView
    get() = window.decorView

fun Activity.resetConfiguration() = resources.resetConfiguration()

/**
 * 重置界面配置，解决系统字体过大或者过小时导致的界面错乱问题
 */
fun Resources.resetConfiguration() {
    val config = Configuration()
    config.setToDefaults()
    updateConfiguration(config, displayMetrics)
}
