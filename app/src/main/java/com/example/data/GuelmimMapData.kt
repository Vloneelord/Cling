package com.example.data

import com.example.model.DriverProfile
import com.example.model.DriverReview
import com.example.model.GuelmimLocation
import com.example.model.GuelmimPoint
import com.example.model.LocationCategory
import com.example.model.NavigationStep
import com.example.model.Store
import com.example.model.StoreCategory
import com.example.model.StoreProduct
import com.example.model.VehicleType

object GuelmimMapData {

    // Key Guelmim city locations
    val BAB_SAHRA = GuelmimLocation(
        id = "loc_bab_sahra",
        name = "Bab Sahra (Monument Gate)",
        arabicName = "باب الصحراء",
        district = "Bab Sahra South",
        point = GuelmimPoint(500f, 750f),
        category = LocationCategory.LANDMARK,
        description = "The iconic monumental Sahara gate, symbol of Guelmim"
    )

    val BIR_ANZARANE = GuelmimLocation(
        id = "loc_bir_anzarane",
        name = "Place Bir Anzarane",
        arabicName = "ساحة بئر أنزران",
        district = "Centre Ville",
        point = GuelmimPoint(480f, 460f),
        category = LocationCategory.LANDMARK,
        description = "Central landmark square and vibrant roundabout"
    )

    val SOUK_AMHIRICH = GuelmimLocation(
        id = "loc_souk_amhirich",
        name = "Souk Amhirich",
        arabicName = "سوق أمحيريش",
        district = "Hay Amhirich",
        point = GuelmimPoint(220f, 620f),
        category = LocationCategory.MARKET,
        description = "Famous historic camel & livestock Sahara market"
    )

    val HAY_AL_QUDS = GuelmimLocation(
        id = "loc_al_quds",
        name = "Hay Al Quds",
        arabicName = "حي القدس",
        district = "Al Quds North",
        point = GuelmimPoint(640f, 250f),
        category = LocationCategory.RESIDENCE,
        description = "Northern residential neighborhood with schools and plazas"
    )

    val HAY_AL_WAHDA = GuelmimLocation(
        id = "loc_al_wahda",
        name = "Hay Al Wahda",
        arabicName = "حي الوحدة",
        district = "Al Wahda West",
        point = GuelmimPoint(320f, 370f),
        category = LocationCategory.RESIDENCE,
        description = "Vibrant residential quarter with local commerce"
    )

    val HAY_OUED_NOUN = GuelmimLocation(
        id = "loc_oued_noun",
        name = "Hay Oued Noun",
        arabicName = "حي واد نون",
        district = "Oued Noun East",
        point = GuelmimPoint(720f, 560f),
        category = LocationCategory.RESIDENCE,
        description = "Residential district alongside the historic valley"
    )

    val OASIS_TIGHMERT = GuelmimLocation(
        id = "loc_tighmert",
        name = "Oasis de Tighmert",
        arabicName = "واحة تيغميرت",
        district = "Tighmert Road",
        point = GuelmimPoint(880f, 380f),
        category = LocationCategory.OASIS,
        description = "Lush traditional date palm oasis east of Guelmim"
    )

    val CENTRE_VILLE = GuelmimLocation(
        id = "loc_centre",
        name = "Avenue Mohammed V",
        arabicName = "شارع محمد الخامس",
        district = "Centre Ville",
        point = GuelmimPoint(480f, 340f),
        category = LocationCategory.RESTAURANT,
        description = "Main commercial avenue with cafes and restaurants"
    )

    val HAY_SALAM = GuelmimLocation(
        id = "loc_hay_salam",
        name = "Hay Salam",
        arabicName = "حي السلام",
        district = "Salam South",
        point = GuelmimPoint(360f, 780f),
        category = LocationCategory.RESIDENCE,
        description = "Southern residential district near ring road"
    )

    val ASRIR = GuelmimLocation(
        id = "loc_asrir",
        name = "Asrir Oasis",
        arabicName = "أسرير",
        district = "Asrir Oasis South",
        point = GuelmimPoint(820f, 760f),
        category = LocationCategory.OASIS,
        description = "Historic oasis community south-east of Guelmim"
    )

    val allLocations = listOf(
        BAB_SAHRA,
        BIR_ANZARANE,
        SOUK_AMHIRICH,
        HAY_AL_QUDS,
        HAY_AL_WAHDA,
        HAY_OUED_NOUN,
        OASIS_TIGHMERT,
        CENTRE_VILLE,
        HAY_SALAM,
        ASRIR
    )

