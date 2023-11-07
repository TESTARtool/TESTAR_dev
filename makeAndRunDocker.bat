call gradlew distTar

docker build -t testar-local/testar:latest .

docker run -d --shm-size=512m --name testar --mount type=bind,source="C:\testardock\settings",target=/testar/bin/settings --mount type=bind,source="C:\Users\username\TESTAR_dev",target=/mnt --mount type=bind,source="c:\testardock\output",target=/testar/bin/output testar-local/testar:latest