package com.github.hereisderek.androidutil.closeable

import android.database.sqlite.SQLiteDatabase
import java.lang.Exception

/**
 *
 * User: derekzhu
 * Date: 12/11/19 11:52 AM
 * Project: android-util
 */

@JvmOverloads
fun <T> SQLiteDatabase.transaction(
    throwException: Boolean = false,
    code: SQLiteDatabase.() -> T
) : T? {
    var t : T? = null
    var exception: Exception? = null
    try {
        beginTransaction()
        t = code()
        setTransactionSuccessful()
    // } catch (e: TransactionAbortException) {
        // Do nothing, just stop the transaction
    } catch (e: Exception) {
        exception = e
    } finally {
        endTransaction()
        if (throwException && exception != null) throw exception
    }
    return t
}

fun <T> SQLiteDatabase.transactionThrow(
    code: SQLiteDatabase.() -> T
) : T {
    var t : T? = null
    var exception: Exception? = null
    try {
        beginTransaction()
        t = code()
        setTransactionSuccessful()
    // } catch (e: TransactionAbortException) {
        // Do nothing, just stop the transaction
    } catch (e: Exception) {
        exception = e
    } finally {
        endTransaction()
        if (exception != null) throw exception
    }
    @Suppress("UNCHECKED_CAST")
    return t as T
}