    // Major avenues for map rendering
    data class MapRoad(
        val name: String,
        val arabicName: String,
        val points: List<GuelmimPoint>,
        val isArterial: Boolean = false,
        val isBoulevard: Boolean = false
    )

    val cityRoads = listOf(
        // Avenue Mohammed V (Main North-South)
        MapRoad(
            name = "Av. Mohammed V",
            arabicName = "شارع محمد الخامس",
            points = listOf(
                GuelmimPoint(480f, 120f),
                GuelmimPoint(480f, 280f),
                GuelmimPoint(480f, 460f),
                GuelmimPoint(500f, 620f),
                GuelmimPoint(500f, 750f),
                GuelmimPoint(500f, 920f)
            ),
            isArterial = true
        ),
        // Boulevard Hassan II (Main East-West)
        MapRoad(
            name = "Bd. Hassan II",
            arabicName = "شارع الحسن الثاني",
            points = listOf(
                GuelmimPoint(120f, 460f),
                GuelmimPoint(300f, 460f),
                GuelmimPoint(480f, 460f),
                GuelmimPoint(680f, 460f),
                GuelmimPoint(920f, 460f)
            ),
            isArterial = true,
            isBoulevard = true
        ),
        // Avenue des FAR (Diagonal Ring Road)
        MapRoad(
            name = "Av. des FAR",
            arabicName = "شارع القوات المسلحة",
            points = listOf(
                GuelmimPoint(200f, 280f),
                GuelmimPoint(340f, 360f),
                GuelmimPoint(480f, 460f),
                GuelmimPoint(640f, 600f),
                GuelmimPoint(780f, 760f)
            ),
            isBoulevard = true
        ),
        // Souk Amhirich Boulevard
        MapRoad(
            name = "Bd. Souk Amhirich",
            arabicName = "طريق سوق أمحيريش",
            points = listOf(
                GuelmimPoint(220f, 620f),
                GuelmimPoint(340f, 600f),
                GuelmimPoint(500f, 620f)
            )
        ),
        // Al Wahda Connection Road
        MapRoad(
            name = "Rue Al Wahda",
            arabicName = "زنقة الوحدة",
            points = listOf(
                GuelmimPoint(320f, 370f),
                GuelmimPoint(320f, 460f),
                GuelmimPoint(320f, 600f)
            )
        ),
        // Al Quds Boulevard
        MapRoad(
            name = "Bd. Al Quds",
            arabicName = "شارع القدس",
            points = listOf(
                GuelmimPoint(480f, 250f),
                GuelmimPoint(640f, 250f),
                GuelmimPoint(760f, 320f)
            )
        ),
        // Route de Tighmert (Oasis Highway)
        MapRoad(
            name = "Route de Tighmert",
            arabicName = "طريق تيغميرت",
            points = listOf(
                GuelmimPoint(680f, 460f),
                GuelmimPoint(760f, 420f),
                GuelmimPoint(880f, 380f),
                GuelmimPoint(960f, 360f)
            ),
            isArterial = true
        ),
        // South Bab Sahra Loop
        MapRoad(
            name = "Rocade Sud",
            arabicName = "الطريق الدائري الجنوبي",
            points = listOf(
                GuelmimPoint(260f, 780f),
                GuelmimPoint(360f, 780f),
                GuelmimPoint(500f, 750f),
                GuelmimPoint(660f, 750f)
            )
        ),
        // Oued Noun District Road
        MapRoad(
            name = "Av. Oued Noun",
            arabicName = "شارع واد نون",
            points = listOf(
                GuelmimPoint(680f, 460f),
                GuelmimPoint(720f, 560f),
                GuelmimPoint(700f, 700f)
            )
        )
    )

    // Oued Noun riverbed contours across Guelmim
    val ouedRiverbedPoints = listOf(
        GuelmimPoint(140f, 700f),
        GuelmimPoint(280f, 670f),
        GuelmimPoint(450f, 690f),
        GuelmimPoint(650f, 660f),
        GuelmimPoint(850f, 620f),
        GuelmimPoint(980f, 590f)
    )

