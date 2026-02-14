#!/bin/bash
mongodump "--authenticationDatabase=${MONGO_AUTH_DB}" -u "${MONGO_USERNAME}" -p "${MONGO_PASSWORD}" -d bahnbilder -h "${MONGO_HOSTS}" --ssl
mongorestore --drop
rm -r dump
