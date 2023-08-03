package com.joseg.fakeyouclient.data.fake

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStreamWriter
import java.lang.Exception

object FakeAssetManager {
    fun open(fileName: String): InputStream = javaClass.getResourceAsStream("/$fileName")

    fun write(fileName: String, json: String) {
        val file = File("src/test/resources/$fileName")
        val fileOutputStream = FileOutputStream(file)
        val outputStreamWriter = OutputStreamWriter(fileOutputStream)
        try {
            file.createNewFile()
            outputStreamWriter.write(json)
            outputStreamWriter.flush()
        } catch (e: Exception) {
        } finally {
            outputStreamWriter.close()
        }
    }
}