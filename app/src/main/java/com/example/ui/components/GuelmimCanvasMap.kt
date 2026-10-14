package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GuelmimMapData
import com.example.model.DeliveryOrder
import com.example.model.GuelmimLocation
import com.example.model.GuelmimPoint
import com.example.model.LocationCategory
import com.example.model.VehicleType
import com.example.ui.theme.MapAccentCyan
import com.example.ui.theme.MapAlleyway
import com.example.ui.theme.MapBoulevard
import com.example.ui.theme.MapBoulevardDark
import com.example.ui.theme.MapGroundSand
import com.example.ui.theme.MapGroundSandDark
import com.example.ui.theme.MapMajorAvenue
import com.example.ui.theme.MapOasisGreenTint
import com.example.ui.theme.MapRiverbedBlue
import com.example.ui.theme.MapRouteGlow
import com.example.ui.theme.MapRouteRemaining
import com.example.ui.theme.OasisGreen
import com.example.ui.theme.SaharaAmber
import com.example.ui.theme.TerracottaPrimary
import kotlin.math.sqrt

@Composable
fun GuelmimCanvasMap(
    order: DeliveryOrder?,
    isFollowCourier: Boolean,
    showTraffic: Boolean,
    selectedLocation: GuelmimLocation?,
    onLocationClick: (GuelmimLocation) -> Unit,
    onToggleFollowCourier: () -> Unit,
    onToggleTraffic: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val textMeasurer = rememberTextMeasurer()

    // Map viewport state: zoom and pan
    var zoomLevel by remember { mutableFloatStateOf(1.0f) }
    var panOffsetX by remember { mutableFloatStateOf(0f) }
    var panOffsetY by remember { mutableFloatStateOf(0f) }

    // Pulsing animation for courier GPS radar beacon
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadiusFraction by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 2.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseRadius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    // Smoothly follow courier when enabled
    LaunchedEffect(order?.currentPosition, isFollowCourier, zoomLevel) {
        if (isFollowCourier && order != null) {
            // Target center coordinates in map space (0..1000)
            val courierX = order.currentPosition.x
            val courierY = order.currentPosition.y
            // Desired center is (500, 500)
            panOffsetX = (500f - courierX) * (zoomLevel * 0.7f)
            panOffsetY = (500f - courierY) * (zoomLevel * 0.7f)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(0.dp))
            .background(if (isDark) MapGroundSandDark else MapGroundSand)
            .testTag("guelmim_interactive_map")
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        panOffsetX += dragAmount.x
                        panOffsetY += dragAmount.y
                    }
                }
                .pointerInput(zoomLevel, panOffsetX, panOffsetY) {
                    detectTapGestures { tapOffset ->
                        // Hit-test landmarks
                        val mapScale = (size.width.coerceAtMost(size.height) / 1000f) * zoomLevel
                        val originX = (size.width / 2f) + panOffsetX - (500f * mapScale)
                        val originY = (size.height / 2f) + panOffsetY - (500f * mapScale)

                        for (loc in GuelmimMapData.allLocations) {
                            val screenX = originX + (loc.point.x * mapScale)
                            val screenY = originY + (loc.point.y * mapScale)
                            val dist = sqrt((tapOffset.x - screenX) * (tapOffset.x - screenX) + (tapOffset.y - screenY) * (tapOffset.y - screenY))
                            if (dist < 42f) {
                                onLocationClick(loc)
                                break
                            }
                        }
                    }
                }
        ) {
            val canvasW = size.width
            val canvasH = size.height
            val baseDimension = canvasW.coerceAtMost(canvasH)
            val scale = (baseDimension / 1000f) * zoomLevel

            // Map coordinate transformation
            val originX = (canvasW / 2f) + panOffsetX - (500f * scale)
            val originY = (canvasH / 2f) + panOffsetY - (500f * scale)

            fun mapToScreen(point: GuelmimPoint): Offset {
                return Offset(
                    x = originX + (point.x * scale),
                    y = originY + (point.y * scale)
                )
            }

            // 1. Draw Guelmim Topography Background & Sahara Dunes
            drawGuelmimTerrain(scale, originX, originY, isDark)

            // 2. Draw Oued Noun Riverbed Wash
            drawOuedRiverbed(scale, originX, originY, isDark)

            // 3. Draw Oasis Tighmert Vegetation Cluster
            val tighmertScreen = mapToScreen(GuelmimMapData.OASIS_TIGHMERT.point)
            drawCircle(
                color = if (isDark) Color(0xFF1B3D2B) else MapOasisGreenTint,
                radius = 70f * scale,
                center = tighmertScreen
            )
            drawCircle(
                color = if (isDark) Color(0xFF244F39) else Color(0xFFD4E9DC),
                radius = 45f * scale,
                center = tighmertScreen
            )

            // 4. Draw Street Network
            drawStreetNetwork(scale, originX, originY, isDark, showTraffic)

            // 5. Draw Street Names
            drawStreetLabels(scale, originX, originY, isDark, textMeasurer)

            // 6. Draw Delivery Route Polylines
            if (order != null && order.routePoints.isNotEmpty()) {
                drawDeliveryRoute(order, scale, originX, originY)
            }

            // 7. Draw City Landmarks & Badges
            for (loc in GuelmimMapData.allLocations) {
                val isSelected = selectedLocation?.id == loc.id
                drawLandmarkMarker(
                    loc = loc,
                    screenPos = mapToScreen(loc.point),
                    scale = scale,
                    isSelected = isSelected,
                    isDark = isDark,
                    textMeasurer = textMeasurer
                )
            }

            // 8. Draw Pickup & Dropoff Destination Pins
            if (order != null) {
                drawPickupAndDropoffPins(order, scale, originX, originY, textMeasurer)

                // 9. Draw Live Courier Vehicle with Orientation & Beacon
                drawCourierVehicle(
                    order = order,
                    screenPos = mapToScreen(order.currentPosition),
                    scale = scale,
                    pulseRadius = pulseRadiusFraction,
                    pulseAlpha = pulseAlpha,
                    isDark = isDark,
                    textMeasurer = textMeasurer
                )
            }
        }

        // Overlay Map Controls
        MapControlsOverlay(
            zoomLevel = zoomLevel,
            onZoomIn = { zoomLevel = (zoomLevel + 0.25f).coerceAtMost(2.8f) },
            onZoomOut = { zoomLevel = (zoomLevel - 0.25f).coerceAtLeast(0.7f) },
            isFollowCourier = isFollowCourier,
            onToggleFollow = onToggleFollowCourier,
            showTraffic = showTraffic,
            onToggleTraffic = onToggleTraffic,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        )

        // Guelmim Gateway Compass & Region Pill
        GuelmimBadge(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        )
    }
}

