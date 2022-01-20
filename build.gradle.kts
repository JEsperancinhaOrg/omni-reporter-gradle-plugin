plugins {
    kotlin("jvm") version "1.6.10"
    `java-gradle-plugin`
    `maven-publish`
    id( "org.jesperancinha.plugins.omni") version "0.0.0"
    jacoco
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

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}
tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        csv.required.set(true)
    }
}
dependencies {
    implementation("org.jesperancinha.plugins:omni-coveragereporter-commons:0.0.0")
    implementation("ch.qos.logback:logback-classic:1.3.0-alpha12")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.2")
    testImplementation("org.junit.platform:junit-platform-suite:1.8.2")
}
