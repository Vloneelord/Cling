package com.example.ui.components

import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.graphics.drawscope.translate
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
import com.example.model.DeliveryStatus
import com.example.model.GuelmimLocation
import com.example.model.GuelmimPoint
import com.example.model.LocationCategory
import com.example.model.VehicleType
import com.example.ui.theme.GlassFrostedBorder
import com.example.ui.theme.GlassFrostedSurface
import com.example.ui.theme.MapAvenueCasing
import com.example.ui.theme.MapAvenueCasingDark
import com.example.ui.theme.MapAvenueWhite
import com.example.ui.theme.MapBlockFootprint
import com.example.ui.theme.MapBlockFootprintDark
import com.example.ui.theme.MapBlockStroke
import com.example.ui.theme.MapBlockStrokeDark
import com.example.ui.theme.MapCanvasBg
import com.example.ui.theme.MapCanvasBgDark
import com.example.ui.theme.MapDividerDash
import com.example.ui.theme.MapParkMint
import com.example.ui.theme.MapParkMintDark
import com.example.ui.theme.MapRiverAzure
import com.example.ui.theme.MapRiverAzureDark
import com.example.ui.theme.MapRouteFuture
import com.example.ui.theme.MapRouteGlowCore
import com.example.ui.theme.MapRouteTraveled
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.BorderStroke
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceVariant
import com.example.ui.theme.ModernAmber
import com.example.ui.theme.ModernBlack
import com.example.ui.theme.ModernElectricBlue
import com.example.ui.theme.ModernEmerald
import com.example.ui.theme.ModernMuted
import com.example.ui.theme.ModernWhite
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import kotlin.math.sqrt

/**
 * Modern High-Resolution Vector Map of Guelmim, Morocco (Bab Sahra).
 * Inspired by Behance OXXO Retail & Zevoa Super App.
 * Clean Black & White minimalist palette with frosted glass overlays,
 * smooth 60fps vehicle glide, animated radar ripples, and micro-mobility routing.
 */
