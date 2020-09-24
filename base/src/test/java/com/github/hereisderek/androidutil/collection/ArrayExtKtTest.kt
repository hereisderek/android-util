package com.github.hereisderek.androidutil.collection

import com.github.hereisderek.androidutil.ext.removeWhiteSpace
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * User: derek
 * Date: 23/9/20 2:37 pm
 * Project: Safety Net
 */
@DisplayName("testing parsing of array content")
internal class ArrayParsingTest {

    @Test
    fun strToListOfInt() {
        val str = "[1, 2 , 3]"
        val expected = listOf(1, 2, 3)
        val actual = ArrayExt.strToListOfInt(str)
        assertEquals(expected, actual)
        val actualStr = actual.debugStr
        assertEquals(str.removeWhiteSpace(), actualStr)
    }


    @Test
    fun strToListOfNullableInt() {
        val str = "[1, 2 , 3， null, 5]"
        val expected = listOf(1, 2, 3, null, 5)
        val actual = ArrayExt.strToListOfNullableInt(str)
        assertEquals(expected, actual)
    }

    @Test
    fun stringTo2DIntArray() {
        val input = "[[5,9],[9,0],[0,0],[7,0],[4,3]]"
        val actual = ArrayExt.stringTo2DIntArray(input)
        val expected : IntArray2D = arrayOf(
            intArrayOf(5, 9), intArrayOf(9, 0), intArrayOf(0, 0), intArrayOf(7, 0), intArrayOf(4, 3)
        )
        assertArrayEquals(expected, actual)
    }
}