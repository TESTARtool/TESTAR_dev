set +m
set +x

username=${ORIENTDB_USERNAME:-testar}
password=${ORIENTDB_PASSWORD:-testar}
database=${ORIENTDB_DATABASE:-testar}
export ORIENTDB_ROOT_PASSWORD=${password}
pwd
ls -alh
sh /orientdb/bin/server.sh &
pid=$!

sleep 10;
echo "ORIENTDB DATABASE INIT STARTED"
sh /orientdb/bin/console.sh "CREATE DATABASE remote:localhost/${database} root ${password} PLOCAL; CREATE USER ${username} IDENTIFIED BY ${password} ROLE admin"
wait $pid
