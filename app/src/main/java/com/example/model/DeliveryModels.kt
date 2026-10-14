package com.example.model

import androidx.compose.ui.graphics.Color

enum class VehicleType(
    val title: String,
    val arabicTitle: String,
    val speedKmH: Int,
    val capacityKg: Int,
    val baseFareMAD: Int,
    val ecoScore: Int, // 1 to 5
    val highlightColor: Color,
    val tagLine: String
) {
    MOTORCYCLE(
        title = "Express Moto",
        arabicTitle = "سكوتر سريع",
        speedKmH = 42,
        capacityKg = 15,
        baseFareMAD = 10,
        ecoScore = 3,
        highlightColor = Color(0xFFC85A32),
        tagLine = "Agile & fast through bustling souks & alleys"
    ),
    CAR(
        title = "City Car",
        arabicTitle = "سيارة توصيل",
        speedKmH = 32,
        capacityKg = 150,
        baseFareMAD = 22,
        ecoScore = 2,
        highlightColor = Color(0xFFD9822B),
        tagLine = "Spacious trunk for groceries & oasis hauls"
    ),
    BICYCLE(
        title = "Eco Bicycle",
        arabicTitle = "دراجة هوائية",
        speedKmH = 18,
        capacityKg = 8,
        baseFareMAD = 7,
        ecoScore = 5,
        highlightColor = Color(0xFF2C6E49),
        tagLine = "100% green & quiet through medina streets"
    )
}

data class GuelmimPoint(
    val x: Float, // Normalized coordinate 0f..1000f
    val y: Float
) {
    fun distanceTo(other: GuelmimPoint): Float {
        val dx = x - other.x
        val dy = y - other.y
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }
}

enum class LocationCategory {
    LANDMARK, RESTAURANT, MARKET, PHARMACY, OASIS, RESIDENCE
}

data class GuelmimLocation(
    val id: String,
    val name: String,
    val arabicName: String,
    val district: String,
    val point: GuelmimPoint,
    val category: LocationCategory,
    val description: String
)

enum class DeliveryStatus(
    val title: String,
    val arabicTitle: String,
    val stepIndex: Int
) {
    PLACED("Order Placed", "تم تأكيد الطلب", 0),
    PREPARING("Preparing in Guelmim", "جاري التجهيز", 1),
    PICKED_UP("Courier En Route", "السائق في الطريق", 2),
    NEARBY("Arriving in 1 min", "السائق وصل الحي", 3),
    DELIVERED("Delivered", "تم التوصيل", 4)
}

data class NavigationStep(
    val instruction: String,
    val arabicInstruction: String,
    val distanceMeters: Int,
    val maneuver: String // STRAIGHT, TURN_RIGHT, TURN_LEFT, ROUNDABOUT, ARRIVED
)

data class DriverReview(
    val id: String,
    val orderId: String,
    val driverName: String,
    val customerName: String,
    val rating: Int, // 1 to 5 stars
    val comment: String,
    val tags: List<String>,
    val timestamp: String
)

data class DriverProfile(
    val id: String,
    val name: String,
    val phone: String,
    val vehicleType: VehicleType,
    val vehiclePlate: String,
    val rating: Double,
    val totalDeliveries: Int,
    val isAvailable: Boolean,
    val currentNeighborhood: String
)

data class DeliveryOrder(
    val id: String,
    val merchantName: String,
    val merchantArabic: String,
    val pickupLocation: GuelmimLocation,
    val dropoffLocation: GuelmimLocation,
    val itemsSummary: String,
    val subtotalMAD: Double,
    val deliveryFeeMAD: Double,
    val vehicleType: VehicleType,
    val status: DeliveryStatus,
    val courierName: String,
    val courierPhone: String,
    val courierPlate: String,
    val courierRating: Double,
    val routePoints: List<GuelmimPoint>,
    val currentPosition: GuelmimPoint,
    val progressFraction: Float, // 0f .. 1f
    val headingDegrees: Float,
    val currentSpeedKmH: Float,
    val remainingDistanceMeters: Int,
    val etaMinutes: Int,
    val currentStep: NavigationStep,
    val customerName: String = "Mohamed (Client Guelmim)",
    val review: DriverReview? = null,
    val createdAt: String = "Today, 12:30"
)

enum class StoreCategory(val label: String, val arabicLabel: String) {
    ALL("All", "الكل"),
    RESTAURANTS("Food & Grills", "مطاعم ومشاوي"),
    SOUK_DATES("Sahara Dates & Souk", "تمور ومكسرات"),
    PATISSERIE("Pastries & Café", "حلويات ومقاهي"),
    PHARMACY("Pharmacies", "صيدليات")
}

data class StoreProduct(
    val id: String,
    val name: String,
    val arabicName: String,
    val priceMAD: Double,
    val description: String
)

data class Store(
    val id: String,
    val name: String,
    val arabicName: String,
    val category: StoreCategory,
    val location: GuelmimLocation,
    val rating: Double,
    val reviewCount: Int,
    val avgDeliveryMin: Int,
    val tag: String,
    val products: List<StoreProduct>
)
