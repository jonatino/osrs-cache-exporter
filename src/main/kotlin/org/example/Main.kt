package org.example

import net.runelite.cache.client.CacheClient
import net.runelite.cache.fs.Store
import net.runelite.cache.fs.flat.FlatStorage
import net.runelite.protocol.api.login.HandshakeResponseType
import org.reflections.Reflections
import org.reflections.scanners.ResourcesScanner
import org.reflections.scanners.SubTypesScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import org.reflections.util.FilterBuilder
import java.io.File
import java.io.IOException
import java.net.URL
import java.util.*
import java.util.regex.Pattern
import kotlin.math.exp
import kotlin.reflect.full.staticFunctions
import kotlin.reflect.jvm.kotlinFunction
import kotlin.system.exitProcess


@Throws(Exception::class)
fun main() {
    val dir = File(".\\cache")
    dir.mkdirs()

    Store(FlatStorage(dir)).use { store ->
        store.load()
        val c = CacheClient(store, "oldschool1.runescape.com", getRsVersion())
        c.connect()
        val handshake = c.handshake()

        val result = handshake.get()
        println("Handshake result: $result")

        if (HandshakeResponseType.RESPONSE_OK != result) {
            System.err.println("Failed to handshake $result")
        }

        c.download()

        c.close()
        store.save()
    }

    val classLoadersList: MutableList<ClassLoader> = LinkedList()
    classLoadersList.add(ClasspathHelper.contextClassLoader())
    classLoadersList.add(ClasspathHelper.staticClassLoader())

    val reflections = Reflections(ConfigurationBuilder()
        .forPackages("org.example.cache")
        .addScanners(SubTypesScanner(false))  // Exclude Object.class
    )

    // Get all the classes in the package
    val classes = reflections.getSubTypesOf(Any::class.java).filter {
        println(it)
        it.packageName == "org.example.cache"
    }

    println("We found ${classes}")

    var failed = listOf< String>()

    // For each class, try to find and call the static dump method
    for (clazz in classes) {
        try {
            // Use Kotlin reflection to find the static dump method
            val dumpMethod = clazz.methods.find { method ->
                method.name == "dump" && method.parameterCount == 0 && method.returnType == Void.TYPE
            }?.kotlinFunction


            // Call the dump method if it exists
            println("Dumping ${clazz.simpleName}")
            if (dumpMethod != null) {
                dumpMethod.call()
            } else {
                System.err.println("Didnt find dump method ${clazz.name}")
                failed += clazz.name
            }
        } catch (e: Exception) {
            println("Failed to call dump method on class ${clazz.name}: ")
            e.printStackTrace()

            failed += clazz.name
        }
    }

    println("Finished dumping everything. Failures below:")
    println(failed)
}

@Throws(IOException::class)
fun getRsVersion(): Int {
    // Create a URL object for the XML file
    val url = URL("https://raw.githubusercontent.com/runelite/runelite/master/pom.xml")

    // Open a connection to the URL
    val reader = url.openStream().bufferedReader()

    // Read the content of the XML file
    val content = reader.use { it.readText() }

    // Define the regex pattern
    val pattern = Pattern.compile("<rs.version>(\\d+)</rs.version>")

    // Match the pattern against the content
    val matcher = pattern.matcher(content)

    // Find the first match
    if (matcher.find()) {
        // Extract the version number
        val version = matcher.group(1)
        println("rs.version value: $version")
        return version.toInt()
    } else {
        throw RuntimeException("Tag <rs.version> not found in the file.")
    }
}

@Throws(IOException::class)
fun getCacheVersion(): Int {
    // Create a URL object for the XML file
    val url = URL("https://raw.githubusercontent.com/runelite/runelite/master/cache/pom.xml")

    // Open a connection to the URL
    val reader = url.openStream().bufferedReader()

    // Read the content of the XML file
    val content = reader.use { it.readText() }

    // Define the regex pattern
    val pattern = Pattern.compile("<cache.version>(\\d+)</cache.version>")

    // Match the pattern against the content
    val matcher = pattern.matcher(content)

    // Find the first match
    if (matcher.find()) {
        // Extract the version number
        val version = matcher.group(1)
        println("cache.version value: $version")
        return version.toInt()
    } else {
        throw RuntimeException("Tag <cache.version> not found in the file.")
    }
}