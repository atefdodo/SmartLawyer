package com.smartlawyer.ui.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Main DatePicker Dialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDatePickerDialog(
    initialDate: String? = null,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val initialMillis = remember(initialDate) {
        val result = initialDate?.takeIf { it.isNotBlank() }?.let {
            try {
                val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it)?.time
                parsed
            } catch (_: Exception) {
                null
            }
        }
        result
    }

    val state = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis
    )

    DatePickerDialog(
        onDismissRequest = {
            Log.d("DatePicker", "Dialog dismissed")
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    state.selectedDateMillis?.let { millis ->
                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val formattedDate = formatter.format(Date(millis))
                        onDateSelected(formattedDate)
                    } ?: run {
                        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .format(Date())
                        onDateSelected(currentDate)
                    }
                    onDismiss()
                }
            ) {
                Text("تأكيد")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text("إلغاء")
            }
        }
    ) {
        DatePicker(state = state)
    }
}

// Alternative implementation using OutlinedTextField (if you prefer the original style)
@Composable
fun OutlinedDateField(
    label: String,
    value: String,
    onClick: () -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = { }, // Read-only
            label = { Text(label) },
            readOnly = true,
            isError = isError,
            placeholder = { Text("اختر التاريخ") },
            trailingIcon = {
                IconButton(onClick = {
                    onClick()
                }) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = "اختيار تاريخ"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isError) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = if (isError) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.outline
            )
        )

        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}