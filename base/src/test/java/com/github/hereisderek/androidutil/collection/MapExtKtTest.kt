package com.github.hereisderek.androidutil.collection

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * User: derek
 * Date: 23/9/20 3:50 pm
 * Project: Safety Net
 */

@DisplayName("testing parsing of map content")
internal class MapExtKtTest {

    @Test
    fun mapEntryDebugStr() {
        val a = Pair(1, "a")
        assertEquals(a.debugStr, "[1:a]")
    }

    @Test
    fun mapDebugStr() {
        val map = mapOf(1 to "a", 2 to "b")
        val expected = "[[1:a], [2:b]]"
        assertEquals(expected, map.debugStr)
    }
}
