package com.zzy.ktx

fun CharSequence?.orEmpty(): String = this?.toString() ?: ""