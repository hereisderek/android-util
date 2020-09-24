package com.github.hereisderek.androidutil.collection


/**
 * Created by  on 24/9/20.
 * Project: Safety Net
 * @see <a href="https://github.com/hereisderek/">Github</a>
 */
 
fun <T> Iterator<T>.nextOrNull() : T? = if (hasNext()) next() else null
