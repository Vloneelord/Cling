package com.example

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.VehicleType
import com.example.ui.components.CourierChatSheet
import com.example.ui.components.CustomerRatingDialog
import com.example.ui.screens.BusinessOrderManagementScreen
import com.example.ui.screens.DispatchOrderScreen
import com.example.ui.screens.DriverModeScreen
import com.example.ui.screens.ExploreStoresScreen
import com.example.ui.screens.TrackingScreen
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import com.example.ui.theme.GlassFrostedBorder
import com.example.ui.theme.GlassFrostedDark
import com.example.ui.theme.GlassFrostedDarkBorder
import com.example.ui.theme.GlassFrostedSurface
import com.example.ui.theme.GuelmimExpressTheme
import com.example.ui.theme.ModernBlack
import com.example.ui.theme.ModernBorder
import com.example.ui.theme.ModernEmerald
import com.example.ui.theme.ModernMuted
import com.example.ui.theme.ModernSurfaceSubtle
import com.example.ui.theme.ModernWhite
import com.example.viewmodel.AppScreen
import com.example.viewmodel.DeliveryViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GuelmimExpressTheme {
                GuelmimExpressApp()
            }
        }
    }
}

@Composable
fun GuelmimExpressApp(viewModel: DeliveryViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var showCallDialog by remember { mutableStateOf(false) }

    // Haptic vibration feedback helper
    fun triggerHapticFeedback() {
        try {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(80)
                }
            }
        } catch (_: Exception) {}
    }

    // Show toast notifications
    LaunchedEffect(uiState.notificationToast) {
        uiState.notificationToast?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    // Back handler: return to Tracking screen if on secondary tabs
    BackHandler(enabled = uiState.activeScreen != AppScreen.TRACKING) {
        viewModel.setScreen(AppScreen.TRACKING)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ModernWhite,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            ModernGuelmimTopBar(
                activeVehicle = uiState.selectedVehicle,
                activeScreen = uiState.activeScreen
            )
        },
        bottomBar = {
            ModernGuelmimBottomNavigationBar(
                activeScreen = uiState.activeScreen,
                onSelectScreen = {
                    triggerHapticFeedback()
                    viewModel.setScreen(it)
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.activeScreen) {
                AppScreen.TRACKING -> {
                    TrackingScreen(
                        uiState = uiState,
                        viewModel = viewModel,
                        onCallCourier = {
                            triggerHapticFeedback()
                            showCallDialog = true
                        }
                    )
                }
                AppScreen.EXPLORE_STORES -> {
                    ExploreStoresScreen(viewModel = viewModel)
                }
                AppScreen.DISPATCH_ORDER -> {
                    DispatchOrderScreen(viewModel = viewModel)
                }
                AppScreen.DRIVER_MODE -> {
                    DriverModeScreen(
                        uiState = uiState,
                        viewModel = viewModel
                    )
                }
                AppScreen.BUSINESS_PORTAL -> {
                    BusinessOrderManagementScreen(
                        uiState = uiState,
                        viewModel = viewModel
                    )
                }
            }

            // Customer Rating Dialog
            if (uiState.showRatingDialog && uiState.orderToRate != null) {
                CustomerRatingDialog(
                    order = uiState.orderToRate!!,
                    onSubmitReview = { rating, comment, tags ->
                        triggerHapticFeedback()
                        viewModel.submitDriverReview(uiState.orderToRate!!.id, rating, comment, tags)
                    },
                    onDismiss = { viewModel.closeRatingDialog() }
                )
            }

            // Chat Bottom Sheet
            if (uiState.showChatSheet) {
                CourierChatSheet(
                    courierName = uiState.currentOrder?.courierName ?: "Livreur",
                    messages = uiState.chatMessages,
                    onSendMessage = {
                        triggerHapticFeedback()
                        viewModel.sendChatMessage(it)
                    },
                    onDismiss = { viewModel.closeChat() }
                )
            }

            // Direct Driver Call Dialog
            if (showCallDialog) {
                val courier = uiState.currentOrder?.courierName ?: "Courier"
                val phone = uiState.currentOrder?.courierPhone ?: "+212 6 62 44 19 33"
                AlertDialog(
                    onDismissRequest = { showCallDialog = false },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Call, contentDescription = null, tint = ModernBlack)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Calling $courier", fontWeight = FontWeight.Bold, color = ModernBlack)
                        }
                    },
                    text = {
                        Column {
                            Text("Connecting directly to Guelmim courier:", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = phone,
                                fontWeight = FontWeight.Black,
                                color = ModernBlack,
                                fontSize = 17.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Livreur is currently in motion with ${uiState.selectedVehicle.title} in Guelmim Centre.",
                                style = MaterialTheme.typography.bodySmall,
                                color = ModernMuted
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { showCallDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = ModernBlack),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Connect Call", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCallDialog = false }) {
                            Text("Cancel", color = ModernMuted)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ModernGuelmimTopBar(
    activeVehicle: VehicleType,
    activeScreen: AppScreen
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) GlassFrostedDark else GlassFrostedSurface
    val borderColor = if (isDark) GlassFrostedDarkBorder else ModernBorder

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp)
    ) {
        // Frosted blur backdrop layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .blur(radius = 16.dp)
                .background(if (isDark) Color(0xD90A0D14) else Color(0xECFFFFFF))
        )
        Surface(
            color = bgColor,
            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(ModernBlack)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeliveryDining,
                            contentDescription = "Guelmim Express",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "GUELMIM EXPRESS",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp,
                                color = ModernBlack
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "كلميم",
                                style = MaterialTheme.typography.titleSmall,
                                color = ModernMuted,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Bab Sahra • Gateway to the Sahara",
                            style = MaterialTheme.typography.bodySmall,
                            color = ModernMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                // Current Vehicle Mode Tag in Top Bar
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = ModernSurfaceSubtle,
                    border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (activeVehicle) {
                                VehicleType.MOTORCYCLE -> Icons.Default.TwoWheeler
                                VehicleType.ESCOOTER -> Icons.Default.ElectricScooter
                                VehicleType.CAR -> Icons.Default.DirectionsCar
                                VehicleType.BICYCLE -> Icons.Default.DirectionsBike
                            },
                            contentDescription = null,
                            tint = ModernBlack,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = activeVehicle.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ModernBlack
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModernGuelmimBottomNavigationBar(
    activeScreen: AppScreen,
    onSelectScreen: (AppScreen) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val navBgColor = if (isDark) GlassFrostedDark else GlassFrostedSurface
    val navBorderColor = if (isDark) GlassFrostedDarkBorder else ModernBorder

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp)
    ) {
        // Frosted blur backdrop layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .blur(radius = 20.dp)
                .background(if (isDark) Color(0xD90A0D14) else Color(0xECFFFFFF))
        )
        NavigationBar(
            containerColor = navBgColor,
            tonalElevation = 0.dp,
            modifier = Modifier
                .navigationBarsPadding()
                .border(width = 1.dp, color = navBorderColor)
                .testTag("guelmim_bottom_nav")
        ) {
            val navItems = listOf(
                Triple(AppScreen.TRACKING, "Track", Icons.Default.Map),
                Triple(AppScreen.EXPLORE_STORES, "Shops", Icons.Default.Storefront),
                Triple(AppScreen.DISPATCH_ORDER, "Send", Icons.Default.LocalShipping),
                Triple(AppScreen.BUSINESS_PORTAL, "Orders", Icons.Default.ReceiptLong),
                Triple(AppScreen.DRIVER_MODE, "Cockpit", Icons.Default.Navigation)
            )

            navItems.forEach { (screen, label, icon) ->
                val isSelected = activeScreen == screen
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onSelectScreen(screen) },
                    alwaysShowLabel = true,
                    icon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = if (isDark) Color.White else ModernBlack,
                        indicatorColor = ModernBlack,
                        unselectedIconColor = Color(0xFF9CA3AF),
                        unselectedTextColor = Color(0xFF9CA3AF)
                    ),
                    modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}")
                )
            }
        }
    }
}
