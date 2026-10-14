package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GuelmimMapData
import com.example.model.DeliveryOrder
import com.example.model.DeliveryStatus
import com.example.model.DriverProfile
import com.example.model.DriverReview
import com.example.model.GuelmimLocation
import com.example.model.GuelmimPoint
import com.example.model.NavigationStep
import com.example.model.Store
import com.example.model.StoreProduct
import com.example.model.VehicleType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.atan2

enum class AppScreen {
    TRACKING,
    EXPLORE_STORES,
    DISPATCH_ORDER,
    DRIVER_MODE,
    BUSINESS_PORTAL
}

data class ChatMessage(
    val id: String,
    val sender: String,
    val text: String,
    val timestamp: String,
    val isFromUser: Boolean
)

data class DeliveryUiState(
    val activeScreen: AppScreen = AppScreen.TRACKING,
    val currentOrder: DeliveryOrder? = null,
    val allOrders: List<DeliveryOrder> = emptyList(),
    val availableDrivers: List<DriverProfile> = GuelmimMapData.sampleDrivers,
    val reviews: List<DriverReview> = GuelmimMapData.initialReviews,
    val selectedVehicle: VehicleType = VehicleType.MOTORCYCLE,
    val isSimulating: Boolean = true,
    val simulationMultiplier: Float = 1.0f,
    val isFollowCourier: Boolean = true,
    val showTrafficOverlay: Boolean = false,
    val selectedLocation: GuelmimLocation? = null,
    val showChatSheet: Boolean = false,
    val chatMessages: List<ChatMessage> = emptyList(),
    val completedOrdersCount: Int = 0,
    val notificationToast: String? = null,
    val driverNotificationBanner: String? = null,
    val showRatingDialog: Boolean = false,
    val orderToRate: DeliveryOrder? = null,
    val selectedBusinessStore: Store = GuelmimMapData.sampleStores.first()
)

class DeliveryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DeliveryUiState())
    val uiState: StateFlow<DeliveryUiState> = _uiState.asStateFlow()

    private var simulationJob: Job? = null

    init {
        initInitialOrders(VehicleType.MOTORCYCLE)
        startSimulationLoop()
        initDefaultChatMessages()
    }

    private fun initDefaultChatMessages() {
        val initialMessages = listOf(
            ChatMessage("m1", "Yassine Livreur", "Salam alaykoum! I'm picking up your order from Restaurant Bab Sahra now.", "12:15", false),
            ChatMessage("m2", "You", "Wa alaykoum salam! Please call when you are near the entrance.", "12:16", true),
            ChatMessage("m3", "Yassine Livreur", "Sure thing! Heading through Avenue Mohammed V now.", "12:18", false)
        )
        _uiState.update { it.copy(chatMessages = initialMessages) }
    }

    fun setScreen(screen: AppScreen) {
        _uiState.update { it.copy(activeScreen = screen) }
    }

    fun selectVehicle(vehicleType: VehicleType) {
        val current = _uiState.value.currentOrder ?: return
        val currentProgress = current.progressFraction

        // Recompute route for the newly chosen vehicle
        val newRoute = GuelmimMapData.buildRoutePath(
            from = current.pickupLocation.point,
            to = current.dropoffLocation.point,
            vehicle = vehicleType
        )

        val newSteps = GuelmimMapData.buildNavigationSteps(
            vehicle = vehicleType,
            pickupName = current.pickupLocation.name,
            dropoffName = current.dropoffLocation.name
        )

        val stepIndex = ((currentProgress * (newSteps.size - 1)).toInt()).coerceIn(0, (newSteps.size - 1).coerceAtLeast(0))

        val updatedOrder = current.copy(
            vehicleType = vehicleType,
            deliveryFeeMAD = vehicleType.baseFareMAD.toDouble(),
            routePoints = newRoute,
            currentStep = newSteps[stepIndex],
            currentSpeedKmH = vehicleType.speedKmH.toFloat()
        )

        val updatedAll = _uiState.value.allOrders.map {
            if (it.id == updatedOrder.id) updatedOrder else it
        }

        _uiState.update {
            it.copy(
                selectedVehicle = vehicleType,
                currentOrder = updatedOrder,
                allOrders = updatedAll,
                notificationToast = "Switched to ${vehicleType.title} (${vehicleType.arabicTitle})"
            )
        }
    }

    fun toggleSimulation() {
        _uiState.update { it.copy(isSimulating = !it.isSimulating) }
    }

    fun setSimulationMultiplier(multiplier: Float) {
        _uiState.update { it.copy(simulationMultiplier = multiplier) }
    }

    fun toggleFollowCourier() {
        _uiState.update { it.copy(isFollowCourier = !it.isFollowCourier) }
    }

    fun toggleTrafficOverlay() {
        _uiState.update { it.copy(showTrafficOverlay = !it.showTrafficOverlay) }
    }

    fun selectLocation(location: GuelmimLocation?) {
        _uiState.update { it.copy(selectedLocation = location) }
    }

    fun openChat() {
        _uiState.update { it.copy(showChatSheet = true) }
    }

    fun closeChat() {
        _uiState.update { it.copy(showChatSheet = false) }
    }

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = ChatMessage(
            id = "user_${System.currentTimeMillis()}",
            sender = "You",
            text = text,
            timestamp = "Just now",
            isFromUser = true
        )
        val updated = _uiState.value.chatMessages + userMsg
        _uiState.update { it.copy(chatMessages = updated) }

        // Simulated reply after delay
        viewModelScope.launch {
            delay(1500)
            val replyMsg = ChatMessage(
                id = "courier_${System.currentTimeMillis()}",
                sender = _uiState.value.currentOrder?.courierName ?: "Livreur",
                text = "Tamam, received your message! Near Bab Sahra roundabout now.",
                timestamp = "Just now",
                isFromUser = false
            )
            _uiState.update { it.copy(chatMessages = it.chatMessages + replyMsg) }
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(notificationToast = null) }
    }

    fun dismissDriverNotification() {
        _uiState.update { it.copy(driverNotificationBanner = null) }
    }

    fun resetSimulation() {
        val current = _uiState.value.currentOrder ?: return
        val newRoute = GuelmimMapData.buildRoutePath(
            from = current.pickupLocation.point,
            to = current.dropoffLocation.point,
            vehicle = current.vehicleType
        )
        val resetOrder = current.copy(
            progressFraction = 0f,
            currentPosition = newRoute.first(),
            status = DeliveryStatus.PREPARING,
            remainingDistanceMeters = 2400,
            etaMinutes = (2400f / (current.vehicleType.speedKmH * 1000f / 60f)).toInt().coerceAtLeast(3)
        )
        val updatedAll = _uiState.value.allOrders.map {
            if (it.id == resetOrder.id) resetOrder else it
        }
        _uiState.update {
            it.copy(
                currentOrder = resetOrder,
                allOrders = updatedAll,
                isSimulating = true,
                notificationToast = "Delivery restarted from ${current.pickupLocation.name}"
            )
        }
    }

    // --- Customer Rating & Review System ---

    fun openRatingDialog(order: DeliveryOrder) {
        _uiState.update {
            it.copy(
                showRatingDialog = true,
                orderToRate = order
            )
        }
    }

    fun closeRatingDialog() {
        _uiState.update {
            it.copy(
                showRatingDialog = false,
                orderToRate = null
            )
        }
    }

    fun submitDriverReview(
        orderId: String,
        rating: Int,
        comment: String,
        tags: List<String>
    ) {
        val targetOrder = _uiState.value.allOrders.find { it.id == orderId } ?: _uiState.value.currentOrder
        val driverName = targetOrder?.courierName ?: "Yassine Livreur"

        val newReview = DriverReview(
            id = "rev_${System.currentTimeMillis()}",
            orderId = orderId,
            driverName = driverName,
            customerName = "Mohamed (Client Guelmim)",
            rating = rating.coerceIn(1, 5),
            comment = comment.ifBlank { "Delivery arrived safely in Guelmim. Excellent service!" },
            tags = tags.ifEmpty { listOf("Fast Delivery", "Polite") },
            timestamp = "Just now"
        )

        // Update driver's aggregated rating in available drivers list
        val updatedDrivers = _uiState.value.availableDrivers.map { driver ->
            if (driver.name == driverName) {
                val newCount = driver.totalDeliveries + 1
                val newRatingScore = ((driver.rating * driver.totalDeliveries + rating) / newCount)
                driver.copy(
                    rating = Math.round(newRatingScore * 100.0) / 100.0,
                    totalDeliveries = newCount
                )
            } else {
                driver
            }
        }

        // Attach review to target order
        val updatedOrders = _uiState.value.allOrders.map { ord ->
            if (ord.id == orderId) {
                ord.copy(review = newReview)
            } else {
                ord
            }
        }

        val updatedCurrent = if (_uiState.value.currentOrder?.id == orderId) {
            _uiState.value.currentOrder?.copy(review = newReview)
        } else {
            _uiState.value.currentOrder
        }

        _uiState.update {
            it.copy(
                reviews = listOf(newReview) + it.reviews,
                allOrders = updatedOrders,
                currentOrder = updatedCurrent,
                availableDrivers = updatedDrivers,
                showRatingDialog = false,
                orderToRate = null,
                notificationToast = "★ $rating-Star Review submitted for $driverName!"
            )
        }
    }

    // --- Real-Time Business Order Management System ---

    fun selectBusinessStore(store: Store) {
        _uiState.update { it.copy(selectedBusinessStore = store) }
    }

    fun updateBusinessOrderStatus(orderId: String, newStatus: DeliveryStatus) {
        val updatedOrders = _uiState.value.allOrders.map { order ->
            if (order.id == orderId) {
                val updated = order.copy(status = newStatus)
                // If now marked delivered, offer rating prompt
                if (newStatus == DeliveryStatus.DELIVERED) {
                    openRatingDialog(updated)
                }
                updated
            } else {
                order
            }
        }

        val updatedCurrent = if (_uiState.value.currentOrder?.id == orderId) {
            _uiState.value.currentOrder?.copy(status = newStatus)
        } else {
            _uiState.value.currentOrder
        }

        _uiState.update {
            it.copy(
                allOrders = updatedOrders,
                currentOrder = updatedCurrent,
                notificationToast = "Order $orderId status updated to ${newStatus.title}"
            )
        }
    }

    fun assignDriverToOrder(orderId: String, driverId: String) {
        val driver = _uiState.value.availableDrivers.find { it.id == driverId } ?: return

        val updatedOrders = _uiState.value.allOrders.map { order ->
            if (order.id == orderId) {
                val newRoute = GuelmimMapData.buildRoutePath(order.pickupLocation.point, order.dropoffLocation.point, driver.vehicleType)
                val newSteps = GuelmimMapData.buildNavigationSteps(driver.vehicleType, order.pickupLocation.name, order.dropoffLocation.name)

                order.copy(
                    courierName = driver.name,
                    courierPhone = driver.phone,
                    courierPlate = driver.vehiclePlate,
                    courierRating = driver.rating,
                    vehicleType = driver.vehicleType,
                    routePoints = newRoute,
                    currentPosition = newRoute.first(),
                    currentStep = newSteps.first(),
                    status = DeliveryStatus.PREPARING
                )
            } else {
                order
            }
        }

        val updatedCurrent = if (_uiState.value.currentOrder?.id == orderId) {
            updatedOrders.find { it.id == orderId }
        } else {
            _uiState.value.currentOrder
        }

        _uiState.update {
            it.copy(
                allOrders = updatedOrders,
                currentOrder = updatedCurrent,
                notificationToast = "Assigned ${driver.name} to order #$orderId",
                driverNotificationBanner = "🔔 DISPATCH ALERT: ${driver.name} received new assignment for #$orderId!"
            )
        }
    }

    // --- Order Dispatching ---

    fun dispatchNewOrder(
        pickup: GuelmimLocation,
        dropoff: GuelmimLocation,
        vehicle: VehicleType,
        itemsDescription: String,
        subtotalMAD: Double
    ) {
        val route = GuelmimMapData.buildRoutePath(pickup.point, dropoff.point, vehicle)
        val steps = GuelmimMapData.buildNavigationSteps(vehicle, pickup.name, dropoff.name)
        val distEstMeters = (pickup.point.distanceTo(dropoff.point) * 5.2f).toInt().coerceAtLeast(800)
        val speedMpm = (vehicle.speedKmH * 1000f / 60f)
        val etaMin = (distEstMeters / speedMpm).toInt().coerceAtLeast(4)

        val newOrder = DeliveryOrder(
            id = "ORD-${(1000..9999).random()}",
            merchantName = pickup.name,
            merchantArabic = pickup.arabicName,
            pickupLocation = pickup,
            dropoffLocation = dropoff,
            itemsSummary = itemsDescription,
            subtotalMAD = subtotalMAD,
            deliveryFeeMAD = vehicle.baseFareMAD.toDouble(),
            vehicleType = vehicle,
            status = DeliveryStatus.PLACED,
            courierName = "Yassine Livreur",
            courierPhone = "+212 6 62 44 19 33",
            courierPlate = "48 | ب | 7842",
            courierRating = 4.95,
            routePoints = route,
            currentPosition = route.first(),
            progressFraction = 0.05f,
            headingDegrees = 45f,
            currentSpeedKmH = vehicle.speedKmH.toFloat(),
            remainingDistanceMeters = distEstMeters,
            etaMinutes = etaMin,
            currentStep = steps.first(),
            customerName = "Mohamed (Client Guelmim)",
            createdAt = "Just now"
        )

        _uiState.update {
            it.copy(
                currentOrder = newOrder,
                allOrders = listOf(newOrder) + it.allOrders,
                selectedVehicle = vehicle,
                activeScreen = AppScreen.TRACKING,
                isSimulating = true,
                notificationToast = "New delivery dispatched via ${vehicle.title}!",
                driverNotificationBanner = "🔔 Driver Alert: New order #${newOrder.id} received at ${pickup.name}!"
            )
        }
    }

    fun orderFromStore(store: Store, product: StoreProduct) {
        dispatchNewOrder(
            pickup = store.location,
            dropoff = GuelmimMapData.HAY_AL_QUDS,
            vehicle = _uiState.value.selectedVehicle,
            itemsDescription = "${product.name} (1x)",
            subtotalMAD = product.priceMAD
        )
    }

    private fun initInitialOrders(vehicle: VehicleType) {
        val pickup = GuelmimMapData.CENTRE_VILLE
        val dropoff = GuelmimMapData.HAY_AL_QUDS
        val route = GuelmimMapData.buildRoutePath(pickup.point, dropoff.point, vehicle)
        val steps = GuelmimMapData.buildNavigationSteps(vehicle, pickup.name, dropoff.name)

        val activeOrder = DeliveryOrder(
            id = "ORD-7391",
            merchantName = "Restaurant Bab Sahra",
            merchantArabic = "مطعم باب الصحراء",
            pickupLocation = pickup,
            dropoffLocation = dropoff,
            itemsSummary = "Sahrawi Camel Meat Tajine & Mint Tea Set",
            subtotalMAD = 93.0,
            deliveryFeeMAD = vehicle.baseFareMAD.toDouble(),
            vehicleType = vehicle,
            status = DeliveryStatus.PICKED_UP,
            courierName = "Yassine Livreur",
            courierPhone = "+212 6 62 44 19 33",
            courierPlate = "48 | ب | 7842",
            courierRating = 4.95,
            routePoints = route,
            currentPosition = route.first(),
            progressFraction = 0.25f,
            headingDegrees = 30f,
            currentSpeedKmH = vehicle.speedKmH.toFloat(),
            remainingDistanceMeters = 1850,
            etaMinutes = 6,
            currentStep = steps[1],
            customerName = "Mohamed (Hay Al Quds)",
            createdAt = "12:15"
        )

        val newPendingOrder = DeliveryOrder(
            id = "ORD-8812",
            merchantName = "Restaurant Bab Sahra",
            merchantArabic = "مطعم باب الصحراء",
            pickupLocation = GuelmimMapData.CENTRE_VILLE,
            dropoffLocation = GuelmimMapData.HAY_AL_WAHDA,
            itemsSummary = "Royal Guelmim Couscous (2x) & Moroccan Salad",
            subtotalMAD = 130.0,
            deliveryFeeMAD = 10.0,
            vehicleType = VehicleType.MOTORCYCLE,
            status = DeliveryStatus.PLACED,
            courierName = "Unassigned",
            courierPhone = "-",
            courierPlate = "-",
            courierRating = 5.0,
            routePoints = GuelmimMapData.buildRoutePath(GuelmimMapData.CENTRE_VILLE.point, GuelmimMapData.HAY_AL_WAHDA.point, VehicleType.MOTORCYCLE),
            currentPosition = GuelmimMapData.CENTRE_VILLE.point,
            progressFraction = 0.0f,
            headingDegrees = 0f,
            currentSpeedKmH = 0f,
            remainingDistanceMeters = 1400,
            etaMinutes = 12,
            currentStep = GuelmimMapData.buildNavigationSteps(VehicleType.MOTORCYCLE, "Restaurant Bab Sahra", "Hay Al Wahda").first(),
            customerName = "Karim B. (Hay Al Wahda)",
            createdAt = "12:28"
        )

        val preparingOrder = DeliveryOrder(
            id = "ORD-8450",
            merchantName = "Restaurant Bab Sahra",
            merchantArabic = "مطعم باب الصحراء",
            pickupLocation = GuelmimMapData.CENTRE_VILLE,
            dropoffLocation = GuelmimMapData.HAY_OUED_NOUN,
            itemsSummary = "Grilled Kefta & Merguez Family Pack",
            subtotalMAD = 180.0,
            deliveryFeeMAD = 22.0,
            vehicleType = VehicleType.CAR,
            status = DeliveryStatus.PREPARING,
            courierName = "Redouane Noun",
            courierPhone = "+212 6 61 88 92 10",
            courierPlate = "48 | أ | 1290",
            courierRating = 4.88,
            routePoints = GuelmimMapData.buildRoutePath(GuelmimMapData.CENTRE_VILLE.point, GuelmimMapData.HAY_OUED_NOUN.point, VehicleType.CAR),
            currentPosition = GuelmimMapData.CENTRE_VILLE.point,
            progressFraction = 0.0f,
            headingDegrees = 90f,
            currentSpeedKmH = 0f,
            remainingDistanceMeters = 2100,
            etaMinutes = 15,
            currentStep = GuelmimMapData.buildNavigationSteps(VehicleType.CAR, "Restaurant Bab Sahra", "Hay Oued Noun").first(),
            customerName = "Amina S. (Hay Oued Noun)",
            createdAt = "12:20"
        )

        val deliveredOrder = DeliveryOrder(
            id = "ORD-6821",
            merchantName = "Restaurant Bab Sahra",
            merchantArabic = "مطعم باب الصحراء",
            pickupLocation = GuelmimMapData.CENTRE_VILLE,
            dropoffLocation = GuelmimMapData.OASIS_TIGHMERT,
            itemsSummary = "Sahrawi Camel Meat Tajine & Mint Tea Set",
            subtotalMAD = 93.0,
            deliveryFeeMAD = 10.0,
            vehicleType = VehicleType.MOTORCYCLE,
            status = DeliveryStatus.DELIVERED,
            courierName = "Yassine Livreur",
            courierPhone = "+212 6 62 44 19 33",
            courierPlate = "48 | ب | 7842",
            courierRating = 4.95,
            routePoints = route,
            currentPosition = GuelmimMapData.OASIS_TIGHMERT.point,
            progressFraction = 1.0f,
            headingDegrees = 90f,
            currentSpeedKmH = 0f,
            remainingDistanceMeters = 0,
            etaMinutes = 0,
            currentStep = steps.last(),
            customerName = "Fatima Z. (Oasis de Tighmert)",
            review = GuelmimMapData.initialReviews.first(),
            createdAt = "11:45"
        )

        _uiState.update {
            it.copy(
                currentOrder = activeOrder,
                allOrders = listOf(activeOrder, newPendingOrder, preparingOrder, deliveredOrder),
                selectedVehicle = vehicle
            )
        }
    }

    private fun startSimulationLoop() {
        simulationJob?.cancel()
        simulationJob = viewModelScope.launch {
            while (true) {
                delay(200) // 5 updates per second for smooth rendering

                val state = _uiState.value
                val order = state.currentOrder

                if (order != null && state.isSimulating && order.status != DeliveryStatus.DELIVERED) {
                    val route = order.routePoints
                    if (route.size >= 2) {
                        val speedMult = state.simulationMultiplier
                        // Step increment based on vehicle speed
                        val baseIncrement = (order.vehicleType.speedKmH / 40f) * 0.0035f * speedMult
                        var newProgress = order.progressFraction + baseIncrement

                        if (newProgress >= 1.0f) {
                            newProgress = 1.0f
                        }

                        // Interpolate position along polyline points
                        val totalSegments = route.size - 1
                        val scaled = newProgress * totalSegments
                        val segmentIndex = scaled.toInt().coerceIn(0, totalSegments - 1)
                        val segmentFrac = (scaled - segmentIndex).coerceIn(0f, 1f)

                        val p1 = route[segmentIndex]
                        val p2 = route[segmentIndex + 1]

                        val curX = p1.x + (p2.x - p1.x) * segmentFrac
                        val curY = p1.y + (p2.y - p1.y) * segmentFrac
                        val curPos = GuelmimPoint(curX, curY)

                        // Calculate heading angle
                        val dx = p2.x - p1.x
                        val dy = p2.y - p1.y
                        val heading = (Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat() + 90f + 360f) % 360f

                        // Status transitions based on progress
                        val newStatus = when {
                            newProgress >= 0.98f -> DeliveryStatus.DELIVERED
                            newProgress >= 0.82f -> DeliveryStatus.NEARBY
                            newProgress >= 0.20f -> DeliveryStatus.PICKED_UP
                            newProgress >= 0.05f -> DeliveryStatus.PREPARING
                            else -> DeliveryStatus.PLACED
                        }

                        val steps = GuelmimMapData.buildNavigationSteps(
                            order.vehicleType,
                            order.pickupLocation.name,
                            order.dropoffLocation.name
                        )
                        val stepIdx = (newProgress * (steps.size - 1)).toInt().coerceIn(0, (steps.size - 1).coerceAtLeast(0))

                        val totalDistanceMeters = 2400
                        val remainingMeters = ((1f - newProgress) * totalDistanceMeters).toInt()
                        val speedMpm = (order.vehicleType.speedKmH * 1000f / 60f)
                        val etaMin = (remainingMeters / speedMpm).toInt().coerceAtLeast(if (newProgress < 0.98f) 1 else 0)

                        val updatedOrder = order.copy(
                            progressFraction = newProgress,
                            currentPosition = curPos,
                            headingDegrees = heading,
                            status = newStatus,
                            currentStep = steps[stepIdx],
                            remainingDistanceMeters = remainingMeters,
                            etaMinutes = etaMin,
                            currentSpeedKmH = if (newProgress >= 0.98f) 0f else (order.vehicleType.speedKmH * (0.85f + (Math.sin(newProgress.toDouble() * 10.0).toFloat() * 0.15f)))
                        )

                        val wasJustDelivered = newProgress >= 0.98f && order.progressFraction < 0.98f

                        val updatedAll = state.allOrders.map {
                            if (it.id == updatedOrder.id) updatedOrder else it
                        }

                        _uiState.update {
                            it.copy(
                                currentOrder = updatedOrder,
                                allOrders = updatedAll,
                                completedOrdersCount = if (wasJustDelivered) it.completedOrdersCount + 1 else it.completedOrdersCount,
                                showRatingDialog = if (wasJustDelivered && updatedOrder.review == null) true else it.showRatingDialog,
                                orderToRate = if (wasJustDelivered && updatedOrder.review == null) updatedOrder else it.orderToRate
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        simulationJob?.cancel()
    }
}
