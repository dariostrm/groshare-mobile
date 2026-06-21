package dev.dariostrm.groshare.groceries

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import groshare.shared.generated.resources.Res
import groshare.shared.generated.resources.ic_add
import groshare.shared.generated.resources.ic_error_filled
import groshare.shared.generated.resources.ic_refresh
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

sealed interface GroceriesAction {
    data class AddGrocery(val name: String) : GroceriesAction
    data object Refresh : GroceriesAction
    data class DeleteGrocery(val id: Long) : GroceriesAction
    data object GroceriesErrorShown : GroceriesAction
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
        var isDialogOpen by remember { mutableStateOf(false) }
        if (isDialogOpen) {
            AddGroceryDialog(
                onDismiss = { isDialogOpen = false },
                onAddGroceryClick = {
                    onAction(GroceriesAction.AddGrocery(it))
                    isDialogOpen = false
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
                FloatingActionButton(onClick = { isDialogOpen = true }) {
                    Icon(painter = painterResource(Res.drawable.ic_add), contentDescription = "Add Grocery")
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
                    NoGroceries(onAddGroceryClick = { isDialogOpen = true })
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
                                onDeleteClick = { id -> onAction(GroceriesAction.DeleteGrocery(id)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddGroceryDialog(
    onDismiss: () -> Unit,
    onAddGroceryClick: (String) -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        val focusRequester = remember { FocusRequester() }

        Card(
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "What do you need bought?",
                    style = MaterialTheme.typography.bodyLarge,
                )
                val groceryNameState = rememberTextFieldState()
                val groceryName = groceryNameState.text.toString()
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
                TextField(
                    state = groceryNameState,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    modifier = Modifier.focusRequester(focusRequester),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done,
                    ),
                    onKeyboardAction = { onAddGroceryClick(groceryName) }
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.align(Alignment.End),
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { onAddGroceryClick(groceryName) },
                        enabled = groceryNameState.text.isNotEmpty(),
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

@Composable
fun GroceryItem(
    grocery: Grocery,
    onDeleteClick: (Long) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(grocery.name)
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