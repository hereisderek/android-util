
inline fun <reified T> _readGradleProperty(name: String, noinline lazyValue: (()->T?)? = null) : T? {
    val value = findProperty(name) ?: return lazyValue?.invoke()
    if (value is T) return value
    return when(T::class){
        Int::class -> when(value) {
            is Number -> value.toInt()
            is String -> value.toInt()
            else -> throw TypeCastException("unable to cast type:${T::class.simpleName} to Int, with value:$value")
        }
        else -> throw TypeCastException("unable to cast type:${T::class.simpleName} to Int, with value:$value")
    } as? T
}


inline fun readGradleProperty(name: String, noinline lazyValue: (()->Any?)? = null) : Any? = _readGradleProperty(name, lazyValue)

extra["readGradleProperty"] = ::readGradleProperty