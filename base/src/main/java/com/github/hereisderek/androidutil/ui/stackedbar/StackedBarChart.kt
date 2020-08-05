package com.github.hereisderek.androidutil.ui.stackedbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.text.Layout
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import androidx.core.graphics.withSave
import androidx.core.util.forEach
import com.github.hereisderek.androidutil.collection.joinToString
import com.github.hereisderek.androidutil.collection.sumByFloat
import timber.log.Timber
import kotlin.math.ceil

enum class AxisType{ X_AXIS, Y_AXIS }




/// data set
/**
 * helper for getting all the indexes where we want to draw labels or baselines based on a given range
 */
typealias StepResolver = (range: Int) -> IntArray
/**
 * single entity in a stacked bar
 */
data class StackBarSection<T>(val height: Float, val obj: T)

/**
 * represents a single bar that composed of all the sections
 */
@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class StackedBarEntry<T>(val sections: Array<StackBarSection<T>>) {
    val height by lazy(LazyThreadSafetyMode.PUBLICATION) { sections.sumByFloat { it.height } }
}

/**
 * the whole data set
 * */
@Suppress("EXPERIMENTAL_FEATURE_WARNING")
class StackedBarData<T>(val entries: Array<StackedBarEntry<T>>) {
    val yRange: Float? = entries.maxBy { it.height }?.height
    val xRange = entries.size
    fun getEntryAtIndex(index: Int) : StackedBarEntry<T>? = entries.getOrNull(index)
}

/// renders

/// labels
/**
 * to resolve a label at a specific index
 * @param max: inclusive
 * @param output: used to save the calculation. it will be pre-cleared
 * */

// typealias XLabelResolver = (xRange: Int, output: SparseArray<String>) -> Unit
//
// typealias YLabelResolver = (yRange: Float, output: HashMap<Float, String>) -> Unit

interface LabelRenderer<T> {
    /**
     * @param axisType on X axis or Y
     * @param range: inclusive
     * @param output: used to save the calculation. it will be pre-cleared
     * */
    fun getLabels(axisType: AxisType, range: Int, dataSet: StackedBarData<T>, output: SparseArray<String>)

    /**
     * returns a new #Layout object for the specific text, the result will(should) be cached until invalidated
     */
    fun renderText(type: AxisType, text: CharSequence) : Layout

    /**
     * to notify the drawer that the text's render has been changed and invalidate the current view
     * hence triggers a re-draw
     */
    fun setInvalidator(invalidator: ()->Unit)
}

/// axis
interface AxisRenderer {
    fun onDrawAxis(type: AxisType)
}

/// bars
interface BarRenderer<T> {
    fun calculateBarWidth(totalBarCount: Int, totalWidth: Int) : Float
    fun onDrawBar(
        section: StackBarSection<T>,
        canvas: Canvas,
        rectF: RectF,
        barIndex: Int,
        sectionIndex: Int,
        totalBarCount: Int,
        totalWidth: Float
    )
}

/// baselines
interface BaseLineResolver {
    /**
     * @param range: max range of the given axis
     * @return: an IntArray contains all the index that we should draw a line
     */
    fun getBaseLineIndexes(axisType: AxisType, range: Int) : IntArray

    fun onDrawBaseLine(canvas: Canvas, x0: Float, y0: Float, x1: Float, y1: Float)
}














