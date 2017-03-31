#!/bin/bash

set -e
source ${APP_RUNTIME_DIR}/functions.sh

case ${1} in
  app:start|app:migrate)
    install_configuration_templates
    initialize_logdir
    clear_cache

    case ${1} in
      app:start)
        check_mysql_connection
        check_redis_connection
        install_migrations
        rm -rf /var/run/supervisor.sock
        exec /usr/bin/supervisord -nc /etc/supervisor/supervisord.conf
        ;;
      app:migrate)
        check_mysql_connection
        install_migrations
        ;;
    esac
    ;;

  app:help)
    echo "Available options:"
    echo " app:start        - Starts the server (default)"
    echo " app:migrate      - Install migrations"
    echo " [command]        - Execute the specified command, eg. bash."
    ;;
  *)
    exec "$@"
    ;;
esac
