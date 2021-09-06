call gradlew distTar

docker build -t testar/testarstatemodel:latest .

rem docker run -d --shm-size=512m --mount type=bind,source="C:\testardock\settings",target=/testar/bin/settings --mount type=bind,source="C:\Users\testar\Desktop\TESTAR_dev",target=/mnt --mount type=bind,source="c:\testardock\output",target=/testar/bin/output testar/testarstatemodel:latest

rem not sure if for the experiments I really need to mount settings and output folder
rem maybe just execute the desired protocol
docker run -d --shm-size=512m --mount type=bind,source="C:\Users\testar\Desktop\TESTAR_dev",target=/mnt testar/testarstatemodel:latest