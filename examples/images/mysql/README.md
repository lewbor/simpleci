# Docker mysql tmpfs images

Repository contains docker images for mysql with /var/lib/mysql mounted as tmpfs.
You need to run this container with --privileged option to allow tmpfs memory allocation.
Example: docker run --privileged -e TMPFS_SIZE=2g -e MYSQL_ALLOW_EMPTY_PASSWORD=yes lewbor/mysql:5.6