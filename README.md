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



 
 
 ### A. Example
 
 #### 1. Lazy init
 
 ```kotlin
import com.github.hereisderek.androidutil.obj.lazyInit

class ImageEditorPreviewViewHolder(context: Context) : RecyclerView.ViewHolder(AppCompatImageView(context).apply {
    layoutParams = LAYOUT_PARAMS.get(context)
}) {

    companion object {
        private val LAYOUT_PARAMS = lazyInit<FrameLayout.LayoutParams, Context>{ context ->
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


#### 4. ICoroutineScope by CoroutineScopeImpl
------
A CoroutineScope implementation that provides mainContext, ioContext, exposes val job for children task creation, handles task 
cancellation (by registering lifecycle `fun registerLifeCycle(owner: LifecycleOwner)`) or simply calling `fun cancelChildrenJobs()`

```kotlin
abstract class BaseFragment : Fragment(), ICoroutineScope by CoroutineScopeImpl() {
    val requireActivity get() = requireActivity()
    val requireContext get() = requireContext()
    val appCompatActivity get() = activity as? AppCompatActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        registerLifeCycle(this)
    }

    fun coroutineCall() = launch{
        async{
            // ... some async tasks
        }
    }

}
```

#### 5. ExpandablePool and SynchronizedExpandablePool

well, it's just like any other pool project, but it takes a generator object so it's guaranteed that `fun acquire(): T` won't return
`null` value (unless T is a nullable)

I'll probably add use cases later but I'm too lazy so I'll just paste a test case here instead (SynchronizedExpandablePool is the thread safe version of the same thing)

```kotlin
    @Test
    fun testTrim() {
        var index = 0
        val pool = SimpleExpandablePool{
            (index++).toString()
        }

        (0..10).forEach {
            assertEquals(it, pool.size)
            pool.release(it.toString())
            assertEquals(it + 1, pool.size)
        }
        pool.trimToSize(4)
        assertEquals(4, pool.size)
    }
```

you can also call `fun <R> use(block: (T) -> R): R` for temporary use (automatically recycled right after)
also an extension method for creating a pool object

```kotlin
    @Test
    fun testCreation() {
        class DummyClass(val index : Int)
        var index = 0
        val pool = pool(LazyThreadSafetyMode.SYNCHRONIZED) {
            DummyClass(index++)
        }

        pool.use {
            assertEquals(1, index)
            assertEquals(it.index, 0)
        }

        assertEquals(1, pool.size)
        assertEquals(0, pool.acquire().index)
    }
```

#### 6. VolatileObject
a self-contained, reusable computed obj that only update when requested but marked as dirty (lazy calculate)


```kotlin
    import com.github.hereisderek.androidutil.obj.MutableObj

    // probably not the best use case but this is what I have right now
    private val mViewCenterVO = MutableObj<PointF?>{
        val laidOut = ViewCompat.isLaidOut(this)
        Timber.d("laidOut:$laidOut, width:${this.width}, height:${this.height}, isReady:$isReady")
        if (laidOut && isReady) {
            (it ?: PointF()).apply {
                x = width / 2f
                y = height / 2f
            }
        } else null
    }

```


#### 7. Cursor operations (toArrayList & to Map)

    * fun <T> Cursor.toArrayList(block: (Cursor) -> T): ArrayList<T> 
    * fun Cursor.toArrayList(close: Boolean = false, block: (Cursor) -> T): ArrayList<T>
    * fun <T> Cursor.toArrayListIndexed(close: Boolean = false, block: (Cursor, index: Int) -> T): ArrayList<T>
    * fun <T> Cursor.toSelfArrayList(close: Boolean = false, block: Cursor.() -> T): ArrayList<T>
    * Cursor.toSelfArrayListIndexed(close: Boolean = false, block: Cursor.(index: Int) -> T): ArrayList<T>
    * fun <K, V> Cursor.toArrayMapIndexed(close: Boolean = false, block: (Cursor, index: Int) -> Pair<K, V>) : Map<K, V>
    * fun <K, V> Cursor.toSelfArrayMapIndexed(close: Boolean = false, block: Cursor.(index: Int) -> Pair<K, V>) : Map<K, V>

example:


```kotlin
    import com.github.hereisderek.androidutil.misc.toArrayList

    /// read
    fun getStatus(
        table: TableClass,
        msgId: String
    ) : List<MsgStatus> = readableDatabase.use { db ->
        db.query(true, table.tableName, null, "$KEY_MSG_ID=?", arrayOf(msgId), null, null, null, null)
            .toArrayList(true) {
                MsgStatus(
                    table,
                    getInt(getColumnIndex(KEY_ID)),
                    getString(getColumnIndex(KEY_MSG_ID)),
                    getString(getColumnIndex(KEY_BY)),
                    getLong(getColumnIndex(VALUE_EVENT_SENT)),
                    getLong(getColumnIndex(VALUE_EVENT_DELIVERED)),
                    getLong(getColumnIndex(VALUE_EVENT_READ))
                )
            }
    }
