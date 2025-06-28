package com.raymondHariyono.playcut.presentation.screens.map

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.raymondHariyono.playcut.domain.model.Branch
import com.raymondHariyono.playcut.presentation.components.BranchMap
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPage(
    navController: NavController,
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var selectedBranch by remember { mutableStateOf<Branch?>(null) }

    Scaffold { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            BranchMap(
                branches = uiState.branches,
                scope = coroutineScope,
                onBranchSelected = { selectedBranch = it },
                onShowSheet = { sheetState.show() }
            )

            // Loading
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            // Error Message
            uiState.errorMessage?.let {
                Text(
                    text = "Error: $it",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
                )
            }
        }
    }

    // Bottom Sheet saat branch dipilih
    selectedBranch?.let { branch ->
        ModalBottomSheet(
            onDismissRequest = { selectedBranch = null },
            sheetState = sheetState
        ) {
            BranchInfoSheet(
                branch = branch,
                onNavigateClick = {
                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {

                    }
                }
            )
        }
    }
}

@Composable
fun BranchInfoSheet(
    branch: Branch,
    onNavigateClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = branch.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = branch.addressFull,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onNavigateClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pesan Sekarang")
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Pesan")
        }
    }
}
