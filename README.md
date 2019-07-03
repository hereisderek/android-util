# AndroidUtil [![Build Status](https://travis-ci.com/hereisderek/AndroidUtil.svg?branch=master)](https://travis-ci.com/hereisderek/AndroidUtil) [![jitpack Status](https://jitpack.io/v/hereisderek/AndroidUtil.svg)](https://jitpack.io/#hereisderek/AndroidUtil/Tag) 

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
    implementation 'com.github.hereisderek:AndroidUtil:master-SNAPSHOT'
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
