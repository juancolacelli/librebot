import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    maven
    kotlin("jvm") version "1.3.11"
    id("com.github.johnrengelman.shadow") version "4.0.3"
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile("com.gitlab.jic:ircbot:master-SNAPSHOT") {
        isChanging = true
    }
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

tasks.withType<ShadowJar> {
    baseName = "librebot"
    classifier = ""
    version = ""
}

application {
    mainClassName = "com.colacelli.librebot.Librebot"
}
