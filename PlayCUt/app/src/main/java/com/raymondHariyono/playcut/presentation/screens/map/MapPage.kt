package com.raymondHariyono.playcut.presentation.screens.map

import androidx.compose.foundation.Image
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.raymondHariyono.playcut.R
import com.raymondHariyono.playcut.R.drawable.ic_map_marker
import com.raymondHariyono.playcut.domain.model.Branch
import com.raymondHariyono.playcut.presentation.components.OsmMapView
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPage(
    navController: NavController,
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var selectedBranch by remember { mutableStateOf<Branch?>(null) }
    var mapView by remember { mutableStateOf<MapView?>(null) }
    val context = LocalContext.current

    LaunchedEffect(uiState.branches, mapView) {
        val map = mapView ?: return@LaunchedEffect

        map.overlays.filterIsInstance<Marker>().forEach { map.overlays.remove(it) }

        val markerIcon: Drawable? = AppCompatResources.getDrawable(context, ic_map_marker)

        uiState.branches.forEach { branch ->
            val marker = Marker(map).apply {
                position = GeoPoint(branch.latitude, branch.longitude)
                title = branch.name
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                icon = markerIcon
                setOnMarkerClickListener { _, _ ->
                    selectedBranch = branch
                    scope.launch { sheetState.show() }
                    true
                }
            }
            map.overlays.add(marker)
        }

        if (map.zoomLevelDouble < 10) {
            map.controller.setZoom(12.0)
            map.controller.setCenter(GeoPoint(-3.3179, 114.5944))
        }

        map.invalidate()
    }

    Scaffold { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            OsmMapView(
                modifier = Modifier.fillMaxSize(),
                onMapViewReady = { map -> mapView = map }
            )
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }

    if (selectedBranch != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedBranch = null },
            sheetState = sheetState
        ) {
            BranchInfoSheet(
                branch = selectedBranch!!,
                onNavigateClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        navController.navigate("detailBranch/${selectedBranch!!.id}")
                    }
                }
            )
        }
    }
}

@Composable
fun BranchInfoSheet(branch: Branch, onNavigateClick: () -> Unit) {
    Column(modifier = Modifier.padding(bottom = 32.dp)) {
        Image(
            painter = painterResource(id = R.drawable.placeholder_branch),
            contentDescription = branch.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        )

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = branch.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(text = branch.addressFull, style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(24.dp))
            Button(onClick = onNavigateClick, modifier = Modifier.fillMaxWidth()) {
                Text("Lihat Detail Cabang")
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Navigate")
            }
        }
    }
}