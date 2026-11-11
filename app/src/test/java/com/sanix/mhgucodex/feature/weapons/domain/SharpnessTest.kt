package com.sanix.mhgucodex.feature.weapons.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SharpnessTest {

    @Test
    fun parsesThreeLevels() {
        val levels = Sharpness.parse("7.3.4.16.0.0.0 7.3.4.16.2.0.0 7.3.4.16.5.0.0")

        assertEquals(3, levels.size)
        assertEquals(listOf(7, 3, 4, 16, 0, 0, 0), levels[0].segments)
        assertEquals(30, levels[0].total)
        assertEquals(35, levels[2].total)
    }

    @Test
    fun emptyForRangedWeapons() {
        assertTrue(Sharpness.parse("").isEmpty())
        assertTrue(Sharpness.parse("   ").isEmpty())
    }

    @Test
    fun skipsMalformedLevels() {
        val levels = Sharpness.parse("1.2.3 1.2.3.4.5.6.7 a.b.c.d.e.f.g")

        assertEquals(listOf(Sharpness(listOf(1, 2, 3, 4, 5, 6, 7))), levels)
    }
}
