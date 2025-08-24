FROM postgres:17beta2-bookworm

ENV PG_MAJOR=17
ENV SNOWFLAKE_NODE=1

USER root

RUN apt-get update && apt-get install -y --no-install-recommends \
    ca-certificates \
    build-essential \
    git \
    "postgresql-server-dev-${PG_MAJOR}" \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /usr/src
RUN git clone https://github.com/pgEdge/snowflake.git \
    && cd snowflake \
    && make USE_PGXS=1 \
    && make USE_PGXS=1 install \
    && rm -rf /usr/src/snowflake

COPY init-db.sh /docker-entrypoint-initdb.d/
RUN chmod +x /docker-entrypoint-initdb.d/init-db.sh

USER postgres