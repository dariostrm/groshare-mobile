package dev.dariostrm.groshare.groceries

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import groshare.shared.generated.resources.Res
import groshare.shared.generated.resources.ic_visibility_filled
import groshare.shared.generated.resources.ic_visibility_off_filled
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

data object GroceriesState

sealed interface GroceriesAction {
}

@Composable
fun GroceriesView(
    viewModel: GroceriesViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->

        }
    }

    GroceriesComponent(
        state = state,
        onAction = viewModel::onAction,
    )
}

@Preview(showBackground = true)
@Composable
fun GroceriesPreview() {
    val state = GroceriesState

    GroceriesComponent(state, onAction = {})
}

@Composable
fun GroceriesComponent(
    state: GroceriesState,
    onAction: (GroceriesAction) -> Unit,
) {
    Text("Groceries PAgeee")
}