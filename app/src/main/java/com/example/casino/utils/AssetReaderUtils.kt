package com.example.casino.utils

import com.example.casino.app.App
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object AssetReaderUtils {

    fun readTextFromFile(fileName: String): String {
        var result = ""
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(
                InputStreamReader(App.instance.assets.open(fileName), "UTF-8")
            )
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                result += line + "\n"
            }

        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return result
    }
}