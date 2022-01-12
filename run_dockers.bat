rem https://testar.org/images/development/experiments/apache-tomcat-cities.zip
rem https://testar.org/images/development/experiments/orientdb_testar_db.zip

rmdir /s /q "apache-tomcat-cities"
rmdir /s /q "orientdb-3.0.34"

robocopy "root\apache-tomcat-cities" "apache-tomcat-cities" /s /e /nfl /ndl /np
robocopy "root\orientdb-3.0.34" "orientdb-3.0.34" /s /e /nfl /ndl /np

START "orientdb" /D "C:\Users\Fernando\Desktop\DistributedTest\orientdb-3.0.34\bin" server.bat
cd "C:\Users\Fernando\Desktop\DistributedTest\apache-tomcat-cities\bin" && cmd /c catalina.bat start

timeout /t 10

cd "C:\Users\Fernando\Documents\GitHub\TESTAR_dev"
call gradlew distTar
docker build -t testar/testarstatemodel:latest .

rem docker container rm $(docker container ls -aq)
FOR /f "tokens=*" %%i IN ('docker ps -aq') DO docker rm %%i

FOR /L %%A IN (1,1,%1) DO (
  docker run -d --add-host=host.docker.internal:host-gateway --shm-size=512m testar/testarstatemodel:latest
)

cd "C:\Users\Fernando\Desktop\DistributedTest"

timeout /t 300

FOR /f "tokens=*" %%i IN ('docker ps -q') DO docker stop %%i