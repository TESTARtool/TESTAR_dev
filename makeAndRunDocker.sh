#!/bin/bash

# Compile TESTAR_dev project with Gradle wrapper
./gradlew distTar

# Stop testar container if it is running
if [ "$(docker ps -q -f name=testar)" ]; then
  docker stop testar
fi

# Remove testar container if it exists
if [ "$(docker ps -aq -f name=testar)" ]; then
  docker rm testar
fi

docker build -t testar-local/testar:latest .

docker run -d --shm-size=512m --name testar \
--mount type=bind,source="/Users/username/TESTAR_dev/testar/resources/settings",target=/testar/bin/settings \
--mount type=bind,source="/Users/username/TESTAR_dev",target=/mnt \
--mount type=bind,source="/Users/username/TESTAR_dev/testar/resources/output",target=/testar/bin/output \
testar-local/testar:latest
