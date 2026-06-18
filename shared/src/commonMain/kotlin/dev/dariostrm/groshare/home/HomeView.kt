package dev.dariostrm.groshare.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.dariostrm.groshare.groceries.GroceriesView
import groshare.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

data object HomeState

sealed interface HomeAction {

}

@Composable
fun HomeView(
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->

        }
    }

    HomeComponent(
        state = state,
        onAction = viewModel::onAction,
    )
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    val state = HomeState

    HomeComponent(state, onAction = {})
}

enum class Tabs {
    Groceries,
    Debts,
    Profile
}

@Composable
fun HomeComponent(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
) {
    var selectedTab by remember { mutableStateOf(Tabs.Groceries) }

    Scaffold(
        Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar() {
                var isSelected = selectedTab == Tabs.Groceries
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { selectedTab = Tabs.Groceries },
                    icon = {
                        Icon(
                            painter = painterResource(
                                if (isSelected) Res.drawable.ic_shopping_bag_filled
                                else Res.drawable.ic_shopping_bag
                            ),
                            contentDescription = "Groceries Page"
                        )
                    },
                    label = { Text("Groceries") }
                )

                isSelected = selectedTab == Tabs.Debts
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { selectedTab = Tabs.Debts },
                    icon = {
                        Icon(
                            painter = painterResource(
                                if (isSelected) Res.drawable.ic_paid_filled
                                else Res.drawable.ic_paid
                            ),
                            contentDescription = "Debts Page"
                        )
                    },
                    label = { Text("Debts") }
                )

                isSelected = selectedTab == Tabs.Profile
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { selectedTab = Tabs.Profile },
                    icon = {
                        Icon(
                            painter = painterResource(
                                if (isSelected) Res.drawable.ic_account_circle_filled
                                else Res.drawable.ic_account_circle
                            ),
                            contentDescription = "Debts Page"
                        )
                    },
                    label = { Text("Profile") }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (selectedTab) {
                Tabs.Groceries -> GroceriesView()
                Tabs.Debts -> Text("Debts Page")
                Tabs.Profile -> Text("Profile Page")
            }
        }
    }
}