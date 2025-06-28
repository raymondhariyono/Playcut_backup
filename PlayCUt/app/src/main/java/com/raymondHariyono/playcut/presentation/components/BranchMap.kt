package com.raymondHariyono.playcut.presentation.components

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.raymondHariyono.playcut.R
import com.raymondHariyono.playcut.domain.model.Branch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.OnMapReadyCallback
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.android.style.layers.PropertyFactory.*

@Composable
fun BranchMap(
    modifier: Modifier = Modifier,
    branches: List<Branch>,
    scope: CoroutineScope,
    onBranchSelected: (Branch) -> Unit,
    onShowSheet: suspend () -> Unit
) {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
                onCreate(null)
                getMapAsync(object : OnMapReadyCallback {
                    override fun onMapReady(map: MapLibreMap) {
                        val styleUrl = "https://demotiles.maplibre.org/style.json"
                        map.setStyle(Style.Builder().fromUri(styleUrl)) { style ->

                            val iconBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_marker)
                            style.addImage("branch-marker", iconBitmap)

                            val features = branches.map {
                                Feature.fromGeometry(Point.fromLngLat(it.longitude, it.latitude)).apply {
                                    addStringProperty("id", it.id.toString())
                                    addStringProperty("name", it.name)
                                }
                            }

                            val source = GeoJsonSource("branches-source", FeatureCollection.fromFeatures(features))
                            style.addSource(source)

                            val layer = SymbolLayer("branches-layer", "branches-source")
                                .withProperties(
                                    iconImage("branch-marker"),
                                    iconAnchor("bottom"),
                                    iconAllowOverlap(true)
                                )
                            style.addLayer(layer)

                            val position = CameraPosition.Builder()
                                .target(LatLng(-3.3179, 114.5944)) // Banjarmasin
                                .zoom(12.0)
                                .build()
                            map.moveCamera(CameraUpdateFactory.newCameraPosition(position))

                            map.addOnMapClickListener { point ->
                                val screenPoint = map.projection.toScreenLocation(point)
                                val features = map.queryRenderedFeatures(screenPoint, "branches-layer")
                                val feature = features.firstOrNull()
                                val branchId = feature?.getStringProperty("id")
                                val selected = branches.find { it.id.toString() == branchId }


                                if (selected != null) {
                                    onBranchSelected(selected)
                                    scope.launch { onShowSheet() }
                                }
                                true
                            }
                        }
                    }
                })
            }
        },
        modifier = modifier
    )
}