    // Local Guelmim Merchants
    val sampleStores = listOf(
        Store(
            id = "store_bab_sahra",
            name = "Restaurant Bab Sahra",
            arabicName = "مطعم باب الصحراء",
            category = StoreCategory.RESTAURANTS,
            location = CENTRE_VILLE,
            rating = 4.8,
            reviewCount = 312,
            avgDeliveryMin = 22,
            tag = "Authentic Sahrawi Grillades & Tajine",
            products = listOf(
                StoreProduct("p1", "Sahrawi Camel Meat Tajine", "طاجين لحم الإبل الصحراوي", 75.0, "Slow-cooked camel meat with desert prunes and almonds"),
                StoreProduct("p2", "Royal Guelmim Couscous", "كسكس كلميم الملكي", 55.0, "Steamed semolina with seven vegetables and tender beef"),
                StoreProduct("p3", "Sahrawi Mint Tea Ceremony Set", "شاي صحراوي أصيل بالنعناع", 18.0, "Three foaming glasses with amber tea & Sahara herbs"),
                StoreProduct("p4", "Grilled Kefta & Merguez Platter", "مشويات كفتة ومرقاز", 45.0, "Served with wood-fired bread and spiced harissa")
            )
        ),
        Store(
            id = "store_tighmert_patisserie",
            name = "Pâtisserie & Café Tighmert",
            arabicName = "حلويات ومقهى تيغميرت",
            category = StoreCategory.PATISSERIE,
            location = BIR_ANZARANE,
            rating = 4.9,
            reviewCount = 245,
            avgDeliveryMin = 18,
            tag = "Honey Briouats & Date Cakes",
            products = listOf(
                StoreProduct("p5", "Majhoul Date Tart", "تارت تمور المجهول", 35.0, "Artisan crust topped with glazed local oasis dates"),
                StoreProduct("p6", "Almond Cornes de Gazelle (12 pcs)", "كعب غزال باللوز الحر", 60.0, "Handmade orange blossom & crushed almond pastries"),
                StoreProduct("p7", "Honey Briouats Box (500g)", "بريوات باللوز والعسل", 50.0, "Crispy triangular filo pastries soaked in wild thyme honey"),
                StoreProduct("p8", "Café Crème & Fresh Avocado Juice", "عصير أفوكادو طازج مع قهوة", 24.0, "Freshly whipped avocado with milk and roasted almonds")
            )
        ),
        Store(
            id = "store_amhirich_dates",
            name = "Marché Amhirich Sahara Bio",
            arabicName = "تعاونية أمحيريش للتمور والأركان",
            category = StoreCategory.SOUK_DATES,
            location = SOUK_AMHIRICH,
            rating = 4.9,
            reviewCount = 420,
            avgDeliveryMin = 25,
            tag = "Pure Oasis Dates & Virgin Argan",
            products = listOf(
                StoreProduct("p9", "Premium Majhoul Dates (1 Kg)", "تمر مجهول ممتاز واحات كلميم", 90.0, "Plump, jumbo caramel-textured desert dates"),
                StoreProduct("p10", "Pure Culinary Argan Oil (500ml)", "زيت أركان أصيل للأكل", 120.0, "Cold-pressed roasted kernels from nearby Anti-Atlas"),
                StoreProduct("p11", "Wild Desert Thyme Honey (1 Kg)", "عسل الزعتر الجبلي الحر", 140.0, "Golden medicinal honey harvested from desert blooms"),
                StoreProduct("p12", "Traditional Amlou Paste (500g)", "أملو بلدي باللوز والأركان", 65.0, "Stone-ground roasted almonds, argan oil and orange blossom honey")
            )
        ),
        Store(
            id = "store_pharmacie_centrale",
            name = "Pharmacie Centrale Bab Sahra",
            arabicName = "صيدلية باب الصحراء المركزية",
            category = StoreCategory.PHARMACY,
            location = BIR_ANZARANE,
            rating = 4.7,
            reviewCount = 180,
            avgDeliveryMin = 15,
            tag = "Medicines & First Aid Rapid",
            products = listOf(
                StoreProduct("p13", "Emergency First Aid Kit", "حقيبة الإسعافات الأولية", 45.0, "Antiseptic, sterile compresses, bandages and tape"),
                StoreProduct("p14", "Sun Protection SPF 50+ Sahara", "واقي شمس للصحراء", 95.0, "Broad spectrum mineral fluid for desert UV rays"),
                StoreProduct("p15", "Hydration Electrolytes Pack", "أملاح الترطيب السريع", 30.0, "Effervescent recovery formula with minerals")
            )
        )
    )

