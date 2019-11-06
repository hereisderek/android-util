package com.github.hereisderek.androidutil.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.github.hereisderek.androidutil.misc.IntentUtil
import timber.log.Timber

/**
 *
 * User: derekzhu
 * Date: 6/11/19 11:41 AM
 * Project: android-util
 */


@Suppress("MemberVisibilityCanBePrivate")
open class SingleFragmentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fragmentName = intent?.getStringExtra(FRAGMENT_NAME)
        val arguments = intent?.getBundleExtra(FRAGMENT_ARGUMENTS)
        val fragmentTag = intent?.getStringExtra(FRAGMENT_TAG) ?: fragmentName

        when {
            !fragmentName.isNullOrEmpty() -> supportFragmentManager.fragmentFactory.instantiate(classLoader, fragmentName)
            // we can probably add other factory method in the future
            else -> {
                Timber.e("Unable to instantiate fragment, empty fragment name")
                setResult(Activity.RESULT_CANCELED)
                finish()
                return
            }
        }.also {
            it.arguments = arguments
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, it, fragmentTag)
                .commit()
        }
    }




    companion object {
        const val FRAGMENT_NAME = "fragment_name"
        const val FRAGMENT_TAG = "fragment_tag"
        const val FRAGMENT_ARGUMENTS = "fragment_arguments"


        // from fragments
        @JvmOverloads
        fun <T : Fragment> start(
            host: Fragment,
            fragmentClass: Class<T>,
            fragmentArguments: Bundle? = null,
            fragmentTag: String? = fragmentClass.name
        ) {
            start(host.requireContext(), fragmentClass, fragmentArguments, fragmentTag)
        }

        @JvmOverloads
        fun <T : Fragment> startForResult(
            host: Fragment,
            fragmentClass: Class<T>,
            fragmentArguments: Bundle? = null,
            requestCode: Int = IntentUtil.newRequestCode,
            fragmentTag: String? = fragmentClass.name
        ) {
            startForResult(host.requireActivity(), fragmentClass, fragmentArguments, requestCode, fragmentTag)
        }


        // from activities
        @JvmOverloads
        fun <T : Fragment> start(
            context: Context,
            fragmentClass: Class<T>,
            fragmentArguments: Bundle? = null,
            fragmentTag: String? = fragmentClass.name
        ){
            context.startActivity(getIntent(context, fragmentClass, fragmentTag, fragmentArguments))
        }

        @JvmOverloads
        fun <T : Fragment> startForResult(
            host: Activity,
            fragmentClass: Class<T>,
            fragmentArguments: Bundle? = null,
            requestCode: Int = IntentUtil.newRequestCode,
            fragmentTag: String? = fragmentClass.name
        ) : Int = requestCode.also {
            host.startActivity(getIntent(host, fragmentClass, fragmentTag, fragmentArguments))
        }


        /// inline argument bundle with lambda

        // from fragments
        fun <T : Fragment> start(
            host: Fragment,
            fragmentClass: Class<T>,
            fragmentTag: String? = fragmentClass.name,
            fragmentArgAction: (Bundle.()->Unit)? = null
        ) = start(host.requireContext(), fragmentClass, fragmentTag, fragmentArgAction)

        fun <T : Fragment> startForResult(
            host: Fragment,
            fragmentClass: Class<T>,
            fragmentTag: String? = fragmentClass.name,
            requestCode: Int = IntentUtil.newRequestCode,
            fragmentArgAction: (Bundle.()->Unit)? = null
        ) = startForResult(host.requireActivity(), fragmentClass, fragmentTag, requestCode, fragmentArgAction)

        // from activities
        fun <T : Fragment> start(
            context: Context,
            fragmentClass: Class<T>,
            fragmentTag: String? = fragmentClass.name,
            fragmentArgAction: (Bundle.()->Unit)? = null
        ) = context.startActivity(getIntent(context, fragmentClass, fragmentTag, fragmentArgAction?.let { action ->
            Bundle().also { action.invoke(it) }
        }))

        fun <T : Fragment> startForResult(
            host: Activity,
            fragmentClass: Class<T>,
            fragmentTag: String? = fragmentClass.name,
            requestCode: Int = IntentUtil.newRequestCode,
            fragmentArgAction: (Bundle.()->Unit)? = null
        ) : Int = requestCode.also {
            host.startActivity(getIntent(host, fragmentClass, fragmentTag, fragmentArgAction?.let { action ->
                Bundle().also { action.invoke(it) }
            }))
        }




        /* utility class */
        private fun <T : Fragment> getIntent(
            context: Context,
            fragmentClass: Class<T>,
            fragmentTag: String? = null,
            fragmentArguments: Bundle? = null
        ) = Intent(context, SingleFragmentActivity::class.java).apply {
            putExtra(FRAGMENT_NAME, fragmentClass.name)
            putExtra(FRAGMENT_TAG, fragmentTag)
            fragmentArguments?.also { putExtra(FRAGMENT_ARGUMENTS, it) }
        }
    }
}