package com.example.tasks.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tasks.data.Workspace

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceSelectionBottomSheet(
    workspaces: List<Workspace>,
    onDismiss: () -> Unit,
    onWorkspaceSelected: (Workspace?) -> Unit,
    onCreateWorkspace: (String, Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var showCreateInput by remember { mutableStateOf(false) }
    var newWorkspaceName by remember { mutableStateOf("") }
    // Simple color selection for now - maybe just random or a fixed one
    val defaultColor = 0xFF6200EE.toInt() 

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
                text = if (showCreateInput) "Create New Workspace" else "Select Workspace",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (showCreateInput) {
                // Color Palette
                val palette = listOf(
                    0xFFF44336, // Red
                    0xFFE91E63, // Pink
                    0xFF9C27B0, // Purple
                    0xFF673AB7, // Deep Purple
                    0xFF3F51B5, // Indigo
                    0xFF2196F3, // Blue
                    0xFF03A9F4, // Light Blue
                    0xFF00BCD4, // Cyan
                    0xFF009688, // Teal
                    0xFF4CAF50, // Green
                    0xFF8BC34A, // Light Green
                    0xFFCDDC39, // Lime
                    0xFFFFEB3B, // Yellow
                    0xFFFFC107, // Amber
                    0xFFFF9800, // Orange
                    0xFFFF5722  // Deep Orange
                )
                // Randomly select one initially when dialog opens, but we need to store it statefully
                // However, showCreateInput toggles, so we might want to reset or randomize then.
                // Let's us a side effect or just rememberSaveable if we want it to persist across recompositions
                // for the same session. for simplicity:
                var selectedColor by remember { mutableStateOf(palette.random().toInt()) }

                OutlinedTextField(
                    value = newWorkspaceName,
                    onValueChange = { newWorkspaceName = it },
                    label = { Text("Workspace Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Select Color", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                   // Display a few colors or a grid. For simple row, maybe just 5-6?
                   // Or a LazyRow if we have many.
                   androidx.compose.foundation.lazy.LazyRow(
                       horizontalArrangement = Arrangement.spacedBy(12.dp)
                   ) {
                       items(palette) { colorHex ->
                           val isSelected = selectedColor == colorHex.toInt()
                           Box(
                               modifier = Modifier
                                   .size(36.dp)
                                   .clip(CircleShape)
                                   .background(Color(colorHex))
                                   .clickable { selectedColor = colorHex.toInt() }
                                   .then(if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape) else Modifier)
                           )
                       }
                   }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { showCreateInput = false }) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            if (newWorkspaceName.isNotBlank()) {
                                onCreateWorkspace(newWorkspaceName, selectedColor)
                                showCreateInput = false
                                newWorkspaceName = ""
                            }
                        },
                        enabled = newWorkspaceName.isNotBlank()
                    ) {
                        Text("Create")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                         Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onWorkspaceSelected(null) } // No Workspace
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.outline)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("No Workspace", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                    
                    items(workspaces) { workspace ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onWorkspaceSelected(workspace) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(Color(workspace.color))
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(workspace.name, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                    
                    item {
                        Button(
                            onClick = { showCreateInput = true },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Create new workspace")
                        }
                    }
                }
            }
        }
    }
}
