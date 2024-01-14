# omni-reporter-gradle-plugin

[![Generic badge](https://img.shields.io/static/v1.svg?label=GitHub&message=omni-coveragereporter-gradle-plugin&color=informational)](https://github.com/JEsperancinhaOrg/omni-reporter-gradle-plugin)

[![GitHub release](https://img.shields.io/github/release/JEsperancinhaOrg/omni-reporter-gradle-plugin.svg)](#)
[![Maven Central](https://img.shields.io/maven-central/v/org.jesperancinha.plugins.omni/org.jesperancinha.plugins.omni.gradle.plugin)](https://mvnrepository.com/artifact/org.jesperancinha.plugins.omni/org.jesperancinha.plugins.omni.gradle.plugin)
[![Sonatype Nexus](https://img.shields.io/nexus/r/https/oss.sonatype.org/org.jesperancinha.plugins.omni/org.jesperancinha.plugins.omni.gradle.plugin.svg)](https://search.maven.org/artifact/org.jesperancinha.plugins.omni/org.jesperancinha.plugins.omni.gradle.plugin)

[![javadoc](https://javadoc.io/badge2/org.jesperancinha.plugins/omni-reporter-gradle-plugin/javadoc.svg)](https://javadoc.io/doc/org.jesperancinha.plugins/omni-reporter-gradle-plugin)

[![GitHub License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)

[![Snyk Score](https://snyk-widget.herokuapp.com/badge/mvn/org.jesperancinha.plugins.omni/org.jesperancinha.plugins.omni.gradle.plugin/badge.svg)](https://github.com/JEsperancinhaOrg/omni-reporter-gradle-plugin)

[![omni-reporter-gradle-plugin](https://github.com/JEsperancinhaOrg/omni-reporter-gradle-plugin/actions/workflows/omni-reporter-gradle-plugin.yml/badge.svg)](https://github.com/JEsperancinhaOrg/omni-reporter-gradle-plugin/actions/workflows/omni-reporter-gradle-plugin.yml)

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/63c95f11610e4524baa24b7fb4a153aa)](https://www.codacy.com/gh/JEsperancinhaOrg/omni-reporter-gradle-plugin/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=JEsperancinhaOrg/omni-reporter-gradle-plugin&amp;utm_campaign=Badge_Grade)
[![codebeat badge](https://codebeat.co/badges/2484198c-5744-4dae-b0ff-9df3f395d0f1)](https://codebeat.co/projects/github-com-jesperancinhaorg-omni-reporter-gradle-plugin-main)
[![BCH compliance](https://bettercodehub.com/edge/badge/JEsperancinhaOrg/omni-reporter-gradle-plugin?branch=main)](https://bettercodehub.com/results/JEsperancinhaOrg/omni-reporter-gradle-plugin)

[![Coverage Status](https://coveralls.io/repos/github/JEsperancinhaOrg/omni-reporter-gradle-plugin/badge.svg?branch=main)](https://coveralls.io/github/JEsperancinhaOrg/omni-reporter-gradle-plugin?branch=main)
[![Codacy Badge](https://app.codacy.com/project/badge/Coverage/63c95f11610e4524baa24b7fb4a153aa)](https://www.codacy.com/gh/JEsperancinhaOrg/omni-reporter-gradle-plugin/dashboard?utm_source=github.com&utm_medium=referral&utm_content=JEsperancinhaOrg/omni-reporter-gradle-plugin&utm_campaign=Badge_Coverage)
[![codecov](https://codecov.io/gh/JEsperancinhaOrg/omni-reporter-gradle-plugin/branch/main/graph/badge.svg?token=SbzGa8Rxg7)](https://codecov.io/gh/JEsperancinhaOrg/omni-reporter-gradle-plugin)

[![GitHub language count](https://img.shields.io/github/languages/count/JEsperancinhaOrg/omni-reporter-gradle-plugin.svg)](#)
[![GitHub top language](https://img.shields.io/github/languages/top/JEsperancinhaOrg/omni-reporter-gradle-plugin.svg)](#)
[![GitHub top language](https://img.shields.io/github/languages/code-size/JEsperancinhaOrg/omni-reporter-gradle-plugin.svg)](#)

## Introduction

This plugin allows to publish reports from different formats to different APIs. As an example you can already send reports from [CoveragePy](https://coverage.readthedocs.io/), [LCov](https://wiki.documentfoundation.org/Development/Lcov), [Jacoco](https://www.jacoco.org/jacoco/)
and [OpenClover](https://openclover.org/index) to frameworks like [Coveralls](https://coveralls.io/), [Codecov](https://about.codecov.io/) and [Codacy](https://www.codacy.com/).

This plugin is a spin-off from the development of it's maven counterpart: [![Generic badge](https://img.shields.io/static/v1.svg?label=GitHub&message=omni-coveragereporter-maven-plugin&color=informational)](https://github.com/JEsperancinhaOrg/omni-reporter-maven-plugin). Both the maven plugin and
this one are developed hand in hand. However, their versions may deviate a bit:

| Maven Plugin                                                                                                                                                                                                                                | Gradle Plugin                                                                                                                                                                                                                                                             |
|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [![Maven Central](https://img.shields.io/maven-central/v/org.jesperancinha.plugins/omni-coveragereporter-maven-plugin)](https://mvnrepository.com/artifact/org.jesperancinha.plugins/omni-coveragereporter-maven-plugin)                    | [![Maven Central](https://img.shields.io/maven-central/v/org.jesperancinha.plugins.omni/org.jesperancinha.plugins.omni.gradle.plugin)](https://mvnrepository.com/artifact/org.jesperancinha.plugins.omni/org.jesperancinha.plugins.omni.gradle.plugin)                                                  |
| [![Sonatype Nexus](https://img.shields.io/nexus/r/https/oss.sonatype.org/org.jesperancinha.plugins/omni-coveragereporter-maven-plugin.svg)](https://search.maven.org/artifact/org.jesperancinha.plugins/omni-coveragereporter-maven-plugin) | [![Sonatype Nexus](https://img.shields.io/nexus/r/https/oss.sonatype.org/org.jesperancinha.plugins.omni/org.jesperancinha.plugins.omni.gradle.plugin.svg)](https://search.maven.org/artifact/org.jesperancinha.plugins.omni/org.jesperancinha.plugins.omni.gradle.plugin) |

Please check the Docs for further information on how to configure the plugin. The configurations are very much alike. The main difference is that where in `Maven` you work with XML format, with gradle you work with `JSON` like these examples:

#### 1.  Gradle Groovy

```groovy
omniConfig {
    extraSourceFolders = [new File("$rootDir/international-airports-gui")]
    extraReportFolders = [new File("$rootDir/international-airports-gui/coverage")]
}
```

#### 2.  Gradle Kotlin

```kotlin
configure<OmniReporterPluginExtension> {
    extraSourceFolders = listOf(File("$rootDir/international-airports-gui"))
    extraReportFolders = listOf(File("$rootDir/international-airports-gui/coverage"))
}
```

## Example projects

1.  [![Generic badge](https://img.shields.io/static/v1.svg?label=BitBucket&message=International%20Airports✈️&color=informational)](https://bitbucket.org/jesperancinha/international-airports-service-root)
2.  [![Generic badge](https://img.shields.io/static/v1.svg?label=GitHub&message=itf-chartizate-android🧿&color=informational)](https://github.com/JEsperancinhaOrg/itf-chartizate-android)
3.  [![Generic badge](https://img.shields.io/static/v1.svg?label=GitHub&message=String%20Array%20Paradigms&color=informational)](https://github.com/jesperancinha/string-array-paradigms)

## Release notes

Please find all the release notes on this separate document [ReleaseNotes.md](./ReleaseNotes.md).

## Compatibility notes

##### Gradle Minimal Support version 7.3.3.

This plugin is being tested for versions >= Gradle 7.3.3. For older versions, I cannot guarantee compatibility. During the migration of one of my projects (
i.e. [![Generic badge](https://img.shields.io/static/v1.svg?label=GitHub&message=Performance%20Objects%20&color=informational)](https://github.com/jesperancinha/performance-projects)), I noticed that I was getting a Kotlin related error. The first thought was just to add one of the Kotlin SDK's as a
dependency. However, after thorough search, I also noticed that my Gradle Wrapper was version 5.2.1. Just by doing an update, it started working as expected. This is not to say that anything in between won't work. I'm just not promising any compatibility with older versions than the stated one.

## Making a release

```shell
gradle clean build test
./gradlew publishMavenPublicationToOSSRHRepository
```

The rest manually in [Nexus Sonatype](https://oss.sonatype.org/).

This file is mandatory before a release:

`gradle.properties`:

```properties
ossrhUsername=<ossrhUsername>
ossrhPassword=<ossrhPassword>
signing.keyId=<signing.keyId>
signing.password=<signing.password>
signing.secretKeyRingFile=/Users/<user>/.gnupg/secring.gpg
```

Generate the signing fields(if not already available. check with `gpg -K` first!):

```shell
gpg --full-generate-key
gpg -K
gpg --keyring secring.gpg --export-secret-keys > ~/.gnupg/secring.gpg
gpg --keyserver keyserver.ubuntu.com --send-keys <KEY>
```

- `gpg --full-generate-key` - Key generation
- `gpg -K` - Lists keys. The `KeyId` is the last 8 Digits of the GPG key
- `gpg --keyring secring.gpg --export-secret-keys > ~/.gnupg/secring.gpg` - Exports the keys to `secring.gpg`
- `gpg --keyserver keyserver.ubuntu.com --send-keys <KEY>` - Exports the key so that Sonatype can verify the release

## Coverage report Graphs

<div align="center">
<img width="30%" src="https://codecov.io/gh/JEsperancinhaOrg/omni-reporter-gradle-plugin/branch/main/graphs/sunburst.svg"/>
<img width="30%" src="https://codecov.io/gh/JEsperancinhaOrg/omni-reporter-gradle-plugin/branch/main/graphs/tree.svg"/>
</div>
<div align="center">
<img width="60%" src="https://codecov.io/gh/JEsperancinhaOrg/omni-reporter-gradle-plugin/branch/main/graphs/icicle.svg"/>
</div>

## References

-   [Codacy Coverage Reporter](https://github.com/codacy/codacy-coverage-reporter)
-   [Jackson Module](https://medium.com/@foxjstephen/how-to-actually-parse-xml-in-java-kotlin-221a9309e6e8)
-   [XCode Environment Variable Reference](https://developer.apple.com/documentation/xcode/environment-variable-reference)
-   [Cross-CI reference](https://github.com/streamich/cross-ci)
-   [Coveralls API reference](https://docs.coveralls.io/api-reference)
-   [Git Hub Environment Variables](https://docs.github.com/en/actions/learn-github-actions/environment-variables)
-   [Git Lab Environment Variables](https://docs.gitlab.com/ee/ci/variables/predefined_variables.html)
-   [Check Run Reporter](https://github.com/marketplace/check-run-reporter)
-   [Codacy Maven Plugin](https://github.com/halkeye/codacy-maven-plugin)
-   [Coveralls Maven Plugin](https://github.com/trautonen/coveralls-maven-plugin)
-   [Example Java Maven for CodeCov](https://github.com/codecov/example-java-maven)
-   [CodeCov Maven Plugin](https://github.com/alexengrig/codecov-maven-plugin)
-   [Working with PGP Signatures](https://central.sonatype.org/publish/requirements/gpg/)
-   [How to publish artifact to Maven Central via Gradle](https://www.albertgao.xyz/2018/01/18/how-to-publish-artifact-to-maven-central-via-gradle/)
-   [Deploying to OSSRH with Gradle - Introduction](https://central.sonatype.org/publish/publish-gradle/)
-   [Writing a simple plugin](https://docs.gradle.org/current/userguide/custom_plugins.html)

## About me

[![GitHub followers](https://img.shields.io/github/followers/jesperancinha.svg?label=Jesperancinha&style=for-the-badge&logo=github&color=grey "GitHub")](https://github.com/jesperancinha)
