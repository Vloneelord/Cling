package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Info
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
import com.example.model.GuelmimLocation
import com.example.ui.components.DeliveryStatusCard
import com.example.ui.components.GuelmimCanvasMap
import com.example.ui.components.VehicleSelectorRow
import com.example.ui.theme.SaharaAmber
import com.example.ui.theme.TerracottaPrimary
import com.example.viewmodel.DeliveryUiState
import com.example.viewmodel.DeliveryViewModel

@Composable
fun TrackingScreen(
    uiState: DeliveryUiState,
    viewModel: DeliveryViewModel,
    onCallCourier: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isDetailsExpanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Interactive Guelmim Canvas Map (flexible height)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
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

                // Location info card when user taps a landmark
                uiState.selectedLocation?.let { loc ->
                    LandmarkDetailBanner(
                        location = loc,
                        onDismiss = { viewModel.selectLocation(null) },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                    )
                }
            }

            // Bottom Delivery Control Console
            Surface(
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Multi-Vehicle Selector Row
                    VehicleSelectorRow(
                        selectedVehicle = uiState.selectedVehicle,
                        onVehicleSelected = { viewModel.selectVehicle(it) }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Real-time Delivery Status & ETA Card
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
                        if (order.status == com.example.model.DeliveryStatus.DELIVERED) {
                            Spacer(modifier = Modifier.height(10.dp))
                            if (order.review == null) {
                                Button(
                                    onClick = { viewModel.openRatingDialog(order) },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SaharaAmber),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("btn_open_rating_sheet")
                                ) {
                                    Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Rate Delivery Experience (★ 1-5)",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = SaharaAmber.copy(alpha = 0.12f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SaharaAmber.copy(alpha = 0.35f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = androidx.compose.material.icons.Icons.Default.Star,
                                            contentDescription = null,
                                            tint = SaharaAmber,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "You rated ${order.courierName} ${order.review.rating} ★",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "\"${order.review.comment}\"",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Expandable Order Details button
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isDetailsExpanded = !isDetailsExpanded }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ReceiptLong,
                                        contentDescription = null,
                                        tint = TerracottaPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Order Details • ${order.id}",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Icon(
                                    imageVector = if (isDetailsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null
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
                                    .padding(vertical = 8.dp)
                            ) {
                                DetailRow("Merchant / المتجر", "${order.merchantName} (${order.merchantArabic})")
                                DetailRow("Pickup / نقطة الاستلام", order.pickupLocation.district)
                                DetailRow("Dropoff / عنوان الوصول", "${order.dropoffLocation.name} (${order.dropoffLocation.district})")
                                DetailRow("Package / المحتوى", order.itemsSummary)
                                DetailRow("Subtotal", "${order.subtotalMAD.toInt()} DH")
                                DetailRow("Delivery Fee (${order.vehicleType.title})", "${order.deliveryFeeMAD.toInt()} DH")
                                DetailRow("Total Paid", "${(order.subtotalMAD + order.deliveryFeeMAD).toInt()} DH", isBold = true)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = if (isBold) TerracottaPrimary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun LandmarkDetailBanner(
    location: GuelmimLocation,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(8.dp),
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
                    .background(TerracottaPrimary.copy(alpha = 0.15f))
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = TerracottaPrimary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${location.name} • ${location.arabicName}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = location.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }
    }
}