@Composable
fun GuelmimMapCanvas(
    order: DeliveryOrder?,
    isFollowCourier: Boolean,
    showTraffic: Boolean,
    selectedLocation: GuelmimLocation?,
    onLocationClick: (GuelmimLocation) -> Unit,
    onToggleFollowCourier: () -> Unit,
    onToggleTraffic: () -> Unit,
    visibleCenterFractionY: Float = 0.32f,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val textMeasurer = rememberTextMeasurer()

    var zoomLevel by remember { mutableFloatStateOf(1.25f) }
    var panOffsetX by remember { mutableFloatStateOf(0f) }
    var panOffsetY by remember { mutableFloatStateOf(0f) }

    // Pulsing radar animation around the active delivery driver
    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    val pulseRadiusFraction by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 2.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseRadius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    // Smooth Uber Eats style 60fps vehicle tracking with animateOffsetAsState
    val targetVehicleOffset = remember(order?.currentPosition) {
        Offset(
            x = order?.currentPosition?.x ?: 480f,
            y = order?.currentPosition?.y ?: 340f
        )
    }
    val animatedVehicleOffset by animateOffsetAsState(
        targetValue = targetVehicleOffset,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "animatedVehicleOffset"
    )
    val animVehicleX = animatedVehicleOffset.x
    val animVehicleY = animatedVehicleOffset.y
    val animHeading by animateFloatAsState(
        targetValue = order?.headingDegrees ?: 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "animHeading"
    )

    // Recenter tracking logic
    var canvasSize by remember { mutableStateOf(Size.Zero) }

    LaunchedEffect(animVehicleX, animVehicleY, isFollowCourier, zoomLevel, canvasSize) {
        if (isFollowCourier && order != null && canvasSize.width > 0f) {
            val baseDimension = canvasSize.width.coerceAtMost(canvasSize.height)
            val currentScale = (baseDimension / 1000f) * zoomLevel
            // Mathematical centering: map origin at center, vehicle at (animVehicleX, animVehicleY)
            panOffsetX = (500f - animVehicleX) * currentScale
            panOffsetY = (500f - animVehicleY) * currentScale
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDark) MapCanvasBgDark else MapCanvasBg)
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
                        val mapScale = (size.width.coerceAtMost(size.height) / 1000f) * zoomLevel
                        val originX = (size.width / 2f) + panOffsetX - (500f * mapScale)
                        val originY = (size.height * visibleCenterFractionY) + panOffsetY - (500f * mapScale)

                        for (loc in GuelmimMapData.allLocations) {
                            val screenX = originX + (loc.point.x * mapScale)
                            val screenY = originY + (loc.point.y * mapScale)
                            val dist = sqrt((tapOffset.x - screenX) * (tapOffset.x - screenX) + (tapOffset.y - screenY) * (tapOffset.y - screenY))

                            if (dist < 44f) {
                                onLocationClick(loc)
                                break
                            }
                        }
                    }
                }
        ) {
            canvasSize = size
            val canvasW = size.width
            val canvasH = size.height
            val baseDimension = canvasW.coerceAtMost(canvasH)
            val scale = (baseDimension / 1000f) * zoomLevel

            val originX = (canvasW / 2f) + panOffsetX - (500f * scale)
            val originY = (canvasH * visibleCenterFractionY) + panOffsetY - (500f * scale)

            fun mapToScreen(point: GuelmimPoint): Offset {
                return Offset(
                    x = originX + (point.x * scale),
                    y = originY + (point.y * scale)
                )
            }

            // 1. Draw Modern Urban Blocks / Building footprint grid
            drawCityUrbanGrid(scale, originX, originY, isDark)

            // 2. Draw Oued Noun Riverbed Ribbon
            drawModernRiverbed(scale, originX, originY, isDark)

            // 3. Draw Oasis Tighmert Vegetation Park
            val tighmertScreen = mapToScreen(GuelmimMapData.OASIS_TIGHMERT.point)
            drawCircle(
                color = if (isDark) MapParkMintDark else MapParkMint,
                radius = 75f * scale,
                center = tighmertScreen
            )
            drawCircle(
                color = if (isDark) Color(0xFF1B4D32) else Color(0xFFBFF3CE),
                radius = 48f * scale,
                center = tighmertScreen
            )

            // 4. Draw Modern Street Network with crisp casing & centerlines
            drawModernStreetNetwork(scale, originX, originY, isDark, showTraffic)

            // 5. Place Bir Anzarane Roundabout Plaza (Micro-mobility hub)
            drawBirAnzaraneRoundabout(scale, originX, originY, isDark)

            // 6. Draw Street Labels
            drawModernStreetLabels(scale, originX, originY, isDark, textMeasurer)

            // 7. Draw Live Uber Eats Style Delivery Route
            val animatedCourierPoint = GuelmimPoint(animVehicleX, animVehicleY)
            val animatedCourierScreen = mapToScreen(animatedCourierPoint)

            if (order != null && order.routePoints.isNotEmpty()) {
                drawModernDeliveryRoute(order, animatedCourierPoint, scale, originX, originY)
            }

            // 8. Draw City Landmark Markers & Badges
            for (loc in GuelmimMapData.allLocations) {
                val isSelected = selectedLocation?.id == loc.id
                drawModernLandmarkMarker(
                    loc = loc,
                    screenPos = mapToScreen(loc.point),
                    scale = scale,
                    isSelected = isSelected,
                    isDark = isDark,
                    textMeasurer = textMeasurer
                )
            }

            // 9. Draw Pickup & Dropoff Destination Badges
            if (order != null) {
                drawModernPickupDropoffPins(order, scale, originX, originY)

                // 10. Draw 3D Courier Vehicle Puck with Heading & Animated Radar
                drawModernCourierVehicle(
                    order = order,
                    screenPos = animatedCourierScreen,
                    headingDegrees = animHeading,
                    scale = scale,
                    pulseRadius = pulseRadiusFraction,
                    pulseAlpha = pulseAlpha,
                    isDark = isDark,
                    textMeasurer = textMeasurer
                )
            }
        }

        // Top Floating Frosted Glass Live Tracking Status Bar
        if (order != null) {
            FrostedTrackingStatusBar(
                order = order,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp, start = 16.dp, end = 80.dp)
            )
        } else {
            // City Badge
            ModernGuelmimBadge(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            )
        }

        // Floating Frosted Glass Map Controls (Recenter, Traffic, Zoom In/Out)
        ModernMapControlsOverlay(
            zoomLevel = zoomLevel,
            onZoomIn = { zoomLevel = (zoomLevel + 0.25f).coerceAtMost(3.0f) },
            onZoomOut = { zoomLevel = (zoomLevel - 0.25f).coerceAtLeast(0.7f) },
            isFollowCourier = isFollowCourier,
            onToggleFollow = onToggleFollowCourier,
            showTraffic = showTraffic,
            onToggleTraffic = onToggleTraffic,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        )
    }
}

