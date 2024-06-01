plugins {
    id("org.jetbrains.kotlin.jvm") version "2.0.0"
    java
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "runelite"
        url = uri("https://repo.runelite.net")
    }
}


dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    implementation("net.runelite:cache:+")
    implementation("net.runelite:cache-client:+")
    implementation("org.slf4j:slf4j-simple:1.7.25")
    implementation("org.reflections:reflections:0.10.2")
}