    /**
     * Generates a multi-point road path between two locations according to vehicle profile
     */
    fun buildRoutePath(from: GuelmimPoint, to: GuelmimPoint, vehicle: VehicleType): List<GuelmimPoint> {
        val points = mutableListOf<GuelmimPoint>()
        points.add(from)

        when (vehicle) {
            VehicleType.MOTORCYCLE -> {
                // Motorcycle can take fast direct souk cuts and alleys
                val midX = (from.x * 0.4f + to.x * 0.6f) + (if (from.x > to.x) -25f else 25f)
                val midY = (from.y * 0.4f + to.y * 0.6f) + (if (from.y > to.y) 15f else -15f)
                points.add(GuelmimPoint(midX, midY))
            }
            VehicleType.ESCOOTER -> {
                // E-Scooter navigates via pedestrian paths & Place Bir Anzarane square
                val plazaPoint = GuelmimPoint(480f, 460f)
                val midWay1 = GuelmimPoint(from.x * 0.6f + plazaPoint.x * 0.4f, from.y * 0.6f + plazaPoint.y * 0.4f)
                val midWay2 = GuelmimPoint(plazaPoint.x * 0.4f + to.x * 0.6f, plazaPoint.y * 0.4f + to.y * 0.6f)
                points.add(midWay1)
                points.add(plazaPoint)
                points.add(midWay2)
            }
            VehicleType.CAR -> {
                // Car adheres to major Boulevards (Hassan II or Mohammed V)
                // Routings via Place Bir Anzarane hub (480f, 460f)
                val hub = GuelmimPoint(480f, 460f)
                // First turn towards main artery
                points.add(GuelmimPoint(from.x, hub.y))
                points.add(hub)
                points.add(GuelmimPoint(hub.x, to.y))
            }
            VehicleType.BICYCLE -> {
                // Bicycle takes quiet oasis / alley paths with smooth bends
                val q1 = GuelmimPoint(from.x * 0.7f + to.x * 0.3f, from.y * 0.8f + to.y * 0.2f)
                val q2 = GuelmimPoint(from.x * 0.3f + to.x * 0.7f, from.y * 0.2f + to.y * 0.8f)
                points.add(q1)
                points.add(q2)
            }
        }

        points.add(to)
        return points
    }

    /**
     * Compute navigation instruction steps
     */
    fun buildNavigationSteps(
        vehicle: VehicleType,
        pickupName: String,
        dropoffName: String
    ): List<NavigationStep> {
        return when (vehicle) {
            VehicleType.MOTORCYCLE -> listOf(
                NavigationStep("Depart $pickupName via souk alleyway", "انطلق من $pickupName عبر الممر السريع", 350, "STRAIGHT"),
                NavigationStep("Cut through Hay Al Wahda shortcut", "اسلك طريق حي الوحدة المختصر", 500, "TURN_RIGHT"),
                NavigationStep("Join Avenue Mohammed V northbound", "ادخل شارع محمد الخامس", 750, "TURN_LEFT"),
                NavigationStep("Cross Place Bir Anzarane roundabout", "اعبر مدارة ساحة بئر أنزران", 200, "ROUNDABOUT"),
                NavigationStep("Arrive at $dropoffName", "الوصول إلى $dropoffName", 50, "ARRIVED")
            )
            VehicleType.ESCOOTER -> listOf(
                NavigationStep("Glide through $pickupName sidewalk zone", "انطلق بالسكوتر الكهربائي عبر الرصيف", 200, "STRAIGHT"),
                NavigationStep("Cruise across Place Bir Anzarane pedestrian plaza", "اعبر ساحة بئر أنزران المخصصة للمشاة", 380, "ROUNDABOUT"),
                NavigationStep("Zip through Avenue Mohammed V dedicated bike path", "اسلك المسار المخصص بشارع محمد الخامس", 450, "TURN_LEFT"),
                NavigationStep("Arrive smoothly at doorstep $dropoffName", "الوصول السلس لباب $dropoffName", 25, "ARRIVED")
            )
            VehicleType.CAR -> listOf(
                NavigationStep("Head out onto Boulevard Hassan II", "توجه نحو شارع الحسن الثاني", 400, "STRAIGHT"),
                NavigationStep("Merge onto Avenue Mohammed V boulevard", "ادخل شارع محمد الخامس الرئيسي", 900, "TURN_RIGHT"),
                NavigationStep("Take 2nd exit at Bab Sahra roundabout", "خذ المخرج الثاني عند مدارة باب الصحراء", 350, "ROUNDABOUT"),
                NavigationStep("Continue straight towards $dropoffName", "واصل السير مباشرة نحو $dropoffName", 600, "STRAIGHT"),
                NavigationStep("Park and deliver at $dropoffName", "الوصول والتوقف عند $dropoffName", 40, "ARRIVED")
            )
            VehicleType.BICYCLE -> listOf(
                NavigationStep("Pedal along quiet oasis greenway", "انطلق عبر المسار الأخضر الهادئ", 250, "STRAIGHT"),
                NavigationStep("Cross pedestrian plaza Bir Anzarane", "اعبر ساحة بئر أنزران المخصصة للمشاة", 400, "TURN_LEFT"),
                NavigationStep("Take shaded palm alley towards destination", "تابع عبر ممر النخيل المظلل", 500, "STRAIGHT"),
                NavigationStep("Arrive at doorstep $dropoffName", "الوصول لباب $dropoffName", 30, "ARRIVED")
            )
        }
    }

