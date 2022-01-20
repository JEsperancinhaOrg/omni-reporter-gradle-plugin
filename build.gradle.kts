plugins {
    kotlin("jvm") version "1.6.10"
    `java-gradle-plugin`
    `maven-publish`
//    id("org.jesperancinha.plugins.omni") version "0.0.0"
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

buildscript {
    repositories {
        mavenLocal()
    }
}

gradlePlugin {
    plugins {
        create("omni") {
            id = "org.jesperancinha.plugins.omni"
            implementationClass = "org.jesperancinha.plugins.omni.coveragereporter.OmniReporterPlugin"
        }
    }
}


publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "org.jesperancinha.plugins.omni"
            artifactId = "org.jesperancinha.plugins.omni.gradle.plugin"
            version = "0.0.0"

            from(components["java"])
        }
    }
}

group = "org.jesperancinha.plugins.omni"

dependencies {
    implementation("org.jesperancinha.plugins:omni-coveragereporter-commons:0.0.0")
}