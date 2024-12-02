if exist "deploy\apache-tomcat-parabank" (echo "apache-tomcat-parabank exists, stopping a possible execution"
:: If apache exists, we are going to stop possible executions
set "CATALINA_HOME=%~dp0\deploy\apache-tomcat-parabank"
CALL "deploy\apache-tomcat-parabank\bin\shutdown.bat"
) 

exit