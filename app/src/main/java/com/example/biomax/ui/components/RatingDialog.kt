package com.example.biomax.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.biomax.model.OrderTransaction
import com.example.ui.theme.*

@Composable
fun RatingDialog(
    order: OrderTransaction?,
    onDismiss: () -> Unit,
    onSubmitRating: (overall: Int, purity: Int, moisture: Int, punctuality: Int, comment: String) -> Unit
) {
    if (order == null) return

    var overallRating by remember { mutableStateOf(5) }
    var purityScore by remember { mutableStateOf(5) }
    var moistureAccuracyScore by remember { mutableStateOf(5) }
    var punctualityScore by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("partner_rating_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Partner Quality Standards Review",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Transaction: ${order.id}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Overall Score Stars
                Text(
                    text = "Overall Partner Rating",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                StarRatingSelector(rating = overallRating, onRatingSelected = { overallRating = it })

                Spacer(modifier = Modifier.height(14.dp))

                // Feedstock Purity Score
                RatingCategoryRow(
                    title = "Feedstock Purity (Zero inorganic contaminants)",
                    score = purityScore,
                    onScoreSelected = { purityScore = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Moisture Accuracy Score
                RatingCategoryRow(
                    title = "Moisture & Spectrophotometer Accuracy",
                    score = moistureAccuracyScore,
                    onScoreSelected = { moistureAccuracyScore = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Logistics & Punctuality
                RatingCategoryRow(
                    title = "Logistics Punctuality & Packaging Sealed",
                    score = punctualityScore,
                    onScoreSelected = { punctualityScore = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Written Feedback
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Quality observations / Lab notes (Optional)") },
                    placeholder = { Text("e.g. Excellent feedstock purity, zero plastics, ideal C:N ratio for digester.") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("rating_comment_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onSubmitRating(overallRating, purityScore, moistureAccuracyScore, punctualityScore, comment)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BioAmberEnergy),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("submit_rating_button")
                ) {
                    Text(
                        text = "Submit Verified Quality Review",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun RatingCategoryRow(
    title: String,
    score: Int,
    onScoreSelected: (Int) -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        StarRatingSelector(rating = score, onRatingSelected = onScoreSelected, starSize = 22)
    }
}

@Composable
fun StarRatingSelector(
    rating: Int,
    onRatingSelected: (Int) -> Unit,
    starSize: Int = 30
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = "$i Stars",
                tint = if (i <= rating) BioAmberEnergy else MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .size(starSize.dp)
                    .clickable { onRatingSelected(i) }
            )
        }
    }
}
