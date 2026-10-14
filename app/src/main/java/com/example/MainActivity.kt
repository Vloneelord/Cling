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
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.material.icons.filled.ReceiptLong
import com.example.model.VehicleType
import com.example.ui.components.CourierChatSheet
import com.example.ui.components.CustomerRatingDialog
import com.example.ui.screens.BusinessOrderManagementScreen
import com.example.ui.screens.DispatchOrderScreen
import com.example.ui.screens.DriverModeScreen
import com.example.ui.screens.ExploreStoresScreen
import com.example.ui.screens.TrackingScreen
import com.example.ui.theme.GuelmimExpressTheme
import com.example.ui.theme.OasisGreen
import com.example.ui.theme.TerracottaPrimary
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
                    vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(100)
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            GuelmimTopBar(
                activeVehicle = uiState.selectedVehicle,
                activeScreen = uiState.activeScreen
            )
        },
        bottomBar = {
            GuelmimBottomNavigationBar(
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

            // Simulated Phone Call Dialog
            if (showCallDialog) {
                val courier = uiState.currentOrder?.courierName ?: "Courier"
                val phone = uiState.currentOrder?.courierPhone ?: "+212 6 62 44 19 33"
                AlertDialog(
                    onDismissRequest = { showCallDialog = false },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Call, contentDescription = null, tint = OasisGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Calling $courier")
                        }
                    },
                    text = {
                        Column {
                            Text("Dialing Guelmim courier direct line:")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = phone,
                                fontWeight = FontWeight.Bold,
                                color = TerracottaPrimary,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Livreur is currently en route with ${uiState.selectedVehicle.title}.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { showCallDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = OasisGreen)
                        ) {
                            Text("Simulate Call")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCallDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun GuelmimTopBar(
    activeVehicle: VehicleType,
    activeScreen: AppScreen
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 3.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(TerracottaPrimary)
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
                            text = "Guelmim Express",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "كلميم",
                            style = MaterialTheme.typography.titleSmall,
                            color = TerracottaPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Bab Sahra • Gateway to the Sahara",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = 11.sp
                    )
                }
            }

            // Current Vehicle Tag in Top Bar
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = activeVehicle.highlightColor.copy(alpha = 0.14f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (activeVehicle) {
                            VehicleType.MOTORCYCLE -> Icons.Default.TwoWheeler
                            VehicleType.CAR -> Icons.Default.DirectionsCar
                            VehicleType.BICYCLE -> Icons.Default.DirectionsBike
                        },
                        contentDescription = null,
                        tint = activeVehicle.highlightColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = activeVehicle.title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = activeVehicle.highlightColor
                    )
                }
            }
        }
    }
}

@Composable
fun GuelmimBottomNavigationBar(
    activeScreen: AppScreen,
    onSelectScreen: (AppScreen) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier
            .navigationBarsPadding()
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
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = TerracottaPrimary,
                    indicatorColor = TerracottaPrimary
                ),
                modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}")
            )
        }
    }
}
