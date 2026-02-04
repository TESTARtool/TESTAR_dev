@echo off
setlocal

set "COMPOSE_FILE=docker-compose-android-budtmo-emulator.yml"

if not exist "%COMPOSE_FILE%" (
  echo ERROR: %COMPOSE_FILE% not found in: %cd%
  exit /b 1
)

echo [1/2] Removing any existing compose stack / containers ...
REM Bring down previous stack (ignore errors)
docker compose -f "%COMPOSE_FILE%" down --remove-orphans -v >nul 2>&1

REM Also force-remove any leftover containers matching the service/container name
for /f "usebackq delims=" %%i in (`docker ps -a -q --filter "name=android-emulator"`) do (
  docker rm -f %%i >nul 2>&1
)

echo [2/2] Pulling image and starting a clean container ...
docker compose -f "%COMPOSE_FILE%" pull
if errorlevel 1 (
  echo docker compose pull failed.
  exit /b 1
)

docker compose -f "%COMPOSE_FILE%" up -d --force-recreate --remove-orphans
if errorlevel 1 (
  echo docker compose up failed.
  exit /b 1
)

echo Done.
echo VNC:    http://localhost:6080
echo Appium: http://localhost:4723/wd/hub

endlocal
