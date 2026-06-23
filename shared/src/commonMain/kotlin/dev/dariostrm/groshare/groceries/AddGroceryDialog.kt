package dev.dariostrm.groshare.groceries

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

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
                    onKeyboardAction = {
                        if (groceryNameState.text.isNotEmpty()) onAddGroceryClick(groceryName)
                    }
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