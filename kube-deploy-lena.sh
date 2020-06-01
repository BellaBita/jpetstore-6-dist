#!/bin/bash

for service in account catalog frontend order ; do
	cp  -f ./kube-depoly-service.yaml.template ./${service}-service/
	cat ./${service}-service/kube-depoly-service.yaml.template | sed "s/%service%/$service/g" > ./${service}-service/kube-depoly-service.yaml
	cat ./${service}-service/kube-depoly-service.yaml
	
	echo "sudo kubectl apply -f ./${service}-service/kube-depoly-service.yaml"
	sudo kubectl apply -f ./${service}-service/kube-depoly-service.yaml
	
	echo "sudo kubectl rollout restart deployment/${service}-service"
	sudo kubectl rollout restart deployment/${service}-service
	
	sudo kubectl get deployment ${service}-service
	
done

# end

