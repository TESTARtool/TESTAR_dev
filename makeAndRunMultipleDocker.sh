#!/bin/bash
#yes | docker system prune --all

./gradlew distTar

docker build -t testar/testarstatemodel:latest .

if [ $# -eq 0 ]
then
  #docker run --add-host=host.docker.internal:host-gateway --shm-size=512m --mount type=bind,source=/home/testar/TESTAR_dev,target=/mnt --mount #type=bind,source=/home/testar/testardock/output,target=/testar/bin/output testar/testarstatemodel:latest
  docker run --add-host=host.docker.internal:host-gateway --shm-size=512m --mount type=bind,source=/home/testar/TESTAR_dev,target=/mnt testar/testarstatemodel:latest
fi

if [ $# -eq 1 ]
then
  for i in `seq $1`
  do
    #docker run -d --add-host=host.docker.internal:host-gateway --shm-size=512m --mount type=bind,source=/home/testar/TESTAR_dev,target=/mnt --mount #type=bind,source=/home/testar/testardock/output,target=/testar/bin/output testar/testarstatemodel:latest
    docker run -d --add-host=host.docker.internal:host-gateway --shm-size=512m --mount type=bind,source=/home/testar/TESTAR_dev,target=/mnt testar/testarstatemodel:latest
  done
fi