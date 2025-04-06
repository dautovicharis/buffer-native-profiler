plugins {
    kotlin("jvm") version "2.1.10"
    `java-library`
}

group = "com.harisdautovic"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

// Configure Java Library components
java {
    withJavadocJar()
    withSourcesJar()
}

// Configure the existing configurations to include Kotlin platform type
configurations {
    named("apiElements") {
        attributes {
            attribute(org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.attribute, org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.jvm)
        }
    }

    named("runtimeElements") {
        attributes {
            attribute(org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.attribute, org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.jvm)
        }
    }
}