#!/usr/bin/env bash
set -e

date=`date +"%Y%m%d%H%M%S"`

docker build -t lewbor/php:${date} .

docker tag lewbor/php:${date} lewbor/php:latest
docker push lewbor/php:${date}
docker push lewbor/php:latest