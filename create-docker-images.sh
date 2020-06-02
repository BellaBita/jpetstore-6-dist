#!/bin/bash

if [ -z "${SERVICE_NAMES}" ]; then
	SERVICE_NAMES="account,catalog,frontend,order"
fi

IFS=',' read -ra SERVICES <<< "${SERVICE_NAMES}"

for service in "${SERVICES[@]}" ; do
	serviceName=${service}-service
	sudo docker build -t 10.81.208.53:5000/jpetstore-$serviceName $serviceName
	sudo docker push 10.81.208.53:5000/jpetstore-$serviceName
	#curl -X GET http://10.81.208.53:5000/v2/jpetstore-$serviceName/tags/list
	#curl -X GET https://registry-1.docker.io/v2/lenasupport/jpetstore-$serviceName/tags/list
done

# end
