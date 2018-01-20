#!/bin/bash
set -o xtrace

date=`date +"%Y%m%d%H%M%S"`

projectName="testing-1153"
zoneName="us-west1-a"
machineType="n1-standard-1"
instanceName="instance-${date}"
snapshotName="simpleci-${date}"

gcloud compute --project ${projectName} instances create ${instanceName} \
    --zone ${zoneName} \
    --machine-type ${machineType} \
    --subnet "default" \
    --image "ubuntu-1604-xenial-v20180112" \
    --image-project "ubuntu-os-cloud" \
    --boot-disk-size "10" \
    --boot-disk-type "pd-ssd" \
    --boot-disk-device-name "${instanceName}"

# Wait for instance up and running
sleep 60

gcloud compute --project ${projectName} copy-files install.sh ${instanceName}:~/install.sh \
    --zone ${zoneName}

gcloud compute --project ${projectName} ssh ${instanceName} \
    --zone ${zoneName} \
    --command 'cd ~/; chmod +x install.sh; sudo ./install.sh; rm install.sh'

gcloud compute --project ${projectName} instances stop ${instanceName} --zone ${zoneName}

gcloud compute --project ${projectName} disks snapshot ${instanceName} --snapshot-names ${snapshotName} --zone ${zoneName}

gcloud compute --project ${projectName} instances delete ${instanceName} --zone ${zoneName} --quiet


