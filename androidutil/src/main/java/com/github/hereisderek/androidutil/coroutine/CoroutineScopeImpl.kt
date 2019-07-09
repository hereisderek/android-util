package com.github.hereisderek.androidutil.coroutine

import androidx.annotation.CallSuper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.github.hereisderek.androidutil.coroutine.ICoroutineScope
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 *
 * User: derekzhu
 * Date: 2019-06-27 17:34
 * Project: Imagician Demo
 */

class CoroutineScopeImpl(parentJob: Job? = null) : ICoroutineScope, LifecycleObserver {
    override val job: Job = SupervisorJob(parentJob)

    override val mainContext: CoroutineContext by lazy { job + Dispatchers.Main }

    override val coroutineContext: CoroutineContext get() = mainContext

    override val ioContext: CoroutineContext by lazy { job + Dispatchers.IO }

    private var mLifecycleOwner : LifecycleOwner? = null


    override fun registerLifeCycle(owner: LifecycleOwner) {
        owner.lifecycle.addObserver(this)
        mLifecycleOwner = owner
    }

    override fun registerLifecycleObserver(observer: LifecycleObserver) {
        mLifecycleOwner?.lifecycle?.addObserver(observer)
    }


    final override fun cancelChildrenJobs() {
        onStop()
    }

    override fun launchIO(
        start: CoroutineStart,
        block: suspend CoroutineScope.() -> Unit
    ) : Job = launch(ioContext, start, block)



    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(){
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    @CallSuper
    fun onStop() {
        job.cancelChildren(CancellationException("CoroutineScopeImpl onStop"))
    }

}