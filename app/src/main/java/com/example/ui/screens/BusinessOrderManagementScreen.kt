package com.example.ui.screens

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
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
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
import com.example.model.DriverProfile
import com.example.model.DriverReview
import com.example.model.Store
import com.example.model.VehicleType
import com.example.ui.theme.OasisGreen
import com.example.ui.theme.SaharaAmber
import com.example.ui.theme.TerracottaPrimary
import com.example.viewmodel.DeliveryUiState
import com.example.viewmodel.DeliveryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessOrderManagementScreen(
    uiState: DeliveryUiState,
    viewModel: DeliveryViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Orders Queue, 1: Driver Performance & Reviews
    var statusFilter by remember { mutableStateOf<DeliveryStatus?>(null) }
    var orderToAssignDriver by remember { mutableStateOf<DeliveryOrder?>(null) }

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
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Business Portal Title & Store Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Guelmim Business Portal",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "بوابة إدارة الطلبات والسائقين • كلميم",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = OasisGreen.copy(alpha = 0.15f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(OasisGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Merchant Live",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = OasisGreen
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Business Store Selection Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(GuelmimMapData.sampleStores) { store ->
                val isSelected = uiState.selectedBusinessStore.id == store.id
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.selectBusinessStore(store) },
                    label = { Text(store.name, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TerracottaPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Real-Time Notification Banner for Driver Assignment
        uiState.driverNotificationBanner?.let { banner ->
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = TerracottaPrimary.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerracottaPrimary.copy(alpha = 0.4f)),
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
                            tint = TerracottaPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = banner,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = TerracottaPrimary
                        )
                    }
                    IconButton(
                        onClick = { viewModel.dismissDriverNotification() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Primary Tab Row (Orders Queue vs Driver Performance & Reviews)
        PrimaryTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Orders Queue (${uiState.allOrders.size})", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Drivers & Reviews (${uiState.reviews.size})", fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedTab == 0) {
            // --- Tab 1: Orders Management Queue ---
            // Status Filter Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                item {
                    FilterChip(
                        selected = statusFilter == null,
                        onClick = { statusFilter = null },
                        label = { Text("All (${uiState.allOrders.size})", fontSize = 11.5.sp) }
                    )
                }
                items(DeliveryStatus.values()) { status ->
                    val count = uiState.allOrders.count { it.status == status }
                    FilterChip(
                        selected = statusFilter == status,
                        onClick = { statusFilter = status },
                        label = { Text("${status.title} ($count)", fontSize = 11.5.sp) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredOrders, key = { it.id }) { order ->
                    BusinessOrderCard(
                        order = order,
                        onUpdateStatus = { newStatus ->
                            viewModel.updateBusinessOrderStatus(order.id, newStatus)
                        },
                        onOpenAssignDriver = {
                            orderToAssignDriver = order
                        },
                        onRateOrder = {
                            viewModel.openRatingDialog(order)
                        }
                    )
                }
            }
        } else {
            // --- Tab 2: Driver Performance Metrics & Customer Reviews ---
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Section 1: Aggregated Driver Performance
                item {
                    Text(
                        text = "Courier Fleet Performance (كلميم)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Driver ratings, availability, and active vehicles",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                items(uiState.availableDrivers) { driver ->
                    DriverPerformanceCard(driver = driver)
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Customer Feedback & Reviews",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Verified reviews submitted by Guelmim customers",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                items(uiState.reviews) { review ->
                    ReviewItemCard(review = review)
                }
            }
        }
    }

    // Assign Driver Bottom Sheet Modal
    orderToAssignDriver?.let { targetOrder ->
        AssignDriverBottomSheet(
            order = targetOrder,
            availableDrivers = uiState.availableDrivers,
            onAssignDriver = { driverId ->
                viewModel.assignDriverToOrder(targetOrder.id, driverId)
                orderToAssignDriver = null
            },
            onDismiss = { orderToAssignDriver = null }
        )
    }
}

@Composable
private fun BusinessOrderCard(
    order: DeliveryOrder,
    onUpdateStatus: (DeliveryStatus) -> Unit,
    onOpenAssignDriver: () -> Unit,
    onRateOrder: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(3.dp),
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
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${order.createdAt}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = when (order.status) {
                        DeliveryStatus.PLACED -> SaharaAmber.copy(alpha = 0.15f)
                        DeliveryStatus.PREPARING -> Color(0xFF1976D2).copy(alpha = 0.15f)
                        DeliveryStatus.PICKED_UP, DeliveryStatus.NEARBY -> TerracottaPrimary.copy(alpha = 0.15f)
                        DeliveryStatus.DELIVERED -> OasisGreen.copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = order.status.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = when (order.status) {
                            DeliveryStatus.PLACED -> SaharaAmber
                            DeliveryStatus.PREPARING -> Color(0xFF1976D2)
                            DeliveryStatus.PICKED_UP, DeliveryStatus.NEARBY -> TerracottaPrimary
                            DeliveryStatus.DELIVERED -> OasisGreen
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
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = order.itemsSummary,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Subtotal & Assigned Driver info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${(order.subtotalMAD + order.deliveryFeeMAD).toInt()} DH (MAD)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TerracottaPrimary
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
                    Text(
                        text = if (order.courierName == "Unassigned") "⚠️ No Driver Assigned" else "${order.courierName} (${order.courierPlate})",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (order.courierName == "Unassigned") SaharaAmber else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Customer Review banner if already rated
            order.review?.let { rev ->
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = SaharaAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${rev.rating}★ Review: \"${rev.comment}\"",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
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
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_assign_driver_${order.id}")
                ) {
                    Icon(Icons.Default.AssignmentInd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Assign Driver", fontSize = 11.5.sp)
                }

                // Progress Status Transitions
                when (order.status) {
                    DeliveryStatus.PLACED -> {
                        Button(
                            onClick = { onUpdateStatus(DeliveryStatus.PREPARING) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_accept_order_${order.id}")
                        ) {
                            Text("Start Preparing", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    DeliveryStatus.PREPARING -> {
                        Button(
                            onClick = { onUpdateStatus(DeliveryStatus.PICKED_UP) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_out_for_delivery_${order.id}")
                        ) {
                            Text("Out for Delivery", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    DeliveryStatus.PICKED_UP, DeliveryStatus.NEARBY -> {
                        Button(
                            onClick = { onUpdateStatus(DeliveryStatus.DELIVERED) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OasisGreen),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_mark_delivered_${order.id}")
                        ) {
                            Text("Mark Delivered", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    DeliveryStatus.DELIVERED -> {
                        if (order.review == null) {
                            FilledTonalButton(
                                onClick = onRateOrder,
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_rate_driver_${order.id}")
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = SaharaAmber, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Rate Driver", fontSize = 11.5.sp)
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = OasisGreen.copy(alpha = 0.12f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = OasisGreen, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Order Complete", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OasisGreen)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DriverPerformanceCard(driver: DriverProfile) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
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
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(SaharaAmber)
                ) {
                    Text(
                        text = driver.name.take(2).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = driver.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${driver.vehicleType.title} • Plate ${driver.vehiclePlate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "Current: ${driver.currentNeighborhood}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = SaharaAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${driver.rating}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "${driver.totalDeliveries} deliveries",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (driver.isAvailable) OasisGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = if (driver.isAvailable) "Idle / Available" else "On Delivery",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (driver.isAvailable) OasisGreen else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewItemCard(review: DriverReview) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = review.customerName,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "For courier: ${review.driverName} • Order #${review.orderId}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                // Stars
                Row {
                    repeat(review.rating) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = SaharaAmber,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "\"${review.comment}\"",
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Compliment Tags
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                review.tags.forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = TerracottaPrimary.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = tag,
                            fontSize = 10.sp,
                            color = TerracottaPrimary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AssignDriverBottomSheet(
    order: DeliveryOrder,
    availableDrivers: List<DriverProfile>,
    onAssignDriver: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Assign Driver to #${order.id}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Select active courier across Guelmim network",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            availableDrivers.forEach { driver ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onAssignDriver(driver.id) }
                        .testTag("driver_pick_${driver.id}")
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
                                    .background(SaharaAmber)
                            ) {
                                Text(
                                    text = driver.name.take(2).uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = driver.name,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${driver.vehicleType.title} • ${driver.vehiclePlate}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }

                        Button(
                            onClick = { onAssignDriver(driver.id) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("Assign & Notify", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
