package com.sanix.mhgucodex.core.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class TopLevelDestinationTest {

    @Test
    fun bottomBarHasFiveDestinationsInOrder() {
        assertEquals(
            listOf("Monsters", "Weapons", "Armor", "Items", "More"),
            TopLevelDestination.entries.map { it.label },
        )
    }
}
