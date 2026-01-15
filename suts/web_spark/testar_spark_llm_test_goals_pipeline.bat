@echo off
setlocal EnableExtensions EnableDelayedExpansion

:::::::::::::::::::::::::::
:: Run SPARK web project ::
:::::::::::::::::::::::::::

docker compose -f spark-docker-compose.yml up --build -d
if errorlevel 1 (
  echo ERROR: docker compose failed.
  exit /b 1
)

:::::::::::::::::::::::::::::::::::::::
:: Wait until SPARK webpage is ready ::
:::::::::::::::::::::::::::::::::::::::

set "URL=http://localhost:3000"
set "MAX_WAIT=600"     REM total seconds
set "INTERVAL=5"       REM seconds between checks
set /a TRIES=MAX_WAIT/INTERVAL

echo Waiting up to %MAX_WAIT%s for %URL% to be ready...

set "READY="
for /L %%i in (1,1,%TRIES%) do (
  curl -fsS "%URL%" >NUL 2>&1
  if not errorlevel 1 (
    set "READY=1"
    goto :spark_ready
  )
  timeout /t %INTERVAL% /nobreak >NUL
)

:spark_ready
if not defined READY (
  echo.
  echo ERROR: Timed out after %MAX_WAIT%s waiting for %URL%.
  exit /b 1
)
echo SPARK is up.

:::::::::::::::::::::::::::::::::::::
:: Build the TESTAR-AUTOLINK image ::
:::::::::::::::::::::::::::::::::::::

docker build -t testar_spark_llm:latest -f spark-testar-autolink.Dockerfile .
if errorlevel 1 (
  echo ERROR: docker build failed.
  exit /b 1
)

:::::::::::::::::::::::::::::::::::::::::
:: Run all TESTAR-AUTOLINK test goals  ::
:::::::::::::::::::::::::::::::::::::::::

REM Base output folder
set "OUTROOT=C:\testardock\testar_spark_llm\output"

REM Common docker run flags
set "COMMON_RUN=--add-host=host.docker.internal:host-gateway --shm-size=1g --security-opt apparmor=unconfined --security-opt seccomp=unconfined"

REM Define goals list
set "GOAL_1=/webdriver_b00_spark_llm/1_spark_valid_login.goal"
set "GOAL_2=/webdriver_b00_spark_llm/2_spark_invalid_login.goal"
set "GOAL_6=/webdriver_b00_spark_llm/6_spark_create_satisfaction_survey.goal"

REM Which ones to run (IDs)
set "GOAL_IDS=1 2 6"

for %%I in (%GOAL_IDS%) do (
  call set "GOAL=%%GOAL_%%I%%"

  if not defined GOAL (
    echo ERROR: Missing GOAL_%%I definition. Continuing...
  ) else (
    set "NAME=testar_spark_llm_goal_%%I"
    set "OUTDIR=%OUTROOT%\goal_%%I"

    if not exist "!OUTDIR!" mkdir "!OUTDIR!" >NUL 2>&1
    docker rm -f "!NAME!" >NUL 2>&1

    echo.
    echo Running !NAME! with goal "!GOAL!" output "!OUTDIR!"...

    docker run --rm ^
      -e OPENAI_API="%OPENAI_API%" ^
      -e SUT_URL="http://localhost:3000" ^
      -e LLM_TEST_GOAL="!GOAL!" ^
      --name "!NAME!" ^
      --mount type=bind,source="!OUTDIR!",target=/home/seluser/testar_autolink/bin/output ^
      %COMMON_RUN% ^
      testar_spark_llm:latest

    if errorlevel 1 (
      echo ERROR: !NAME! failed. Continuing...
    ) else (
      echo !NAME! finished.
    )
  )
)

echo.
echo Done.
exit /b 0
