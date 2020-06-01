#!/bin/bash

for service in account catalog frontend order ; do
	cp  -f ./kube-depoly-service.yaml.template ./${service}/
	cat ./${service}/kube-depoly-service.yaml.template | sed "s/%service%/$service/g" > ./${service}/kube-depoly-service.yaml
	sudo kubectl apply -f ./${service}/kube-depoly-service.yaml
	sudo kubectl rollout restart deployment/${service}
done

# end

