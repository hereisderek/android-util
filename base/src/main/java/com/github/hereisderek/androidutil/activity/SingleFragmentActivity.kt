package com.github.hereisderek.androidutil.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.github.hereisderek.androidutil.ext.ifNotEqualThen
import timber.log.Timber


/**
 *
 * User: derekzhu
 * Date: 6/11/19 11:41 AM
 * Project: android-util
 */


@Suppress("MemberVisibilityCanBePrivate")
open class SingleFragmentActivity : AppCompatActivity() {
    private var themeId : Int = -1
    private var mTag: String? = null

    data class FragData(
        val frag: Fragment,
        val tag: String? = frag.javaClass.name,
        val arguments: Bundle? = null
    )

    private fun fromTag(tag: String?, fm: FragmentManager, savedInstanceState: Bundle?) : FragData? {
        if (tag == null) return null
        val frag = fm.findFragmentByTag(tag) ?: return null
        val bundle = savedInstanceState?.run { getBundle(bundleKeyFromTag(tag)) }
        return FragData(frag, tag, bundle)
    }

    private fun fromIntent(intent: Intent? = null) : FragData? = intent?.run {
        val fragName = getStringExtra(FRAGMENT_NAME) ?: return@run null
        val tag = getStringExtra(FRAGMENT_TAG) ?: return@run null
        val bundle = getBundleExtra(bundleKeyFromTag(tag))
        val frag = supportFragmentManager.fragmentFactory.instantiate(classLoader, fragName)
        return FragData(frag, tag, bundle)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTag = savedInstanceState?.getString(FRAGMENT_TAG)

        val fragData = mTag?.run {
            fromTag(this, supportFragmentManager, savedInstanceState).also { Timber.d("fromTag:$it") }
        } ?: createFragment().also {
            Timber.d("createFragment():$it")
        } ?: fromIntent(intent).also {
            Timber.d("fromIntent:$it")
        }

        if (fragData != null) {
            fragData.frag.arguments = fragData.arguments
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, fragData.frag, fragData.tag)
                .commit()
            mTag = fragData.tag
        } else {
            Timber.e("Unable to instantiate fragment, empty fragment name")
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(FRAGMENT_TAG, mTag)
    }

    open fun createFragment() : FragData? = null


    override fun getTheme(): Resources.Theme = super.getTheme().apply {
        themeId.ifNotEqualThen(-1) {
            applyStyle(it, true)
        }
    }

    companion object {
        private const val FRAGMENT_NAME = "fragment_name"
        private const val FRAGMENT_TAG = "fragment_tag"
        private const val ACTIVITY_THEME = "activity_theme"
        private const val FRAGMENT_ARGUMENTS = "fragment_arguments"

        private fun bundleKeyFromTag(tag: String?) = tag?.run { this + "_bundle" }

        fun <T : Fragment> getLaunchIntent(context: Context, fragmentClass: Class<T>, @IdRes themeId: Int = -1, fragmentTag: String? = null, fragmentArguments: Bundle? = null) = Intent(context, SingleFragmentActivity::class.java).apply {
            putExtra(FRAGMENT_NAME, fragmentClass.name)
            putExtra(FRAGMENT_TAG, fragmentTag)
            putExtra(ACTIVITY_THEME, themeId)
            fragmentArguments?.also { putExtra(bundleKeyFromTag(fragmentTag), it) }
        }
    }
}
