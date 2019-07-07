# android-util [![Build Status](https://travis-ci.com/hereisderek/android-util.svg?branch=master)](https://travis-ci.com/hereisderek/android-util) [![jitpack Status](https://jitpack.io/v/hereisderek/android-util.svg)](https://jitpack.io/#hereisderek/android-util) 

common android utilities

### integration
#### Step 1. Add the JitPack repository to your build file
``` groovy
allprojects {
 repositories {
  ...
  maven { url 'https://jitpack.io' }
 }
}
```

#### Step 2. Add the dependency
``` groovy
dependencies {
    implementation 'com.github.hereisderek:android-util:-SNAPSHOT'
}
 ```



 
 
 ### Example
 
 #### 1. Lazy init
 
 ```kotlin
import com.github.hereisderek.androidutil.util.obj.lazy

class ImageEditorPreviewViewHolder(context: Context) : RecyclerView.ViewHolder(AppCompatImageView(context).apply {
    layoutParams = LAYOUT_PARAMS.get(context)
}) {

    companion object {
        private val LAYOUT_PARAMS = lazy<FrameLayout.LayoutParams, Context>{ context ->
            context.resources.let {
                FrameLayout.LayoutParams(
                    it.getDimension(R.dimen.preview_slider_item_width).toInt(),
                    it.getDimension(R.dimen.preview_slider_item_height).toInt()
                )
            }
        }
    }
}

```


#### 2. SolidColorRoundBitmapGenerator

```kotlin
    private val mSolidColorRoundBitmapGenerator by lazy {
        SolidColorRoundBitmapGenerator(Injection.provideBitmapPoolUtil(this))
    }
    
    private suspend fun debugSolidColorCircularBitmapWithBorder(radius: Int, borderWidth: Int, shadowWidth: Int) : Bitmap {
            val (time, image) = withContext(ioContext) {
                measureNanoTime {
                    mSolidColorRoundBitmapGenerator.solidColorCircularBitmapWithBorder(
                        ColorUtil.getNextRainBowColor(this@MainActivity),
                        radius,
                        Color.WHITE,
                        borderWidth.toFloat(),
                        shadowWidth = shadowWidth.toFloat()
                    )
                }
            }
            val message = "Bitmap with border generated, width:${image.width}, height:${image.height}, took: $time nanoseconds, ${time / 1000_000L} milliseconds"
            Timber.d(message)
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
            return image
        }
    
    
        private suspend fun debugSolidColorCircularBitmapWithBorderParallel(radius: Int, borderWidth: Int, shadowWidth: Int, parallel: Int = 5) : List<Bitmap> {
            return (0 until parallel).map {
                async {
                    measureNanoTime {
                        debugSolidColorCircularBitmapWithBorder(radius, borderWidth, shadowWidth)
                    }
                }
            }.map { deferred ->
                deferred.await()
            }.mapIndexed { index, result ->
                val (time, image) = result
                val message = "$index - with border result returned, width:${image.width}, height:${image.height}, took: $time nanoseconds, ${time / 1000_000L} milliseconds"
                Timber.d(message)
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                image
            }
        }
```

#### 3. ObjectPool<T>

```kotlin
    private val mPaintPool = ObjectPool(Paint::class.java, MAX_POOL_SIZE){
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    private val mCanvasPool = ObjectPool(MAX_POOL_SIZE){
        Canvas()
    }
    
    fun solidColorCircularBitmap(
            @ColorInt color: Int,
            radius: Int,
            config: Bitmap.Config = mConfig
        ) : Bitmap {
            val bitmap = createEmptyBitmap(radius * 2, config = config)
            val radiusF = radius.toFloat()
            mCanvasPool.acquire { canvas ->
                mPaintPool.acquire { paint ->
                    canvas.setBitmap(bitmap)
                    paint.style = Paint.Style.FILL
                    paint.color = color
                    canvas.drawCircle(radiusF, radiusF, radiusF, paint)
                }
            }
            return bitmap
        }
    
```
