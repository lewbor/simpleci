# SimpleCI backend

SimpleCI backend part. Components:
* Dispatcher - communicate between database and worker, creates build jobs, store build log
* Worker - generate build script, execute build, downloading and uploading cache
* Shared - common part for dispatcher and worker. Contains communication protocol classes, utils, etc.
