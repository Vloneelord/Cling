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
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.draw.clipToBounds
import com.example.model.DeliveryStatus
import com.example.model.GuelmimLocation
import com.example.ui.components.DeliveryStatusCard
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.GuelmimCanvasMap
import com.example.ui.components.VehicleSelectorRow
import com.example.ui.theme.GlassFrostedBorder
import com.example.ui.theme.GlassFrostedSurface
import com.example.ui.theme.ModernAmber
import com.example.ui.theme.ModernBlack
import com.example.ui.theme.ModernBorder
import com.example.ui.theme.ModernMuted
import com.example.ui.theme.ModernSurfaceSubtle
import com.example.ui.theme.ModernWhite
import com.example.viewmodel.DeliveryUiState
import com.example.viewmodel.DeliveryViewModel

/**
 * Modern Live Tracking Screen with Interactive Vector Map and Floating Control Console.
 * Inspired by Behance OXXO Retail & Zevoa Super App.
 */
@Composable
fun TrackingScreen(
    uiState: DeliveryUiState,
    viewModel: DeliveryViewModel,
    onCallCourier: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    var isDetailsExpanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Interactive Guelmim Canvas Map (with strict clipToBounds to prevent bleed)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clipToBounds()
            ) {
                GuelmimCanvasMap(
                    order = uiState.currentOrder,
                    isFollowCourier = uiState.isFollowCourier,
                    showTraffic = uiState.showTrafficOverlay,
                    selectedLocation = uiState.selectedLocation,
                    onLocationClick = { viewModel.selectLocation(it) },
                    onToggleFollowCourier = { viewModel.toggleFollowCourier() },
                    onToggleTraffic = { viewModel.toggleTrafficOverlay() }
                )

                // Landmark detail banner when tapped - Positioned safely below top status bar
                uiState.selectedLocation?.let { loc ->
                    ModernLandmarkDetailBanner(
                        location = loc,
                        onDismiss = { viewModel.selectLocation(null) },
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 76.dp, start = 16.dp, end = 16.dp)
                    )
                }
            }

            // Floating Bottom Sheet Console (Opaque high-contrast surface to eliminate background bleed-through)
            GlassmorphicCard(
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                backgroundColor = if (isDark) Color(0xFF0F172A) else ModernWhite,
                borderColor = if (isDark) Color(0xFF334155) else ModernBorder,
                blurRadius = 20.dp,
                elevation = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Modern Drag Handle
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(42.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(ModernBorder)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Multi-Vehicle Selector Row (Moto, E-Scooter, Car, Bicycle)
                    VehicleSelectorRow(
                        selectedVehicle = uiState.selectedVehicle,
                        onVehicleSelected = { viewModel.selectVehicle(it) }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

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
                            onCallCourier = onCallCourier
                        )

                        // Rating Prompt if delivered
                        if (order.status == DeliveryStatus.DELIVERED) {
                            Spacer(modifier = Modifier.height(12.dp))
                            if (order.review == null) {
                                Button(
                                    onClick = { viewModel.openRatingDialog(order) },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = ModernBlack),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("btn_open_rating_sheet")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = ModernAmber,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Rate Delivery Experience (★ 1-5)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                }
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = ModernSurfaceSubtle,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ModernBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = ModernAmber,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "You rated ${order.courierName} ${order.review.rating} ★",
                                                fontWeight = FontWeight.Bold,
                                                color = ModernBlack,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "\"${order.review.comment}\"",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = ModernMuted
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
                            color = ModernSurfaceSubtle,
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
                                        tint = ModernBlack,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Order Details • ${order.id}",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = ModernBlack
                                    )
                                }
                                Icon(
                                    imageVector = if (isDetailsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = ModernBlack
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
            }
        }
    }
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
            color = ModernMuted
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Medium,
            color = ModernBlack
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
                    .background(ModernBlack)
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${location.name} • ${location.arabicName}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = ModernBlack
                )
                Text(
                    text = location.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = ModernMuted
                )
            }
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = ModernBlack)
            }
        }
    }
}
