#!/bin/bash

### install java and ant
yes | apt-get update && upgrade
yes | apt-get install openjdk-11-jdk
yes | apt-get install ant

### prepare and run the jacoco dumper application
rm -r -f /home/testar/dumper_parabank/
cp -r /home/testar/qsamba/suts/dumper_parabank/ /home/testar/
cd /home/testar/dumper_parabank/ && java -jar jacocoDumper.jar &>/dev/null &
cd /home/testar/

### prepare a clean apache server instance the with SUT
rm -r -f /home/testar/apache-tomcat-parabank/
cp -r /home/testar/qsamba/suts/apache-tomcat-parabank/ /home/testar/
cd /home/testar/apache-tomcat-parabank/bin/ && ./testar_catalina.sh start
cd /home/testar/

### prepare a clean orientdb testar database (testar / testar)
rm -r -f /home/testar/orientdb-3.0.34
cp -r /home/testar/qsamba/suts/orientdb-3.0.34 /home/testar
### execute the orientdb database in the background
/home/testar/orientdb-3.0.34/bin/server.sh &>/dev/null &

### Wait 60 sec for apache server and orientdb to be deployed
sleep 60

### Prune all docker images and containers
yes | docker system prune --all

### Prepare a testar compilation
rm -r -f /home/testar/TESTAR_dev/
cp -r /home/testar/qsamba/suts/TESTAR_dev/ /home/testar/
cd /home/testar/TESTAR_dev/
./gradlew distTar

### Build a testar docker image
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

### Wait until all docker containers have finished
until [ -z "$(docker ps -a -q --filter "status=running")" ]
do
  sleep 10
done

### Stop apache server, jacoco dumper will end automatically
cd /home/testar/apache-tomcat-parabank/bin/ && ./testar_catalina.sh stop
cd /home/testar/

### stop the orientdb database that runs in the background
/home/testar/orientdb-3.0.34/bin/shutdown.sh localhost 2424 -u root -p testar

### Wait a bit in case jacocoDumper has some thread finishing
sleep 20
### Cope web metrics results to samba server
cd /home/testar/dumper_parabank/
cp webCoverageMetrics* /home/testar/qsamba/results/