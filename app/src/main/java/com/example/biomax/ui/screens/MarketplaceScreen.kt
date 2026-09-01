package com.example.biomax.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biomax.localization.LocalizationManager
import com.example.biomax.model.*
import com.example.biomax.ui.components.FeedstockListingCard
import com.example.ui.theme.*

@Composable
fun MarketplaceScreen(
    listings: List<WasteListing>,
    currentCurrency: CurrencyUnit,
    currentLanguage: AppLanguage,
    searchQuery: String,
    selectedCategory: FeedstockCategory?,
    selectedGrade: FreshnessGrade?,
    onSearchChange: (String) -> Unit,
    onCategorySelect: (FeedstockCategory?) -> Unit,
    onGradeSelect: (FreshnessGrade?) -> Unit,
    onProcureListing: (WasteListing) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredListings = remember(listings, searchQuery, selectedCategory, selectedGrade) {
        listings.filter { listing ->
            val matchesSearch = searchQuery.isBlank() ||
                    listing.title.contains(searchQuery, ignoreCase = true) ||
                    listing.restaurantName.contains(searchQuery, ignoreCase = true) ||
                    listing.category.displayName.contains(searchQuery, ignoreCase = true)

            val matchesCategory = selectedCategory == null || listing.category == selectedCategory
            val matchesGrade = selectedGrade == null || listing.freshnessGrade == selectedGrade

            matchesSearch && matchesCategory && matchesGrade
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("marketplace_screen")
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search waste food batches, bakery, grease, grains...", fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BioGreenDark) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .testTag("marketplace_search_input")
        )

        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onCategorySelect(null) },
                label = { Text("All Feedstocks", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BioGreenDark,
                    selectedLabelColor = Color.White
                )
            )

            FeedstockCategory.values().forEach { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { onCategorySelect(if (selectedCategory == cat) null else cat) },
                    label = { Text(cat.displayName, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BioGreenDark,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Freshness Grade Quick Filter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Grade:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            FreshnessGrade.values().forEach { grade ->
                AssistChip(
                    onClick = { onGradeSelect(if (selectedGrade == grade) null else grade) },
                    label = { Text(grade.name.replace("_", " "), fontSize = 10.sp) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (selectedGrade == grade) BioGreenPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    border = AssistChipDefaults.assistChipBorder(
                        enabled = true,
                        borderColor = if (selectedGrade == grade) BioGreenPrimary else Color.Transparent
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Listings List
        if (filteredListings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FilterListOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No matching feedstock listings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Try clearing your filters or search terms",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 6.dp, bottom = 80.dp)
            ) {
                items(filteredListings, key = { it.id }) { listing ->
                    FeedstockListingCard(
                        listing = listing,
                        currentCurrency = currentCurrency,
                        currentLanguage = currentLanguage,
                        onProcureClick = { onProcureListing(listing) }
                    )
                }
            }
        }
    }
}