```
 
#### 8. SingleFragmentActivity (you can override theme/style by adding the activity in your manifest file)
To view other start options, see [here](base/src/main/java/com/github/hereisderek/androidutil/activity/SingleFragmentActivity.kt)

```kotlin
    // called from within an activity or a fragment for example
    // you can use an auto generated requestCode, or specify yours
    val requestCode = SingleFragmentActivity.startForResult(this, MyFragment::class.java){
          putString("arg1", "arg1_value")
    }

```




#### 9. `fun useOrCreateAndClose()` and `Closeable?.closeQuiet()`

##### 9.1 `fun useOrCreateAndClose()`

```kotlin
fun <T> useOrCreateAndClose(
    generator: ()->Closeable,
    _closeable: Closeable? = null,
    action: Closeable.(created: Boolean)-> T
) : T
```

To use an existing `Closeable` Object (such as `SQLiteDatabase`) or create a new one by invoking the generator, if it was from the generator, the newly created `Closeable` will be automatically closed after used,
otherwise, you will also be given a chance to manually close it in the `action{}` (if you want to that is):
this can be used for sharing the same database object on a chain of methods so we won't need to keep requesting new database object

##### 9.2 `Closeable?.closeQuiet(handler: ((Exception)->Unit)? = null)`
 
Just to close a `Closeable?` object quietly, pass along the exception to the handler or suppress it if none given 



#### 10. CollectionExt.flatMap() with CollectionExtKtTest.flatMapTo(ArrayList<R>(), byKey, transform) and others


```kotlin
val test = mapOf(
    1 to intArrayOf(1, 2, 3, 4),
    2 to intArrayOf(11, 12, 13, 14),
    // note: map doesn't allow duplicated keys
    // 2 to intArrayOf(18, 19, 17, 16),
    4 to intArrayOf(1111, 2222, 3333, 4444),
    5 to intArrayOf(5555)
)

val result = test.flatMap(
    byKey = intArrayOf(2, 3).toTypedArray()
){
    ArrayList<Int>().apply {
       addAll(it.component2().toTypedArray())
    }
}

// result: 11, 12, 13, 14, 1, 2, 3, 4, 1111, 2222, 3333, 4444, 5555
```


#### 11. DBHelperImpl


#### 12. DelegateRW<T>
`DelegateRW<T>` is an interface that can be used as a delegate(`var variable by DelegateRW<T>`) provides four methods to enable read/write access and notify listeners when changes are made

```kotlin
interface DelegateRW<T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T)
    operator fun plusAssign(onChangeListener: OnChangedListener<T>)
    fun subscribe(onChangeListener: OnChangedListener<T>) = this.also { this += onChangeListener }
}
```

concrate implementations are as follows

* DelegateRWUnSafe<T> (and it's threadsafe version: DelegateRWSafe<T>)
* DelegateLazyRW<T> (and it's threadsafe version: DelegateLazyRWSafe<T>)

also with a helper method

```kotlin
fun <T> delegateWR(
    lazyValue: () -> T,
    mode : LazyThreadSafetyMode = LazyThreadSafetyMode.NONE,
    vararg onChangeListener: OnChangedListener<T>
) : DelegateRW<T>
```

```kotlin
fun <T> delegateWR(
    init: T,
    mode : LazyThreadSafetyMode = LazyThreadSafetyMode.NONE,
    onChangeListener: OnChangedListener<T>? = null
) = when(mode) {
    LazyThreadSafetyMode.SYNCHRONIZED -> DelegateRWSafe(init, onChangeListener)
    else -> DelegateRWUnSafe(init, onChangeListener)
}
```

```kotlin
fun <V : View, T> V.onChangeViewInvalidate(
    init: T,
    mode : LazyThreadSafetyMode = LazyThreadSafetyMode.NONE,
    onChangeListener: OnChangedListener<T>? = null
) : DelegateRW<T> = delegateWR(init, mode, onChangeListener).apply {
    subscribe { _, _ -> invalidate() }
}
```


```kotlin
class CustomView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {
    // whenever changed, it will also trigger view invalidation
    var size: Int by this.onChangeViewInvalidate(10).also {
        it.subscribe { old, new ->
            // you can also add extra layer of listener for value changes
            println("size value changed from:$old to:$new")
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // use the value of size for drawing...
    }
}
```