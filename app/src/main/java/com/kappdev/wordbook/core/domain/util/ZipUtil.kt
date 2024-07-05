package com.kappdev.wordbook.core.domain.util

import okio.use
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

object ZipUtil {

    fun zipContains(zipFile: File, fileName: String): Boolean {
        try {
            ZipFile(zipFile).use { zip ->
                zip.entries().asSequence().forEach { entry ->
                    if (entry.name == fileName) return true
                }
            }
            return false
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}