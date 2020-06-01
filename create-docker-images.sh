#!/bin/bash

for service in account-service catalog-service frontend-service order-service ; do
	docker build -t lenasupport/jpetstore-$service $service
	docker push lenasupport/jpetstore-$service
	#curl -X GET https://registry-1.docker.io/v2/lenasupport/jpetstore-$service/tags/list
done

# end
