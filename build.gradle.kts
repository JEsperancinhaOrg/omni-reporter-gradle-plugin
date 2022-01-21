plugins {
    kotlin("jvm") version "1.6.10"
    `java-gradle-plugin`
    `maven-publish`
    id("org.jesperancinha.plugins.omni") version "0.0.0"
    jacoco
    signing
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

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(tasks.javadoc)
}

tasks.withType<Sign>().configureEach {
    onlyIf {
        gradle.taskGraph.hasTask(":publishToSonatype")
    }
}

signing {
    val signingKey = providers
        .environmentVariable("GPG_SIGNING_KEY")
        .forUseAtConfigurationTime()
    val signingPassphrase = providers
        .environmentVariable("GPG_SIGNING_PASSPHRASE")
        .forUseAtConfigurationTime()
    if (signingKey.isPresent && signingPassphrase.isPresent) {
        useInMemoryPgpKeys(signingKey.get(), signingPassphrase.get())
        val extension = extensions
            .getByName("publishing") as PublishingExtension
        sign(extension.publications)
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
        create<MavenPublication>("mavenJava") {
            artifact(sourcesJar.get())
            artifact(javadocJar.get())
            pom {
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
                        name.set("João Esperancinha")
                        email.set("jofisaes@gmail.com")
                        organization.set("jessperancinha.org")
                        organizationUrl.set("https://joaofilipesabinoesperancinha.nl/")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/JEsperancinhaOrg.git")
                    developerConnection.set("scm:git:git://github.com/JEsperancinhaOrg.git")
                    url.set("https://github.com/JEsperancinhaOrg/omni-reporter-commons")
                    tag.set("0.0.0")
                }
            }
        }
    }
}

val JACOCO = "0.8.7"
val JUPITER = "5.8.2"

dependencies {
    implementation("org.jesperancinha.plugins:omni-coveragereporter-commons:0.0.0")
    implementation("ch.qos.logback:logback-classic:1.3.0-alpha12")
    implementation("org.jacoco:org.jacoco.core:${JACOCO}")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$JUPITER")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$JUPITER")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$JUPITER")
    testImplementation("org.junit.platform:junit-platform-suite:1.8.2")
}
