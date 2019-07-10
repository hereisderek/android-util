package com.github.hereisderek.androidutil.util.obj

import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*

/**
 * User: derekzhu
 * Date: 2019-07-10 15:22
 * Project: Imagician Demo
 */


class SimpleExpandablePoolTest {

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun testTrim() {
        var index = 0
        val pool = SimpleExpandablePool{
            (index++).toString()
        }

        (0..10).forEach {
            assertEquals(it, pool.size)
            pool.release(it.toString())
            assertEquals(it + 1, pool.size)
        }
        pool.trimToSize(4)
        assertEquals(4, pool.size)
    }

    @Test
    fun testCreation() {
        class DummyClass(val index : Int)
        var index = 0
        val pool = pool(LazyThreadSafetyMode.SYNCHRONIZED) {
            DummyClass(index++)
        }

        pool.use {
            assertEquals(1, index)
            assertEquals(it.index, 0)
        }

        assertEquals(1, pool.size)
        assertEquals(0, pool.acquire().index)
    }
}