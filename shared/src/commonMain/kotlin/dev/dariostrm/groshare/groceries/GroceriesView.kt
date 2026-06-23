package dev.dariostrm.groshare.groceries

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import groshare.shared.generated.resources.Res
import groshare.shared.generated.resources.ic_add
import groshare.shared.generated.resources.ic_check
import groshare.shared.generated.resources.ic_delete
import groshare.shared.generated.resources.ic_more_vert
import groshare.shared.generated.resources.ic_refresh
import groshare.shared.generated.resources.ic_shopping_cart_filled
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

sealed interface GroceriesAction {
    data class AddGrocery(val name: String) : GroceriesAction
    data object Refresh : GroceriesAction
    data class DeleteGrocery(val id: Long) : GroceriesAction
    data object GroceriesErrorShown : GroceriesAction
    data class ToggleGrocerySelection(val grocery: Grocery) : GroceriesAction
    data class BuyGroceries(val purchases: Map<Long, Float>) : GroceriesAction
}

@Composable
fun GroceriesView(
    viewModel: GroceriesViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    GroceriesComponent(
        state = state,
        onAction = viewModel::onAction,
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GroceriesPreview() {
    val state = GroceriesState(
        isLoading = false,
        groceriesError = null,
    )

    GroceriesComponent(state, onAction = {})
}

@Composable
fun GroceriesComponent(
    state: GroceriesState,
    onAction: (GroceriesAction) -> Unit,
) {
    if (state.isLoading && state.groceriesError == null)
        GroceriesLoading()
    else if (state.groceries.isEmpty() && state.groceriesError != null) {
        GroceriesFailedToLoad(
            error = state.groceriesError,
            isRefreshing = state.isRefreshing,
            tryAgain = { onAction(GroceriesAction.Refresh) }
        )
    } else {
        var isAddDialogOpen by remember { mutableStateOf(false) }
        var isBuyDialogOpen by remember { mutableStateOf(false) }
        if (isBuyDialogOpen) {
            BuyGroceriesDialog(
                groceries = state.selectedGroceries.toList(),
                onDismiss = { isBuyDialogOpen = false },
                onSubmit = {
                    onAction(GroceriesAction.BuyGroceries(it))
                    isBuyDialogOpen = false
                }
            )
        }
        if (isAddDialogOpen) {
            AddGroceryDialog(
                onDismiss = { isAddDialogOpen = false },
                onAddGroceryClick = {
                    onAction(GroceriesAction.AddGrocery(it))
                    isAddDialogOpen = false
                }
            )
        }
        val snackBarHostState = remember { SnackbarHostState() }
        LaunchedEffect(state.groceriesError) {
            if (state.groceriesError != null) {
                snackBarHostState.showSnackbar(state.groceriesError)
                onAction(GroceriesAction.GroceriesErrorShown)
            }
        }
        Scaffold(
            floatingActionButton = {
                AnimatedContent(
                    targetState = state.selectedGroceries.isNotEmpty(),
                    label = "fab_morph"
                ) { isSelecting ->
                    if (isSelecting) {
                        ExtendedFloatingActionButton(
                            onClick = { isBuyDialogOpen = true },
                            icon = {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_shopping_cart_filled),
                                    contentDescription = "Buy Groceries"
                                )
                            },
                            text = { Text("Checkout (${state.selectedGroceries.size})") }
                        )
                    } else {
                        FloatingActionButton(onClick = { isAddDialogOpen = true }) {
                            Icon(painter = painterResource(Res.drawable.ic_add), contentDescription = "Add Grocery")
                        }
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackBarHostState) }
        ) { innerPadding ->
            PullToRefreshBox(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                isRefreshing = state.isRefreshing,
                onRefresh = { onAction(GroceriesAction.Refresh) }
            ) {
                if (state.groceries.isEmpty())
                    NoGroceries(onAddGroceryClick = { isAddDialogOpen = true })
                else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(
                            items = state.groceries,
                            key = { grocery -> grocery.id }
                        ) {
                            GroceryItem(
                                grocery = it,
                                isSelected = it in state.selectedGroceries,
                                modifier = Modifier.padding(16.dp, 4.dp),
                                onDeleteClick = { id -> onAction(GroceriesAction.DeleteGrocery(id)) },
                                onSelectionToggled = { onAction(GroceriesAction.ToggleGrocerySelection(it)) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BuyGroceriesDialog(
    groceries: List<Grocery>,
    onDismiss: () -> Unit,
    onSubmit: (Map<Long, Float>) -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    var equalTotalInput by remember { mutableStateOf("") }

    var itemizedInputs by remember {
        mutableStateOf(groceries.map { it.id }.associateWith { "" })
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Checkout (${groceries.size} items)") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TabRow(selectedTabIndex = selectedTabIndex) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("Split Equally") }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("Itemized") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedTabIndex == 0) {
                    OutlinedTextField(
                        value = equalTotalInput,
                        onValueChange = { equalTotalInput = it },
                        label = { Text("Total Receipt Amount ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    val parsedTotal = equalTotalInput.toFloatOrNull() ?: 0f
                    val perItem = if (groceries.isNotEmpty()) parsedTotal / groceries.size else 0f

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Each item: $${String.format("%.2f", perItem)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        groceries.forEach { grocery ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(grocery.name, modifier = Modifier.weight(1f))
                                OutlinedTextField(
                                    value = itemizedInputs[grocery.id] ?: "",
                                    onValueChange = { newVal ->
                                        itemizedInputs = itemizedInputs.toMutableMap().apply { put(grocery.id, newVal) }
                                    },
                                    prefix = { Text("$") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    modifier = Modifier.width(120.dp)
                                )
                            }
                        }

                        val currentSum = itemizedInputs.values.sumOf { (it.toFloatOrNull() ?: 0f).toDouble() }
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Total: $${String.format("%.2f", currentSum)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedTabIndex == 0) {
                        val total = equalTotalInput.toFloatOrNull() ?: 0f
                        val split = total / groceries.size
                        val payload = groceries.map { it.id }.associateWith { split }
                        onSubmit(payload)
                    } else {
                        val payload = itemizedInputs.mapValues { it.value.toFloatOrNull() ?: 0f }
                        onSubmit(payload)
                    }
                }
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun GroceryItem(
    grocery: Grocery,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier,
    onDeleteClick: (Long) -> Unit,
    onSelectionToggled: () -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                else Color.Transparent
            )
            .selectable(selected = isSelected, onClick = onSelectionToggled)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        AnimatedContent(
            targetState = isSelected
        ) { isSelected ->
            if (isSelected) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_check),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            } else {
                UsernameAvatar(
                    username = grocery.addedByUsername,
                    modifier = Modifier,
                    isDarkMode = isSystemInDarkTheme()
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text = grocery.name,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "added by ${grocery.addedByUsername}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }

        Box {
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    painter = painterResource(Res.drawable.ic_more_vert),
                    contentDescription = "Grocery options"
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_delete),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    onClick = {
                        showMenu = false
                        onDeleteClick(grocery.id)
                    }
                )
            }
        }
    }
}

@Composable
fun NoGroceries(
    onAddGroceryClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No Groceries yet",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onAddGroceryClick) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(Res.drawable.ic_add),
                    contentDescription = "Add Grocery",
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Grocery")
            }
        }
    }
}

@Composable
fun GroceriesFailedToLoad(
    error: String,
    isRefreshing: Boolean,
    tryAgain: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Failed to load groceries",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = tryAgain) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isRefreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        trackColor = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Trying again...")
                } else {
                    Icon(
                        painter = painterResource(Res.drawable.ic_refresh),
                        contentDescription = "Try loading groceries again",
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Try again")
                }
            }
        }
    }
}

@Composable
fun GroceriesLoading() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(64.dp),
            strokeWidth = 8.dp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Loading...",
            style = MaterialTheme.typography.titleLarge
        )
    }
}