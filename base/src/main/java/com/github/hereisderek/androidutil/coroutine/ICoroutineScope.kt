package com.github.hereisderek.androidutil.coroutine

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 *
 * User: derekzhu
 * Date: 2019-06-28 12:19
 * Project: Imagician Demo
 */


interface ICoroutineScope : CoroutineScope {
    public val ioContext: CoroutineContext
    public val mainContext: CoroutineContext
    public val job: Job


    fun registerLifeCycle(owner: LifecycleOwner)

    fun launchIO(
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) : Job


    fun registerLifecycleObserver(observer: LifecycleObserver)

    fun cancelChildrenJobs()
}