private fun DrawScope.drawGuelmimTerrain(scale: Float, originX: Float, originY: Float, isDark: Boolean) {
    // Subtle desert dune ripples
    val duneColor = if (isDark) Color(0xFF28211C) else Color(0xFFF3ECE0)
    val duneStroke = Stroke(width = 2.5f * scale, cap = StrokeCap.Round)

    for (duneY in listOf(180f, 320f, 520f, 700f, 880f)) {
        val path = Path().apply {
            val startX = originX + (50f * scale)
            val y = originY + (duneY * scale)
            moveTo(startX, y)
            quadraticTo(
                originX + (350f * scale), y - (24f * scale),
                originX + (650f * scale), y + (18f * scale)
            )
            quadraticTo(
                originX + (850f * scale), y - (12f * scale),
                originX + (950f * scale), y
            )
        }
        drawPath(path = path, color = duneColor, style = duneStroke)
    }
}

private fun DrawScope.drawOuedRiverbed(scale: Float, originX: Float, originY: Float, isDark: Boolean) {
    val riverColor = if (isDark) Color(0xFF1A262E) else MapRiverbedBlue
    val path = Path()

    val points = GuelmimMapData.ouedRiverbedPoints
    if (points.isNotEmpty()) {
        path.moveTo(originX + (points.first().x * scale), originY + (points.first().y * scale))
        for (i in 1 until points.size) {
            val pt = points[i]
            val prev = points[i - 1]
            val midX = (prev.x + pt.x) / 2f
            val midY = (prev.y + pt.y) / 2f
            path.quadraticTo(
                originX + (prev.x * scale), originY + (prev.y * scale),
                originX + (midX * scale), originY + (midY * scale)
            )
        }
        val last = points.last()
        path.lineTo(originX + (last.x * scale), originY + (last.y * scale))

        drawPath(
            path = path,
            color = riverColor,
            style = Stroke(width = 22f * scale, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

private fun DrawScope.drawStreetNetwork(
    scale: Float,
    originX: Float,
    originY: Float,
    isDark: Boolean,
    showTraffic: Boolean
) {
    val arterialRoadColor = if (isDark) MapBoulevardDark else MapBoulevard
    val boulevardRoadColor = if (isDark) Color(0xFF453830) else Color(0xFFE5CEBA)
    val dividerColor = if (isDark) Color(0xFF6B584C) else Color(0xFFFFFFFF)

    for (road in GuelmimMapData.cityRoads) {
        val roadPath = Path()
        val pts = road.points
        if (pts.isEmpty()) continue

        roadPath.moveTo(originX + (pts.first().x * scale), originY + (pts.first().y * scale))
        for (i in 1 until pts.size) {
            roadPath.lineTo(originX + (pts[i].x * scale), originY + (pts[i].y * scale))
        }

        val baseWidth = when {
            road.isBoulevard -> 22f * scale
            road.isArterial -> 18f * scale
            else -> 12f * scale
        }

        // Base road casing
        drawPath(
            path = roadPath,
            color = if (road.isBoulevard) boulevardRoadColor else arterialRoadColor,
            style = Stroke(width = baseWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Center line for major boulevards
        if (road.isArterial || road.isBoulevard) {
            drawPath(
                path = roadPath,
                color = dividerColor,
                style = Stroke(
                    width = 2.2f * scale,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f * scale, 12f * scale), 0f),
                    cap = StrokeCap.Round
                )
            )
        }

        // Traffic simulation overlay
        if (showTraffic && (road.isArterial || road.isBoulevard)) {
            val trafficColor = if (road.name.contains("Mohammed V")) {
                Color(0xFFE65100).copy(alpha = 0.65f) // Busier main commercial avenue
            } else {
                Color(0xFF2E7D32).copy(alpha = 0.65f) // Smooth flowing boulevard
            }
            drawPath(
                path = roadPath,
                color = trafficColor,
                style = Stroke(width = 4f * scale, cap = StrokeCap.Round)
            )
        }
    }

    // Place Bir Anzarane Roundabout Circle
    val birAnzaraneScreen = Offset(
        originX + (GuelmimMapData.BIR_ANZARANE.point.x * scale),
        originY + (GuelmimMapData.BIR_ANZARANE.point.y * scale)
    )
    drawCircle(
        color = boulevardRoadColor,
        radius = 28f * scale,
        center = birAnzaraneScreen
    )
    drawCircle(
        color = if (isDark) MapGroundSandDark else MapGroundSand,
        radius = 16f * scale,
        center = birAnzaraneScreen
    )
}

private fun DrawScope.drawStreetLabels(
    scale: Float,
    originX: Float,
    originY: Float,
    isDark: Boolean,
    textMeasurer: TextMeasurer
) {
    val labelColor = if (isDark) Color(0xFFC7B3A3) else Color(0xFF7A6455)
    val textStyle = TextStyle(
        fontSize = (9f * scale).coerceIn(8f, 13f).sp,
        fontWeight = FontWeight.SemiBold,
        color = labelColor
    )

    // Label for Avenue Mohammed V
    val mohammedVPos = Offset(originX + (495f * scale), originY + (200f * scale))
    drawText(
        textMeasurer = textMeasurer,
        text = "Av. Mohammed V",
        topLeft = mohammedVPos,
        style = textStyle
    )

    // Label for Boulevard Hassan II
    val hassanIIPos = Offset(originX + (220f * scale), originY + (442f * scale))
    drawText(
        textMeasurer = textMeasurer,
        text = "Bd. Hassan II",
        topLeft = hassanIIPos,
        style = textStyle
    )

    // Label for Route de Tighmert
    val tighmertPos = Offset(originX + (730f * scale), originY + (430f * scale))
    drawText(
        textMeasurer = textMeasurer,
        text = "Route de Tighmert (Oasis)",
        topLeft = tighmertPos,
        style = textStyle
    )
}

private fun DrawScope.drawDeliveryRoute(
    order: DeliveryOrder,
    scale: Float,
    originX: Float,
    originY: Float
) {
    val pts = order.routePoints
    if (pts.size < 2) return

    val curPos = order.currentPosition
    val routeProgress = order.progressFraction

    // 1. Completed Path (From start to courier): Vibrant Terracotta / Desert Gold
    val completedPath = Path()
    completedPath.moveTo(originX + (pts.first().x * scale), originY + (pts.first().y * scale))

    val totalSegments = pts.size - 1
    val scaled = routeProgress * totalSegments
    val segmentIndex = scaled.toInt().coerceIn(0, totalSegments - 1)

    for (i in 1..segmentIndex) {
        completedPath.lineTo(originX + (pts[i].x * scale), originY + (pts[i].y * scale))
    }
    completedPath.lineTo(originX + (curPos.x * scale), originY + (curPos.y * scale))

    // Glow underlay
    drawPath(
        path = completedPath,
        color = MapRouteGlow.copy(alpha = 0.35f),
        style = Stroke(width = 16f * scale, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
    // Main completed line
    drawPath(
        path = completedPath,
        color = MapRouteGlow,
        style = Stroke(width = 8f * scale, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )

    // 2. Remaining Path (From courier to destination): Dashed Sahara Amber
    val remainingPath = Path()
    remainingPath.moveTo(originX + (curPos.x * scale), originY + (curPos.y * scale))
    for (i in (segmentIndex + 1) until pts.size) {
        remainingPath.lineTo(originX + (pts[i].x * scale), originY + (pts[i].y * scale))
    }

    drawPath(
        path = remainingPath,
        color = MapRouteRemaining,
        style = Stroke(
            width = 6f * scale,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f * scale, 12f * scale), 0f),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}

private fun DrawScope.drawLandmarkMarker(
    loc: GuelmimLocation,
    screenPos: Offset,
    scale: Float,
    isSelected: Boolean,
    isDark: Boolean,
    textMeasurer: TextMeasurer
) {
    val markerRadius = if (isSelected) 14f * scale else 10f * scale
    val badgeColor = when (loc.category) {
        LocationCategory.LANDMARK -> TerracottaPrimary
        LocationCategory.MARKET -> SaharaAmber
        LocationCategory.OASIS -> OasisGreen
        LocationCategory.RESTAURANT -> Color(0xFFD32F2F)
        LocationCategory.PHARMACY -> Color(0xFF00897B)
        LocationCategory.RESIDENCE -> Color(0xFF5D4037)
    }

    // Outer shadow ring
    drawCircle(
        color = Color.Black.copy(alpha = 0.25f),
        radius = markerRadius + (3f * scale),
        center = screenPos + Offset(0f, 2f * scale)
    )
    // White / Dark border
    drawCircle(
        color = if (isDark) Color(0xFF2A231E) else Color.White,
        radius = markerRadius + (2f * scale),
        center = screenPos
    )
    // Main badge
    drawCircle(
        color = badgeColor,
        radius = markerRadius,
        center = screenPos
    )

    // Inner icon dot
    drawCircle(
        color = Color.White,
        radius = markerRadius * 0.45f,
        center = screenPos
    )

    // Landmark Name Label pill
    if (loc.category == LocationCategory.LANDMARK || loc.category == LocationCategory.MARKET || isSelected) {
        val labelText = loc.name.substringBefore(" (")
        val textLayout = textMeasurer.measure(
            text = labelText,
            style = TextStyle(
                fontSize = (9f * scale).coerceIn(8f, 12f).sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color(0xFFF0E5DC) else Color(0xFF2C221C)
            )
        )

        val pillW = textLayout.size.width + (14f * scale)
        val pillH = textLayout.size.height + (8f * scale)
        val pillX = screenPos.x - (pillW / 2f)
        val pillY = screenPos.y + markerRadius + (4f * scale)

        // Pill background
        drawRoundRect(
            color = if (isDark) Color(0xDD28201B) else Color(0xEEFFFFFF),
            topLeft = Offset(pillX, pillY),
            size = Size(pillW, pillH),
            cornerRadius = CornerRadius(6f * scale, 6f * scale)
        )
        // Pill border
        drawRoundRect(
            color = badgeColor.copy(alpha = 0.5f),
            topLeft = Offset(pillX, pillY),
            size = Size(pillW, pillH),
            cornerRadius = CornerRadius(6f * scale, 6f * scale),
            style = Stroke(width = 1f * scale)
        )

        drawText(
            textLayoutResult = textLayout,
            topLeft = Offset(pillX + (7f * scale), pillY + (4f * scale))
        )
    }
}

private fun DrawScope.drawPickupAndDropoffPins(
    order: DeliveryOrder,
    scale: Float,
    originX: Float,
    originY: Float,
    textMeasurer: TextMeasurer
) {
    // 1. Pickup Pin (Store)
    val pickupPos = Offset(
        originX + (order.pickupLocation.point.x * scale),
        originY + (order.pickupLocation.point.y * scale)
    )
    drawCircle(
        color = TerracottaPrimary,
        radius = 16f * scale,
        center = pickupPos
    )
    drawCircle(
        color = Color.White,
        radius = 8f * scale,
        center = pickupPos
    )

    // 2. Dropoff Pin (Customer Destination)
    val dropoffPos = Offset(
        originX + (order.dropoffLocation.point.x * scale),
        originY + (order.dropoffLocation.point.y * scale)
    )
    // Pulsing target halo
    drawCircle(
        color = MapAccentCyan.copy(alpha = 0.25f),
        radius = 24f * scale,
        center = dropoffPos
    )
    drawCircle(
        color = MapAccentCyan,
        radius = 16f * scale,
        center = dropoffPos
    )
    drawCircle(
        color = Color.White,
        radius = 8f * scale,
        center = dropoffPos
    )
}

private fun DrawScope.drawCourierVehicle(
    order: DeliveryOrder,
    screenPos: Offset,
    scale: Float,
    pulseRadius: Float,
    pulseAlpha: Float,
    isDark: Boolean,
    textMeasurer: TextMeasurer
) {
    val vehicle = order.vehicleType
    val vehicleColor = vehicle.highlightColor

    // 1. Pulsing Radar Halo
    drawCircle(
        color = vehicleColor.copy(alpha = pulseAlpha * 0.4f),
        radius = 22f * scale * pulseRadius,
        center = screenPos
    )

    // 2. Courier Marker Base
    drawCircle(
        color = Color.Black.copy(alpha = 0.35f),
        radius = 18f * scale,
        center = screenPos + Offset(0f, 3f * scale)
    )
    drawCircle(
        color = if (isDark) Color(0xFF2E241E) else Color.White,
        radius = 17f * scale,
        center = screenPos
    )
    drawCircle(
        color = vehicleColor,
        radius = 14f * scale,
        center = screenPos
    )

    // 3. Directional Heading Arrow / Vehicle Pointer
    rotate(degrees = order.headingDegrees, pivot = screenPos) {
        val arrowPath = Path().apply {
            moveTo(screenPos.x, screenPos.y - (18f * scale))
            lineTo(screenPos.x + (6f * scale), screenPos.y - (9f * scale))
            lineTo(screenPos.x - (6f * scale), screenPos.y - (9f * scale))
            close()
        }
        drawPath(
            path = arrowPath,
            color = Color.White
        )

        // Draw vehicle specific glyph inside circle
        when (vehicle) {
            VehicleType.MOTORCYCLE -> {
                // Two wheels and chassis
                drawCircle(color = Color.White, radius = 3f * scale, center = screenPos + Offset(0f, -4f * scale))
                drawCircle(color = Color.White, radius = 3f * scale, center = screenPos + Offset(0f, 4f * scale))
                drawLine(
                    color = Color.White,
                    start = screenPos + Offset(0f, -4f * scale),
                    end = screenPos + Offset(0f, 4f * scale),
                    strokeWidth = 2.5f * scale
                )
            }
            VehicleType.CAR -> {
                // Car rectangular chassis
                drawRoundRect(
                    color = Color.White,
                    topLeft = screenPos - Offset(4.5f * scale, 7f * scale),
                    size = Size(9f * scale, 14f * scale),
                    cornerRadius = CornerRadius(2.5f * scale, 2.5f * scale)
                )
            }
            VehicleType.BICYCLE -> {
                // Eco bike slim silhouette
                drawCircle(color = Color.White, radius = 2.5f * scale, center = screenPos + Offset(0f, -5f * scale))
                drawCircle(color = Color.White, radius = 2.5f * scale, center = screenPos + Offset(0f, 5f * scale))
            }
        }
    }

    // 4. Floating Speed & Vehicle Tag
    val speedText = "${order.currentSpeedKmH.toInt()} km/h"
    val speedLayout = textMeasurer.measure(
        text = speedText,
        style = TextStyle(
            fontSize = (8.5f * scale).coerceIn(8f, 11f).sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    )
    val tagW = speedLayout.size.width + (10f * scale)
    val tagH = speedLayout.size.height + (4f * scale)
    val tagX = screenPos.x - (tagW / 2f)
    val tagY = screenPos.y - (28f * scale)

    drawRoundRect(
        color = Color(0xDD1E1916),
        topLeft = Offset(tagX, tagY),
        size = Size(tagW, tagH),
        cornerRadius = CornerRadius(4f * scale, 4f * scale)
    )
    drawText(
        textLayoutResult = speedLayout,
        topLeft = Offset(tagX + (5f * scale), tagY + (2f * scale))
    )
}

@Composable
private fun MapControlsOverlay(
    zoomLevel: Float,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    isFollowCourier: Boolean,
    onToggleFollow: () -> Unit,
    showTraffic: Boolean,
    onToggleTraffic: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        // Follow Courier button
        FloatingActionButton(
            onClick = onToggleFollow,
            containerColor = if (isFollowCourier) TerracottaPrimary else MaterialTheme.colorScheme.surface,
            contentColor = if (isFollowCourier) Color.White else MaterialTheme.colorScheme.onSurface,
            elevation = FloatingActionButtonDefaults.elevation(4.dp),
            modifier = Modifier
                .size(44.dp)
                .testTag("btn_follow_courier")
        ) {
            Icon(
                imageVector = if (isFollowCourier) Icons.Default.MyLocation else Icons.Default.LocationSearching,
                contentDescription = "Follow Courier Live",
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Traffic layer toggle
        FloatingActionButton(
            onClick = onToggleTraffic,
            containerColor = if (showTraffic) OasisGreen else MaterialTheme.colorScheme.surface,
            contentColor = if (showTraffic) Color.White else MaterialTheme.colorScheme.onSurface,
            elevation = FloatingActionButtonDefaults.elevation(4.dp),
            modifier = Modifier
                .size(44.dp)
                .testTag("btn_traffic_overlay")
        ) {
            Icon(
                imageVector = Icons.Default.Layers,
                contentDescription = "Traffic Overlay",
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Zoom controls
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp
        ) {
            Column {
                IconButton(
                    onClick = onZoomIn,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("btn_zoom_in")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Zoom In")
                }
                IconButton(
                    onClick = onZoomOut,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("btn_zoom_out")
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Zoom Out")
                }
            }
        }
    }
}

@Composable
private fun GuelmimBadge(modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
        shadowElevation = 4.dp,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(TerracottaPrimary)
            ) {
                Icon(
                    imageVector = Icons.Default.Explore,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Guelmim • كلميم",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Bab Sahra • باب الصحراء",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    fontSize = 11.sp
                )
            }
        }
    }
}
