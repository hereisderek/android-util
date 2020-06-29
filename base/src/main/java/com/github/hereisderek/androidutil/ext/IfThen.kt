package com.github.hereisderek.androidutil.ext


fun <T, Result> T.ifThen(predicate: (it: T)->Boolean, then: T.()->Result) : Result?
        = if (predicate.invoke(this)) then.invoke(this) else null

fun <T, Result> T.ifNotThen(predicate: (it: T)->Boolean, then: T.()->Result) : Result?
        = if (!predicate.invoke(this)) then.invoke(this) else null

fun <T, Result> T.ifThen(check: T, then: (it: T)->Result) : Result?
    = if (this == check) then.invoke(this) else null

fun <T, Result> T.ifNotThen(check: T, then: (it: T)->Result) : Result?
    = if (this != check) then.invoke(this) else null




