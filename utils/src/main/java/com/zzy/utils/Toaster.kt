package com.zzy.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.zzy.model.ToastType

/**
 *    author : zzy
 *    time   : 2024/10/16
 *    desc   : 自定义Toast 工具类
 */
object Toaster {
    private var mToast: Toast? = null
    private var mApplication: Application? = null

    fun init(application: Application) {
        mApplication = application
    }

    /**
     * 显示Toast消息
     *
     * @param text 要显示的文本内容
     * @param tips Toast的类型，用于区分不同的提示风格，默认为闲置类型
     * @param gravity Toast显示的位置，默认为顶部
     * @param duration Toast显示的持续时间，默认为短时间
     */
    fun show(
        text: String,
        tips: ToastType = ToastType.idle,
        gravity: Int = Gravity.TOP,
        duration: Int = Toast.LENGTH_SHORT
    ) {
        showTopToast(text, tips, gravity, duration)
    }

    /**
     * 显示Toast消息
     *
     * @param textResId 文本资源ID，用于获取Toast显示的文本
     * @param tips Toast类型，支持 idle（空闲状态）、error（错误提示）等，默认为 idle
     * @param gravity Toast显示的位置，默认为屏幕顶部
     * @param duration Toast显示的持续时间，默认为短时间显示
     */
    fun show(
        textResId: Int,
        tips: ToastType = ToastType.idle,
        gravity: Int = Gravity.TOP,
        duration: Int = Toast.LENGTH_SHORT
    ) {
        // 检查应用上下文是否为空，为空则不显示Toast
        if (mApplication == null) return
        // 获取Toast显示的文本
        val text = mApplication!!.getString(textResId)
        // 调用主Toast显示函数显示信息
        showTopToast(text, tips, gravity, duration)
    }

    private fun showTopToast(
        text: String,
        tips: ToastType = ToastType.idle,
        gravity: Int = Gravity.TOP,
        duration: Int = Toast.LENGTH_SHORT
    ) {
        try {
            // 确保在主线程上执行
            if (Looper.myLooper() != Looper.getMainLooper()) {
                Handler(Looper.getMainLooper()).post {
                    showMainToast(text, tips, gravity, duration)
                }
            } else {
                showMainToast(text, tips, gravity, duration)
            }
        } catch (e: Exception) {
            Log.e("ToastUtils", "Failed to show toast: ${e.message}")
        }
    }

    /**
     * 显示自定义的Toast消息
     *
     * @param text 要显示的Toast文本
     * @param tips Toast的类型，默认为idle
     * @param gravity Toast显示的位置，默认为顶部
     * @param duration Toast显示的持续时间，默认为短时间
     */
    @SuppressLint("InflateParams")
    private fun showMainToast(
        text: String,
        tips: ToastType = ToastType.idle,
        gravity: Int = Gravity.TOP,
        duration: Int = Toast.LENGTH_SHORT
    ) {
        cancelToast()
        if (mApplication == null) return
        // 防止GC无法回收
        val layout = LayoutInflater.from(mApplication!!).inflate(R.layout.layout_toast, null)
        val container = layout.findViewById<LinearLayout>(R.id.toast_bg)
        val textView = layout.findViewById<TextView>(R.id.toast_text)
        val tipsView = layout.findViewById<ImageView>(R.id.toast_tips)
        textView.text = text
        when (tips) {
            ToastType.info -> {
                tipsView.apply {
                    setImageDrawable(ContextCompat.getDrawable(mApplication!!, R.drawable.ic_info))
                    setColorFilter(ContextCompat.getColor(mApplication!!, R.color.toast_info))
                    visibility = View.VISIBLE
                }
                textView.setTextColor(Color.BLACK)
                container.setBackgroundResource(R.drawable.toast_bg_info)
            }

            ToastType.success -> {
                tipsView.apply {
                    setImageDrawable(
                        ContextCompat.getDrawable(
                            mApplication!!,
                            R.drawable.ic_success
                        )
                    )
                    setColorFilter(ContextCompat.getColor(mApplication!!, R.color.toast_success))
                    visibility = View.VISIBLE
                }
                textView.setTextColor(Color.BLACK)
                container.setBackgroundResource(R.drawable.toast_bg_success)
            }

            ToastType.warn -> {
                tipsView.apply {
                    setImageDrawable(ContextCompat.getDrawable(mApplication!!, R.drawable.ic_info))
                    setColorFilter(ContextCompat.getColor(mApplication!!, R.color.toast_warn))
                    visibility = View.VISIBLE
                }
                textView.setTextColor(Color.BLACK)
                container.setBackgroundResource(R.drawable.toast_bg_warn)
            }

            ToastType.error -> {
                tipsView.apply {
                    setImageDrawable(ContextCompat.getDrawable(mApplication!!, R.drawable.ic_error))
                    setColorFilter(ContextCompat.getColor(mApplication!!, R.color.toast_error))
                    visibility = View.VISIBLE
                }
                textView.setTextColor(Color.BLACK)
                container.setBackgroundResource(R.drawable.toast_bg_error)
            }

            else -> {
                tipsView.visibility = View.GONE
                textView.setTextColor(Color.WHITE)
                textView.setPadding(mApplication!!.resources.getDimensionPixelSize(R.dimen.dp_8))
                container.setPadding(40, 40, 40, 40)
                container.setBackgroundResource(R.drawable.toast)
            }
        }

        mToast = Toast(mApplication!!)
        mToast!!.apply {
            view = layout
            setGravity(gravity, 0, getStatusBarHeight(mApplication!!) / 2)
            setDuration(duration)
        }
        mToast!!.show()
    }

    /**
     * 取消当前的Toast提示
     *
     * 本函数用于取消一个正在显示的Toast提示。如果成员变量mToast不为空，即有Toast正在显示或即将显示，
     * 则调用其cancel方法来取消该Toast。这样做通常在需要提前结束一个Toast的显示时使用，例如用户交互
     * 或其他逻辑变化使得继续显示Toast不再必要或合适。
     */
    private fun cancelToast() {
        mToast?.cancel()
    }

    /**
     * 获取状态栏高度
     *
     * 此函数旨在动态获取安卓设备上状态栏的高度
     * 它通过资源ID查找并返回状态栏的高度尺寸
     *
     * @param context 上下文对象，用于访问资源
     * @return 返回状态栏的高度，如果无法找到对应的资源则返回0
     */
    private fun getStatusBarHeight(context: Context): Int {
        var result = 0;
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

}