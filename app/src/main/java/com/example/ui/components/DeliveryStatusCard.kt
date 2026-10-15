package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DeliveryOrder
import com.example.model.DeliveryStatus
import com.example.ui.theme.ModernAmber
import com.example.ui.theme.ModernBlack
import com.example.ui.theme.ModernBorder
import com.example.ui.theme.ModernDarkGray
import com.example.ui.theme.ModernElectricBlue
import com.example.ui.theme.ModernEmerald
import com.example.ui.theme.ModernMuted
import com.example.ui.theme.ModernSurfaceSubtle
import com.example.ui.theme.ModernWhite

/**
 * Modern High-Fidelity Delivery Status Card (OXXO Retail & Zevoa Super App style)
 * Fixes:
 * - Total step progress tracker refactoring (centered nodes, 24/20/12dp scale, connecting lines through center, uniform label baselines)
 * - Start-aligned bilingual turn-by-turn instruction card
 * - Dual-ring framed circular avatar container
 * - Uncluttered rating & plate number badge with secondary dividers
 * - Mathematically centered chat & call icon buttons
 * - Generous vertical padding and comfortable height for the Live Glide bar
 */
@Composable
fun DeliveryStatusCard(
    order: DeliveryOrder,
    isSimulating: Boolean,
    simMultiplier: Float,
    onToggleSimulation: () -> Unit,
    onSetMultiplier: (Float) -> Unit,
    onResetSimulation: () -> Unit,
    onOpenChat: () -> Unit,
    onCallCourier: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    GlassmorphicCard(
        shape = RoundedCornerShape(26.dp),
        blurRadius = 18.dp,
        elevation = 8.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header: Status title + Big bold ETA Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .clip(CircleShape)
                                .background(
                                    if (order.status == DeliveryStatus.DELIVERED) ModernEmerald else ModernElectricBlue
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = order.status.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isDark) ModernWhite else ModernBlack
                        )
                    }
                    Text(
                        text = order.status.arabicTitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = ModernMuted,
                        fontSize = 12.sp
                    )
                }

                // ETA Big Black Capsule
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = ModernBlack,
                    modifier = Modifier.testTag("eta_pill")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (order.status == DeliveryStatus.DELIVERED) "ARRIVED" else "${order.etaMinutes} MIN",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${order.remainingDistanceMeters}m",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Step Progress Bar (Evenly distributed flex/row component)
            ModernDeliveryStepProgress(currentStatus = order.status)

            Spacer(modifier = Modifier.height(18.dp))

            // Turn-By-Turn Navigation Instruction banner with Start-aligned bilingual text
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isDark) Color(0xFF1E293B) else ModernSurfaceSubtle,
                border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else ModernBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ModernBlack)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Navigation,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = order.currentStep.instruction,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) ModernWhite else ModernBlack,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = order.currentStep.arabicInstruction,
                            style = MaterialTheme.typography.bodySmall,
                            color = ModernMuted,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Courier Profile & Contact Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Styled Dual-Ring Framed Circular Avatar Container
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color(0xFF1E293B) else ModernSurfaceSubtle)
                            .border(1.5.dp, if (isDark) Color(0xFF334155) else ModernBorder, CircleShape)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(ModernBlack)
                        ) {
                            Text(
                                text = order.courierName.take(2).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = order.courierName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) ModernWhite else ModernBlack
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        // Uncluttered Rating & Plate badges with secondary dividers
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = ModernAmber,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${order.courierRating}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isDark) ModernWhite else ModernBlack
                                )
                            }
                            Text(
                                text = "•",
                                color = ModernMuted,
                                fontSize = 11.sp
                            )
                            // Plate badge pill
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isDark) Color(0xFF1E293B) else ModernSurfaceSubtle,
                                border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else ModernBorder)
                            ) {
                                Text(
                                    text = order.courierPlate,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ModernMuted,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                // Call & Chat buttons with mathematically centered icons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Chat button
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color(0xFF1E293B) else ModernSurfaceSubtle)
                            .border(1.dp, if (isDark) Color(0xFF334155) else ModernBorder, CircleShape)
                            .clickable { onOpenChat() }
                            .testTag("btn_courier_chat")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "Chat",
                            tint = if (isDark) ModernWhite else ModernBlack,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Call button
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(ModernBlack)
                            .clickable { onCallCourier() }
                            .testTag("btn_courier_call")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Call",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Live Simulation Control Console (Live Glide Bar with comfortable padding)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isDark) Color(0xFF1E293B) else ModernSurfaceSubtle,
                border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else ModernBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 9.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Play / Pause + Label
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onToggleSimulation() }
                            .testTag("btn_play_pause_sim")
                            .padding(end = 6.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(ModernBlack)
                        ) {
                            Icon(
                                imageVector = if (isSimulating) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isSimulating) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isSimulating) "Live Glide" else "Paused",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isDark) ModernWhite else ModernBlack
                            )
                            Text(
                                text = if (isSimulating) "Tap to pause" else "Tap to glide",
                                fontSize = 10.sp,
                                color = ModernMuted
                            )
                        }
                    }

                    // Multiplier Pills: 1x, 2x, 5x + Reset
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(1.0f, 2.0f, 5.0f).forEach { mult ->
                            val isSelected = simMultiplier == mult
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) ModernBlack else (if (isDark) Color(0xFF0F172A) else ModernWhite),
                                border = BorderStroke(1.dp, if (isSelected) ModernBlack else ModernBorder),
                                modifier = Modifier
                                    .clickable { onSetMultiplier(mult) }
                            ) {
                                Text(
                                    text = "${mult.toInt()}x",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else (if (isDark) ModernWhite else ModernBlack),
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                                )
                            }
                        }

                        // Reset Route button
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(if (isDark) Color(0xFF0F172A) else ModernWhite)
                                .border(1.dp, if (isDark) Color(0xFF334155) else ModernBorder, CircleShape)
                                .clickable { onResetSimulation() }
                                .testTag("btn_reset_simulation")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Restart Route",
                                tint = if (isDark) ModernWhite else ModernBlack,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Modern Step Progress Indicator
 * Evenly distributed flex/row component:
 * - Step nodes centered vertically on a 28dp high row
 * - Connecting lines running strictly through node centers
 * - Coherent design token scale:
 *     - Completed = 24dp (Emerald + Checkmark)
 *     - Current   = 20dp (Black + Emerald Ring + White Core)
 *     - Pending   = 12dp (Neutral Muted Circle)
 * - Text labels centered directly underneath each corresponding node without overlapping
 */
