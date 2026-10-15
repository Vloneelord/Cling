package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DeliveryStatus
import com.example.model.GuelmimLocation
import com.example.ui.components.DeliveryStatusCard
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.GuelmimMapCanvas
import com.example.ui.components.VehicleSelectorRow
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AppBackground
import com.example.ui.theme.BorderStroke
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceVariant
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import com.example.viewmodel.DeliveryUiState
import com.example.viewmodel.DeliveryViewModel
import kotlinx.coroutines.launch

/**
 * Modern High-Contrast Real-Time Delivery Tracking Screen (TrackScreen)
 *
 * Implements a real Material 3 BottomSheetScaffold with:
 * - Full-bleed interactive background Guelmim Vector Canvas Map (occupies fillMaxSize)
 * - Map elements (Av. Mohammed V, Bd Hassan II, Route Tighmert, Place Bir Anzarane, route & driver)
 *   carefully anchored in the upper 60% of the screen
 * - Draggable & tap-to-toggle Bottom Sheet (peek height = 280.dp)
 * - Interactive Drag Handle pill with smooth animated expansion
 * - Floating map control buttons: "Center on Driver" & "Map / Tracker View"
 * - High-contrast Sahara Night dark theme tokens (zero black-on-navy bugs)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackScreen(
    uiState: DeliveryUiState,
    viewModel: DeliveryViewModel,
    onCallCourier: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = true
        )
    )
    var isDetailsExpanded by remember { mutableStateOf(false) }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 280.dp,
        sheetContainerColor = CardSurface,
        sheetContentColor = PrimaryText,
        sheetShadowElevation = 18.dp,
        sheetTonalElevation = 0.dp,
        sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        sheetDragHandle = {
            // Interactive Drag Handle: draggable by native gesture + clickable to animate toggle
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardSurface)
                    .clickable {
                        coroutineScope.launch {
                            if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                                scaffoldState.bottomSheetState.partialExpand()
                            } else {
                                scaffoldState.bottomSheetState.expand()
                            }
                        }
                    }
                    .padding(top = 10.dp, bottom = 6.dp)
                    .testTag("track_sheet_drag_handle"),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Drag handle pill
                Box(
                    modifier = Modifier
                        .width(44.dp)
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(BorderStroke)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(AccentEmerald)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LIVE GUELMIM DISPATCH",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = AccentEmerald,
                            letterSpacing = 0.6.sp
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                                "Tap or swipe down for map"
                            } else {
                                "Tap or swipe up for details"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = SecondaryText
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                                Icons.Default.KeyboardArrowDown
                            } else {
                                Icons.Default.KeyboardArrowUp
                            },
                            contentDescription = null,
                            tint = SecondaryText,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        },
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardSurface)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 36.dp)
            ) {
                // Real-Time Delivery Status & Driver Console
                uiState.currentOrder?.let { order ->
                    DeliveryStatusCard(
                        order = order,
                        isSimulating = uiState.isSimulating,
                        simMultiplier = uiState.simulationMultiplier,
                        onToggleSimulation = { viewModel.toggleSimulation() },
                        onSetMultiplier = { viewModel.setSimulationMultiplier(it) },
                        onResetSimulation = { viewModel.resetSimulation() },
                        onOpenChat = { viewModel.openChat() },
                        onCallCourier = onCallCourier,
                        onAutoAssignDriver = {
                            val driver = uiState.drivers.firstOrNull { it.isAvailable } ?: uiState.drivers.first()
                            viewModel.assignDriver(order.id, driver)
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Multi-Vehicle Selector Row (Moto, E-Scooter, Car, Bicycle)
                    VehicleSelectorRow(
                        selectedVehicle = uiState.selectedVehicle,
                        onVehicleSelected = { viewModel.selectVehicle(it) }
                    )

                    // Rating Prompt if delivered
                    if (order.status == DeliveryStatus.DELIVERED) {
                        Spacer(modifier = Modifier.height(12.dp))
                        if (order.review == null) {
                            Button(
                                onClick = { viewModel.openRatingDialog(order) },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentAmber),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("btn_open_rating_sheet")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = CardSurface,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Rate Delivery Experience (★ 1-5)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = CardSurface
                                )
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = CardSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderStroke),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = AccentAmber,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "You rated ${order.courierName} ${order.review.rating} ★",
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryText,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "\"${order.review.comment}\"",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = SecondaryText
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Expandable Order Details button
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = CardSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderStroke),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isDetailsExpanded = !isDetailsExpanded }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = null,
                                    tint = AccentBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Order Details • ${order.id}",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryText
                                )
                            }
                            Icon(
                                imageVector = if (isDetailsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = PrimaryText
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = isDetailsExpanded,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 4.dp)
                        ) {
                            ModernDetailRow("Merchant / المتجر", "${order.merchantName} (${order.merchantArabic})")
                            ModernDetailRow("Pickup / نقطة الاستلام", order.pickupLocation.district)
                            ModernDetailRow("Dropoff / عنوان الوصول", "${order.dropoffLocation.name} (${order.dropoffLocation.district})")
                            ModernDetailRow("Package / المحتوى", order.itemsSummary)
                            ModernDetailRow("Subtotal", "${order.subtotalMAD.toInt()} DH")
                            ModernDetailRow("Delivery Fee (${order.vehicleType.title})", "${order.deliveryFeeMAD.toInt()} DH")
                            ModernDetailRow("Total Paid", "${(order.subtotalMAD + order.deliveryFeeMAD).toInt()} DH", isBold = true)
                        }
                    }
                }
            }
        },
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        // Full-Screen Interactive Map Layer (Background body)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppBackground)
        ) {
            // 1. Full-bleed interactive Guelmim Canvas Map
            GuelmimMapCanvas(
                order = uiState.currentOrder,
                isFollowCourier = uiState.isFollowCourier,
                showTraffic = uiState.showTrafficOverlay,
                selectedLocation = uiState.selectedLocation,
                onLocationClick = { viewModel.selectLocation(it) },
                onToggleFollowCourier = { viewModel.toggleFollowCourier() },
                onToggleTraffic = { viewModel.toggleTrafficOverlay() },
                visibleCenterFractionY = 0.32f,
                modifier = Modifier.fillMaxSize()
            )

            // 2. Selected Landmark Detail Banner safely positioned near top
            uiState.selectedLocation?.let { loc ->
                ModernLandmarkDetailBanner(
                    location = loc,
                    onDismiss = { viewModel.selectLocation(null) },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                )
            }

            // 3. Floating map control buttons in bottom-right corner of visible map area (above 280dp sheet)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 296.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End
            ) {
                // Floating "Center on Driver" Button
                Surface(
                    shape = CircleShape,
                    color = CardSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (uiState.isFollowCourier) AccentAmber else BorderStroke),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .size(46.dp)
                        .clickable { viewModel.toggleFollowCourier() }
                        .testTag("btn_center_driver")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Center on Driver",
                            tint = if (uiState.isFollowCourier) AccentAmber else PrimaryText,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Floating "Expand / Collapse Sheet" Toggle Button
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = CardSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderStroke),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .height(38.dp)
                        .clickable {
                            coroutineScope.launch {
                                if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                                    scaffoldState.bottomSheetState.partialExpand()
                                } else {
                                    scaffoldState.bottomSheetState.expand()
                                }
                            }
                        }
                        .testTag("btn_toggle_sheet")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                                Icons.Default.KeyboardArrowDown
                            } else {
                                Icons.Default.KeyboardArrowUp
                            },
                            contentDescription = "Toggle Sheet",
                            tint = AccentBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) "Map View" else "Tracker View",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )
                    }
                }
            }
        }
    }
}

/**
 * Backward-compatible alias so existing call sites for TrackingScreen continue working seamlessly.
 */
@Composable
fun TrackingScreen(
    uiState: DeliveryUiState,
    viewModel: DeliveryViewModel,
    onCallCourier: () -> Unit,
    modifier: Modifier = Modifier
) {
    TrackScreen(
        uiState = uiState,
        viewModel = viewModel,
        onCallCourier = onCallCourier,
        modifier = modifier
    )
}

@Composable
private fun ModernDetailRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = SecondaryText
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Medium,
            color = if (isBold) AccentEmerald else PrimaryText
        )
    }
}

@Composable
private fun ModernLandmarkDetailBanner(
    location: GuelmimLocation,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassmorphicCard(
        shape = RoundedCornerShape(20.dp),
        backgroundColor = CardSurface,
        borderColor = BorderStroke,
        blurRadius = 16.dp,
        elevation = 10.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CardSurfaceVariant)
                    .border(1.dp, BorderStroke, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = AccentBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${location.name} • ${location.arabicName}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
                Text(
                    text = location.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText
                )
            }
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = PrimaryText)
            }
        }
    }
}
