package com.kira.kmpbase.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.revenuecat.placeholder.PlaceholderSurface
import com.revenuecat.placeholder.ProvidePlaceholderTheme
import com.revenuecat.placeholder.materialPlaceholderTheme
import com.revenuecat.placeholder.placeholderText

private const val SKELETON_ITEM_COUNT = 5

@Composable
fun ContactListSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = SKELETON_ITEM_COUNT,
) {
    ProvidePlaceholderTheme(materialPlaceholderTheme()) {
        PlaceholderSurface {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .placeholderText(
                            enabled = true,
                            lines = 1,
                            style = MaterialTheme.typography.headlineSmall,
                        ),
                )
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(itemCount) {
                        ContactCardSkeleton()
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactCardSkeleton(
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .placeholderText(
                        enabled = true,
                        lines = 1,
                        style = MaterialTheme.typography.titleMedium,
                    ),
            )
            Text(
                text = "",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .placeholderText(
                        enabled = true,
                        lines = 1,
                        style = MaterialTheme.typography.bodyMedium,
                    ),
            )
        }
    }
}
