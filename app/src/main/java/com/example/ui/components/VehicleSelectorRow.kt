package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VehicleType
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.BorderStroke
import com.example.ui.theme.CardSurfaceVariant
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText

/**
 * Modern Segmented Vehicle Mode Switcher
 * - 4 cards: Moto, E-Scoot, Car, Bike
 * - Selected card: 2.dp AccentAmber border, tinted icon circle, PrimaryText title & price
 * - Unselected cards: CardSurfaceVariant with BorderStroke, PrimaryText title & SecondaryText price
 * - Proper edge padding (16dp) preventing horizontal clipping
 */
@Composable
fun VehicleSelectorRow(
    selectedVehicle: VehicleType,
    onVehicleSelected: (VehicleType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Bilingual Header: English primary + dynamic pill, Arabic sub-label start-aligned
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Delivery Vehicle",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryText,
                    letterSpacing = 0.2.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CardSurfaceVariant,
                    border = BorderStroke(1.dp, BorderStroke)
                ) {
                    Text(
                        text = "DYNAMIC",
                        color = AccentAmber,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "وسيلة التوصيل المعتمدة",
                style = MaterialTheme.typography.bodySmall,
                color = SecondaryText,
                fontSize = 11.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 4 Segmented Vehicle Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            VehicleType.values().forEach { vehicle ->
                val isSelected = vehicle == selectedVehicle

                val cardBg by animateColorAsState(
                    targetValue = if (isSelected) CardSurfaceVariant else CardSurfaceVariant.copy(alpha = 0.65f),
                    label = "cardBg"
                )
                val borderColor by animateColorAsState(
                    targetValue = if (isSelected) AccentAmber else BorderStroke,
                    label = "borderColor"
                )

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = cardBg,
                    border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
                    shadowElevation = if (isSelected) 8.dp else 0.dp,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onVehicleSelected(vehicle) }
                        .testTag("vehicle_option_${vehicle.name.lowercase()}")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Vehicle Icon Circle
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) {
                                        AccentAmber.copy(alpha = 0.20f)
                                    } else {
                                        CardSurfaceVariant
                                    }
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) AccentAmber else BorderStroke,
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = when (vehicle) {
                                    VehicleType.MOTORCYCLE -> Icons.Default.TwoWheeler
                                    VehicleType.ESCOOTER -> Icons.Default.ElectricScooter
                                    VehicleType.CAR -> Icons.Default.DirectionsCar
                                    VehicleType.BICYCLE -> Icons.Default.DirectionsBike
                                },
                                contentDescription = vehicle.title,
                                tint = if (isSelected) PrimaryText else SecondaryText,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Vehicle Short Title
                        Text(
                            text = when (vehicle) {
                                VehicleType.MOTORCYCLE -> "Moto"
                                VehicleType.ESCOOTER -> "E-Scoot"
                                VehicleType.CAR -> "Car"
                                VehicleType.BICYCLE -> "Bike"
                            },
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                            fontSize = 12.sp,
                            color = PrimaryText,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        // Fare Badge
                        Text(
                            text = "${vehicle.baseFareMAD} DH",
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            color = if (isSelected) AccentAmber else SecondaryText,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Speed / Eco Metric
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Eco,
                                contentDescription = null,
                                tint = if (isSelected) AccentEmerald else (if (vehicle.ecoScore >= 4) AccentEmerald else SecondaryText.copy(alpha = 0.6f)),
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${vehicle.speedKmH}k",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) PrimaryText else SecondaryText,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
