package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VehicleType
import com.example.ui.theme.ModernBlack
import com.example.ui.theme.ModernBorder
import com.example.ui.theme.ModernDarkGray
import com.example.ui.theme.ModernEmerald
import com.example.ui.theme.ModernMuted
import com.example.ui.theme.ModernSurfaceSubtle
import com.example.ui.theme.ModernWhite

/**
 * Modern Segmented Vehicle Mode Switcher (OXXO & Zevoa Super App style)
 * Fixes:
 * - High-contrast white text & icons for selected active card
 * - Unified muted container styling for inactive cards
 * - Proper edge padding (16dp) preventing horizontal clipping
 * - Clean baseline alignment & line-height for sub-labels
 * - Clean LTR grouping and start-aligned secondary Arabic title
 */
@Composable
fun VehicleSelectorRow(
    selectedVehicle: VehicleType,
    onVehicleSelected: (VehicleType) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    Column(modifier = modifier.fillMaxWidth()) {
        // Bilingual Header: English primary + dynamic pill, Arabic sub-label cleanly start-aligned
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
                    color = if (isDark) ModernWhite else ModernBlack,
                    letterSpacing = 0.2.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isDark) ModernWhite else ModernBlack
                ) {
                    Text(
                        text = "DYNAMIC",
                        color = if (isDark) ModernBlack else Color.White,
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
                color = ModernMuted,
                fontSize = 11.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 4 Segmented Vehicle Cards with 16dp edge-safe layout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            VehicleType.values().forEach { vehicle ->
                val isSelected = vehicle == selectedVehicle

                // Inactive cards: Muted subtle dark container in dark mode, light clean subtle in light mode
                val inactiveBg = if (isDark) Color(0xFF1E293B) else ModernSurfaceSubtle
                val activeBg = if (isDark) Color(0xFF0F172A) else ModernBlack

                val cardBg by animateColorAsState(
                    targetValue = if (isSelected) activeBg else inactiveBg,
                    label = "cardBg"
                )
                val borderColor by animateColorAsState(
                    targetValue = if (isSelected) vehicle.highlightColor else if (isDark) Color(0xFF334155) else ModernBorder,
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
                        // Vehicle Icon Circle - High contrast white icon on selected state
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) {
                                        vehicle.highlightColor.copy(alpha = 0.25f)
                                    } else {
                                        if (isDark) Color(0xFF0F172A) else ModernWhite
                                    }
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) vehicle.highlightColor else Color.Transparent,
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
                                tint = if (isSelected) Color.White else (if (isDark) ModernWhite else ModernDarkGray),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Vehicle Short Title - High-contrast white when selected
                        Text(
                            text = when (vehicle) {
                                VehicleType.MOTORCYCLE -> "Moto"
                                VehicleType.ESCOOTER -> "E-Scoot"
                                VehicleType.CAR -> "Car"
                                VehicleType.BICYCLE -> "Bike"
                            },
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isSelected) Color.White else (if (isDark) ModernWhite else ModernBlack),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        // Fare Badge - High-contrast white/accent when selected
                        Text(
                            text = "${vehicle.baseFareMAD} DH",
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            color = if (isSelected) Color.White else ModernMuted,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Micro Metric (Speed / Eco) - Baseline aligned without colliding
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Eco,
                                contentDescription = null,
                                tint = if (isSelected) ModernEmerald else (if (vehicle.ecoScore >= 4) ModernEmerald else ModernMuted.copy(alpha = 0.6f)),
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${vehicle.speedKmH}k",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) Color.White.copy(alpha = 0.85f) else ModernMuted,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Selected Vehicle Info Strip with clear contrast and high readability
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isDark) Color(0xFF1E293B) else ModernSurfaceSubtle,
            border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else ModernBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(selectedVehicle.highlightColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${selectedVehicle.title}: ${selectedVehicle.tagLine}",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = if (isDark) ModernWhite else ModernBlack,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