/**
 * Backward-compatible alias for GuelmimMapCanvas
 */
@Composable
fun GuelmimCanvasMap(
    order: DeliveryOrder?,
    isFollowCourier: Boolean,
    showTraffic: Boolean,
    selectedLocation: GuelmimLocation?,
    onLocationClick: (GuelmimLocation) -> Unit,
    onToggleFollowCourier: () -> Unit,
    onToggleTraffic: () -> Unit,
    visibleCenterFractionY: Float = 0.32f,
    modifier: Modifier = Modifier
) {
    GuelmimMapCanvas(
        order = order,
        isFollowCourier = isFollowCourier,
        showTraffic = showTraffic,
        selectedLocation = selectedLocation,
        onLocationClick = onLocationClick,
        onToggleFollowCourier = onToggleFollowCourier,
        onToggleTraffic = onToggleTraffic,
        visibleCenterFractionY = visibleCenterFractionY,
        modifier = modifier
    )
}

/**
 * Draws clean geometric urban blocks to simulate modern city architectural fabric.
 */
private fun DrawScope.drawCityUrbanGrid(scale: Float, originX: Float, originY: Float, isDark: Boolean) {
    val blockFill = if (isDark) MapBlockFootprintDark else MapBlockFootprint
    val blockStroke = if (isDark) MapBlockStrokeDark else MapBlockStroke

    val urbanBlocks = listOf(
        // Al Quds residential cluster (North-East)
        Offset(540f, 180f) to Size(80f, 60f),
        Offset(640f, 160f) to Size(90f, 70f),
        Offset(550f, 260f) to Size(110f, 80f),
        Offset(680f, 250f) to Size(90f, 75f),

        // Centre Ville commercial sector
        Offset(360f, 290f) to Size(100f, 60f),
        Offset(380f, 370f) to Size(80f, 70f),
        Offset(510f, 320f) to Size(85f, 65f),
        Offset(510f, 400f) to Size(95f, 50f),

        // Hay Al Wahda (West)
        Offset(210f, 360f) to Size(90f, 80f),
        Offset(210f, 460f) to Size(95f, 70f),
        Offset(330f, 500f) to Size(90f, 80f),

        // Souk Amhirich area (South-West)
        Offset(160f, 580f) to Size(110f, 90f),
        Offset(290f, 640f) to Size(90f, 70f),

        // Bab Sahra South quarter
        Offset(420f, 700f) to Size(70f, 90f),
        Offset(520f, 690f) to Size(80f, 80f),
        Offset(440f, 810f) to Size(100f, 70f),

        // Hay Oued Noun (East)
        Offset(690f, 490f) to Size(90f, 80f),
        Offset(790f, 540f) to Size(85f, 75f)
    )

    for ((pos, size) in urbanBlocks) {
        val topLeft = Offset(originX + (pos.x * scale), originY + (pos.y * scale))
        val blockSize = Size(size.width * scale, size.height * scale)
        val corner = CornerRadius(8f * scale, 8f * scale)

        drawRoundRect(
            color = blockFill,
            topLeft = topLeft,
            size = blockSize,
            cornerRadius = corner
        )
        drawRoundRect(
            color = blockStroke,
            topLeft = topLeft,
            size = blockSize,
            cornerRadius = corner,
            style = Stroke(width = 1.2f * scale)
        )
    }
}

/**
 * Draws Oued Noun river with sleek modern light azure wash.
 */
