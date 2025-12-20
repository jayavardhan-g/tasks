package com.example.tasks.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WorkspaceProgressCard(
    name: String,
    color: Long,
    completedTasks: Int,
    totalTasks: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (totalTasks > 0) completedTasks.toFloat() / totalTasks else 0f
    val workspaceColor = Color(color)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .drawWithContent {
                drawContent()
                
                val strokeWidth = 3.dp.toPx()
                val halfStroke = strokeWidth / 2
                val cornerRadius = 16.dp.toPx()
                
                val rectPath = Path().apply {
                    val w = size.width
                    val h = size.height
                    
                    // Start at Top-Center
                    moveTo(w / 2, halfStroke)
                    
                    // Top edge to Right
                    lineTo(w - cornerRadius, halfStroke)
                    
                    // Top-Right Corner
                    arcTo(
                        rect = Rect(w - 2 * cornerRadius + halfStroke, halfStroke, w - halfStroke, 2 * cornerRadius - halfStroke),
                        startAngleDegrees = 270f,
                        sweepAngleDegrees = 90f,
                        forceMoveTo = false
                    )
                    
                    // Right edge to Bottom
                    lineTo(w - halfStroke, h - cornerRadius)
                    
                    // Bottom-Right Corner
                    arcTo(
                        rect = Rect(w - 2 * cornerRadius + halfStroke, h - 2 * cornerRadius + halfStroke, w - halfStroke, h - halfStroke),
                        startAngleDegrees = 0f,
                        sweepAngleDegrees = 90f,
                        forceMoveTo = false
                    )
                    
                    // Bottom edge to Left
                    lineTo(cornerRadius, h - halfStroke)
                    
                    // Bottom-Left Corner
                    arcTo(
                        rect = Rect(halfStroke, h - 2 * cornerRadius + halfStroke, 2 * cornerRadius - halfStroke, h - halfStroke),
                        startAngleDegrees = 90f,
                        sweepAngleDegrees = 90f,
                        forceMoveTo = false
                    )
                    
                    // Left edge to Top
                    lineTo(halfStroke, cornerRadius)
                    
                    // Top-Left Corner
                    arcTo(
                        rect = Rect(halfStroke, halfStroke, 2 * cornerRadius - halfStroke, 2 * cornerRadius - halfStroke),
                        startAngleDegrees = 180f,
                        sweepAngleDegrees = 90f,
                        forceMoveTo = false
                    )
                    
                    // Back to Top-Center
                    close()
                }

                val pathMeasure = PathMeasure()
                pathMeasure.setPath(rectPath, false)
                val length = pathMeasure.length
                
                // Track
                drawPath(
                    path = rectPath,
                    color = workspaceColor.copy(alpha = 0.1f),
                    style = Stroke(width = strokeWidth)
                )

                // Progress
                if (progress > 0f) {
                    val progressPath = Path()
                    pathMeasure.getSegment(0f, length * progress, progressPath)
                    drawPath(
                        path = progressPath,
                        color = workspaceColor,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = workspaceColor.copy(alpha = 0.05f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(workspaceColor)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 20.sp,
                        letterSpacing = (-0.5).sp
                    ),
                    fontWeight = FontWeight.Black,
                    color = workspaceColor
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = workspaceColor,
                trackColor = workspaceColor.copy(alpha = 0.12f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "$completedTasks / $totalTasks completed",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}
