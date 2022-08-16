#!/bin/bash

./mvnw gitflow:release-start gitflow:release-finish

git push origin master

git push --tags

git push origin develop

read -p "Which version to publish? > " version

git checkout $version

./mvnw deploy -Prelease