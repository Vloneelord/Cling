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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DeliveryStatus
import com.example.model.SampleDeliveryData
import com.example.model.UserRole
import com.example.ui.components.GuelmimCanvasMap
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AppBackground
import com.example.ui.theme.BorderStroke
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceVariant
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import com.example.viewmodel.AppScreen
import com.example.viewmodel.DeliveryUiState
import com.example.viewmodel.DeliveryViewModel

/**
 * Modern Sahara Night Cockpit Screen (Tab 4: Cockpit)
 * Comprehensive User & Driver Login / Role Switcher:
 * - Customer Mode: track orders, dispatch parcels, browse menus
 * - Driver Mode: toggle Online/Offline, view assigned order, tap "Advance Delivery Step", telemetry gauges
 * - Merchant Portal Mode: manage restaurant incoming orders & assign drivers
 */
@Composable
fun CockpitScreen(
    uiState: DeliveryUiState,
    viewModel: DeliveryViewModel,
    modifier: Modifier = Modifier
) {
    val currentRole = uiState.authState.role
    var isDriverOnline by remember { mutableStateOf(true) }
    var customUsername by remember { mutableStateOf(uiState.authState.currentUser) }
    val order = uiState.currentOrder

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Cockpit Header
        item {
            Column {
                Text(
                    text = "Guelmim Express Cockpit",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "تبديل الحساب • الزبون، السائق، ومدير المطعم",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText
                )
            }
        }

        // Active Profile Banner & Login Input
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = BorderStroke(1.dp, BorderStroke),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
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
                                Icon(
                                    imageVector = when (currentRole) {
                                        UserRole.CUSTOMER -> Icons.Default.Person
                                        UserRole.DRIVER -> Icons.Default.TwoWheeler
                                        UserRole.MERCHANT -> Icons.Default.Storefront
                                    },
                                    contentDescription = null,
                                    tint = when (currentRole) {
                                        UserRole.CUSTOMER -> AccentBlue
                                        UserRole.DRIVER -> AccentEmerald
                                        UserRole.MERCHANT -> AccentAmber
                                    },
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = uiState.authState.currentUser,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryText
                                )
                                Text(
                                    text = "Active Role: ${currentRole.name}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SecondaryText
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = when (currentRole) {
                                UserRole.CUSTOMER -> AccentBlue.copy(alpha = 0.2f)
                                UserRole.DRIVER -> AccentEmerald.copy(alpha = 0.2f)
                                UserRole.MERCHANT -> AccentAmber.copy(alpha = 0.2f)
                            },
                            border = BorderStroke(
                                1.dp,
                                when (currentRole) {
                                    UserRole.CUSTOMER -> AccentBlue
                                    UserRole.DRIVER -> AccentEmerald
                                    UserRole.MERCHANT -> AccentAmber
                                }
                            )
                        ) {
                            Text(
                                text = currentRole.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = when (currentRole) {
                                    UserRole.CUSTOMER -> AccentBlue
                                    UserRole.DRIVER -> AccentEmerald
                                    UserRole.MERCHANT -> AccentAmber
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Quick Switch Profile / تبديل سريع للهوية:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryText
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (currentRole == UserRole.CUSTOMER) AccentBlue else CardSurfaceVariant,
                            border = BorderStroke(1.dp, if (currentRole == UserRole.CUSTOMER) AccentBlue else BorderStroke),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    viewModel.login(UserRole.CUSTOMER, "Mohamed (Client Guelmim)")
                                }
                        ) {
                            Text(
                                text = "Customer",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (currentRole == UserRole.DRIVER) AccentEmerald else CardSurfaceVariant,
                            border = BorderStroke(1.dp, if (currentRole == UserRole.DRIVER) AccentEmerald else BorderStroke),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    viewModel.login(UserRole.DRIVER, "Yassine Livreur")
                                }
                        ) {
                            Text(
                                text = "Driver",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (currentRole == UserRole.MERCHANT) AccentAmber else CardSurfaceVariant,
                            border = BorderStroke(1.dp, if (currentRole == UserRole.MERCHANT) AccentAmber else BorderStroke),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    viewModel.login(UserRole.MERCHANT, "Bab Sahra Manager")
                                }
                        ) {
                            Text(
                                text = "Merchant",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentRole == UserRole.MERCHANT) CardSurface else PrimaryText,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Section Content Based on Selected Role
        when (currentRole) {
            UserRole.CUSTOMER -> {
                // Customer Dashboard Card
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSurface),
                        border = BorderStroke(1.dp, BorderStroke),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Customer Dashboard / لوحة الزبون",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "You have ${uiState.orders.size} active orders placed in Guelmim.",
                                style = MaterialTheme.typography.bodySmall,
                                color = SecondaryText
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Quick Navigation Actions
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.setScreen(AppScreen.TRACKING) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Navigation, contentDescription = null, tint = PrimaryText, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Track Order", color = PrimaryText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { viewModel.setScreen(AppScreen.EXPLORE_STORES) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = CardSurfaceVariant),
                                    border = BorderStroke(1.dp, BorderStroke),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Storefront, contentDescription = null, tint = PrimaryText, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Browse Shops", color = PrimaryText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            UserRole.DRIVER -> {
                // Driver GPS Online / Offline Switch
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSurface),
                        border = BorderStroke(1.dp, BorderStroke),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isDriverOnline) "Driver Status: ONLINE" else "Driver Status: OFFLINE",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDriverOnline) AccentEmerald else SecondaryText
                                )
                                Text(
                                    text = if (isDriverOnline) "Accepting delivery requests in Guelmim" else "You are not receiving orders",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SecondaryText
                                )
                            }
                            Switch(
                                checked = isDriverOnline,
                                onCheckedChange = { isDriverOnline = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = PrimaryText,
                                    checkedTrackColor = AccentEmerald,
                                    uncheckedThumbColor = SecondaryText,
                                    uncheckedTrackColor = CardSurfaceVariant
                                )
                            )
                        }
                    }
                }

                // Active Mission Control & Advance Status CTA
                item {
                    order?.let { ord ->
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = CardSurface),
                            border = BorderStroke(1.dp, AccentEmerald.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Active Delivery Mission • #${ord.id}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryText
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = AccentEmerald.copy(alpha = 0.2f),
                                        border = BorderStroke(1.dp, AccentEmerald)
                                    ) {
                                        Text(
                                            text = ord.status.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = AccentEmerald,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Destination: ${ord.dropoffLocation.name} (${ord.dropoffLocation.district})",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PrimaryText
                                )
                                Text(
                                    text = "Next step: ${ord.currentStep.instruction}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SecondaryText
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Tap "Advance Delivery Step"
                                Button(
                                    onClick = {
                                        viewModel.advanceOrderStatus(ord.id)
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("btn_driver_advance_step")
                                ) {
                                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = PrimaryText)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Advance Delivery Step (${ord.status.name} → Next)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryText
                                    )
                                }
                            }
                        }
                    }
                }

                // Driver Telemetry Gauges (Speedometer, ETA, Distance)
                item {
                    order?.let { ord ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = CardSurface,
                                border = BorderStroke(1.dp, BorderStroke),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.Speed, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(22.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("${ord.currentSpeedKmH.toInt()}", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryText)
                                    Text("km/h", fontSize = 11.sp, color = SecondaryText)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = CardSurface,
                                border = BorderStroke(1.dp, BorderStroke),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.Navigation, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(22.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("${ord.etaMinutes}", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryText)
                                    Text("min ETA", fontSize = 11.sp, color = SecondaryText)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = CardSurface,
                                border = BorderStroke(1.dp, BorderStroke),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AccentEmerald, modifier = Modifier.size(22.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("${ord.remainingDistanceMeters}", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryText)
                                    Text("meters left", fontSize = 11.sp, color = SecondaryText)
                                }
                            }
                        }
                    }
                }

                // Mini Navigation Map View
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, BorderStroke),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        GuelmimCanvasMap(
                            order = order,
                            isFollowCourier = true,
                            showTraffic = true,
                            selectedLocation = null,
                            onLocationClick = {},
                            onToggleFollowCourier = { viewModel.toggleFollowCourier() },
                            onToggleTraffic = { viewModel.toggleTrafficOverlay() }
                        )
                    }
                }
            }

            UserRole.MERCHANT -> {
                // Merchant Portal Card
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSurface),
                        border = BorderStroke(1.dp, BorderStroke),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Merchant Restaurant Portal",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Managing orders for Restaurant Bab Sahra & Pâtisserie Tighmert.",
                                style = MaterialTheme.typography.bodySmall,
                                color = SecondaryText
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { viewModel.setScreen(AppScreen.BUSINESS_PORTAL) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentAmber),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Storefront, contentDescription = null, tint = CardSurface)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Open Orders Queue & Dispatch", color = CardSurface, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
