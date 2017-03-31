FROM ubuntu:16.04

RUN locale-gen en_US.UTF-8

ENV SIMPLECI_USER=simpleci \
    SIMPLECI_HOME="/home/simpleci" \
    SETUP_DIR="/var/cache/simpleci" \
    DOCKER_VERSION="1.12.5" \
    LANG="en_US.UTF-8" \
    LANGUAGE="en_US:en" \
    LC_ALL="en_US.UTF-8"

COPY install.sh ${SETUP_DIR}/
COPY php_versions_install.php ${SETUP_DIR}/
RUN bash ${SETUP_DIR}/install.sh

EXPOSE 22
CMD ["/usr/sbin/sshd", "-D"]