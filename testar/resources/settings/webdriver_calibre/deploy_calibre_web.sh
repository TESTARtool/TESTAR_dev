#!/bin/bash

calibre="linuxserver/calibre-web:0.6.19"

# Stop all existing "calibre" containers
docker ps -q --filter "name=calibre-web" | xargs docker stop

# Clean all existing "calibre" containers
docker ps -a -q --filter "name=calibre-web" | xargs docker rm

# Pull the "calibre" image
docker pull $calibre

# Run the "calibre" container
docker run -d --name=calibre-web -e PUID=1000 -e PGID=1000 -e TZ=Etc/UTC -p 8083:8083 -v "$(pwd)"/books:/books --restart unless-stopped $calibre
