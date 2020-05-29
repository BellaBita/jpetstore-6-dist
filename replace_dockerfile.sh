#!/bin/bash

cp -f ./docker-compose-$1.yml ./docker-compose.yml
 
for service in account-service catalog-service frontend-service order-service ; do
	cp -f ./${service}/Dockerfile_$1 ./${service}/Dockerfile
done

# end

