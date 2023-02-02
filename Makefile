b: buildw
wrapper:
	gradle wrapper
buildw:
	gradle clean build test publishToMavenLocal -i
	sed 's+//++g' build.gradle.kts  > plugin.build.gradle.kts
	./gradlew -c plugin.settings.gradle.kts build -i
upgrade:
	gradle wrapper --gradle-version 7.6
upgrade-mac-os:
	brew upgrade gradle
test:
	gradle clean build test
stage: test
	./gradlew publishMavenPublicationToOSSRHRepository
