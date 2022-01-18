rem https://testar.org/images/development/experiments/apache-tomcat-parabank.zip
rem https://testar.org/images/development/experiments/dumper_parabank.zip
rem https://testar.org/images/development/experiments/orientdb_testar_db.zip

rmdir /s /q "dumper_parabank"
rmdir /s /q "apache-tomcat-parabank"
rmdir /s /q "orientdb-3.0.34"

robocopy "root\dumper_parabank" "dumper_parabank" /s /e /nfl /ndl /np
robocopy "root\apache-tomcat-parabank" "apache-tomcat-parabank" /s /e /nfl /ndl /np
robocopy "root\orientdb-3.0.34" "orientdb-3.0.34" /s /e /nfl /ndl /np

START "dumper" /D "C:\Users\Fernando\Desktop\DistributedTest\dumper_parabank" java -jar jacocoDumper.jar
START "orientdb" /D "C:\Users\Fernando\Desktop\DistributedTest\orientdb-3.0.34\bin" server.bat
cd "C:\Users\Fernando\Desktop\DistributedTest\apache-tomcat-parabank\bin" && cmd /c testar_catalina.bat start

timeout /t 30

cd "C:\Users\Fernando\Documents\GitHub\TESTAR_dev"
call gradlew distTar
docker build -t testar/testarstatemodel:latest .

rem docker container rm $(docker container ls -aq)
FOR /f "tokens=*" %%i IN ('docker ps -aq') DO docker rm %%i

@echo Started Execution: %date% %time%

FOR /L %%A IN (1,1,%1) DO (
  docker run -d --add-host=host.docker.internal:host-gateway --shm-size=512m testar/testarstatemodel:latest
)

cd "C:\Users\Fernando\Desktop\DistributedTest"

:wait
timeout /t 5
FOR /f "tokens=*" %%i IN ('docker ps -a -q --filter "status=running"') DO GOTO wait

@echo End Execution: %date% %time%

timeout /t 20

cd "C:\Users\Fernando\Desktop\DistributedTest\apache-tomcat-parabank\bin" && cmd /c testar_catalina.bat stop
