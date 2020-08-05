package com.github.hereisderek.androidutil.fragment

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.github.hereisderek.androidutil.fragment.FragUtil.fragProvider
import com.github.hereisderek.androidutil.javaClass
import timber.log.Timber

object FragUtil {
    interface FragProvider : LifecycleObserver {
        fun register(fm: FragmentManager, lifecycle: Lifecycle)
        fun register(activity: FragmentActivity) = register(activity.supportFragmentManager, activity.lifecycle)
        fun register(fragment: Fragment) = register(fragment.childFragmentManager, fragment.lifecycle)

        // fun <T : Fragment> getFragmentForTag(tag: String) : T?
        fun <T : Fragment> getOrRecreateFragment(tag: String) : T?
        fun <T : Fragment> registerFragment(tag: String, lazy: ()->T)
    }

    class FragProviderImpl : FragProvider {
        private lateinit var _fm: FragmentManager
        private val fm get() = if(::_fm.isInitialized) {
            _fm
        } else error("fm has not been initialized")

        private val map = HashMap<String, ()->Fragment>()

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        private fun onDestroy(){
            println("onDestroy")
            map.clear()
        }

        @Suppress("MemberVisibilityCanBePrivate")
        override fun register(fm: FragmentManager, lifecycle: Lifecycle) {
            this._fm = fm
            Timber.d("registered fm")
            lifecycle.addObserver(this)
        }


        @Suppress("UNCHECKED_CAST")
        override fun <T : Fragment> getOrRecreateFragment(tag: String): T? =
            fm.findFragmentByTag(tag) as? T ?: (map[tag]?.invoke() as? T)?.also {
                fm.beginTransaction().add(it, tag).commit()
            }

        override fun <T : Fragment> registerFragment(tag: String, lazy: () -> T) {
            map[tag] = lazy
        }
    }


    fun FragmentActivity.fragProvider() : FragProvider = FragProviderImpl().apply {
        register(supportFragmentManager, lifecycle)
    }

    fun Fragment.fragProvider() : FragProvider = FragProviderImpl().apply {
        register(childFragmentManager, lifecycle)
    }
}