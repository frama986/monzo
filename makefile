.PHONY: all run dist

all: run

run: dist
	./app/build/install/app/bin/app

dist:
	./gradlew installDist