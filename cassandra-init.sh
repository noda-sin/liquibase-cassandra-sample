query="create keyspace if not exists test with replication = {'class': 'SimpleStrategy', 'replication_factor': 1};"

until echo $query | cqlsh
do
	now=$(date +%T)
	echo "[$now INIT CQLSH]: Node still unavailable, will retry another time"
	sleep 2
done &

exec /usr/local/bin/docker-entrypoint.sh "$@"
