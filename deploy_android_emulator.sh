#!/usr/bin/env bash
set -euo pipefail

COMPOSE_FILE="docker-compose-android-budtmo-emulator.yml"

if [[ ! -f "${COMPOSE_FILE}" ]]; then
  echo "ERROR: ${COMPOSE_FILE} not found in: $(pwd)" >&2
  exit 1
fi

echo "[1/2] Removing any existing compose stack / containers ..."
# Bring down previous stack (ignore errors)
docker compose -f "${COMPOSE_FILE}" down --remove-orphans -v >/dev/null 2>&1 || true

# Also force-remove any leftover containers matching the service/container name
ids="$(docker ps -a -q --filter "name=android-emulator" || true)"
if [[ -n "${ids}" ]]; then
  docker rm -f ${ids} >/dev/null 2>&1 || true
fi

echo "[2/2] Pulling image and starting a clean container ..."
docker compose -f "${COMPOSE_FILE}" pull
docker compose -f "${COMPOSE_FILE}" up -d --force-recreate --remove-orphans

echo "Done."
echo "VNC:    http://localhost:6080"
echo "Appium: http://localhost:4723/wd/hub"
