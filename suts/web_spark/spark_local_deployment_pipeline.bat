@echo off

:: Check that prerequisites dependencies are installed
call git --version
call docker --version
call node --version
call npm --version

:::::::::::::::::::::::::::::
:: Clean the SPARK folders ::
:::::::::::::::::::::::::::::

rmdir /s /q "spark-core"
rmdir /s /q "spark-dashboard"
rmdir /s /q "spark-server-backend"

:::::::::::::::::::::::::::::::::::
:: Prepare the SPARK core module ::
:::::::::::::::::::::::::::::::::::

git clone https://gitlab.com/elsa-lab/spark/spark-core.git
cd spark-core
git checkout Develop
call del package-lock.json
call npm install
call npm run build

cd ..

::::::::::::::::::::::::::::::::::::::
:: Prepare the SPARK backend module ::
::::::::::::::::::::::::::::::::::::::

git clone https://gitlab.com/elsa-lab/spark/spark-server-backend.git
cd spark-server-backend
git checkout mvp

:: Prepare the server Docker Compose File
>docker-compose.yaml (
  echo version: '3'
  echo services:
  echo   mongo:
  echo     image: mongo
  echo     restart: always
  echo     environment:
  echo       MONGO_INITDB_ROOT_USERNAME: root
  echo       MONGO_INITDB_ROOT_PASSWORD: test
  echo     ports:
  echo       - 27017:27017
  echo   mongo-express:
  echo     image: mongo-express
  echo     restart: always
  echo     ports:
  echo       - 8081:8081
  echo     environment:
  echo       ME_CONFIG_MONGODB_URL: "mongodb://root:test@mongo:27017/"
  echo       ME_CONFIG_MONGODB_ADMINUSERNAME: root
  echo       ME_CONFIG_MONGODB_ADMINPASSWORD: test
  echo   redis:
  echo     image: redis
  echo     restart: always
  echo     ports:
  echo       - 6379:6379
  echo   nats:
  echo     image: nats
  echo     restart: always
  echo     ports:
  echo       - 4222:4222
  echo       - 8222:8222
)

:: Stop and delete all possible existing dockers
docker compose down -v

:: Deploy the Docker containers
docker compose -f docker-compose.yaml up -d
docker ps

:: Prepare the server .env file
:: this contains some custom test 32 chars keys
del /f example.env

>.env (
  echo # Development environment variables
  echo DB="mongodb://root:test@localhost:27017/"
  echo NATS_SERVER=
  echo REDIS_HOST="localhost:6379"
  echo ROOT_KEY="test0123456789test0123456789test"
  echo DB_KEY="test0123456789test0123456789test"
  echo DASHBOARD_URL="localhost"
  echo PORT=80
  echo HTTPS=off
)

:: Install dependencies and build the backend
call npm install
call npm run build

:: Run the backend
start "Spark Backend" cmd /k "npm run start-sw"

cd..

::::::::::::::::::::::::::::::::::::::::
:: Prepare the SPARK dashboard module ::
::::::::::::::::::::::::::::::::::::::::

git clone https://gitlab.com/elsa-lab/spark/spark-dashboard.git
cd spark-dashboard
git checkout mvp-app
del package-lock.json

:: Prepare the dashboard config
>config.json (
  echo {
  echo     "SERVER_URL": "localhost",
  echo     "DEFAULT_LANGUAGE": "en-US"
  echo }
)

:: Install dependencies and build the dashboard
call npm install react@17.0.2 react-dom@17.0.2
call npm install @mui/system@^5.18.0 @mui/material@^5.18.0 @mui/icons-material@^5.18.0
call npm install --save-dev @types/react@17 @types/react-dom@17 http-server
:: call npm run build

:: Change the buildEnv line that changes the env file
setlocal EnableDelayedExpansion

set "file=buildEnv.js"
set "tmp=%file%.tmp"
set "replaced=0"

> "%tmp%" (
  for /f "usebackq delims=" %%L in ("%file%") do (
    set "line=%%L"
    if !replaced! EQU 0 (
      rem Check if this line contains REACT_APP_API_URL=
      if "!line:REACT_APP_API_URL=!"=="!line!" (
        echo(!line!
      ) else (
        echo                 `REACT_APP_API_URL=${config.SERVER_URL}`,
        set "replaced=1"
      )
    ) else (
      echo(!line!
    )
  )
)

move /y "%tmp%" "%file%"
endlocal

:: Run the dashboard
start "Spark Dashboard" cmd /k "npm run dev"

cd ..

:::::::::::::::::::::::::::::::::::::::
:: Wait until SPARK webpage is ready ::
:::::::::::::::::::::::::::::::::::::::

set "URL=http://localhost:3000"
set "MAX_WAIT=600"     REM total seconds
set "INTERVAL=10"      REM seconds between checks
set /a TRIES=%MAX_WAIT% / %INTERVAL%

echo Waiting up to %MAX_WAIT%s for %URL% to be ready...

for /L %%i in (1,1,%TRIES%) do (
    curl -s -o NUL "%URL%" >NUL 2>&1

    if not errorlevel 1 (
        goto :EOF
    )

    timeout /t %INTERVAL% /nobreak >NUL
)

echo.
echo ERROR: Timed out after %MAX_WAIT%s waiting for %URL%.
goto :EOF
