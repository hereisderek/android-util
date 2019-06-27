package com.github.hereisderek.androidutil.util.obj

import android.os.Build
import android.util.ArrayMap
import com.github.hereisderek.androidutil.util.collection.getOrPutNotNull

/**
 *
 * User: derekzhu
 * Date: 2019-06-26 14:32
 * Project: AndroidUtil
 */


class ObjectRetrieverCached<T, Key, ResultType>(
    source: T? = null,
    retriever: Retriever<T, Key, ResultType>
) : ObjectRetriever<T, Key, ResultType>(source, retriever) {

    private val cache : MutableMap<Key, ResultType> =
        if (Build.VERSION.SDK_INT >= 19) ArrayMap<Key, ResultType>() else HashMap<Key, ResultType>()

    override operator fun get(key: Key) : ResultType? {
        return cache.getOrPutNotNull(key){
            super.get(key)
        }
    }
}