private fun DrawScope.drawModernRiverbed(scale: Float, originX: Float, originY: Float, isDark: Boolean) {
    val riverColor = if (isDark) MapRiverAzureDark else MapRiverAzure
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
            style = Stroke(width = 24f * scale, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

/**
 * Draws modern street network with crisp white pavement, smooth road borders, and center dividers.
 */
private fun DrawScope.drawModernStreetNetwork(
    scale: Float,
    originX: Float,
    originY: Float,
    isDark: Boolean,
    showTraffic: Boolean
) {
    val roadCasing = if (isDark) MapAvenueCasingDark else MapAvenueCasing
    val roadPavement = if (isDark) Color(0xFF1E293B) else MapAvenueWhite
    val dividerColor = if (isDark) Color(0xFF475569) else MapDividerDash

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

        // 1. Road Outer Casing (Border)
        drawPath(
            path = roadPath,
            color = roadCasing,
            style = Stroke(width = baseWidth + (3f * scale), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // 2. Road Pavement (White Ribbon)
        drawPath(
            path = roadPath,
            color = roadPavement,
            style = Stroke(width = baseWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // 3. Center Dashed Line on Boulevards
        if (road.isArterial || road.isBoulevard) {
            drawPath(
                path = roadPath,
                color = dividerColor,
                style = Stroke(
                    width = 1.8f * scale,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f * scale, 10f * scale), 0f),
                    cap = StrokeCap.Round
                )
            )
        }

        // 4. Traffic Flow Simulation
        if (showTraffic && (road.isArterial || road.isBoulevard)) {
            val trafficColor = if (road.name.contains("Mohammed V")) {
                ModernAmber.copy(alpha = 0.7f) // Busy commercial artery
            } else {
                ModernEmerald.copy(alpha = 0.65f) // Smooth flowing avenue
            }
            drawPath(
                path = roadPath,
                color = trafficColor,
                style = Stroke(width = 3.5f * scale, cap = StrokeCap.Round)
            )
        }
    }
}

/**
 * Draws the central Place Bir Anzarane Roundabout Plaza.
 */
private fun DrawScope.drawBirAnzaraneRoundabout(scale: Float, originX: Float, originY: Float, isDark: Boolean) {
    val center = Offset(
        originX + (GuelmimMapData.BIR_ANZARANE.point.x * scale),
        originY + (GuelmimMapData.BIR_ANZARANE.point.y * scale)
    )
    val casingColor = if (isDark) MapAvenueCasingDark else MapAvenueCasing
    val pavementColor = if (isDark) Color(0xFF1E293B) else MapAvenueWhite
    val parkGreen = if (isDark) MapParkMintDark else MapParkMint

    // Outer road ring
    drawCircle(color = casingColor, radius = 32f * scale, center = center)
    drawCircle(color = pavementColor, radius = 29f * scale, center = center)

    // Inner landscaped island
    drawCircle(color = parkGreen, radius = 17f * scale, center = center)
    drawCircle(color = if (isDark) Color(0xFF1E293B) else Color.White, radius = 7f * scale, center = center)
}

/**
 * Draws clean, high-contrast street labels with frosted dark pills.
 */
private fun DrawScope.drawModernStreetLabels(
    scale: Float,
    originX: Float,
    originY: Float,
    isDark: Boolean,
    textMeasurer: TextMeasurer
) {
    val textStyle = TextStyle(
        fontSize = (9.5f * scale).coerceIn(8.5f, 12.5f).sp,
        fontWeight = FontWeight.Bold,
        color = if (isDark) Color(0xFFF9FAFB) else Color(0xFF0F172A)
    )

    fun drawPillLabel(text: String, x: Float, y: Float) {
        val layout = textMeasurer.measure(text = text, style = textStyle)
        val pillW = layout.size.width + (14f * scale)
        val pillH = layout.size.height + (8f * scale)
        val left = x - (pillW / 2f)
        val top = y - (pillH / 2f)

        // Subtle background pill
        drawRoundRect(
            color = if (isDark) Color(0xF0111827) else Color(0xF0FFFFFF),
            topLeft = Offset(left, top),
            size = Size(pillW, pillH),
            cornerRadius = CornerRadius(5f * scale, 5f * scale)
        )
        // High-contrast subtle border
        drawRoundRect(
            color = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0),
            topLeft = Offset(left, top),
            size = Size(pillW, pillH),
            cornerRadius = CornerRadius(5f * scale, 5f * scale),
            style = Stroke(width = 1f * scale)
        )
        drawText(
            textLayoutResult = layout,
            topLeft = Offset(left + (7f * scale), top + (4f * scale))
        )
    }

    // 1. Avenue Mohammed V (Central North Avenue)
    drawPillLabel("Avenue Mohammed V", originX + (495f * scale), originY + (205f * scale))

    // 2. Boulevard Hassan II (South-West Main Commercial Boulevard)
    drawPillLabel("Boulevard Hassan II", originX + (220f * scale), originY + (440f * scale))

    // 3. Route de Tighmert (Eastern Oasis Route)
    drawPillLabel("Route de Tighmert", originX + (730f * scale), originY + (430f * scale))

    // 4. Place Bir Anzarane (Central Roundabout Hub)
    drawPillLabel("Place Bir Anzarane", originX + (480f * scale), originY + (510f * scale))

    // 5. Hay Al Wahda (Western Residential Sector)
    drawPillLabel("Hay Al Wahda", originX + (305f * scale), originY + (355f * scale))
}

/**
 * Draws modern Uber Eats style delivery route polyline with electric core.
 */
private fun DrawScope.drawModernDeliveryRoute(
    order: DeliveryOrder,
    curPos: GuelmimPoint,
    scale: Float,
    originX: Float,
    originY: Float
) {
    val pts = order.routePoints
    if (pts.size < 2) return

    val routeProgress = order.progressFraction
    val totalSegments = pts.size - 1
    val scaled = routeProgress * totalSegments
    val segmentIndex = scaled.toInt().coerceIn(0, totalSegments - 1)

    // 1. Completed Path (From origin to driver): High-contrast carbon with electric neon core
    val completedPath = Path()
    completedPath.moveTo(originX + (pts.first().x * scale), originY + (pts.first().y * scale))
    for (i in 1..segmentIndex) {
        completedPath.lineTo(originX + (pts[i].x * scale), originY + (pts[i].y * scale))
    }
    completedPath.lineTo(originX + (curPos.x * scale), originY + (curPos.y * scale))

    // Outer glow
    drawPath(
        path = completedPath,
        color = MapRouteGlowCore.copy(alpha = 0.28f),
        style = Stroke(width = 16f * scale, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
    // Dark track
    drawPath(
        path = completedPath,
        color = MapRouteTraveled,
        style = Stroke(width = 8f * scale, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
    // Neon electric core
    drawPath(
        path = completedPath,
        color = MapRouteGlowCore,
        style = Stroke(width = 3.5f * scale, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )

    // 2. Remaining Path (From driver to customer): Clean dotted high-contrast track
    val remainingPath = Path()
    remainingPath.moveTo(originX + (curPos.x * scale), originY + (curPos.y * scale))
    for (i in (segmentIndex + 1) until pts.size) {
        remainingPath.lineTo(originX + (pts[i].x * scale), originY + (pts[i].y * scale))
    }

    drawPath(
        path = remainingPath,
        color = MapRouteFuture,
        style = Stroke(
            width = 5.5f * scale,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f * scale, 12f * scale), 0f),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}

/**
 * Draws landmark pins with modern frosted badges.
 */
private fun DrawScope.drawModernLandmarkMarker(
    loc: GuelmimLocation,
    screenPos: Offset,
    scale: Float,
    isSelected: Boolean,
    isDark: Boolean,
    textMeasurer: TextMeasurer
) {
    val markerRadius = if (isSelected) 14f * scale else 9f * scale

    val badgeColor = when (loc.category) {
        LocationCategory.LANDMARK -> ModernBlack
        LocationCategory.MARKET -> ModernAmber
        LocationCategory.OASIS -> ModernEmerald
        LocationCategory.RESTAURANT -> ModernElectricBlue
        LocationCategory.PHARMACY -> Color(0xFF06B6D4)
        LocationCategory.RESIDENCE -> Color(0xFF64748B)
    }

    // Drop shadow
    drawCircle(
        color = Color.Black.copy(alpha = 0.2f),
        radius = markerRadius + (2.5f * scale),
        center = screenPos + Offset(0f, 2f * scale)
    )
    // White ring
    drawCircle(
        color = if (isDark) Color(0xFF0F172A) else Color.White,
        radius = markerRadius + (2f * scale),
        center = screenPos
    )
    // Main badge
    drawCircle(color = badgeColor, radius = markerRadius, center = screenPos)
    // Center white dot
    drawCircle(color = Color.White, radius = markerRadius * 0.4f, center = screenPos)

    // Frosted Name Pill
    if (loc.category == LocationCategory.LANDMARK || loc.category == LocationCategory.MARKET || isSelected) {
        val labelText = loc.name.substringBefore(" (")
        val textLayout = textMeasurer.measure(
            text = labelText,
            style = TextStyle(
                fontSize = (9f * scale).coerceIn(8f, 12f).sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else ModernBlack
            )
        )

        val pillW = textLayout.size.width + (14f * scale)
        val pillH = textLayout.size.height + (8f * scale)
        val pillX = screenPos.x - (pillW / 2f)
        val pillY = screenPos.y + markerRadius + (4f * scale)

        // Frosted background
        drawRoundRect(
            color = if (isDark) Color(0xF20F172A) else Color(0xF2FFFFFF),
            topLeft = Offset(pillX, pillY),
            size = Size(pillW, pillH),
            cornerRadius = CornerRadius(6f * scale, 6f * scale)
        )
        // Hairline border
        drawRoundRect(
            color = if (isSelected) ModernBlack else Color(0x1F000000),
            topLeft = Offset(pillX, pillY),
            size = Size(pillW, pillH),
            cornerRadius = CornerRadius(6f * scale, 6f * scale),
            style = Stroke(width = if (isSelected) 1.5f * scale else 1f * scale)
        )

        drawText(
            textLayoutResult = textLayout,
            topLeft = Offset(pillX + (7f * scale), pillY + (4f * scale))
        )
    }
}

/**
 * Draws modern black & white destination badges (Store Pickup & Customer Dropoff).
 */
private fun DrawScope.drawModernPickupDropoffPins(
    order: DeliveryOrder,
    scale: Float,
    originX: Float,
    originY: Float
) {
    // 1. Pickup Pin (Merchant): Solid Black Disc with Inner White Ring
    val pickupPos = Offset(
        originX + (order.pickupLocation.point.x * scale),
        originY + (order.pickupLocation.point.y * scale)
    )
    drawCircle(color = Color.Black.copy(alpha = 0.25f), radius = 17f * scale, center = pickupPos + Offset(0f, 2f * scale))
    drawCircle(color = ModernBlack, radius = 15f * scale, center = pickupPos)
    drawCircle(color = Color.White, radius = 6f * scale, center = pickupPos)

    // 2. Dropoff Pin (Customer): Glowing Electric Azure Target
    val dropoffPos = Offset(
        originX + (order.dropoffLocation.point.x * scale),
        originY + (order.dropoffLocation.point.y * scale)
    )
    drawCircle(color = ModernElectricBlue.copy(alpha = 0.22f), radius = 24f * scale, center = dropoffPos)
    drawCircle(color = Color.Black.copy(alpha = 0.25f), radius = 17f * scale, center = dropoffPos + Offset(0f, 2f * scale))
    drawCircle(color = ModernElectricBlue, radius = 15f * scale, center = dropoffPos)
    drawCircle(color = Color.White, radius = 6f * scale, center = dropoffPos)
}

/**
 * Draws modern 3D vehicle puck inspired by Uber Eats:
 * Rotating heading arrow, vehicle glyph (e-scooter, moto, car, bike),
 * animated radar ripple, and frosted glass floating speed badge.
 */
private fun DrawScope.drawModernCourierVehicle(
    order: DeliveryOrder,
    screenPos: Offset,
    headingDegrees: Float,
    scale: Float,
    pulseRadius: Float,
    pulseAlpha: Float,
    isDark: Boolean,
    textMeasurer: TextMeasurer
) {
    val vehicle = order.vehicleType
    val accentColor = vehicle.highlightColor

    // 1. Radar pulse ripple
    drawCircle(
        color = accentColor.copy(alpha = pulseAlpha * 0.45f),
        radius = 24f * scale * pulseRadius,
        center = screenPos
    )

    // 2. 3D Puck Base with drop shadow
    drawCircle(
        color = Color.Black.copy(alpha = 0.35f),
        radius = 18f * scale,
        center = screenPos + Offset(0f, 3.5f * scale)
    )
    // Crisp outer white ring
    drawCircle(
        color = if (isDark) Color(0xFF1E293B) else Color.White,
        radius = 17f * scale,
        center = screenPos
    )
    // Deep carbon inner disc
    drawCircle(
        color = ModernBlack,
        radius = 14f * scale,
        center = screenPos
    )

    // 3. Directional Vehicle Pointer & Glyph (Rotating with heading)
    rotate(degrees = headingDegrees, pivot = screenPos) {
        // Forward pointer triangle
        val arrowPath = Path().apply {
            moveTo(screenPos.x, screenPos.y - (19f * scale))
            lineTo(screenPos.x + (6f * scale), screenPos.y - (10f * scale))
            lineTo(screenPos.x - (6f * scale), screenPos.y - (10f * scale))
            close()
        }
        drawPath(path = arrowPath, color = accentColor)

        // Vehicle specific white icon glyph inside the disc
        when (vehicle) {
            VehicleType.MOTORCYCLE -> {
                drawCircle(color = Color.White, radius = 2.8f * scale, center = screenPos + Offset(0f, -4f * scale))
                drawCircle(color = Color.White, radius = 2.8f * scale, center = screenPos + Offset(0f, 4f * scale))
                drawLine(
                    color = Color.White,
                    start = screenPos + Offset(0f, -4f * scale),
                    end = screenPos + Offset(0f, 4f * scale),
                    strokeWidth = 2.5f * scale
                )
            }
            VehicleType.ESCOOTER -> {
                // Micro-mobility E-Scooter
                drawCircle(color = Color.White, radius = 2.2f * scale, center = screenPos + Offset(0f, -5f * scale))
                drawCircle(color = Color.White, radius = 2.2f * scale, center = screenPos + Offset(0f, 5f * scale))
                // Handlebars
                drawLine(
                    color = Color.White,
                    start = screenPos + Offset(-3.5f * scale, -5f * scale),
                    end = screenPos + Offset(3.5f * scale, -5f * scale),
                    strokeWidth = 1.8f * scale
                )
                // Stem & Deck
                drawLine(
                    color = Color.White,
                    start = screenPos + Offset(0f, -5f * scale),
                    end = screenPos + Offset(0f, 5f * scale),
                    strokeWidth = 2.2f * scale
                )
            }
            VehicleType.CAR -> {
                drawRoundRect(
                    color = Color.White,
                    topLeft = screenPos - Offset(4.5f * scale, 7f * scale),
                    size = Size(9f * scale, 14f * scale),
                    cornerRadius = CornerRadius(2.5f * scale, 2.5f * scale)
                )
            }
            VehicleType.BICYCLE -> {
                drawCircle(color = Color.White, radius = 2.5f * scale, center = screenPos + Offset(0f, -5f * scale))
                drawCircle(color = Color.White, radius = 2.5f * scale, center = screenPos + Offset(0f, 5f * scale))
                drawLine(
                    color = Color.White,
                    start = screenPos + Offset(0f, -5f * scale),
                    end = screenPos + Offset(0f, 5f * scale),
                    strokeWidth = 1.5f * scale
                )
            }
        }
    }

    // 4. Floating Frosted Speed & Vehicle Tag
    val speedText = "${order.currentSpeedKmH.toInt()} km/h"
    val speedLayout = textMeasurer.measure(
        text = speedText,
        style = TextStyle(
            fontSize = (8.5f * scale).coerceIn(8f, 11f).sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    )
    val tagW = speedLayout.size.width + (12f * scale)
    val tagH = speedLayout.size.height + (5f * scale)
    val tagX = screenPos.x - (tagW / 2f)
    val tagY = screenPos.y - (30f * scale)

    drawRoundRect(
        color = ModernBlack.copy(alpha = 0.88f),
        topLeft = Offset(tagX, tagY),
        size = Size(tagW, tagH),
        cornerRadius = CornerRadius(6f * scale, 6f * scale)
    )
    drawText(
        textLayoutResult = speedLayout,
        topLeft = Offset(tagX + (6f * scale), tagY + (2.5f * scale))
    )
}

/**
 * Top floating Frosted Glass Status Bar inspired by Uber Eats & Zevoa.
 */
@Composable
private fun FrostedTrackingStatusBar(
    order: DeliveryOrder,
    modifier: Modifier = Modifier
) {
    GlassmorphicCard(
        shape = RoundedCornerShape(20.dp),
        backgroundColor = CardSurface,
        borderColor = BorderStroke,
        blurRadius = 16.dp,
        elevation = 8.dp,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Live pulsing beacon
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(CardSurfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.Navigation,
                    contentDescription = null,
                    tint = AccentEmerald,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (order.status == DeliveryStatus.DELIVERED) "Delivered" else "${order.etaMinutes} MIN",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryText
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${order.status.title}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = if (order.status == DeliveryStatus.DELIVERED) AccentEmerald else AccentBlue
                    )
                }
                Text(
                    text = "${order.dropoffLocation.name} (${order.dropoffLocation.district})",
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText,
                    fontSize = 11.sp
                )
            }
        }
    }
}

/**
 * Floating Map Controls with Frosted Glass look.
 */
@Composable
private fun ModernMapControlsOverlay(
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
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Recenter / Follow Courier button
        GlassmorphicCard(
            shape = CircleShape,
            backgroundColor = if (isFollowCourier) AccentBlue else CardSurface,
            borderColor = if (isFollowCourier) AccentBlue else BorderStroke,
            blurRadius = 16.dp,
            elevation = 6.dp,
            modifier = Modifier.size(44.dp)
        ) {
            IconButton(
                onClick = onToggleFollow,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("btn_follow_courier")
            ) {
                Icon(
                    imageVector = if (isFollowCourier) Icons.Default.MyLocation else Icons.Default.LocationSearching,
                    contentDescription = "Follow Courier",
                    tint = PrimaryText,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Traffic Flow toggle
        GlassmorphicCard(
            shape = CircleShape,
            backgroundColor = if (showTraffic) AccentEmerald else CardSurface,
            borderColor = if (showTraffic) AccentEmerald else BorderStroke,
            blurRadius = 16.dp,
            elevation = 6.dp,
            modifier = Modifier.size(44.dp)
        ) {
            IconButton(
                onClick = onToggleTraffic,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("btn_traffic_overlay")
            ) {
                Icon(
                    imageVector = Icons.Default.Layers,
                    contentDescription = "Traffic Flow",
                    tint = if (showTraffic) CardSurface else PrimaryText,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Zoom Controls Capsule
        GlassmorphicCard(
            shape = RoundedCornerShape(22.dp),
            backgroundColor = CardSurface,
            borderColor = BorderStroke,
            blurRadius = 16.dp,
            elevation = 6.dp,
            modifier = Modifier.width(44.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onZoomIn,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("btn_zoom_in")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = PrimaryText)
                }
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(1.dp)
                        .background(BorderStroke)
                )
                IconButton(
                    onClick = onZoomOut,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("btn_zoom_out")
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = PrimaryText)
                }
            }
        }
    }
}

@Composable
private fun ModernGuelmimBadge(modifier: Modifier = Modifier) {
    GlassmorphicCard(
        shape = RoundedCornerShape(18.dp),
        backgroundColor = CardSurface,
        borderColor = BorderStroke,
        blurRadius = 16.dp,
        elevation = 6.dp,
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
                    .background(CardSurfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.Explore,
                    contentDescription = null,
                    tint = AccentBlue,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Guelmim • كلميم",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
                Text(
                    text = "Bab Sahra • Live Map",
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText,
                    fontSize = 11.sp
                )
            }
        }
    }
}