    // Registered Couriers in Guelmim
    val sampleDrivers = listOf(
        DriverProfile(
            id = "drv_yassine",
            name = "Yassine Livreur",
            phone = "+212 6 62 44 19 33",
            vehicleType = VehicleType.MOTORCYCLE,
            vehiclePlate = "48 | ب | 7842",
            rating = 4.95,
            totalDeliveries = 342,
            isAvailable = true,
            currentNeighborhood = "Avenue Mohammed V"
        ),
        DriverProfile(
            id = "drv_redouane",
            name = "Redouane Noun",
            phone = "+212 6 61 88 92 10",
            vehicleType = VehicleType.CAR,
            vehiclePlate = "48 | أ | 1290",
            rating = 4.88,
            totalDeliveries = 215,
            isAvailable = true,
            currentNeighborhood = "Place Bir Anzarane"
        ),
        DriverProfile(
            id = "drv_mehdi",
            name = "Mehdi Sahraoui",
            phone = "+212 6 70 12 34 56",
            vehicleType = VehicleType.BICYCLE,
            vehiclePlate = "48-ECO-09",
            rating = 4.92,
            totalDeliveries = 188,
            isAvailable = true,
            currentNeighborhood = "Hay Al Wahda"
        ),
        DriverProfile(
            id = "drv_hamza",
            name = "Hamza Sahraoui",
            phone = "+212 6 78 55 44 33",
            vehicleType = VehicleType.ESCOOTER,
            vehiclePlate = "48-E-SCOOT",
            rating = 4.96,
            totalDeliveries = 245,
            isAvailable = true,
            currentNeighborhood = "Place Bir Anzarane"
        ),
        DriverProfile(
            id = "drv_omar",
            name = "Omar Tighmerti",
            phone = "+212 6 55 99 88 77",
            vehicleType = VehicleType.MOTORCYCLE,
            vehiclePlate = "48 | د | 4410",
            rating = 4.90,
            totalDeliveries = 280,
            isAvailable = false,
            currentNeighborhood = "Oasis de Tighmert"
        )
    )

    // Initial Customer Reviews for Driver Performance Metrics
    val initialReviews = listOf(
        DriverReview(
            id = "rev_1",
            orderId = "ORD-6821",
            driverName = "Yassine Livreur",
            customerName = "Fatima Z. (Hay Al Quds)",
            rating = 5,
            comment = "Tajine arrived boiling hot! Fast route through Bab Sahra and very courteous driver.",
            tags = listOf("Fast Delivery", "Hot & Fresh", "Polite"),
            timestamp = "Yesterday"
        ),
        DriverReview(
            id = "rev_2",
            orderId = "ORD-6790",
            driverName = "Redouane Noun",
            customerName = "Hassan M. (Oued Noun)",
            rating = 5,
            comment = "Car delivery was perfect for our big date box from Marché Amhirich. Excellent service.",
            tags = listOf("Careful Handling", "Great Communication"),
            timestamp = "2 days ago"
        ),
        DriverReview(
            id = "rev_3",
            orderId = "ORD-6744",
            driverName = "Mehdi Sahraoui",
            customerName = "Salma K. (Hay Al Wahda)",
            rating = 4,
            comment = "Eco bike delivery right to the medina door. Very quiet and friendly.",
            tags = listOf("Eco Friendly", "Polite"),
            timestamp = "3 days ago"
        )
    )
}
