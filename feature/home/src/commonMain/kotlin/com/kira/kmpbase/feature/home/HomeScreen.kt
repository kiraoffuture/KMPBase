package com.kira.kmpbase.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kira.kmpbase.core.ui.components.ContactListSkeleton
import com.kira.kmpbase.core.ui.components.EmptyView
import com.kira.kmpbase.core.ui.components.ErrorView
import com.kira.kmpbase.core.ui.generated.resources.Res
import com.kira.kmpbase.core.ui.generated.resources.home_contacts_title
import com.kira.kmpbase.core.ui.generated.resources.home_no_contacts
import com.kira.kmpbase.core.ui.generated.resources.home_test_crashlytics
import com.kira.kmpbase.core.ui.localization.toLocalizedMessage
import com.kira.kmpbase.core.ui.viewmodel.koinViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val errorMessage = uiState.error?.toLocalizedMessage()

    Column(modifier = modifier.fillMaxSize()) {
        Button(
            onClick = viewModel::testCrashlytics,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Text(stringResource(Res.string.home_test_crashlytics))
        }
        Box(modifier = Modifier.weight(1f)) {
            when {
                uiState.isLoading && uiState.contacts.isEmpty() -> ContactListSkeleton(Modifier.fillMaxSize())
                errorMessage != null && uiState.contacts.isEmpty() -> ErrorView(
                    message = errorMessage,
                    onRetry = viewModel::refresh,
                    modifier = Modifier.fillMaxSize(),
                )
                uiState.contacts.isEmpty() -> EmptyView(
                    message = stringResource(Res.string.home_no_contacts),
                    modifier = Modifier.fillMaxSize(),
                )
                else -> HomeContent(
                    uiState = uiState,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(Res.string.home_contacts_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(uiState.contacts, key = { it.id }) { contact ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = contact.name, style = MaterialTheme.typography.titleMedium)
                        Text(text = contact.phone, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
