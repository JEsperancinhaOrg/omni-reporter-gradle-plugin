include Makefile.mk

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
deps-plugins-update:
	curl -sL https://raw.githubusercontent.com/jesperancinha/project-signer/master/pluginUpdatesOne.sh | bash -s -- $(PARAMS)
deps-java-update:
	curl -sL https://raw.githubusercontent.com/jesperancinha/project-signer/master/javaUpdatesOne.sh | bash
deps-gradle-update:
	curl -sL https://raw.githubusercontent.com/jesperancinha/project-signer/master/gradleUpdatesOne.sh | bash
deps-quick-update: deps-gradle-update deps-plugins-update deps-java-update
