package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Inventory2
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
import com.example.data.GuelmimMapData
import com.example.model.GuelmimLocation
import com.example.model.VehicleType
import com.example.ui.components.VehicleSelectorRow
import com.example.ui.theme.TerracottaPrimary
import com.example.viewmodel.DeliveryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DispatchOrderScreen(
    viewModel: DeliveryViewModel,
    modifier: Modifier = Modifier
) {
    var selectedPickup by remember { mutableStateOf(GuelmimMapData.BIR_ANZARANE) }
    var selectedDropoff by remember { mutableStateOf(GuelmimMapData.HAY_OUED_NOUN) }
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

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        item {
            Column {
                Text(
                    text = "Dispatch Guelmim Courier",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "إرسال طرد فوري داخل كلميم وضواحيها",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        // Pickup Location Selector
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(TerracottaPrimary)
                        ) {
                            Text("A", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pickup Location / نقطة الاستلام",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
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
                            shape = RoundedCornerShape(12.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = isPickupExpanded,
                            onDismissRequest = { isPickupExpanded = false }
                        ) {
                            GuelmimMapData.allLocations.forEach { loc ->
                                DropdownMenuItem(
                                    text = { Text("${loc.name} • ${loc.arabicName}") },
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

        // Dropoff Location Selector
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0D9488))
                        ) {
                            Text("B", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Dropoff Destination / عنوان التسليم",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
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
                            shape = RoundedCornerShape(12.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = isDropoffExpanded,
                            onDismissRequest = { isDropoffExpanded = false }
                        ) {
                            GuelmimMapData.allLocations.forEach { loc ->
                                DropdownMenuItem(
                                    text = { Text("${loc.name} • ${loc.arabicName}") },
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

        // Package Description Input
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Package Description / وصف الطرد",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = packageDescription,
                        onValueChange = { packageDescription = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("package_description_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            }
        }

        // Vehicle Mode Selector
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(3.dp),
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

        // Summary & Dispatch Button
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Estimated Distance:", color = MaterialTheme.colorScheme.outline)
                        Text("$estimatedDistMeters meters", fontWeight = FontWeight.SemiBold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Estimated Travel Time:", color = MaterialTheme.colorScheme.outline)
                        Text("~$estimatedTimeMin minutes", fontWeight = FontWeight.SemiBold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Delivery Fee (${selectedVehicle.title}):", color = MaterialTheme.colorScheme.outline)
                        Text("$totalFareMAD DH", fontWeight = FontWeight.Bold, color = TerracottaPrimary)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            viewModel.dispatchNewOrder(
                                pickup = selectedPickup,
                                dropoff = selectedDropoff,
                                vehicle = selectedVehicle,
                                itemsDescription = packageDescription,
                                subtotalMAD = totalFareMAD.toDouble()
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_dispatch_now")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Dispatch Delivery Now ($totalFareMAD DH)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}
