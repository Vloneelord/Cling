package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.GuelmimMapData
import com.example.model.DeliveryOrder
import com.example.model.DeliveryStatus
import com.example.model.Driver
import com.example.model.SampleDeliveryData
import com.example.model.VehicleType
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

/**
 * Modern Sahara Night Business Orders & Driver Portal (Tab 3: Orders)
 * Fixes:
 * - High-contrast dark filter chips (CardSurfaceVariant unselected, AccentBlue selected)
 * - Removed stray divider line
 * - Explicit SecondaryText on descriptions & PrimaryText on prices
 * - Working "Assign Driver" modal sheet
 * - Working "Rate Driver" 5-star modal dialog
 * - Clean "Advance Status" action for unassigned orders (no empty boxes)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessOrderManagementScreen(
    uiState: DeliveryUiState,
    viewModel: DeliveryViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Orders Queue, 1: Driver Directory
    var statusFilter by remember { mutableStateOf<DeliveryStatus?>(null) }
    var orderToAssignDriver by remember { mutableStateOf<DeliveryOrder?>(null) }
    var orderToRate by remember { mutableStateOf<DeliveryOrder?>(null) }

    val filteredOrders = remember(uiState.allOrders, statusFilter) {
        if (statusFilter == null) {
            uiState.allOrders
        } else {
            uiState.allOrders.filter { it.status == statusFilter }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Business Portal Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Guelmim Orders & Dispatch",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryText
                )
                Text(
                    text = "بوابة إدارة الطلبات وتعيين السائقين",
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CardSurfaceVariant,
                border = BorderStroke(1.dp, BorderStroke)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(AccentEmerald)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Live Sync",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentEmerald
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Store Chips (Restaurant Bab Sahra, Café Tighmert, etc.)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 2.dp)
        ) {
            items(GuelmimMapData.sampleStores) { store ->
                val isSelected = uiState.selectedBusinessStore.id == store.id
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.selectBusinessStore(store) },
                    label = {
                        Text(
                            text = store.name,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) PrimaryText else SecondaryText
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentBlue,
                        selectedLabelColor = PrimaryText,
                        containerColor = CardSurfaceVariant,
                        labelColor = SecondaryText
                    ),
                    border = BorderStroke(1.dp, if (isSelected) AccentBlue else BorderStroke)
                )
            }
        }

        // Real-Time Notification Banner for Driver Assignment
        uiState.driverNotificationBanner?.let { banner ->
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CardSurfaceVariant,
                border = BorderStroke(1.dp, AccentAmber.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = AccentAmber,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = banner,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryText
                        )
                    }
                    IconButton(
                        onClick = { viewModel.dismissDriverNotification() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = SecondaryText, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Primary Tab Row
        PrimaryTabRow(
            selectedTabIndex = selectedTab,
            containerColor = CardSurface,
            contentColor = AccentBlue,
            divider = {}, // Removed stray horizontal divider line
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, BorderStroke, RoundedCornerShape(14.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        text = "Orders Queue (${uiState.allOrders.size})",
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTab == 0) AccentBlue else SecondaryText
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        text = "Drivers (${SampleDeliveryData.drivers.size})",
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTab == 1) AccentBlue else SecondaryText
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedTab == 0) {
            // Status Filter Row (High-Contrast Dark Chips)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                item {
                    val isAllSelected = statusFilter == null
                    FilterChip(
                        selected = isAllSelected,
                        onClick = { statusFilter = null },
                        label = {
                            Text(
                                text = "All (${uiState.allOrders.size})",
                                fontSize = 11.5.sp,
                                fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isAllSelected) PrimaryText else SecondaryText
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentBlue,
                            selectedLabelColor = PrimaryText,
                            containerColor = CardSurfaceVariant,
                            labelColor = SecondaryText
                        ),
                        border = BorderStroke(1.dp, if (isAllSelected) AccentBlue else BorderStroke)
                    )
                }
                items(DeliveryStatus.values()) { status ->
                    val isSelected = statusFilter == status
                    val count = uiState.allOrders.count { it.status == status }
                    FilterChip(
                        selected = isSelected,
                        onClick = { statusFilter = status },
                        label = {
                            Text(
                                text = "${status.title} ($count)",
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) PrimaryText else SecondaryText
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentBlue,
                            selectedLabelColor = PrimaryText,
                            containerColor = CardSurfaceVariant,
                            labelColor = SecondaryText
                        ),
                        border = BorderStroke(1.dp, if (isSelected) AccentBlue else BorderStroke)
                    )
                }
            }

            // Orders List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 4.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredOrders, key = { it.id }) { order ->
                    ModernBusinessOrderCard(
                        order = order,
                        onUpdateStatus = { nextStatus ->
                            viewModel.updateBusinessOrderStatus(order.id, nextStatus)
                        },
                        onOpenAssignDriver = {
                            orderToAssignDriver = order
                        },
                        onRateOrder = {
                            orderToRate = order
                        }
                    )
                }
            }
        } else {
            // Driver Directory Tab
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 4.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(SampleDeliveryData.drivers) { driver ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSurface),
                        border = BorderStroke(1.dp, BorderStroke),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(CardSurfaceVariant)
                                        .border(1.dp, BorderStroke, CircleShape)
                                ) {
                                    Text(driver.initials, fontWeight = FontWeight.Bold, color = PrimaryText)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(driver.name, fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("${driver.vehicleType.title} • Plate: ${driver.plateNumber}", color = SecondaryText, fontSize = 11.sp)
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("${driver.rating}", fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Working "Assign Driver" Modal BottomSheet
    orderToAssignDriver?.let { targetOrder ->
        ModalBottomSheet(
            onDismissRequest = { orderToAssignDriver = null },
            sheetState = rememberModalBottomSheetState(),
            containerColor = CardSurface,
            contentColor = PrimaryText
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Assign Driver to #${targetOrder.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryText
                )
                Text(
                    text = "Choose an active courier in Guelmim",
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText
                )

                Spacer(modifier = Modifier.height(16.dp))

                SampleDeliveryData.drivers.forEach { driver ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = CardSurfaceVariant,
                        border = BorderStroke(1.dp, BorderStroke),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                viewModel.assignDriver(targetOrder.id, driver)
                                orderToAssignDriver = null
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(AccentBlue.copy(alpha = 0.2f))
                                        .border(1.dp, AccentBlue, CircleShape)
                                ) {
                                    Text(driver.initials, fontWeight = FontWeight.Bold, color = PrimaryText)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(driver.name, fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 13.sp)
                                    Text("${driver.vehicleType.title} • ${driver.plateNumber}", color = SecondaryText, fontSize = 11.sp)
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("${driver.rating}", fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Working "Rate Driver" 5-Star Interactive Dialog
    orderToRate?.let { targetOrder ->
        var ratingStars by remember { mutableIntStateOf(5) }
        AlertDialog(
            onDismissRequest = { orderToRate = null },
            containerColor = CardSurface,
            title = {
                Text(
                    text = "Rate Courier for #${targetOrder.id}",
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "How was the delivery by ${targetOrder.courierName}?",
                        color = SecondaryText,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        (1..5).forEach { star ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "$star Stars",
                                tint = if (star <= ratingStars) AccentAmber else BorderStroke,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable { ratingStars = star }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$ratingStars of 5 Stars",
                        fontWeight = FontWeight.Bold,
                        color = AccentAmber,
                        fontSize = 14.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitDriverRating(targetOrder.id, ratingStars)
                        orderToRate = null
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentAmber)
                ) {
                    Text("Submit Rating", color = CardSurface, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { orderToRate = null }) {
                    Text("Cancel", color = SecondaryText)
                }
            }
        )
    }
}

/**
 * Modern High-Contrast Order Card for Business Management
 */
