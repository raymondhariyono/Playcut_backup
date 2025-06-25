package com.raymondHariyono.playcut.presentation.screens.branch.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.raymondHariyono.playcut.presentation.components.BranchItem
import com.raymondHariyono.playcut.presentation.components.ButtonNavBar

@Composable
fun SearchBranchPage(
    navController: NavController,
    viewModel: SearchBranchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Text(
                text = "Cari Cabang Kami",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )
        },
        bottomBar = { ButtonNavBar(navController = navController) }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.error != null) {
                Text(text = "Error: ${uiState.error}")
            } else if (uiState.branches.isEmpty()) {
                Text(text = "Tidak ada cabang yang ditemukan.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(uiState.branches, key = { it.id }) { branch ->
                        BranchItem(
                            branch = branch,
                            onDetailClick = { branchId ->
                                navController.navigate("DetailBranch/$branchId")
                            }
                        )
                    }
                }
            }
        }
    }
}