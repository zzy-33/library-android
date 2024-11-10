package com.hjq.widget.layout

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup

/**
 * Created by Jordan on 2020/12/4.
 */
class FlowLayout(context: Context, attrs: AttributeSet) : ViewGroup(context, attrs) {

    //每个item横向间距
    private val mHorizontalSpacing = dp2px(10)

    //item的行间距
    private val mVerticalSpacing = dp2px(8)

    // 记录所有的行，一行一行的存储，用于layout
    private val allLines = mutableListOf<List<View>>()

    // 记录每一行的行高，用于layout
    private var lineHeights = ArrayList<Int>()

    //用 clear 不用 new 防止频繁GC产生大量内存碎片
    private fun clearMeasureParams() {
        allLines.clear()
        lineHeights.clear()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //父View会多次调用onMeasure
        clearMeasureParams()
        //获取父View的padding
        val paddingLeft = paddingLeft
        val paddingRight = paddingRight
        val paddingTop = paddingTop
        val paddingBottom = paddingBottom

        //ViewGroup解析的父View给的宽度
        val selfWidth = MeasureSpec.getSize(widthMeasureSpec)
        //ViewGroup解析的父View给我的高度
        val selfHeight = MeasureSpec.getSize(heightMeasureSpec)

        //保存一行中的所有的view
        var lineViews = mutableListOf<View>()
        //记录这行已经使用了多宽
        var lineWidthUsed = 0
        // 一行的行高
        var lineHeight = 0
        // measure过程中，子View要求的父ViewGroup的宽
        var parentNeededWidth = 0
        // measure过程中，子View要求的父ViewGroup的高
        var parentNeededHeight = 0

        val count = if (childCount > 10) {
            10
        } else {
            childCount
        }
        for (i in 0 until count) {
            val childView = getChildAt(i)
            //子View告诉父View，自己要如何布局
            val childLP = childView.layoutParams
            if (childView.visibility != View.GONE) {
                //getChildMeasureSpec在于结合我们从子视图的LayoutParams所给出的MeasureSpec信息来获取最合适的结果
                val childWidthMeasureSpec =
                    getChildMeasureSpec(widthMeasureSpec, paddingLeft + paddingRight, childLP.width)

                /**
                 *  参数：
                 *  spec 父窗口传递给子视图的大小和模式
                 *  padding 父窗口的边距，也就是xml中的android:padding
                 *  childDimension 子视图想要绘制的准确大小，但最终不一定绘制此值
                 */
                val childHeightMeasureSpec =
                    getChildMeasureSpec(
                        heightMeasureSpec,
                        paddingTop + paddingBottom,
                        childLP.height
                    )
                //调用子view的measure方法
                childView.measure(childWidthMeasureSpec, childHeightMeasureSpec)
                //获取子view的度量宽高
                val childMeasuredWidth = childView.measuredWidth
                val childMeasuredHeight = childView.measuredHeight

                //换行操作 自view的宽度+已经用过的宽度+行间距>父View给的宽度则换行
                if (childMeasuredWidth + lineWidthUsed + mHorizontalSpacing > selfWidth) {
                    //一旦换行，我们就可以判断当前行需要的宽和高了，所以此时要记录下来
                    allLines.add(lineViews)
                    lineHeights.add(lineHeight)
                    //ViewGroup 的实际高度 = 每一行的高度+行间距
                    parentNeededHeight += lineHeight + mVerticalSpacing
                    //ViewGroup 的实际宽度 = 自view布局产生的最大宽度
                    parentNeededWidth =
                        Math.max(parentNeededWidth, lineWidthUsed + mHorizontalSpacing)
                    //换行之后重置
                    lineViews = ArrayList()
                    lineWidthUsed = 0
                    lineHeight = 0
                }
                // view 是分行layout的，所以要记录每一行有哪些view，这样可以方便layout布局
                lineViews.add(childView)
                //每行都会有自己的宽和高 每行已经用过的宽度 = 子view宽度+行间距
                lineWidthUsed += childMeasuredWidth + mHorizontalSpacing
                //行高
                lineHeight = Math.max(lineHeight, childMeasuredHeight)

                //处理最后一行数据
                if (i == childCount - 1) {
                    allLines.add(lineViews)
                    lineHeights.add(lineHeight)
                    parentNeededHeight += lineHeight + mVerticalSpacing
                    parentNeededWidth =
                        parentNeededWidth.coerceAtLeast(lineWidthUsed + mHorizontalSpacing)
                }
            }
        }

        //再度量自己的高度保存
        //根据子View的度量结果，来重新度量自己ViewGroup
        //作为一个ViewGroup，它自己也是一个View,它的大小也需要根据它的父View给它提供的宽高来度量
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        /**
         * UNSPECIFIED：不对View大小做限制，如：ListView，ScrollView
         * EXACTLY：确切的大小，如：100dp或者march_parent 只有确切的大小采用父View给的大小，否则用自己的大小
         * AT_MOST：大小不可超过某数值，如：wrap_content
         */
        val realWidth = if (widthMode == MeasureSpec.EXACTLY) selfWidth else parentNeededWidth
        val realHeight = if (heightMode == MeasureSpec.EXACTLY) selfHeight else parentNeededHeight
        setMeasuredDimension(realWidth, realHeight)
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        //行数
        val lineCount = allLines.size
        //去掉父view的Padding的实际的左上起点
        var curL = paddingLeft
        var curT = paddingTop
        for (i in 0 until lineCount) {
            //获取每一行的view
            val lineViews = allLines[i]
            //获取每一行的高度
            val lineHeight = lineHeights[i]
            lineViews.forEach { view ->
                //每个view 的上下左右点
                val left = curL
                val top = curT
                val right = left + view.measuredWidth
                val bottom = top + view.measuredHeight
                //获取view的上下左右去布局
                view.layout(left, top, right, bottom)
                //下一个view的左起点 上一个view的右起点+行间距
                curL = right + mHorizontalSpacing
            }
            //下一次的启动起点 = 行高 + 行间距
            curT += lineHeight + mVerticalSpacing
            //重置左起点
            curL = paddingLeft
        }

    }

    private fun dp2px(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            Resources.getSystem().displayMetrics
        ).toInt()
    }
}
