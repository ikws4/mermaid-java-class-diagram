build:
	./gradlew assemble
	rm -rf dist
	unzip app/build/distributions/app.zip -d dist/
