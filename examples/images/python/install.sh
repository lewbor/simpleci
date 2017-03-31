#!/bin/bash
set -o xtrace

export DEBIAN_FRONTEND=noninteractive
echo 'APT::Install-Recommends "false";' >> /etc/apt/apt.conf.d/30install-suggests
echo 'APT::Install-Suggests "false";' >> /etc/apt/apt.conf.d/30install-suggests

apt-get -qq update
apt-get -y -q install sudo curl netcat git git-core wget pv socat \
    hashdeep openssh-client openssh-server unzip apt-transport-https \
    software-properties-common build-essential \
    python  python-pip \
    libssl-dev zlib1g-dev libbz2-dev libreadline-dev libsqlite3-dev
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

# installing pythons
sudo -HEu ${SIMPLECI_USER} ${SETUP_DIR}/python_install.sh

