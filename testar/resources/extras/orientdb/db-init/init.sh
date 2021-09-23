set +m
set +x

username=${ORIENTDB_USERNAME:-testar} # UNUSED
password=${ORIENTDB_PASSWORD:-testar}
database=${ORIENTDB_DATABASE:-testar}
export ORIENTDB_ROOT_PASSWORD=${password}
pwd
ls -alh
sh /orientdb/bin/server.sh &
pid=$!

sleep 10;
sh /orientdb/bin/console.sh CREATE DATABASE remote:localhost/${database} root ${password} PLOCAL
wait $pid