@Composable
private fun ModernBusinessOrderCard(
    order: DeliveryOrder,
    onUpdateStatus: (DeliveryStatus) -> Unit,
    onOpenAssignDriver: () -> Unit,
    onRateOrder: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, BorderStroke),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("business_order_${order.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Order ID, Timestamp & Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = order.id,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${order.createdAt}",
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryText
                    )
                }

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = when (order.status) {
                        DeliveryStatus.PLACED -> AccentAmber.copy(alpha = 0.18f)
                        DeliveryStatus.PREPARING -> AccentBlue.copy(alpha = 0.18f)
                        DeliveryStatus.PICKED_UP, DeliveryStatus.NEARBY -> AccentBlue.copy(alpha = 0.25f)
                        DeliveryStatus.DELIVERED -> AccentEmerald.copy(alpha = 0.18f)
                    },
                    border = BorderStroke(
                        1.dp,
                        when (order.status) {
                            DeliveryStatus.PLACED -> AccentAmber
                            DeliveryStatus.PREPARING, DeliveryStatus.PICKED_UP, DeliveryStatus.NEARBY -> AccentBlue
                            DeliveryStatus.DELIVERED -> AccentEmerald
                        }
                    )
                ) {
                    Text(
                        text = order.status.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = when (order.status) {
                            DeliveryStatus.PLACED -> AccentAmber
                            DeliveryStatus.PREPARING, DeliveryStatus.PICKED_UP, DeliveryStatus.NEARBY -> AccentBlue
                            DeliveryStatus.DELIVERED -> AccentEmerald
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Customer & Item details
            Text(
                text = "${order.customerName} → ${order.dropoffLocation.name} (${order.dropoffLocation.district})",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryText
            )
            Spacer(modifier = Modifier.height(2.dp))
            // Explicitly set item description to SecondaryText (#9CA3AF)
            Text(
                text = order.itemsSummary,
                style = MaterialTheme.typography.bodySmall,
                color = SecondaryText
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtotal & Assigned Driver info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Explicitly set price to PrimaryText with Bold
                Text(
                    text = "${(order.subtotalMAD + order.deliveryFeeMAD).toInt()} DH (MAD)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when (order.vehicleType) {
                            VehicleType.MOTORCYCLE -> Icons.Default.TwoWheeler
                            VehicleType.ESCOOTER -> Icons.Default.ElectricScooter
                            VehicleType.CAR -> Icons.Default.DirectionsCar
                            VehicleType.BICYCLE -> Icons.Default.DirectionsBike
                        },
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = order.vehicleType.highlightColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    val isUnassigned = order.courierName.isBlank() || order.courierName.equals("Unassigned", ignoreCase = true)
                    Text(
                        text = if (isUnassigned) "⚠️ No Driver" else "${order.courierName} (${order.courierPlate})",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isUnassigned) AccentAmber else SecondaryText
                    )
                }
            }

            // Customer Review banner if already rated
            order.review?.let { rev ->
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CardSurfaceVariant,
                    border = BorderStroke(1.dp, BorderStroke),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = AccentAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${rev.rating}★ Review: \"${rev.comment}\"",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = PrimaryText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons based on state
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Assign Driver Button
                OutlinedButton(
                    onClick = onOpenAssignDriver,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, AccentBlue),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_assign_driver_${order.id}")
                ) {
                    Icon(Icons.Default.AssignmentInd, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Assign Driver", fontSize = 11.5.sp, color = AccentBlue, fontWeight = FontWeight.Bold)
                }

                // Progress Status Transitions
                when (order.status) {
                    DeliveryStatus.PLACED -> {
                        Button(
                            onClick = { onUpdateStatus(DeliveryStatus.PREPARING) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_accept_order_${order.id}")
                        ) {
                            Text("Start Preparing", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                        }
                    }
                    DeliveryStatus.PREPARING -> {
                        Button(
                            onClick = { onUpdateStatus(DeliveryStatus.PICKED_UP) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_out_for_delivery_${order.id}")
                        ) {
                            Text("Out for Delivery", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                        }
                    }
                    DeliveryStatus.PICKED_UP, DeliveryStatus.NEARBY -> {
                        Button(
                            onClick = { onUpdateStatus(DeliveryStatus.DELIVERED) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_mark_delivered_${order.id}")
                        ) {
                            Text("Mark Delivered", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                        }
                    }
                    DeliveryStatus.DELIVERED -> {
                        if (order.review == null) {
                            FilledTonalButton(
                                onClick = onRateOrder,
                                shape = RoundedCornerShape(12.dp),
                                colors = androidx.compose.material3.ButtonDefaults.filledTonalButtonColors(
                                    containerColor = CardSurfaceVariant,
                                    contentColor = AccentAmber
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_rate_driver_${order.id}")
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Rate Driver", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = AccentAmber)
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = AccentEmerald.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, AccentEmerald),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AccentEmerald, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Order Complete", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentEmerald)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
