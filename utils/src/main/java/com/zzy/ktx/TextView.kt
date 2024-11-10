package com.zzy.ktx

import android.widget.TextView


fun TextView.clearText() {
    text = null
}

val TextView.textString: String
    get() = if (text != null) text.toString() else ""

val TextView.length: Int
    get() = length()
