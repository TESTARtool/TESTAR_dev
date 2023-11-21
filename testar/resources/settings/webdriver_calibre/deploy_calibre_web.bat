@echo off
set calibre=linuxserver/calibre-web:0.6.19

:: Stop all existing "calibre" containers
FOR /F "tokens=*" %%i IN ('docker ps -q --filter "name=calibre-web"') DO docker stop %%i

:: Clean all existing "calibre" containers
FOR /F "tokens=*" %%i IN ('docker ps -a -q --filter "name=calibre-web"') DO docker rm %%i

:: Pull the "calibre" image
docker pull %calibre%

:: Run the "calibre" container
docker run -d --name=calibre-web -e PUID=1000 -e PGID=1000 -e TZ=Etc/UTC -p 8083:8083 -v %cd%/books:/books --restart unless-stopped %calibre%
