# Omni Reporter Commons Release Notes

#### Release 0.0.2 - 2022/01/22

Secure Google Http Client

#### Release 0.0.1 - 2022/01/21

Multimodule support

#### Release 0.0.0 - 2022/01/21

First release in a flash way

---

## Release notes from Omni Reporter Maven Plugin

#### Release 0.0.11 - 2022/01/13

1. Ignore if package not found for Codecov with `failOnUnknown`

#### Release 0.0.10 - 2022/01/09

1. Path Corrections for Codecov Jacoco reports

#### Release 0.0.9 - 2022/01/09

1. Codecov support for endpoint V4 version
2. API token support for codacy

#### Release 0.0.8 - 2022/01/08

1. Disable flags for Coveralls and Codacy to force them out even when environment variables are available
    1. `disableCoveralls`
    2. `disableCodacy`
2. Exception handling for Codacy formatting issue
    3. `failOnXmlParsingError`, false by default
3. Codacy update so solve Xerces module error. Manual implementation required

#### Release 0.0.7 - 2022/01/06

1. Codacy support - JAVA 11 Only
2. `failOnReportNotFound`
3. `failOnUnknown` Bug fix
4. Possibility to add external root sources - useful in cases where projects are using scala, java, kotlin and/or clojure at the same time. The plugin only recognizes one source directory. Parameter name is `extraSourceFolders`
5. `failOnReportSendingError`

#### Release 0.0.6 - 2022/01/05

1. Rollback `CI_COMMIT_REF_NAME`. Unfortunately this messes up the way the JOB ID is calculated.

> It has been observed that GitLab checks out the repo on the desired commit, but looses reference to the branch at this point. This means that the only reference to the commit is the hash. If we change the branch name, it will use the hash-number regardless of what we configure for the Job ID.
> Example
>
> Branch = main, Hash = AAAAAAAAAAAAA and service_number = null => JobId = #AAAAAAAAAAAAA, Branch Name = main
>
> Branch = AAAAAAAAAAAAA, Hash AAAAAAAAAAAAA and service_number = null -> JobId = #AAAAAAAAAAAAA, Branch Name = AAAAAAAAAAAAA
>
> Branch = AAAAAAAAAAAAA, Hash AAAAAAAAAAAAA and service_number = 99 -> JobId = 99, Branch Name = AAAAAAAAAAAAA

#### Release 0.0.5 - 2022/01/05

1. Fix branch naming of GitLab using `CI_COMMIT_REF_NAME`

#### Release 0.0.4 - 2022/01/03

1. Fix JAXB upgrade -> Use [Jackson Module](https://medium.com/@foxjstephen/how-to-actually-parse-xml-in-java-kotlin-221a9309e6e8) parser

#### Release 0.0.3 - 2022/01/03

1. Correct JOB_ID and RUN_ID for GIT_RUN pipeline
2. `useCoverallsCount` to let Coveralls decide Job and run numbers.

#### Release 0.0.2 - 2022/01/03

1. Source encoding gets automatically chosen unless we configure flag `failOnNoEncoding` to `true`
2. Ignore test build directory by default. Make `ignoreTestBuildDirectory`, `true` by default.
3. Find files in all sources directories including generated sources

#### Release 0.0.1 - 2022/01/02

1. Rejection words implemented. Fixes issue with GitHub pipelines build names for Coveralls Report
2. Token log shadowing (even in debug) for Coveralls Report

#### Release 0.0.0 - 2022/01/01

1. We can ignore unknown class error generated by Jacoco. This happens with some Kotlin code. The option is `failOnUnknown`
2. [Saga](https://timurstrekalov.github.io/saga/) and [Cobertura](https://www.mojohaus.org/cobertura-maven-plugin/) support is not given because of the lack of updates in these plugins for more than 5 years.
3. Plugin will search for all jacoco.xml files located in the build directory.
4. If there are two reports with the same file reported, the result will be a sum.
5. Coveralls support
6. DOM processing instead of SAX. Using an event parser for XML can be quite cumbersome and if the XML document isn't correctly validated, we run the risk of having misleading or false results. In any case, when making a code report, we usually don't need to worry about performance and if we do, it
   is probably a sign that the codebase is too big and that our code is becoming a monolith.
7. Line Coverage