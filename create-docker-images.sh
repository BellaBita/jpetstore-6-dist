#!/bin/bash

for service in account-service catalog-service frontend-service order-service ; do
	docker build -t 10.81.208.53:5000/jpetstore-$service $service
	docker push 10.81.208.53:5000/jpetstore-$service
	curl -X GET http://localhost:5000/v2/hello-world/tags/list
done

# end