@Suppress("PrivatePropertyName")
open class StackedBarChart<T> @JvmOverloads constructor (
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {



    private val invalidator = { invalidate() }
    private var invalided = false

    /// labels
    // cache objects to avoid re-allocations
    private val __xLabels = SparseArray<String>()
    private val __yLabels = SparseArray<String>()
    private val __xLabelLayouts = SparseArray<Layout>()
    private val __yLabelLayouts = SparseArray<Layout>()

    private var widestXLabel = INVALID
    private var highestYLabel = INVALID
    private var labelRenderer: LabelRenderer<T>? = null
    private var xLabels : SparseArray<String>? = null
    private var yLabels : SparseArray<String>? = null
    private var xLabelLayouts : SparseArray<Layout>? = null
    private var yLabelLayouts : SparseArray<Layout>? = null


    /// axis


    /// baselines
    private var horizontalBaseLineResolver: BaseLineResolver? = null
    private var verticalBaseLineResolver: BaseLineResolver? = null

    /// data
    private var stackedBarData: StackedBarData<T>? = null

    /// positioning
    private val viewPort = Rect()

    /// dimension
    // the actual, resolved x/y range (x range is for how many bars)
    private var dimensionX = INVALID
    private var dimensionY = INVALID

    // the user's setting, could be the actual range, or WRAP_CONTENT to setup dynamically
    private var _dimensionX = WRAP_CONTENT
    private var _dimensionY = WRAP_CONTENT



    /// setters
    fun setGraphDimension(dimensionX: Int = WRAP_CONTENT, dimensionY: Int = WRAP_CONTENT) {
        this._dimensionX = dimensionX
        this._dimensionY = dimensionY
        this.dimensionX = INVALID
        this.dimensionY = INVALID
    }

    fun setData(data: StackedBarData<T>) {
        Timber.d("setData, ranges:${data.xRange}*${data.yRange}")
        stackedBarData = data
        invalidateLabels()
        invalidate()
    }

    fun setLabelRenderer(labelRenderer: LabelRenderer<T>) {
        Timber.d("setLabelRenderer, same as old: ${this.labelRenderer == labelRenderer}")
        if (this.labelRenderer != labelRenderer) {
            this.labelRenderer = labelRenderer
            labelRenderer.setInvalidator(invalidator)
            invalidateLabels()
            invalidate()
        }
    }

    // invalidate after settings
    private fun invalidateLabels() {
        Timber.d("invalidateLabels, current values: xLabels:${xLabels?.size()}, yLabels:${yLabels?.size()}")
        xLabels = null
        yLabels = null
        widestXLabel = INVALID
        highestYLabel = INVALID
        xLabelLayouts = null
        yLabelLayouts = null
        dimensionX = INVALID
        dimensionY = INVALID
    }


    /// size and positioning
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        Timber.d("onLayout changed:$changed, left:$left, top:$top, right:$right, bottom:$bottom")
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Timber.d("onSizeChanged w:$w, h:$h oldw:$oldw, oldh:$oldh")
        viewPort.set(paddingLeft, paddingTop, w - paddingRight, h - paddingBottom)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.withSave {
            if (invalided) {
                invalided = false
                onPreDraw()
            }

            checkDimensionsSet()
            checkLabels()
            onDrawAxis(this)
            onDrawBaseLine(this)
            onDrawBars(this)
            onDrawTooltip(this)
        }
    }

    override fun invalidate() {
        Timber.d("invalidate() been called, currently invalided:$invalided")
        if (!invalided) {
            invalided = true
            super.invalidate()
        }
    }


    /// pre-drawing calculation
    protected open fun onPreDraw() {
        Timber.d("onPreDraw calculation")
        calculateDimensions()
        calculateLabelsText()
        calculateLabelsLayout()
    }

    private fun getDimensionFromDataSize(axisType: AxisType, range: Number) : Int {
        val rangeF = range.toFloat()
        return when(axisType) {
            AxisType.X_AXIS -> {
                ((rangeF / 5).toInt() + 1) * 5
            }
            AxisType.Y_AXIS -> (rangeF + 0.5f).toInt()
        }
    }
    /**
     * calculate graph dimension depending on the current data set or specified dimension if given
     * */
    private fun calculateDimensions() {
        val stackedBarData = this.stackedBarData
        Timber.d("calculateDimensions, ${stackedBarData?.xRange}:${stackedBarData?.yRange}, dimension:$dimensionX*$dimensionY")

        if (this.dimensionX == INVALID) {
            this.dimensionX = if (this._dimensionX >= 0) {
                this._dimensionX
            } else stackedBarData?.xRange?.let {
                getDimensionFromDataSize(AxisType.X_AXIS, it)
            } ?: DEFAULT_X_DIMENSION
        }

        if (this.dimensionY == INVALID) {
            this.dimensionY = if (this._dimensionY >= 0) {
                this._dimensionY
            } else stackedBarData?.yRange?.let {
                getDimensionFromDataSize(AxisType.Y_AXIS, it)
            } ?: DEFAULT_Y_DIMENSION
        }
        Timber.d("new dimensions: dimension:$dimensionX*$dimensionY")
    }

    private fun calculateLabelsText() {
        calculateLabelsText(AxisType.X_AXIS)
        calculateLabelsText(AxisType.Y_AXIS)
    }

    private fun calculateLabelsText(axisType: AxisType) : SparseArray<String>? {
        val labelRenderer = this.labelRenderer ?: return null.also {
            Timber.w("calculateLabelsText skipped, labelRenderer is null")
        }

        val dataSet = this.stackedBarData ?: return null.also {
            Timber.w("calculateLabelsText skipped, current dataSet is null")
        }
        checkDimensionsSet()

        val label = when(axisType) {
            AxisType.X_AXIS -> this.xLabels
            AxisType.Y_AXIS -> this.yLabels
        }
        if (label != null) return label
        
        val range = when(axisType) {
            AxisType.X_AXIS -> this.dimensionX
            AxisType.Y_AXIS -> this.dimensionY
        }
        val output = when(axisType) {
            AxisType.X_AXIS -> this.__xLabels
            AxisType.Y_AXIS -> this.__yLabels
        }

        output.clear()
        labelRenderer.getLabels(axisType, range, dataSet, output)
        Timber.d("calculateLabelsText for $axisType, range:$range, size:${output.size()} result:${output.joinToString()}")
        when(axisType) {
            AxisType.X_AXIS -> { xLabels = output }
            AxisType.Y_AXIS -> { yLabels = output }
        }

        return output
    }

    private fun calculateLabelsLayout(axisType: AxisType? = null) {
        if (axisType == null) {
            AxisType.values().forEach {
                calculateLabelsLayout(it)
            }
            return
        }

        val labelRenderer = this.labelRenderer ?: return Unit.also {
            Timber.d("calculateLabelsLayout for $axisType skipping because labelRenderer is null")
        }

        checkDimensionsSet()
        val labels = when(axisType) {
            AxisType.X_AXIS -> this.xLabels
            AxisType.Y_AXIS -> this.yLabels
        }.let {
            checkNotNull(it){ "label is null for axis:$axisType" }
        }

        val layouts = when(axisType) {
            AxisType.X_AXIS -> this.xLabelLayouts
            AxisType.Y_AXIS -> this.yLabelLayouts
        }

        // check if actually need to update
        if (layouts == null || layouts.size() != labels.size()) {
            layouts?.clear()

            val newLayout = when(axisType) {
                AxisType.X_AXIS -> this.__xLabelLayouts.also {
                    this.xLabelLayouts = it
                }
                AxisType.Y_AXIS -> this.__yLabelLayouts.also {
                    this.yLabelLayouts = it
                }
            }

            newLayout.clear()
            var widestXLabel = INVALID
            var highestYLabel = INVALID
            labels.forEach { key, value ->
                labelRenderer.renderText(axisType, value).apply {
                    newLayout.put(key, this)
                    if (axisType == AxisType.X_AXIS && height > highestYLabel) {
                        highestYLabel = height
                    }
                    if (axisType == AxisType.Y_AXIS && width > widestXLabel) {
                        widestXLabel = width
                    }
                }
            }

            when(axisType) {
                AxisType.X_AXIS -> {
                    this.highestYLabel = highestYLabel
                    this.xLabelLayouts = newLayout
                }
                AxisType.Y_AXIS -> {
                    this.widestXLabel = widestXLabel
                    this.yLabelLayouts = newLayout
                }
            }
        }

        checkLabels(axisType)
    }



    /// drawing
    protected open fun onDrawAxis(canvas: Canvas) {

    }

    protected open fun onDrawBaseLine(canvas: Canvas) {

    }

    protected open fun onDrawBars(canvas: Canvas) {

    }

    protected open fun onDrawTooltip(canvas: Canvas) {

    }


    /// checkers
    private fun checkDimensionsSet() {
        require(this.dimensionX >= 0) {
            "this.dimensionX is INVALID($dimensionX), need to calculate by calling calculateDimensions()"
        }
        require(this.dimensionY >= 0) {
            "this.dimensionY is INVALID($dimensionY), need to calculate by calling calculateDimensions()"
        }
    }

    private fun checkLabels(axisType: AxisType? = null) {
        if (axisType == null || axisType == AxisType.X_AXIS) {
            require(this.xLabels?.size() == this.xLabelLayouts?.size()) {
                "this.xLabels: ${this.xLabels?.size()} doesn't match this.xLabelLayouts:${this.xLabelLayouts?.size()}"
            }
        }
        if (axisType == null || axisType == AxisType.Y_AXIS) {
            require(this.yLabels?.size() == this.yLabelLayouts?.size()) {
                "this.yLabels: ${this.yLabels?.size()} doesn't match this.yLabelLayouts:${this.yLabelLayouts?.size()}"
            }
        }
    }

    /*
    private fun checkLabelsTextSet(axisType: AxisType?) {
        if (axisType == null) {
            AxisType.values().forEach {
                checkLabelsTextSet(it)
            }
        } else {
            val labels = when(axisType) {
                AxisType.X_AXIS -> xLabels
                AxisType.Y_AXIS -> yLabels
            }

            require((labelRenderer == null) == (labels == null))
        }
    }
    */

    companion object {
        const val INVALID = -1
        const val WRAP_CONTENT = -2

        private const val DEFAULT_X_DIMENSION = 30
        private const val DEFAULT_Y_DIMENSION = 30

        object StepResolvers {
            // 1, 5, 10, 15, 20 ...
            /**
             *
             * */
            val stepResolverStep5StartWith1 : StepResolver = {
                IntArray(it / 5 + 1) {
                    if (it == 0) 1 else it * 5
                }
            }

            /**
             * range -> highest number on the axis
             * 0 -> 0
             * 1-10 -> 1
             * 11-20 -> 2
             * 21-29 -> 3
             * */
            val stepResolverStep10StartWith0DividedBy10 : StepResolver = { range: Int ->
                val size = ceil((range / 10f)).toInt() + 1
                IntArray(size) { it }
            }

        }


    }
}
