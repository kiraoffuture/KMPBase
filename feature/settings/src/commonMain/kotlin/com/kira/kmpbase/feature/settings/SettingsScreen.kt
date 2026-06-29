package com.kira.kmpbase.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kira.kmpbase.core.ui.generated.resources.Res
import com.kira.kmpbase.core.ui.generated.resources.settings_logout
import com.kira.kmpbase.core.ui.generated.resources.settings_title
import com.kira.kmpbase.core.ui.viewmodel.koinViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.settings_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        Button(
            onClick = viewModel::logout,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(Res.string.settings_logout))
        }
    }
}
