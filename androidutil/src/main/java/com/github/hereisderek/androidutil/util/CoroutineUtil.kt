package com.github.hereisderek.androidutil.util

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

/**
 *
 * User: derekzhu
 * Date: 2019-06-26 16:28
 * Project: AndroidUtil
 */


suspend fun <A, B> Iterable<A>.pmap(f: suspend (A) -> B): List<B> = coroutineScope {
    map { async { f(it) } }.map { it.await() }
}


suspend fun <A, B> Iterable<A>.pmap(c: CoroutineContext, f: suspend (A) -> B): List<B> = coroutineScope {
    map { async(c) { f(it) } }.map { it.await() }
}


suspend fun <A, B> Iterable<A>.pmap(c: CoroutineContext, onError:(() -> Unit)?, f: suspend (A) -> B): List<B> = coroutineScope {
    map { async(c) { f(it) } }.map { it.await() }
}



suspend fun <A, B> Iterable<A>.pmapIndexed(f: suspend (A, Int) -> B): List<B> = coroutineScope {
    mapIndexed { index, a -> async { f(a, index) } }.map {
        it.await()
    }
}