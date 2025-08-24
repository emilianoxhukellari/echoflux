set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE EXTENSION IF NOT EXISTS snowflake;
    ALTER SYSTEM SET snowflake.node = ${SNOWFLAKE_NODE};
EOSQL

echo "✅ Snowflake extension created and node ID set to ${SNOWFLAKE_NODE}"