#!/bin/bash
set -e
source ${APP_RUNTIME_DIR}/env_defaults.sh

## Replace placeholders with values
# $1: file with placeholders to replace
# $x: placeholders to replace
update_template() {
  local FILE=${1?missing argument}
  shift

  [[ ! -f ${FILE} ]] && return 1

  local VARIABLES=($@)
  local USR=$(stat -c %U ${FILE})
  local tmp_file=$(mktemp)
  cp -a "${FILE}" ${tmp_file}

  local variable
  for variable in ${VARIABLES[@]}; do
    # Keep the compatibilty: {{VAR}} => ${VAR}
    sed -ri "s/[{]{2}$variable[}]{2}/\${$variable}/g" ${tmp_file}
  done

  # Replace placeholders
  (
    export ${VARIABLES[@]}
    local IFS=":"; sudo -HEu ${APP_USER} envsubst "${VARIABLES[*]/#/$}" < ${tmp_file} > ${FILE}
  )
  rm -f ${tmp_file}
}

exec_as_app_user() {
  if [[ $(whoami) == ${APP_USER} ]]; then
    $@
  else
    sudo -HEu ${APP_USER} "$@"
  fi
}

check_mysql_connection() {
  prog="mysqladmin -h ${DATABASE_HOST} -P ${DATABASE_PORT} -u ${DATABASE_USER} ${DATABASE_PASSWORD:+-p$DATABASE_PASSWORD} status"
  timeout=60
  echo "Waiting for mysql: ${DATABASE_HOST}:${DATABASE_PORT} ${DATABASE_USER}:${DATABASE_PASSWORD}"
  while ! ${prog} >/dev/null 2>&1
  do
    timeout=$(expr $timeout - 1)
    if [[ $timeout -eq 0 ]]; then
      echo "Could not connect to database server. Aborting..."
      exit 1
    fi
    echo "mysql ."
    sleep 1
  done
  echo
}

check_redis_connection() {
  timeout=60
  echo "Waiting for redis: ${REDIS_HOST}:${REDIS_PORT} "
  while ! redis-cli -h ${REDIS_HOST} -p ${REDIS_PORT} ping >/dev/null 2>&1
  do
    timeout=$(expr $timeout - 1)
    if [[ $timeout -eq 0 ]]; then
      echo ""
      echo "Could not connect to redis server. Aborting..."
      exit 1
    fi
    echo "redis ."
    sleep 1
  done
  echo
}

install_configuration_templates() {
    echo "Setup configuration"
    cp ${APP_RUNTIME_DIR}/config/nginx.conf /etc/nginx/nginx.conf
    cp ${APP_RUNTIME_DIR}/config/centrifugo.json /etc/centrifugo.json
    cp ${APP_RUNTIME_DIR}/config/supervisord.conf /etc/supervisor/supervisord.conf
    cp ${APP_RUNTIME_DIR}/config/php-fpm.conf /etc/php/7.0/fpm/php-fpm.conf
    cp ${APP_RUNTIME_DIR}/config/app_parameters.yml ${APP_DIR}/app/config/parameters.yml

    update_template /etc/nginx/nginx.conf APP_USER LOG_DIR APP_DIR CENTRIFUGO_HOST CENTRIFUGO_PORT
    update_template /etc/php/7.0/fpm/php-fpm.conf APP_USER LOG_DIR
    update_template /etc/supervisor/supervisord.conf APP_USER LOG_DIR
    update_template /etc/centrifugo.json SECRET REDIS_HOST REDIS_PORT

    update_template ${APP_DIR}/app/config/parameters.yml \
        DATABASE_HOST \
        DATABASE_PORT \
        DATABASE_NAME \
        DATABASE_USER \
        DATABASE_PASSWORD \
        RABBITMQ_HOST \
        RABBITMQ_PORT \
        RABBITMQ_USER \
        RABBITMQ_PASSWORD \
        APP_HOST \
        APP_LOCALE \
        SECRET
}

initialize_logdir() {
  echo "Initializing log dirs"
  mkdir -m 0755 -p ${LOG_DIR}/supervisor
  chmod -R 0755 ${LOG_DIR}/supervisor
  chown -R root:root ${LOG_DIR}/supervisor

  mkdir -m 0755 -p ${LOG_DIR}/nginx
  chmod -R 0755 ${LOG_DIR}/nginx
  chown ${APP_USER}:${APP_USER} ${LOG_DIR}/nginx

  mkdir -m 0755 -p ${LOG_DIR}/php_fpm
  chmod -R 0755 ${LOG_DIR}/php_fpm
  chown ${APP_USER}:${APP_USER} ${LOG_DIR}/php_fpm

  mkdir -m 0755 -p ${LOG_DIR}/centrifugo
  chmod -R 0755 ${LOG_DIR}/centrifugo
  chown ${APP_USER}:${APP_USER} ${LOG_DIR}/centrifugo
}

install_migrations(){
  QUERY="SELECT count(*) FROM information_schema.tables WHERE table_schema = '${DATABASE_NAME}';"
  COUNT=$(mysql -h ${DATABASE_HOST} -P ${DATABASE_PORT} -u ${DATABASE_USER} ${DATABASE_PASSWORD:+-p$DATABASE_PASSWORD} -ss -e "${QUERY}")
  if [[ -z ${COUNT} || ${COUNT} -eq 0 ]]; then
    echo "Setting up simpleci for first run"
    echo "Install database migrations"
    exec_as_app_user ${APP_DIR}/bin/console doctrine:migrations:migrate --no-interaction --no-debug -e prod
    echo "Creating user"
    exec_as_app_user ${APP_DIR}/bin/console fos:user:create admin admin@localhost admin --super-admin -e prod
    return
  fi

   echo "Install database migrations"
   exec_as_app_user ${APP_DIR}/bin/console doctrine:migrations:migrate --no-interaction --no-debug -e prod
}

clear_cache(){
    exec_as_app_user ${APP_DIR}/bin/console cache:clear -e prod
}

