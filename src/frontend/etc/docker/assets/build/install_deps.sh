#!/bin/bash
set -e

install_centrifugo() {
  local download_url=https://github.com/centrifugal/centrifugo/releases/download/v$1/centrifugo-$1-linux-amd64.zip

  curl -sSL "${download_url}" -o /tmp/centrifugo.zip
  unzip -jo /tmp/centrifugo.zip -d /tmp/
  mv /tmp/centrifugo /usr/bin/centrifugo
  rm -f /tmp/centrifugo.zip
  echo "centrifugo - nofile 65536" >> /etc/security/limits.d/centrifugo.nofiles.conf
}

install_composer() {
    php -r "copy('https://getcomposer.org/installer', 'composer-setup.php');"
    php composer-setup.php --install-dir=bin --filename=composer
    php -r "unlink('composer-setup.php');"
}

install_centrifugo "1.6.1"
install_composer

adduser --disabled-login --gecos ${APP_USER} ${APP_USER}
passwd -d ${APP_USER}
chown -R ${APP_USER}:${APP_USER} ${APP_DIR}






