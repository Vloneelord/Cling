package com.example.model

import androidx.compose.ui.graphics.Color

// ============================================================================
// --- CORE DOMAIN MODELS (Task 2 Specifications) ---
// ============================================================================

enum class UserRole {
    CUSTOMER,
    DRIVER,
    MERCHANT
}

data class AuthState(
    val isAuthenticated: Boolean = true,
    val currentUser: String = "Mohamed (Client Guelmim)",
    val role: UserRole = UserRole.CUSTOMER
)

data class MenuItem(
    val id: String,
    val nameEn: String,
    val nameAr: String,
    val priceMad: Double,
    val description: String = ""
)

data class Restaurant(
    val id: String,
    val nameEn: String,
    val nameAr: String,
    val address: String,
    val deliveryTimeMin: Int,
    val rating: Double,
    val menu: List<MenuItem>
)

data class Driver(
    val id: String,
    val name: String,
    val initials: String,
    val rating: Double,
    val deliveriesCount: Int,
    val plateNumber: String,
    val vehicleType: VehicleType,
    val isAvailable: Boolean = true
)

data class VehicleOption(
    val type: VehicleType,
    val label: String,
    val priceMad: Int,
    val ecoMetric: String,
    val tagline: String
)

enum class OrderStatus(val title: String, val arabicTitle: String) {
    PLACED("Order Placed", "تم تأكيد الطلب"),
    PREPARING("Preparing in Kitchen", "جاري التجهيز بالمطبخ"),
    EN_ROUTE("Driver En Route", "السائق في الطريق"),
    NEARBY("Arriving in 1 min", "السائق وصل الحي"),
    DELIVERED("Delivered", "تم التوصيل بنجاح")
}

data class Order(
    val id: String,
    val customerName: String,
    val pickupLocation: String,
    val dropoffLocation: String,
    val itemsSummary: String,
    val totalMad: Double,
    val status: OrderStatus,
    val assignedDriver: Driver?,
    val selectedVehicle: VehicleType,
    val rating: Int? = null,
    val timestamp: String = "Just now"
)

