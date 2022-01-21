wrapper:
	gradle wrapper
buildw:
	gradle clean build test publishToMavenLocal -i
	sed 's+//++g' build.gradle.kts  > plugin.build.gradle.kts
	./gradlew -c plugin.settings.gradle.kts build -i
upgrade:
	gradle wrapper --gradle-version 7.3.3
upgrade-mac-os:
	brew upgrade gradle
stage:
	gradle clean build test
	./gradlew publishMavenPublicationToOSSRHRepository
