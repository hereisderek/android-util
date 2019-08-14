package com.github.hereisderek.androidutil.draw

/**
 *
 * User: derekzhu
 * Date: 2019-07-08 03:00
 * Project: Imagician Demo
 */


import android.graphics.Bitmap
import androidx.annotation.NonNull

/**
 * An interface for a pool that allows users to reuse [android.graphics.Bitmap] objects.
 */
interface BitmapPool {

    /**
     * Returns the current maximum size of the pool in bytes.
     */
    val maxSize: Long

    /**
     * Multiplies the initial size of the pool by the given multiplier to dynamically and
     * synchronously allow users to adjust the size of the pool.
     *
     *
     *  If the current total size of the pool is larger than the max size after the given
     * multiplier is applied, [Bitmap]s should be evicted until the pool is smaller than the new
     * max size.
     *
     * @param sizeMultiplier The size multiplier to apply between 0 and 1.
     */
    fun setSizeMultiplier(sizeMultiplier: Float)

    /**
     * Adds the given [android.graphics.Bitmap] if it is eligible to be re-used and the pool
     * can fit it, or calls [Bitmap.recycle] on the Bitmap and discards it.
     *
     *
     *  Callers must *not* continue to use the Bitmap after calling this method.
     *
     * @param bitmap The [android.graphics.Bitmap] to attempt to add.
     * @see android.graphics.Bitmap.isMutable
     * @see android.graphics.Bitmap.recycle
     */
    fun put(bitmap: Bitmap)

    /**
     * Returns a [android.graphics.Bitmap] of exactly the given width, height, and
     * configuration, and containing only transparent pixels.
     *
     *
     *  If no Bitmap with the requested attributes is present in the pool, a new one will be
     * allocated.
     *
     *
     *  Because this method erases all pixels in the [Bitmap], this method is slightly slower
     * than [.getDirty]. If the [ ] is being obtained to be used in [android.graphics.BitmapFactory]
     * or in any other case where every pixel in the [android.graphics.Bitmap] will always be
     * overwritten or cleared, [.getDirty] will be
     * faster. When in doubt, use this method to ensure correctness.
     *
     * <pre>
     * Implementations can should clear out every returned Bitmap using the following:
     *
     * `bitmap.eraseColor(Color.TRANSPARENT);
    ` *
    </pre> *
     *
     * @param width  The width in pixels of the desired [android.graphics.Bitmap].
     * @param height The height in pixels of the desired [android.graphics.Bitmap].
     * @param config The [android.graphics.Bitmap.Config] of the desired [               ].
     * @see .getDirty
     */
    @NonNull
    operator fun get(width: Int, height: Int, config: Bitmap.Config): Bitmap

    /**
     * Identical to [.get] except that any returned
     * [android.graphics.Bitmap] may *not* have been erased and may contain random data.
     *
     *
     * If no Bitmap with the requested attributes is present in the pool, a new one will be
     * allocated.
     *
     *
     *  Although this method is slightly more efficient than [.get] it should be used with caution and only when the caller is
     * sure that they are going to erase the [android.graphics.Bitmap] entirely before writing
     * new data to it.
     *
     * @param width  The width in pixels of the desired [android.graphics.Bitmap].
     * @param height The height in pixels of the desired [android.graphics.Bitmap].
     * @param config The [android.graphics.Bitmap.Config] of the desired [               ].
     * @return A [android.graphics.Bitmap] with exactly the given width, height, and config
     * potentially containing random image data.
     * @see .get
     */
    @NonNull
    fun getDirty(width: Int, height: Int, config: Bitmap.Config): Bitmap

    /**
     * Removes all [android.graphics.Bitmap]s from the pool.
     */
    fun clearMemory()

    /**
     * Reduces the size of the cache by evicting items based on the given level.
     *
     * @param level The level from [android.content.ComponentCallbacks2] to use to determine how
     * many [android.graphics.Bitmap]s to evict.
     * @see android.content.ComponentCallbacks2
     */
    fun trimMemory(level: Int)
}
