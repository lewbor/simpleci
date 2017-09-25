#!/bin/bash
set -o xtrace

export DEBIAN_FRONTEND=noninteractive
echo 'APT::Install-Recommends "false";' >> /etc/apt/apt.conf.d/30install-suggests
echo 'APT::Install-Suggests "false";' >> /etc/apt/apt.conf.d/30install-suggests

apt-get -y -q install sudo curl netcat git git-core wget pv socat \
    hashdeep openssh-client openssh-server unzip apt-transport-https \
    libbz2-dev libmcrypt-dev libtidy-dev libxml2-dev libjpeg-dev libmemcached-dev \
    libicu-dev libreadline-dev libxslt1-dev libpq-dev libldap2-dev libfreetype6-dev \
    libcurl3 libcurl3-gnutls libcurl4-openssl-dev libssl-dev libfontconfig1-dev \
    software-properties-common build-essential \
    default-jdk \
    php7.0-common php7.0-cli php7.0-dev php7.0-curl php-pear \
    re2c bison chrpath mysql-client \
    xvfb x11-utils xfonts-100dpi xfonts-75dpi xfonts-scalable xfonts-cyrillic \
    chromium-browser ffmpeg tmux

# Install nodejs
curl -sL https://deb.nodesource.com/setup_8.x | bash -
apt-get install -y nodejs

# Install yarn
curl -sS https://dl.yarnpkg.com/debian/pubkey.gpg | apt-key add -
echo "deb https://dl.yarnpkg.com/debian/ stable main" | tee /etc/apt/sources.list.d/yarn.list
apt-get -qq update
apt-get install yarn

rm -rf /var/lib/apt/lists/*

# Install docker client for building docker images
curl -O https://get.docker.com/builds/Linux/x86_64/docker-${DOCKER_VERSION}.tgz
tar -zxvf docker-${DOCKER_VERSION}.tgz
mv docker/docker /usr/bin/docker
rm -r docker

adduser --disabled-password --gecos '' simpleci
adduser simpleci sudo

# Allow simpleci user using sudo without password
sed 's|%sudo.*|%sudo ALL=(ALL) NOPASSWD:ALL|g' -i /etc/sudoers
# Do not reset env variables in sudo to keep build env (e.g. DEBIAN_FRONTEND or build settings)
sed 's|Defaults\s*env_reset|Defaults\t\!env_reset|' -i /etc/sudoers

# set password to allow ssh login
echo 'simpleci:simpleci' | chpasswd
mkdir /var/run/sshd
sed 's@session\s*required\s*pam_loginuid.so@session optional pam_loginuid.so@g' -i /etc/pam.d/sshd

# Mysql client settings
cat > /etc/mysql/my.cnf <<EOF
[client]
protocol=tcp
port=3306

EOF

# Install phpbrew
curl -L -O https://github.com/phpbrew/phpbrew/raw/master/phpbrew
chmod +x phpbrew
mv phpbrew /usr/bin/phpbrew
echo 'source /home/simpleci/.phpbrew/bashrc' >> /etc/profile.d/phpbrew.sh
sudo -HEu ${SIMPLECI_USER} phpbrew init

# install php versions
php ${SETUP_DIR}/php_versions_install.php | sudo -HEu ${SIMPLECI_USER} bash -

# Install composer
curl -sS https://getcomposer.org/installer | php -- --install-dir=/usr/local/bin --filename=composer
chmod +x /usr/local/bin/composer




