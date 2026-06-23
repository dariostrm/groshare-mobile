package dev.dariostrm.groshare.home

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.dariostrm.groshare.groceries.UsernameAvatar
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileView(
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    ProfileComponent(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun ProfileComponent(
    state: ProfileState,
    onAction: (ProfileAction) -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDarkMode = state.isDarkMode ?: isSystemDark

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))
        
        UsernameAvatar(
            username = state.profile?.username ?: "?",
            isDarkMode = isDarkMode,
            size = 120.dp
        )
        
        Spacer(Modifier.height(16.dp))
        
        Text(
            text = state.profile?.username ?: "Loading...",
            style = MaterialTheme.typography.headlineMedium
        )
        
        state.profile?.email?.let { email ->
            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Dark Mode", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = if (state.isDarkMode == null) "Following system" else "Custom setting",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { onAction(ProfileAction.ToggleDarkMode(it)) }
                    )
                }
            }
            
            if (state.isDarkMode != null) {
                item {
                    TextButton(
                        onClick = { onAction(ProfileAction.ToggleDarkMode(null)) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Reset to system theme")
                    }
                }
            }

            item {
                Button(
                    onClick = { onAction(ProfileAction.Logout) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Logout")
                }
            }
        }
    }
}
