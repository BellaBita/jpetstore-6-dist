#!/bin/bash

for service in account-service catalog-service frontend-service order-service ; do
	sudo docker build -t lenasupport/jpetstore-$service $service
	sudo docker push lenasupport/jpetstore-$service
	#curl -X GET https://registry-1.docker.io/v2/lenasupport/jpetstore-$service/tags/list
done

# end
