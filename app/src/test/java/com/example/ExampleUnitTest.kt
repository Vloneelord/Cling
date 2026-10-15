package com.example

import androidx.compose.ui.graphics.Color
import com.example.model.OrderStatus
import com.example.model.SampleDeliveryData
import com.example.model.UserRole
import com.example.model.VehicleType
import com.example.viewmodel.DeliveryViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testColorCreation() {
        val c = VehicleType.MOTORCYCLE.highlightColor
        assertTrue(c.alpha > 0f)
    }

    @Test
    fun testAuthAndRoleSwitching() {
        val viewModel = DeliveryViewModel()
        viewModel.login(UserRole.DRIVER, "Amine Driver")
        assertEquals(UserRole.DRIVER, viewModel.uiState.value.authState.role)
        assertEquals("Amine Driver", viewModel.uiState.value.authState.currentUser)

        viewModel.switchRole(UserRole.MERCHANT)
        assertEquals(UserRole.MERCHANT, viewModel.uiState.value.authState.role)
    }

    @Test
    fun testPlaceRestaurantOrderAndAdvanceStatus() {
        val viewModel = DeliveryViewModel()
        val restaurant = SampleDeliveryData.restaurants.first()
        val item = restaurant.menu.first()

        val initialCount = viewModel.uiState.value.orders.size
        viewModel.placeRestaurantOrder(restaurant, item)

        val newCount = viewModel.uiState.value.orders.size
        assertEquals(initialCount + 1, newCount)

        val activeOrder = viewModel.uiState.value.orders.first()
        assertEquals(OrderStatus.PLACED, activeOrder.status)
        assertTrue(activeOrder.itemsSummary.contains(item.nameEn))

        // Advance to PREPARING
        viewModel.advanceOrderStatus(activeOrder.id)
        assertEquals(OrderStatus.PREPARING, viewModel.uiState.value.orders.first().status)

        // Advance to EN_ROUTE
        viewModel.advanceOrderStatus(activeOrder.id)
        assertEquals(OrderStatus.EN_ROUTE, viewModel.uiState.value.orders.first().status)

        // Assign driver
        val driver = SampleDeliveryData.drivers.last()
        viewModel.assignDriver(activeOrder.id, driver)
        assertEquals(driver.name, viewModel.uiState.value.orders.first().assignedDriver?.name)

        // Advance to NEARBY
        viewModel.advanceOrderStatus(activeOrder.id)
        assertEquals(OrderStatus.NEARBY, viewModel.uiState.value.orders.first().status)

        // Advance to DELIVERED
        viewModel.advanceOrderStatus(activeOrder.id)
        assertEquals(OrderStatus.DELIVERED, viewModel.uiState.value.orders.first().status)

        // Submit driver rating
        viewModel.submitDriverRating(activeOrder.id, 5)
        val ratedOrder = viewModel.uiState.value.orders.first()
        assertEquals(5, ratedOrder.rating)
    }

    @Test
    fun testDispatchCourierOrderAndVehicleSelection() {
        val viewModel = DeliveryViewModel()
        val initialCount = viewModel.uiState.value.orders.size

        viewModel.dispatchCourierOrder(
            pickup = "Avenue Mohammed V",
            dropoff = "Hay Al Quds",
            packageDesc = "Medical Supplies Box",
            vehicle = VehicleType.CAR
        )

        assertEquals(initialCount + 1, viewModel.uiState.value.orders.size)

        // Switch vehicle to ESCOOTER
        viewModel.selectVehicle(VehicleType.ESCOOTER)
        assertEquals(VehicleType.ESCOOTER, viewModel.uiState.value.selectedVehicle)
    }
}

