package com.github.hereisderek.androidutil.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.github.hereisderek.androidutil.ext.ifNotThen
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fragmentName = intent?.getStringExtra(FRAGMENT_NAME)
        val arguments = intent?.getBundleExtra(FRAGMENT_ARGUMENTS)
        val fragmentTag = intent?.getStringExtra(FRAGMENT_TAG) ?: fragmentName
        themeId = intent?.getIntExtra(ACTIVITY_THEME, -1) ?: -1

        themeId.ifNotThen(-1) { setTheme(it) }

        val target = createFragment() ?: if (fragmentTag != null && fragmentName != null) {
            fragmentTag to supportFragmentManager.fragmentFactory.instantiate(classLoader, fragmentName)
        } else null

        if (target != null) {
            target.second.arguments = arguments
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, target.second, target.first)
                .commit()
        } else {
            Timber.e("Unable to instantiate fragment, empty fragment name")
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }
    }

    open fun  createFragment() : Pair<String, Fragment>? = null

    override fun getTheme(): Resources.Theme = super.getTheme().apply {
        themeId.ifNotThen(-1) {
            applyStyle(it, true)
        }
    }

    companion object {
        private const val FRAGMENT_NAME = "fragment_name"
        private const val FRAGMENT_TAG = "fragment_tag"
        private const val ACTIVITY_THEME = "activity_theme"
        private const val FRAGMENT_ARGUMENTS = "fragment_arguments"

        fun <T : Fragment> getLaunchIntent(context: Context, fragmentClass: Class<T>, @IdRes themeId: Int = -1, fragmentTag: String? = null, fragmentArguments: Bundle? = null) = Intent(context, SingleFragmentActivity::class.java).apply {
            putExtra(FRAGMENT_NAME, fragmentClass.name)
            putExtra(FRAGMENT_TAG, fragmentTag)
            putExtra(ACTIVITY_THEME, themeId)
            fragmentArguments?.also { putExtra(FRAGMENT_ARGUMENTS, it) }
        }
    }
}