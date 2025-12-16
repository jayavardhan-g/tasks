package com.example.tasks.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistBottomSheet(
    onDismiss: () -> Unit,
    onAddChecklistItem: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var text by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        windowInsets = WindowInsets.ime
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Add Subtasks",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Subtask") },
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        if (text.isNotBlank()) {
                            onAddChecklistItem(text)
                            text = ""
                        }
                    })
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (text.isNotBlank()) {
                            onAddChecklistItem(text)
                            text = ""
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Add"
                    )
                }
            }
        }
    }
    
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
