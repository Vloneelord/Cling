package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.BorderStroke
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceVariant
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText

/**
 * Modern High-Fidelity Delivery Status Card
 * - Explicit high-contrast Sahara Night tokens throughout
 * - 5-Step progress tracker with SecondaryText pending and PrimaryText active/completed labels
 * - Styled circular driver avatar with rating and plate badge
 * - Waiting for Driver Assignment fallback state with auto-assign action
 * - Roomy Live Glide control bar with multiplier pills
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
    onAutoAssignDriver: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
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
            // Header: Status title + Big bold ETA Capsule
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
                                    if (order.status == DeliveryStatus.DELIVERED) AccentEmerald else AccentBlue
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = order.status.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryText
                        )
                    }
                    Text(
                        text = order.status.arabicTitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryText,
                        fontSize = 12.sp
                    )
                }

                // ETA Capsule
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = CardSurfaceVariant,
                    border = BorderStroke(1.dp, BorderStroke),
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
                            color = PrimaryText
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${order.remainingDistanceMeters}m",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SecondaryText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Step Progress Bar (Evenly distributed flex/row component)
            ModernDeliveryStepProgress(currentStatus = order.status)

            Spacer(modifier = Modifier.height(18.dp))

            // Turn-By-Turn Navigation Instruction banner
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CardSurfaceVariant,
                border = BorderStroke(1.dp, BorderStroke),
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
                            .background(CardSurface)
                            .border(1.dp, BorderStroke, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Navigation,
                            contentDescription = null,
                            tint = AccentBlue,
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
                            color = PrimaryText,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = order.currentStep.arabicInstruction,
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryText,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Courier Profile & Contact Actions or Unassigned State
            val isDriverAssigned = order.courierName.isNotBlank() && !order.courierName.equals("Unassigned", ignoreCase = true)

            if (isDriverAssigned) {
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
                                .background(CardSurfaceVariant)
                                .border(1.5.dp, BorderStroke, CircleShape)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(CardSurface)
                            ) {
                                Text(
                                    text = order.courierName.take(2).uppercase(),
                                    color = PrimaryText,
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
                                color = PrimaryText
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            // Rating & Moroccan Plate Badges
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = AccentAmber,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "${order.courierRating}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = PrimaryText
                                    )
                                }
                                Text(
                                    text = "•",
                                    color = SecondaryText,
                                    fontSize = 11.sp
                                )
                                // Moroccan Plate badge pill
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = CardSurfaceVariant,
                                    border = BorderStroke(1.dp, BorderStroke)
                                ) {
                                    Text(
                                        text = order.courierPlate,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = SecondaryText,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Centered Chat & Call Action Buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Chat button
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(CardSurfaceVariant)
                                .border(1.dp, BorderStroke, CircleShape)
                                .clickable { onOpenChat() }
                                .testTag("btn_courier_chat")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "Chat",
                                tint = PrimaryText,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Call button
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(AccentBlue)
                                .clickable { onCallCourier() }
                                .testTag("btn_courier_call")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Call",
                                tint = PrimaryText,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            } else {
                // Waiting for Driver Assignment Fallback State
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = CardSurfaceVariant,
                    border = BorderStroke(1.dp, AccentAmber.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Waiting for Driver Assignment...",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = AccentAmber
                            )
                            Text(
                                text = "في انتظار تعيين سائق التوصيل",
                                style = MaterialTheme.typography.bodySmall,
                                color = SecondaryText,
                                fontSize = 11.sp
                            )
                        }
                        if (onAutoAssignDriver != null) {
                            Button(
                                onClick = onAutoAssignDriver,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentAmber),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.AssignmentInd, contentDescription = null, tint = CardSurface, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Assign Now", fontSize = 12.sp, color = CardSurface, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Live Simulation Control Console (Live Glide Bar)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CardSurfaceVariant,
                border = BorderStroke(1.dp, BorderStroke),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("simulation_glide_bar")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Play/Pause Action with Label
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onToggleSimulation() }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(AccentBlue)
                        ) {
                            Icon(
                                imageVector = if (isSimulating) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isSimulating) "Pause" else "Play",
                                tint = PrimaryText,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isSimulating) "Live Glide" else "Paused",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = PrimaryText
                            )
                            Text(
                                text = if (isSimulating) "Tap to pause" else "Tap to glide",
                                fontSize = 10.sp,
                                color = SecondaryText
                            )
                        }
                    }

                    // Multiplier Pills: 1x, 2x, 5x + Reset
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(1.0f, 2.0f, 5.0f).forEach { mult ->
                            val isSelected = simMultiplier == mult
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) AccentBlue else CardSurface,
                                border = BorderStroke(1.dp, if (isSelected) AccentBlue else BorderStroke),
                                modifier = Modifier.clickable { onSetMultiplier(mult) }
                            ) {
                                Text(
                                    text = "${mult.toInt()}x",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) PrimaryText else SecondaryText,
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
                                .background(CardSurface)
                                .border(1.dp, BorderStroke, CircleShape)
                                .clickable { onResetSimulation() }
                                .testTag("btn_reset_simulation")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Restart Route",
                                tint = PrimaryText,
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
 *     - Current   = 20dp (CardSurfaceVariant + Emerald Ring + White Core)
 *     - Pending   = 12dp (Neutral Muted Circle)
 * - Text labels: SecondaryText (11.sp) for pending, PrimaryText bold for active/completed
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
                                    .background(AccentEmerald)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = PrimaryText,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                        isCurrent -> {
                            // Current node: 20dp with emerald ring and white core
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(CardSurfaceVariant)
                                    .border(2.dp, AccentEmerald, CircleShape)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryText)
                                )
                            }
                        }
                        else -> {
                            // Pending node: 12dp subtle neutral circle
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(BorderStroke)
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
                            .background(if (lineCompleted) AccentEmerald else BorderStroke)
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
                    fontSize = 11.sp,
                    fontWeight = if (isCurrent || isCompleted) FontWeight.Bold else FontWeight.Normal,
                    color = if (isCurrent || isCompleted) PrimaryText else SecondaryText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
