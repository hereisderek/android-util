package com.github.hereisderek.androidutil.base

import java.io.File

/**
 *
 * User: derekzhu
 * Date: 2019-08-06 13:21
 * Project: androidutil
 */


class FileAlreadyExistException(val file: File) : Exception("File already Exist:${file.absolutePath}")