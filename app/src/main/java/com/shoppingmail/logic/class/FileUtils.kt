package com.shoppingmail.logic.`class`

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.FileUtils
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream

/**
 *   author ： Jason
 *   time    ： 2021/6/28
 */
object FileUtils {
    /*
        实现Uri 转 File的方法
        适配Android10
        uri
        context 上下文
        file文件
    */
    fun uriToFile(uri:Uri?, activity: Activity):File?{
        var file:File? = null

        if (uri == null){
            return null
        }

        //Android 10以下
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            try {
                //该Cursor对象包含了Content URI所关联的文件的名称和大小
                val cursor = activity.contentResolver.query(uri, null, null, null, null)
                if (cursor != null){
                    //获取文件名的列号
                    val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    //将指针指向第一位
                    cursor.moveToFirst()
                    val name = cursor.getString(index)
                    file = File(activity.filesDir, name)
                    val inputStream = activity.contentResolver.openInputStream(uri)
                    val outputStream = FileOutputStream(file)
                    var read:Int
                    val maxBufferSize = 1024*1024
                    if (inputStream != null){
                        val bytesAvailable = inputStream.available()
                        val bufferSize = Math.min(bytesAvailable, maxBufferSize)
                        val buffers = ByteArray(bufferSize)
                        while (inputStream.read(buffers).also { read = it } != -1){
                            outputStream.write(buffers, 0, read)
                        }
                        cursor.close()
                        inputStream.close()
                        outputStream.close()
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }else{
            //适配Android 10以上
            if (uri.scheme != null){
                if (uri.scheme == ContentResolver.SCHEME_FILE && uri.path != null){
                    file = File(uri.path)
                }else if (uri.scheme == ContentResolver.SCHEME_CONTENT){
                    //将文件复制到沙盘目录中
                    val contentResolver = activity.contentResolver
                    val displayName:String = (System.currentTimeMillis() + Math.round((Math.random() + 1)*1000)).toString() + "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri))
                    try {
                        val inputStream = contentResolver.openInputStream(uri)
                        if (activity.externalCacheDir != null){
                            val cache = File(
                                activity.externalCacheDir!!.absolutePath, displayName
                            )
                            val fileOutputStream = FileOutputStream(cache)
                            if (inputStream != null){
                                FileUtils.copy(inputStream, fileOutputStream)
                                file = cache
                                fileOutputStream.close()
                                inputStream.close()
                            }
                        }

                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }
        }
        return file
    }
}