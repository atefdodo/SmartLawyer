package com.smartlawyer.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.smartlawyer.ui.utils.FileUtils
import com.smartlawyer.ui.utils.FileType
import java.io.File

/**
 * File picker component for selecting PDF and image files
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilePicker(
    title: String,
    documents: List<String>,
    images: List<String>,
    onDocumentsChanged: (List<String>) -> Unit,
    onImagesChanged: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // Document picker launcher
    val documentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            val fileName = FileUtils.generateFileName(
                selectedUri.lastPathSegment ?: "document.pdf",
                FileType.DOCUMENT
            )
            
            val savedPath = FileUtils.saveFileFromUri(context, selectedUri, fileName, FileType.DOCUMENT)
            savedPath?.let { path ->
                val newDocuments = documents.toMutableList()
                newDocuments.add(path)
                onDocumentsChanged(newDocuments)
            }
        }
    }
    
    // Image picker launcher
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            val fileName = FileUtils.generateFileName(
                selectedUri.lastPathSegment ?: "image.jpg",
                FileType.IMAGE
            )
            
            val savedPath = FileUtils.saveFileFromUri(context, selectedUri, fileName, FileType.IMAGE)
            savedPath?.let { path ->
                val newImages = images.toMutableList()
                newImages.add(path)
                onImagesChanged(newImages)
            }
        }
    }
    
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Document Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "المستندات (PDF)",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    IconButton(
                        onClick = { documentLauncher.launch("application/pdf") }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "إضافة مستند")
                    }
                }
                
                if (documents.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(documents) { documentPath ->
                            DocumentCard(
                                filePath = documentPath,
                                onDelete = {
                                    val newDocuments = documents.toMutableList()
                                    newDocuments.remove(documentPath)
                                    onDocumentsChanged(newDocuments)
                                    FileUtils.deleteFile(documentPath)
                                }
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Image Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "الصور",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    IconButton(
                        onClick = { imageLauncher.launch("image/*") }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "إضافة صورة")
                    }
                }
                
                if (images.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(images) { imagePath ->
                            ImageCard(
                                filePath = imagePath,
                                onDelete = {
                                    val newImages = images.toMutableList()
                                    newImages.remove(imagePath)
                                    onImagesChanged(newImages)
                                    FileUtils.deleteFile(imagePath)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Card component for displaying a document
 */
@Composable
fun DocumentCard(
    filePath: String,
    onDelete: () -> Unit
) {
    val file = FileUtils.getFile(filePath)
    
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = "مستند",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = file?.name ?: "مستند",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                
                if (file != null) {
                    Text(
                        text = FileUtils.getFileSizeString(file),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Delete button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "حذف",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * Card component for displaying an image
 */
@Composable
fun ImageCard(
    filePath: String,
    onDelete: () -> Unit
) {
    val file = FileUtils.getFile(filePath)
    
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (file != null && file.exists()) {
                AsyncImage(
                    model = file,
                    contentDescription = "صورة",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "صورة",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Delete button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "حذف",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
