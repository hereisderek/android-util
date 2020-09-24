@file:Suppress("MemberVisibilityCanBePrivate")

package com.github.hereisderek.androidutil.collection

import com.github.hereisderek.androidutil.ext.nextIntOrNull
import java.util.*
import kotlin.collections.ArrayList


/**
 * User: derek
 * Date: 23/9/20 2:32 pm
 * Project: Safety Net
 */

typealias IntArray2D = Array<IntArray>
typealias ListOfIntArray = List<IntArray>

// [1,2,3]
val IntArray.debugStr : String get() = joinToString(",", "[", "]")

val Iterable<Int>.debugStr : String get() = joinToString(",", "[", "]")

// [[5,9],[9,0],[0,0],[7,0],[4,3]]
val IntArray2D.debugStr : String get() = this.joinToString(",", "[", "]"){ it.debugStr }


object ArrayExt {
    // [1,2,3]
    fun strToListOfInt(str: String) : List<Int> = Scanner(str).useDelimiter("([^0-9])+").use {
        ArrayList<Int>().apply { while (it.hasNextInt()) { add(it.nextInt()) } }
    }


    // [1,2,3,null,5]
    fun strToListOfNullableInt(str: String) : List<Int?> = Scanner(str).useDelimiter("([^(0-9)null])+").use {
        ArrayList<Int?>().apply {
            while (it.hasNext()) { add(it.next().toIntOrNull()) }
        }
    }

    // [[5,9],[9,0],[0,0],[7,0],[4,3]]
    fun stringTo2DIntArray(input: String) : IntArray2D = stringToListOfIntArray(input).run {
        Array<IntArray>(size){ get(it) }
    }

    // [[5,9],[9,0],[0,0],[7,0],[4,3]]
    fun stringToListOfIntArray(input: String) : ListOfIntArray = Scanner(input).useDelimiter("([^0-9])+").run {
        ArrayList<IntArray>().apply {
            while (true) {
                add(intArrayOf(nextIntOrNull() ?: return@apply, nextIntOrNull() ?: return@apply))
            }
        }
    }


}



