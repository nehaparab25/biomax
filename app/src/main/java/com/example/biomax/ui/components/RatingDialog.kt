package com.example.biomax.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.biomax.model.OrderTransaction

@Composable
fun RatingDialog(
    order: OrderTransaction,
    onDismiss: () -> Unit,
    onSubmitRating: (overall: Int, purity: Int, moisture: Int, punctuality: Int, comment: String) -> Unit
) {
    var overallRating by remember { mutableIntStateOf(5) }
    var purityScore by remember { mutableIntStateOf(5) }
    var moistureScore by remember { mutableIntStateOf(5) }
    var punctualityScore by remember { mutableIntStateOf(5) }
    var comment by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("rating_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Partner Quality Review",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Text(
                    text = "Order #${order.id} • ${order.restaurantName}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Star Rating
                Text("Overall Transaction Rating", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    (1..5).forEach { star ->
                        IconButton(onClick = { overallRating = star }) {
                            Icon(
                                imageVector = if (star <= overallRating) Icons.Filled.Star else Icons.Filled.StarBorder,
                                contentDescription = "$star Stars",
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Detailed Sub-Scores
                ScoreSelectorRow("Organic Purity & Sort Quality", purityScore) { purityScore = it }
                ScoreSelectorRow("Moisture Accuracy vs IoT", moistureScore) { moistureScore = it }
                ScoreSelectorRow("Fleet Loading Punctuality", punctualityScore) { punctualityScore = it }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Quality Feedback Notes") },
                    placeholder = { Text("e.g. Excellent purity, zero glass or plastic contaminants") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onSubmitRating(overallRating, purityScore, moistureScore, punctualityScore, comment)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_rating_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Submit Verified Partner Review", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun ScoreSelectorRow(label: String, currentScore: Int, onScoreSelect: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            (1..5).forEach { num ->
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(if (num == currentScore) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onScoreSelect(num) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = num.toString(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (num == currentScore) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
