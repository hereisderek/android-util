package com.github.hereisderek.androidutil.closeable

import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import java.util.*

/**
 * User: derekzhu
 * Date: 12/11/19 11:20 AM
 * Project: android-util
 */

/**
 * @hide
 */
@Deprecated(
    "THIS IMPLEMENTATION HAS NOT BEEN TESTED, it might eat your cat",
    level = DeprecationLevel.HIDDEN
)
class CloseableSafeTest {
    object A
    object B
    object C

    @Before
    fun setUp() {
    }

    @Test
    fun testSet1(){
        val a : Object? = null

        // val closeableSafe = DummyCloseableSafe()
    }


    /*class DummyCloseableSafe<Object>(
        obj: Object?,
        generator: ()-> Object
    ) : CloseableSafe(obj, generator) {

    }*/
}
