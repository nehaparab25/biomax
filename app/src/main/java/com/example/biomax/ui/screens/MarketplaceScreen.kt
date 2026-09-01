package com.example.biomax.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.biomax.model.*
import com.example.biomax.ui.components.FeedstockListingCard
import com.example.biomax.ui.components.GlassmorphicSurface

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
            .background(MaterialTheme.colorScheme.background)
            .testTag("marketplace_screen")
    ) {
        // Minimal Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search waste lots, grains, oils...", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .testTag("marketplace_search_input")
        )

        // Fluid Category Filter Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onCategorySelect(null) },
                label = { Text("All Feedstock", fontSize = 10.5.sp, fontWeight = FontWeight.Bold) },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )

            FeedstockCategory.values().forEach { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { onCategorySelect(if (selectedCategory == cat) null else cat) },
                    label = { Text(cat.displayName, fontSize = 10.5.sp) },
                    shape = RoundedCornerShape(20.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        // Listings Stream
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AVAILABLE LOTS (${filteredListings.size})",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "Real-time dispatch",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (filteredListings.isEmpty()) {
                item {
                    GlassmorphicSurface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Outlined.SearchOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No matching organic feedstock lots",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Try adjusting your search keywords or filters",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            } else {
                items(filteredListings, key = { it.id }) { listing ->
                    FeedstockListingCard(
                        listing = listing,
                        currentCurrency = currentCurrency,
                        onProcure = { onProcureListing(listing) }
                    )
                }
            }
        }
    }
}
