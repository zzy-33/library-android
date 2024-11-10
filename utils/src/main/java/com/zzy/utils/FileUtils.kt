package com.zzy.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

/**
 *    author : Jordan
 *    time   : 2024/04/17
 *    desc   :
 */
class FileUtils {

    /**
     * 将file:// 转换成 content://
     * @param context Context
     * @param
     */
    fun getFileUri(context: Context, authority: String, path: String?): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, authority, File(path))
        } else {
            Uri.fromFile(File(path))
        }
    }

    /**
     * 写入文件(字符流)
     * @param context  Context
     * @param fileName 存储文件名称
     * @param data     存储内容
     */
    fun writeFile(context: Context, fileName: String, data: String): Boolean {
        return try {
            val stream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            val out = OutputStreamWriter(stream)
            out.write(data)
            out.close()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 读取文件
     * @param context Context
     * @param fileName 存储文件名称
     */
    fun readFile(context: Context, fileName: String): String? {
        return try {
            val openFile = context.openFileInput(fileName)
            val stream = InputStreamReader(openFile)
            val content = stream.readText()
            stream.close()
            return content
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 写入到指定目录到文件
     * @param file 指定文件
     * @param data 内容
     */
    fun writePathFile(file: File, data: String): Boolean {
        return try {
            FileOutputStream(file).use {
                it.write(data.toByteArray())
                it.flush()
                true
            }
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }


    /**
     * 读取指定文件
     * @param file 指定文件
     * @param data 内容
     */
    fun readPathFile(file: File): String {
        return try {
            InputStreamReader(FileInputStream(file)).use {
                it.readText()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * 删除文件夹下以及文件夹内容
     * @param dir File
     */
    fun deleteDirWithFile(dir: File?) {
        if (dir == null || !dir.exists() || !dir.isDirectory) return
        for (file in dir.listFiles()!!) {
            if (file.isFile) file.delete() // 删除所有文件
            else if (file.isDirectory) deleteDirWithFile(file) // 递规的方式删除文件夹
        }
        dir.delete() // 删除目录本身
    }

}