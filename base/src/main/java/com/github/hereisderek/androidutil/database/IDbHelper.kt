package com.github.hereisderek.androidutil.database

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 *
 * User: derekzhu
 * Date: 4/12/19 10:36 PM
 * Project: android-util
 */


interface IDbHelper {
    val dbRead : SQLiteDatabase
    val dbRW : SQLiteDatabase
    fun attachHelper(attachTo: SQLiteOpenHelper)
}


@Suppress("MemberVisibilityCanBePrivate", "unused")
class DBHelperImpl @JvmOverloads constructor(
    private var _attachTo: SQLiteOpenHelper? = null
) : IDbHelper {
    private var _dbRead : SQLiteDatabase? = null
    private var _dbRW : SQLiteDatabase? = null
    private val attachTo : SQLiteOpenHelper get() = checkNotNull(_attachTo){
        "DBHelperImpl has not been initialized, please call attachHelper(SQLiteOpenHelper)"
    }

    override val dbRead : SQLiteDatabase @Synchronized get() = _dbRead.let {
        if (it == null || !it.isOpen) {
            attachTo.readableDatabase.also { _dbRead = it }
        } else { it }
    }

    @Suppress("MemberVisibilityCanBePrivate", "unused")
    override val dbRW : SQLiteDatabase @Synchronized get() = _dbRW.let {
        if (it == null || !it.isOpen || it.isReadOnly) {
            attachTo.writableDatabase.also { _dbRW = it }
        } else { it }
    }

    override fun attachHelper(attachTo: SQLiteOpenHelper) {
        if (_attachTo != attachTo) _attachTo = attachTo
    }
}