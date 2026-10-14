package com.example

import androidx.compose.ui.graphics.Color
import com.example.model.VehicleType
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testColorCreation() {
        val c = VehicleType.MOTORCYCLE.highlightColor
        println("Color is: $c, alpha: ${c.alpha}, red: ${c.red}")
        assertTrue(c.alpha > 0f)
    }
}
