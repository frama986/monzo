.PHONY: all run dist fatJar jar

all: run

run: dist
	./app/build/install/app/bin/app

dist:
	./gradlew installDist

jar: fatJar
	java -jar app/build/libs/app.jar

fatJar:
	./gradlew jar
