package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.GuelmimMapData
import com.example.model.DeliveryStatus
import com.example.model.GuelmimPoint
import com.example.model.VehicleType
import com.example.viewmodel.DeliveryViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Guelmim Express", appName)
    }

    @Test
    fun `verify multi vehicle routing profiles`() {
        val from = GuelmimPoint(480f, 460f) // Bir Anzarane
        val to = GuelmimPoint(500f, 750f)   // Bab Sahra

        val motoRoute = GuelmimMapData.buildRoutePath(from, to, VehicleType.MOTORCYCLE)
        val scooterRoute = GuelmimMapData.buildRoutePath(from, to, VehicleType.ESCOOTER)
        val carRoute = GuelmimMapData.buildRoutePath(from, to, VehicleType.CAR)
        val bikeRoute = GuelmimMapData.buildRoutePath(from, to, VehicleType.BICYCLE)

        assertTrue("Moto route has waypoints", motoRoute.size >= 3)
        assertTrue("Scooter route has pedestrian waypoints", scooterRoute.size >= 4)
        assertTrue("Car route has boulevards waypoints", carRoute.size >= 4)
        assertTrue("Bike route has eco waypoints", bikeRoute.size >= 4)

        assertEquals(VehicleType.MOTORCYCLE.speedKmH, 42)
        assertEquals(VehicleType.ESCOOTER.speedKmH, 25)
        assertEquals(VehicleType.CAR.speedKmH, 32)
        assertEquals(VehicleType.BICYCLE.speedKmH, 18)
    }

    @Test
    fun `verify customer rating and business order management`() {
        val viewModel = DeliveryViewModel()
        val initialOrders = viewModel.uiState.value.allOrders
        assertTrue("Initial business orders available", initialOrders.isNotEmpty())

        val testOrder = initialOrders.first()
        viewModel.updateBusinessOrderStatus(testOrder.id, DeliveryStatus.PREPARING)
        val updatedOrder = viewModel.uiState.value.allOrders.find { it.id == testOrder.id }
        assertEquals(DeliveryStatus.PREPARING, updatedOrder?.status)

        // Assign driver
        viewModel.assignDriverToOrder(testOrder.id, "drv_redouane")
        val orderWithDriver = viewModel.uiState.value.allOrders.find { it.id == testOrder.id }
        assertEquals("Redouane Noun", orderWithDriver?.courierName)
        assertEquals(VehicleType.CAR, orderWithDriver?.vehicleType)

        // Submit customer review
        val initialReviewsCount = viewModel.uiState.value.reviews.size
        viewModel.submitDriverReview(
            orderId = testOrder.id,
            rating = 5,
            comment = "Super fast delivery through Bab Sahra!",
            tags = listOf("Fast Delivery", "Hot & Fresh")
        )

        assertEquals(initialReviewsCount + 1, viewModel.uiState.value.reviews.size)
        val latestReview = viewModel.uiState.value.reviews.first()
        assertEquals(5, latestReview.rating)
        assertEquals("Redouane Noun", latestReview.driverName)
    }
}
