#!/bin/bash
set -o xtrace

DOCKER_VERSION="17.06.0~ce-0~ubuntu"

apt-get update
apt-get -y upgrade
apt-get install -y linux-image-extra-$(uname -r) linux-image-extra-virtual apt-transport-https ca-certificates curl software-properties-common

curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
apt-get update
apt-get install -y docker-ce=${DOCKER_VERSION}
service docker start
systemctl enable docker

sudo docker pull simpleci/worker
sudo docker pull lewbor/php:latest
sudo docker pull lewbor/mysql:5.6
sudo docker pull memcached
