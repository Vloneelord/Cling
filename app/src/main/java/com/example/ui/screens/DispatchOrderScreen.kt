package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GuelmimMapData
import com.example.model.GuelmimLocation
import com.example.model.VehicleType
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
import com.example.viewmodel.DeliveryViewModel

/**
 * Modern Sahara Night Dispatch Courier Screen (Tab 2: Send)
 * - Interactive Dropdowns for Pickups and Dropoffs
 * - OutlinedTextField with explicit PrimaryText and BorderStroke
 * - Integrated VehicleSelectorRow
 * - Sticky Bottom CTA: "Dispatch Courier • {Price} DH"
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DispatchOrderScreen(
    viewModel: DeliveryViewModel,
    modifier: Modifier = Modifier
) {
    // Curated Pickups & Dropoffs per prompt specification
    val pickupOptions = remember {
        listOf(
            GuelmimMapData.BIR_ANZARANE,
            GuelmimMapData.CENTRE_VILLE,
            GuelmimMapData.BAB_SAHRA,
            GuelmimMapData.OASIS_TIGHMERT
        )
    }

    val dropoffOptions = remember {
        listOf(
            GuelmimMapData.HAY_OUED_NOUN,
            GuelmimMapData.HAY_AL_QUDS,
            GuelmimMapData.HAY_AL_WAHDA,
            GuelmimMapData.ASRIR
        )
    }

    var selectedPickup by remember { mutableStateOf(pickupOptions[0]) }
    var selectedDropoff by remember { mutableStateOf(dropoffOptions[0]) }
    var selectedVehicle by remember { mutableStateOf(VehicleType.MOTORCYCLE) }
    var packageDescription by remember { mutableStateOf("Documents & Sahara Sweet Box") }

    var isPickupExpanded by remember { mutableStateOf(false) }
    var isDropoffExpanded by remember { mutableStateOf(false) }

    val estimatedDistMeters = remember(selectedPickup, selectedDropoff) {
        (selectedPickup.point.distanceTo(selectedDropoff.point) * 5.2f).toInt().coerceAtLeast(600)
    }

    val estimatedTimeMin = remember(estimatedDistMeters, selectedVehicle) {
        val speedMpm = selectedVehicle.speedKmH * 1000f / 60f
        (estimatedDistMeters / speedMpm).toInt().coerceAtLeast(3)
    }

    val totalFareMAD = selectedVehicle.baseFareMAD

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Column {
                    Text(
                        text = "Dispatch Guelmim Courier",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryText
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "إرسال طرد فوري داخل كلميم وضواحيها",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SecondaryText
                    )
                }
            }

            // Pickup Location Selector Dropdown
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    border = BorderStroke(1.dp, BorderStroke),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(AccentBlue)
                            ) {
                                Text("A", color = PrimaryText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Pickup Location / نقطة الاستلام",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        ExposedDropdownMenuBox(
                            expanded = isPickupExpanded,
                            onExpandedChange = { isPickupExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = "${selectedPickup.name} (${selectedPickup.arabicName})",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isPickupExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                                    .testTag("pickup_dropdown"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = PrimaryText,
                                    unfocusedTextColor = PrimaryText,
                                    focusedBorderColor = AccentBlue,
                                    unfocusedBorderColor = BorderStroke,
                                    focusedContainerColor = CardSurfaceVariant,
                                    unfocusedContainerColor = CardSurfaceVariant
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = isPickupExpanded,
                                onDismissRequest = { isPickupExpanded = false },
                                modifier = Modifier.background(CardSurface)
                            ) {
                                pickupOptions.forEach { loc ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "${loc.name} • ${loc.arabicName}",
                                                    color = PrimaryText,
                                                    fontWeight = if (loc.id == selectedPickup.id) FontWeight.Bold else FontWeight.Normal
                                                )
                                            }
                                        },
                                        onClick = {
                                            selectedPickup = loc
                                            isPickupExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Dropoff Location Selector Dropdown
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    border = BorderStroke(1.dp, BorderStroke),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(AccentEmerald)
                            ) {
                                Text("B", color = PrimaryText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Dropoff Destination / عنوان التسليم",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        ExposedDropdownMenuBox(
                            expanded = isDropoffExpanded,
                            onExpandedChange = { isDropoffExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = "${selectedDropoff.name} (${selectedDropoff.arabicName})",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropoffExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                                    .testTag("dropoff_dropdown"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = PrimaryText,
                                    unfocusedTextColor = PrimaryText,
                                    focusedBorderColor = AccentEmerald,
                                    unfocusedBorderColor = BorderStroke,
                                    focusedContainerColor = CardSurfaceVariant,
                                    unfocusedContainerColor = CardSurfaceVariant
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = isDropoffExpanded,
                                onDismissRequest = { isDropoffExpanded = false },
                                modifier = Modifier.background(CardSurface)
                            ) {
                                dropoffOptions.forEach { loc ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = AccentEmerald, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "${loc.name} • ${loc.arabicName}",
                                                    color = PrimaryText,
                                                    fontWeight = if (loc.id == selectedDropoff.id) FontWeight.Bold else FontWeight.Normal
                                                )
                                            }
                                        },
                                        onClick = {
                                            selectedDropoff = loc
                                            isDropoffExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Package Description Working OutlinedTextField
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    border = BorderStroke(1.dp, BorderStroke),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Package Description / وصف الطرد",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = packageDescription,
                            onValueChange = { packageDescription = it },
                            placeholder = { Text("e.g., Documents, Souk Spices, Keys...", color = SecondaryText) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("package_description_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = PrimaryText,
                                unfocusedTextColor = PrimaryText,
                                focusedBorderColor = AccentBlue,
                                unfocusedBorderColor = BorderStroke,
                                focusedContainerColor = CardSurfaceVariant,
                                unfocusedContainerColor = CardSurfaceVariant,
                                cursorColor = AccentBlue
                            )
                        )
                    }
                }
            }

            // Vehicle Mode Selector
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    border = BorderStroke(1.dp, BorderStroke),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        VehicleSelectorRow(
                            selectedVehicle = selectedVehicle,
                            onVehicleSelected = { selectedVehicle = it }
                        )
                    }
                }
            }

            // Route Estimates Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurfaceVariant),
                    border = BorderStroke(1.dp, BorderStroke),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Estimated Distance:", color = SecondaryText, fontSize = 13.sp)
                            Text("$estimatedDistMeters meters", fontWeight = FontWeight.SemiBold, color = PrimaryText, fontSize = 13.sp)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Estimated Travel Time:", color = SecondaryText, fontSize = 13.sp)
                            Text("~$estimatedTimeMin minutes", fontWeight = FontWeight.SemiBold, color = PrimaryText, fontSize = 13.sp)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Delivery Fare (${selectedVehicle.title}):", color = SecondaryText, fontSize = 13.sp)
                            Text("$totalFareMAD DH", fontWeight = FontWeight.Bold, color = AccentAmber, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // Sticky Bottom CTA Button: "Dispatch Courier • {Price} DH"
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = CardSurface,
            border = BorderStroke(1.dp, BorderStroke),
            shadowElevation = 16.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.dispatchCourierOrder(
                            pickup = selectedPickup.name,
                            dropoff = selectedDropoff.name,
                            packageDesc = packageDescription,
                            vehicle = selectedVehicle
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_dispatch_courier_sticky")
                ) {
                    Icon(Icons.Default.LocalShipping, contentDescription = null, tint = PrimaryText)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Dispatch Courier • $totalFareMAD DH",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = PrimaryText
                    )
                }
            }
        }
    }
}