// ============================================================================
// --- VEHICLES & MAP POINTS ---
// ============================================================================

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
        highlightColor = Color(0xFF3B82F6), // AccentBlue
        tagLine = "Agile & fast through bustling souks & alleys"
    ),
    ESCOOTER(
        title = "E-Scooter",
        arabicTitle = "سكوتر كهربائي",
        speedKmH = 25,
        capacityKg = 10,
        baseFareMAD = 8,
        ecoScore = 4,
        highlightColor = Color(0xFF10B981), // AccentEmerald
        tagLine = "Micro-mobility via pedestrian plazas & alleyways"
    ),
    CAR(
        title = "City Car",
        arabicTitle = "سيارة توصيل",
        speedKmH = 32,
        capacityKg = 150,
        baseFareMAD = 22,
        ecoScore = 2,
        highlightColor = Color(0xFF3B82F6), // AccentBlue
        tagLine = "Spacious trunk for groceries & oasis hauls"
    ),
    BICYCLE(
        title = "Eco Bicycle",
        arabicTitle = "دراجة هوائية",
        speedKmH = 18,
        capacityKg = 8,
        baseFareMAD = 7,
        ecoScore = 5,
        highlightColor = Color(0xFFF59E0B), // AccentAmber
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
    DELIVERED("Delivered", "تم التوصيل", 4);

    fun toOrderStatus(): OrderStatus = when (this) {
        PLACED -> OrderStatus.PLACED
        PREPARING -> OrderStatus.PREPARING
        PICKED_UP -> OrderStatus.EN_ROUTE
        NEARBY -> OrderStatus.NEARBY
        DELIVERED -> OrderStatus.DELIVERED
    }
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

// ============================================================================
// --- PRE-POPULATED DATA SEEDS (Task 2 Specifications) ---
// ============================================================================

object SampleDeliveryData {
    val drivers = listOf(
        Driver(
            id = "drv_1",
            name = "Yassine Livreur",
            initials = "YL",
            rating = 4.95,
            deliveriesCount = 1420,
            plateNumber = "48 | 7842 | ب",
            vehicleType = VehicleType.MOTORCYCLE,
            isAvailable = true
        ),
        Driver(
            id = "drv_2",
            name = "Amine Sahraoui",
            initials = "AS",
            rating = 4.88,
            deliveriesCount = 980,
            plateNumber = "48 | 2109 | د",
            vehicleType = VehicleType.ESCOOTER,
            isAvailable = true
        ),
        Driver(
            id = "drv_3",
            name = "Tariq Oasis",
            initials = "TO",
            rating = 4.92,
            deliveriesCount = 1150,
            plateNumber = "48 | 5431 | أ",
            vehicleType = VehicleType.CAR,
            isAvailable = true
        )
    )

    val vehicleOptions = listOf(
        VehicleOption(
            type = VehicleType.MOTORCYCLE,
            label = "Express Moto",
            priceMad = 10,
            ecoMetric = "⚡ 42k",
            tagline = "Agile & fast through bustling souks & alleys"
        ),
        VehicleOption(
            type = VehicleType.ESCOOTER,
            label = "E-Scooter",
            priceMad = 8,
            ecoMetric = "⚡ 25k",
            tagline = "Micro-mobility via pedestrian plazas & alleyways"
        ),
        VehicleOption(
            type = VehicleType.CAR,
            label = "City Car",
            priceMad = 22,
            ecoMetric = "⚡ 32k",
            tagline = "Spacious trunk for groceries & oasis hauls"
        ),
        VehicleOption(
            type = VehicleType.BICYCLE,
            label = "Eco Bicycle",
            priceMad = 7,
            ecoMetric = "⚡ 18k",
            tagline = "100% green & quiet through medina streets"
        )
    )

    val restaurants = listOf(
        Restaurant(
            id = "rest_bab_sahra",
            nameEn = "Restaurant Bab Sahra",
            nameAr = "مطعم باب الصحراء",
            address = "Avenue Mohammed V, Guelmim Centre",
            deliveryTimeMin = 25,
            rating = 4.9,
            menu = listOf(
                MenuItem("m1", "Sahrawi Camel Meat Tajine", "طاجين لحم الإبل الصحراوي", 100.0, "Slow-cooked tender camel meat with caramelised prunes & almonds"),
                MenuItem("m2", "Royal Couscous with Seven Vegetables", "كسكس ملكي بسبع خضار", 85.0, "Steamed semolina topped with tender beef and garden vegetables"),
                MenuItem("m3", "Guelmim Charcoal Mixed Grill", "مشاوي مشكلة على الفحم", 110.0, "Lamb skewers, kefta, and merguez with fresh bread and harissa"),
                MenuItem("m4", "Sahrawi Sahra Mint Tea Pot", "براد أتاي صحراوي بالنعناع", 18.0, "Traditional three-glass ceremonial desert tea")
            )
        ),
        Restaurant(
            id = "rest_tighmert",
            nameEn = "Pâtisserie & Café Tighmert",
            nameAr = "حلويات ومقهى تغمرت",
            address = "Boulevard Hassan II, Hay Oued Noun",
            deliveryTimeMin = 18,
            rating = 4.8,
            menu = listOf(
                MenuItem("m5", "Tighmert Medjool Stuffed Dates (500g)", "تمر مجهول واحة تغمرت فاخر", 65.0, "Organic jumbo Medjool dates filled with walnuts and orange zest"),
                MenuItem("m6", "Almond Cornes de Gazelle Box", "علبة كعب غزال باللوز الحر", 75.0, "Artisanal Moroccan pastry with pure almond paste & orange blossom"),
                MenuItem("m7", "Desert Honey M'semen Plate", "رغايف مسمن بالعسل الحر والسمن", 25.0, "Flaky layered pan-fried crepes served with Sahara wild honey"),
                MenuItem("m8", "Iced Avocado & Almond Shake", "عصير أفوكادو باللوز والتمر", 28.0, "Rich energizing smoothie with fresh Guelmim dairy")
            )
        )
    )
}
