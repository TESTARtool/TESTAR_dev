#!/bin/bash

number_dockers=1

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
dos2unix gradlew
dos2unix runImage
./gradlew distTar

### Build a testar docker image
docker build -t testar/testarstatemodel:latest .

### Number of dockers containers that run in parallel
for i in {1..$number_dockers}
do
  docker run -d --add-host=host.docker.internal:host-gateway --shm-size=512m --mount type=bind,source=/home/testar/TESTAR_dev,target=/mnt testar/testarstatemodel:latest
done

### We move to dumper folder to have all the metrics in the same place
cd /home/testar/dumper_parabank/
### Create a text file to print performance metrics
filedate=$(date +performanceMetrics_%Y_%m_%d_%H_%M_%S).txt
touch $filedate

echo "Waiting for TESTAR docker containers to finish the GUI exploration..."
### Wait until all docker containers have finished
until [ -z "$(docker ps -a -q --filter "status=running")" ]
do
  sleep 5
  ### Print the total cpu and memory usage of all the processes (OrientDB, Apache Tomcat, Dockers, Java Dumper)
  #ps -a -o %cpu,%mem | tail -n +2 | awk '{cpu+=$1; mem+=$2} END { print "CPU | " cpu " | MEM | " mem }1' | tail -n 1
  # awk 'BEGIN {cpu=0;mem=0} {cpu+=$1; mem+=$2} END {print cpu,mem}'
  per=$(ps -e -o %cpu,%mem | tail -n +2 | awk 'BEGIN {cpu=0;mem=0} {cpu+=$1; mem+=$2} END { print "CPU | " cpu " | MEM | " mem }1' | tail -n 1)
  nodes=$(docker ps -a -q --filter "status=running" | wc -l)
  echo "Time | $(date +%Y_%m_%d_%H_%M_%S) | Dockers | $nodes | $per" >> $filedate
done

### Stop apache server, jacoco dumper will end automatically
cd /home/testar/apache-tomcat-parabank/bin/ && ./testar_catalina.sh stop
cd /home/testar/

### stop the orientdb database that runs in the background
/home/testar/orientdb-3.0.34/bin/shutdown.sh localhost 2424 -u root -p testar

### Wait a bit in case jacocoDumper has some thread finishing
sleep 30

### Create folders if do not exist
mkdir -p /home/testar/qsamba/results/dockers_$number_dockers/coverage
mkdir -p /home/testar/qsamba/results/dockers_$number_dockers/model
mkdir -p /home/testar/qsamba/results/dockers_$number_dockers/performance

### Copy metrics results to samba server
cd /home/testar/dumper_parabank/
cp webCoverageMetrics* /home/testar/qsamba/results/dockers_$number_dockers/coverage
cp stateModelMetrics* /home/testar/qsamba/results/dockers_$number_dockers/model
cp performanceMetrics* /home/testar/qsamba/results/dockers_$number_dockers/performance
