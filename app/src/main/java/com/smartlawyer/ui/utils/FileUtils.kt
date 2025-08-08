package com.smartlawyer.ui.utils

import android.content.Context
import android.net.Uri

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class for handling file operations including PDF and image uploads
 */
object FileUtils {
    private const val TAG = "FileUtils"
    private const val DOCUMENTS_DIR = "documents"
    private const val IMAGES_DIR = "images"
    private const val CASES_DIR = "cases"
    private const val CLIENTS_DIR = "clients"
    
    private val gson = Gson()
    
    /**
     * Save a file from URI to internal storage
     */
    fun saveFileFromUri(context: Context, uri: Uri, fileName: String, type: FileType): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = createFile(context, fileName, type)
            
            inputStream?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            
            file.absolutePath
        } catch (_: Exception) {
            null
        }
    }
    
    /**
     * Create a file in the appropriate directory
     */
    private fun createFile(context: Context, fileName: String, type: FileType): File {
        val dir = when (type) {
            FileType.DOCUMENT -> File(context.filesDir, "$DOCUMENTS_DIR/$fileName")
            FileType.IMAGE -> File(context.filesDir, "$IMAGES_DIR/$fileName")
        }
        
        dir.parentFile?.mkdirs()
        return dir
    }
    
    /**
     * Generate unique filename with timestamp
     */
    fun generateFileName(originalName: String, type: FileType): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val extension = when (type) {
            FileType.DOCUMENT -> if (originalName.endsWith(".pdf")) ".pdf" else ".pdf"
            FileType.IMAGE -> if (originalName.endsWith(".jpg") || originalName.endsWith(".jpeg")) ".jpg" else ".png"
        }
        return "${timestamp}_${originalName.replace(" ", "_")}$extension"
    }
    
    /**
     * Get file from path
     */
    fun getFile(path: String): File? {
        return try {
            val file = File(path)
            if (file.exists()) file else null
        } catch (_: Exception) {
            null
        }
    }
    
    /**
     * Delete file from path
     */
    fun deleteFile(path: String): Boolean {
        return try {
            val file = File(path)
            if (file.exists()) {
                file.delete()
            } else {
                true
            }
        } catch (_: Exception) {
            false
        }
    }
    
    /**
     * Convert list of file paths to JSON string
     */
    fun pathsToJson(paths: List<String>): String {
        return gson.toJson(paths)
    }
    
    /**
     * Convert JSON string to list of file paths
     */
    fun jsonToPaths(json: String): List<String> {
        return try {
            if (json.isBlank()) return emptyList()
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }
    
    /**
     * Add file path to existing JSON string
     */
    fun addFilePathToJson(existingJson: String, newPath: String): String {
        val paths = jsonToPaths(existingJson).toMutableList()
        paths.add(newPath)
        return pathsToJson(paths)
    }
    
    /**
     * Remove file path from JSON string
     */
    fun removeFilePathFromJson(existingJson: String, pathToRemove: String): String {
        val paths = jsonToPaths(existingJson).toMutableList()
        paths.remove(pathToRemove)
        return pathsToJson(paths)
    }
    
    /**
     * Get file size in human readable format
     */
    fun getFileSizeString(file: File): String {
        val bytes = file.length()
        val kilobytes = bytes / 1024.0
        val megabytes = kilobytes / 1024.0
        
        return when {
            megabytes >= 1 -> String.format("%.1f MB", megabytes)
            kilobytes >= 1 -> String.format("%.1f KB", kilobytes)
            else -> "$bytes bytes"
        }
    }
    
    /**
     * Get file extension
     */
    fun getFileExtension(fileName: String): String {
        return if (fileName.contains(".")) {
            fileName.substringAfterLast(".")
        } else {
            ""
        }
    }
    
    /**
     * Check if file is PDF
     */
    fun isPdfFile(fileName: String): Boolean {
        return getFileExtension(fileName).lowercase() == "pdf"
    }
    
    /**
     * Check if file is image
     */
    fun isImageFile(fileName: String): Boolean {
        val extension = getFileExtension(fileName).lowercase()
        return extension in listOf("jpg", "jpeg", "png", "gif", "bmp", "webp")
    }
}

/**
 * Enum for file types
 */
enum class FileType {
    DOCUMENT,
    IMAGE
}
