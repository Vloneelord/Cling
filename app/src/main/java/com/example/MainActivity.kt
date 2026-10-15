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
import androidx.compose.ui.zIndex
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
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
import androidx.compose.ui.draw.shadow
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
import com.example.ui.screens.CockpitScreen
import com.example.ui.screens.DispatchOrderScreen
import com.example.ui.screens.ExploreStoresScreen
import com.example.ui.screens.TrackingScreen
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AppBackground
import com.example.ui.theme.BorderStroke
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceVariant
import com.example.ui.theme.GuelmimExpressTheme
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
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
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
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
        containerColor = AppBackground,
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
                AppScreen.BUSINESS_PORTAL -> {
                    BusinessOrderManagementScreen(
                        uiState = uiState,
                        viewModel = viewModel
                    )
                }
                AppScreen.DRIVER_MODE -> {
                    CockpitScreen(
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

            // Direct Driver Call Dialog (Locked to dark card surface)
            if (showCallDialog) {
                val courier = uiState.currentOrder?.courierName ?: "Courier"
                val phone = uiState.currentOrder?.courierPhone ?: "+212 6 62 44 19 33"
                AlertDialog(
                    onDismissRequest = { showCallDialog = false },
                    containerColor = CardSurface,
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Call, contentDescription = null, tint = AccentBlue)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Calling $courier", fontWeight = FontWeight.Bold, color = PrimaryText)
                        }
                    },
                    text = {
                        Column {
                            Text("Connecting directly to Guelmim courier:", style = MaterialTheme.typography.bodyMedium, color = SecondaryText)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = phone,
                                fontWeight = FontWeight.Black,
                                color = PrimaryText,
                                fontSize = 17.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Livreur is currently in motion with ${uiState.selectedVehicle.title} in Guelmim Centre.",
                                style = MaterialTheme.typography.bodySmall,
                                color = SecondaryText
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { showCallDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Connect Call", color = PrimaryText, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCallDialog = false }) {
                            Text("Cancel", color = SecondaryText)
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
    Surface(
        color = CardSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderStroke),
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(10f)
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
                        .background(CardSurfaceVariant)
                        .border(1.dp, BorderStroke, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeliveryDining,
                        contentDescription = "Guelmim Express",
                        tint = AccentBlue,
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
                            color = PrimaryText
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "كلميم",
                            style = MaterialTheme.typography.titleSmall,
                            color = SecondaryText,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Bab Sahra • Gateway to the Sahara",
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryText,
                        fontSize = 11.sp
                    )
                }
            }

            // Current Vehicle Mode Tag in Top Bar
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = CardSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderStroke)
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
                        tint = AccentAmber,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = activeVehicle.title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryText
                    )
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp)
    ) {
        NavigationBar(
            containerColor = CardSurface,
            tonalElevation = 0.dp,
            modifier = Modifier
                .navigationBarsPadding()
                .border(width = 1.dp, color = BorderStroke)
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
                        selectedIconColor = PrimaryText,
                        selectedTextColor = PrimaryText,
                        indicatorColor = AccentBlue,
                        unselectedIconColor = SecondaryText,
                        unselectedTextColor = SecondaryText
                    ),
                    modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}")
                )
            }
        }
    }
}