@Composable
private fun ModernDeliveryStepProgress(currentStatus: DeliveryStatus) {
    val steps = listOf(
        DeliveryStatus.PLACED to "Placed",
        DeliveryStatus.PREPARING to "Kitchen",
        DeliveryStatus.PICKED_UP to "En Route",
        DeliveryStatus.NEARBY to "Nearby",
        DeliveryStatus.DELIVERED to "Arrived"
    )

    val currentIdx = when (currentStatus) {
        DeliveryStatus.PLACED -> 0
        DeliveryStatus.PREPARING -> 1
        DeliveryStatus.PICKED_UP -> 2
        DeliveryStatus.NEARBY -> 3
        DeliveryStatus.DELIVERED -> 4
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Row 1: Centered Nodes and Horizontal Connecting Lines
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            steps.forEachIndexed { index, (status, _) ->
                val isCompleted = index < currentIdx || (currentStatus == DeliveryStatus.DELIVERED && index == currentIdx)
                val isCurrent = index == currentIdx && currentStatus != DeliveryStatus.DELIVERED

                // Each node slot is centered in a 28dp x 28dp container so lines run through exact center
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(28.dp)
                ) {
                    when {
                        isCompleted -> {
                            // Completed node: 24dp Emerald with white checkmark
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(ModernEmerald)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                        isCurrent -> {
                            // Current node: 20dp Black with emerald ring and white core
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(ModernBlack)
                                    .border(2.dp, ModernEmerald, CircleShape)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                            }
                        }
                        else -> {
                            // Pending node: 12dp subtle neutral circle
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(ModernBorder)
                            )
                        }
                    }
                }

                // Connecting line running strictly through center of nodes
                if (index < steps.size - 1) {
                    val lineCompleted = index < currentIdx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.5.dp)
                            .background(if (lineCompleted) ModernEmerald else ModernBorder)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Row 2: Fixed-width/distributed text labels with uniform baseline
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            steps.forEachIndexed { index, (_, label) ->
                val isCurrent = index == currentIdx
                val isCompleted = index <= currentIdx
                Text(
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = if (isCurrent) FontWeight.ExtraBold else if (isCompleted) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isCurrent) ModernBlack else if (isCompleted) ModernDarkGray else ModernMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
