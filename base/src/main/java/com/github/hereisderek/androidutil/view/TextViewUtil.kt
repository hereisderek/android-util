package com.github.hereisderek.androidutil.view

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import timber.log.Timber

/**
 *
 * User: derekzhu
 * Date: 2019-09-03 17:38
 * Project: Imagician Demo
 */


/**
 * Change the editor type integer associated with the text view, which
 * is reported to an Input Method Editor (IME) with {@link EditorInfo#imeOptions}
 * when it has focus.
 * @see #getImeOptions
 * @see android.view.inputmethod.EditorInfo
 * @attr ref android.R.styleable#TextView_imeOptions
 */
fun TextView.setOnEditorActionListenerWithAction(
    imeOptions: Int? = null,
    onEditorActionListener: (v: TextView, actionId: Int , event: KeyEvent, donePressed: Boolean) -> Boolean
){
    if (imeOptions != null) { setImeOptions(imeOptions) }

    setOnEditorActionListener{ view, actionId, event ->
        Timber.d("setOnEditorActionListener actionId:$actionId action:${event?.action}, imeOptions:$imeOptions")
        val buttonPressed = when {
            event == null -> when (actionId) {
                EditorInfo.IME_ACTION_DONE,
                EditorInfo.IME_ACTION_NEXT -> true
                imeOptions -> true
                else -> false
            }
            actionId == EditorInfo.IME_NULL && event.action == KeyEvent.ACTION_DOWN -> true
            else -> false
        }
        onEditorActionListener.invoke(view, actionId, event, buttonPressed)
    }
}