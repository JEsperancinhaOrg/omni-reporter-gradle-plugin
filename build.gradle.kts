plugins {
    kotlin("jvm") version "2.2.0"
    `java-gradle-plugin`
    `maven-publish`
//    id("org.jesperancinha.plugins.omni") version "0.3.1"
    jacoco
    signing
    `java-library`
    id("io.codearte.nexus-staging") version "0.30.0"
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(11))
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

group = "org.jesperancinha.plugins.omni"
version = "0.4.5"

val test by tasks.getting(Test::class) {
    useJUnitPlatform { }
}

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

val JACOCO = "0.8.13"
val JUPITER = "5.13.4"

configure<JavaPluginExtension> {
    withJavadocJar()
    withSourcesJar()
}

configure<PublishingExtension> {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                groupId = "org.jesperancinha.plugins.omni"
                artifactId = "org.jesperancinha.plugins.omni.gradle.plugin"
                version = "0.4.5"
                name.set("Omni Coverage Reporter Gradle Plugin")
                description.set("A plugin to report coverage to differet API's")
                url.set("https://joaofilipesabinoesperancinha.nl/")

                licenses {
                    license {
                        name.set("Apache 2.0")
                        url.set("https://opensource.org/licenses/Apache-2.0")
                        distribution.set("repo")
                    }
                }

                developers {
                    developer {
                        id.set("jesperancinha")
                        name.set("João Esperancinha")
                        email.set("jofisaes@gmail.com")
                        organization.set("jesperancinha.org")
                        organizationUrl.set("https://joaofilipesabinoesperancinha.nl/")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/JEsperancinhaOrg.git")
                    developerConnection.set("scm:git:git://github.com/JEsperancinhaOrg.git")
                    url.set("https://github.com/JEsperancinhaOrg/omni-reporter-commons")
                    tag.set("0.4.5")
                }
            }
        }
        repositories {
            if (project.properties["ossrhUsername"] != null) {
                maven {
                    name = "OSSRH"
                    setUrl("https://oss.sonatype.org/service/local/staging/deploy/maven2")
                    credentials {
                        username = project.properties["ossrhUsername"] as String
                        password = project.properties["ossrhPassword"] as String
                    }
                }
            }
            mavenLocal()
        }
    }
}

configure<SigningExtension> {
    if (project.properties["ossrhUsername"] != null) {
        sign(publishing.publications.findByName("maven"))
    }
}

apply<MavenPublishPlugin>()

apply<SigningPlugin>()

dependencies {
    implementation("org.jesperancinha.plugins:omni-coveragereporter-commons:0.4.5")
    implementation("org.jacoco:org.jacoco.core:${JACOCO}")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$JUPITER")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$JUPITER")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$JUPITER")
    testImplementation("org.junit.platform:junit-platform-suite:1.13.4")
}
