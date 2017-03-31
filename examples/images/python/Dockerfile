FROM ubuntu:16.04

ENV SIMPLECI_USER=simpleci \
    SIMPLECI_HOME="/home/simpleci" \
    SETUP_DIR="/var/cache/simpleci" \
    DOCKER_VERSION="1.12.5"

COPY install.sh ${SETUP_DIR}/
COPY python_install.sh ${SETUP_DIR}/
RUN bash ${SETUP_DIR}/install.sh

EXPOSE 22
CMD ["/usr/sbin/sshd", "-D"]