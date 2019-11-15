package com.github.hereisderek.androidutil.collection

import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * User: derekzhu
 * Date: 15/11/19 4:59 PM
 * Project: android-util
 */


class CollectionExtKtTest {

    @Before
    fun setUp() {
    }

    @Test
    fun flatMap01() {
        val test = mapOf(
            1 to intArrayOf(1, 2, 3, 4),
            2 to intArrayOf(11, 12, 13, 14),
            // note: map doesn't allow duplicated keys
            // 2 to intArrayOf(18, 19, 17, 16),
            4 to intArrayOf(1111, 2222, 3333, 4444),
            5 to intArrayOf(5555)
        )

        val result = test.flatMap(
            byKey = intArrayOf(2, 3).toTypedArray()
        ){
            ArrayList<Int>().apply {
               addAll(it.component2().toTypedArray())
            }
        }

        // result: 11, 12, 13, 14, 1, 2, 3, 4, 1111, 2222, 3333, 4444, 5555

        var count = 0
        test.forEach { (_, u) ->
            count += u.count()
        }
        assertEquals(count, result.count())
        val result_2_exp = test.getValue(2).toList()
        assertEquals(result_2_exp, result.toList().subList(0, 4))



        val result_4_exp = test.getValue(4).toList()
        assertEquals(result_4_exp, result.toList().subList(8, 12))
        print("finish")
    }

    @Test
    fun flatMapTo() {
    }
}