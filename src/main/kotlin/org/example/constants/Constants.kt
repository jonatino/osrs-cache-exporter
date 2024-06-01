package org.example.constants

import net.runelite.cache.fs.flat.FlatStorage
import java.io.File
import java.io.IOException

object Constants {
    @JvmField
    val CACHE: File = File("./cache")

    @JvmField
    val DUMP_DIR: File = File("./dump")

    @JvmField
    val STORAGE: FlatStorage

    init {
        DUMP_DIR.mkdirs()

        try {
            STORAGE = FlatStorage(CACHE)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}