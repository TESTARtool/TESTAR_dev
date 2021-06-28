call gradlew distTar

docker build -t aslomp/testarstatemodel:latest .

docker run -d --shm-size=512m --mount type=bind,source="C:\testardock\settings",target=/testar/bin/settings --mount type=bind,source="C:\Users\a.slomp\Source\Repos\TESTAR_dev",target=/mnt --mount type=bind,source="c:\testardock\output",target=/testar/bin/output aslomp/testarstatemodel:latest