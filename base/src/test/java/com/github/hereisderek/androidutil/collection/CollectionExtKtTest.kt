package com.github.hereisderek.androidutil.collection

import org.junit.jupiter.api.*

import org.junit.jupiter.api.Assertions.*

internal class CollectionFlatMapByKeyTest {

    @Test
    @DisplayName("testCollectionFlatMapByKey")
    fun testCollectionFlatMapByKey(){
        val destination = mutableListOf<String>()
        val map = (0 until 8).associateWith { ('a'.toInt() + it).toChar().toString() }
        val actual = map.flatMapTo(destination, arrayOf(1, 3, 5)) {
            it.run { listOf("$key:$value") }
        }
        val expected = listOf("1:b", "3:d", "5:f", "0:a", "2:c", "4:e", "6:g", "7:h")
        assertEquals(expected, actual)
    }


    @Nested
    inner class CollectionMergeTest{
        @Test
        fun merge(){
            val list1 = listOf(1, 2, 3)
            val list2 = listOf("a", "b")
            val actual1 = list1.merge(list2) { a, b ->
                a.toString() + b
            }
            val expected1 = listOf("1a", "1b", "2a", "2b", "3a", "3b")
            assertEquals(expected1, actual1)

            val actual2 = list2.merge(list1) { a, b ->
                a + b.toString()
            }
            val expected2 = listOf("a1", "a2", "a3", "b1", "b2", "b3")
            assertEquals(expected2, actual2)
        }
    }
}
