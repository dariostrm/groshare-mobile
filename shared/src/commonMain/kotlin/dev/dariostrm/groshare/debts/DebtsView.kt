package dev.dariostrm.groshare.debts

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.dariostrm.groshare.groceries.UsernameAvatar
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DebtsView(
    viewModel: DebtsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    DebtsComponent(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun DebtsComponent(
    state: DebtsState,
    onAction: (DebtsAction) -> Unit
) {
    LaunchedEffect(Unit) {
        onAction(DebtsAction.Refresh)
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        if (state.error != null) {
            snackbarHostState.showSnackbar(state.error)
            onAction(DebtsAction.ErrorShown)
        }
    }

    if (state.isLoading && state.debts == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { onAction(DebtsAction.Refresh) },
                modifier = Modifier.padding(padding).fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.debts?.let { debts ->
                        item {
                            TotalNetCard(debts.totalNetCents)
                        }

                        items(debts.debts) { debt ->
                            DebtItem(debt, onSettle = { amount ->
                                onAction(DebtsAction.SettleDebt(debt.roommate.id, amount))
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TotalNetCard(totalNetCents: Long) {
    val amount = totalNetCents / 100f
    val color = when {
        totalNetCents > 0 -> MaterialTheme.colorScheme.primary
        totalNetCents < 0 -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Total Net Balance", style = MaterialTheme.typography.labelMedium)
            Text(
                text = "${if (amount > 0) "+" else ""}${amount}€",
                style = MaterialTheme.typography.headlineMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DebtItem(debt: Debt, onSettle: (Long) -> Unit) {
    var showSettleDialog by remember { mutableStateOf(false) }

    if (showSettleDialog) {
        SettleDebtDialog(
            username = debt.roommate.username,
            suggestedAmountCents = kotlin.math.abs(debt.amountCents),
            onDismiss = { showSettleDialog = false },
            onConfirm = { amount ->
                onSettle(amount)
                showSettleDialog = false
            }
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UsernameAvatar(
            username = debt.roommate.username,
            isDarkMode = isSystemInDarkTheme()
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(debt.roommate.username, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            val amount = debt.amountCents / 100f
            val text = when {
                debt.amountCents > 0 -> "${debt.roommate.username} owes you ${amount}€"
                debt.amountCents < 0 -> "You owe ${debt.roommate.username} ${kotlin.math.abs(amount)}€"
                else -> "All settled with ${debt.roommate.username}"
            }
            Text(text, style = MaterialTheme.typography.bodyMedium)
        }
        if (debt.amountCents < 0) {
            Button(onClick = { showSettleDialog = true }) {
                Text("Settle")
            }
        }
    }
}

@Composable
fun SettleDebtDialog(
    username: String,
    suggestedAmountCents: Long,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    var amountInput by remember { mutableStateOf((suggestedAmountCents / 100f).toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Settle Debt") },
        text = {
            Column {
                Text("How much did you pay $username?")
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text("Amount (€)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val amount = amountInput.toFloatOrNull() ?: 0f
                onConfirm(kotlin.math.round(amount * 100).toLong())
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
