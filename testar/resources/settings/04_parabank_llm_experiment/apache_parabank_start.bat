:: Prepare an Apache Tomcat with parabank
:: https://testar.org/images/development/experiments/apache-tomcat-parabank.zip

if exist "deploy\apache-tomcat-parabank" (echo "apache-tomcat-parabank exists, stopping a possible execution"
:: If apache exists, we are going to stop possible executions
set "CATALINA_HOME=%~dp0\deploy\apache-tomcat-parabank"
CALL "deploy\apache-tomcat-parabank\bin\shutdown.bat"
) else (echo "downloading apache-tomcat-parabank..."
:: If apache does not exists, we download the software from the link and unzip
curl -L "https://testar.org/images/development/experiments/apache-tomcat-parabank.zip" -o "deploy\apache-tomcat-parabank.zip" --create-dirs
tar -xf "deploy\apache-tomcat-parabank.zip" -C "deploy"
)

:: This timeout does not work when invoking from the current java buffer
:: Only if opened in a new cmd
timeout /t 10

:: Remove the parabank webapp, then next execution will deploy a clean one
rmdir /s /q "deploy\apache-tomcat-parabank\webapps\parabank"

set "CATALINA_HOME=%~dp0\deploy\apache-tomcat-parabank"
CALL "deploy\apache-tomcat-parabank\bin\startup.bat"

exit