#!/bin/bash

if [ -z "${SERVICE_NAMES}" ]; then
	SERVICE_NAMES="account,catalog,frontend,order"
fi

IFS=',' read -ra SERVICES <<< "${SERVICE_NAMES}"

for service in "${SERVICES[@]}" ; do
	cp  -f ./kube-depoly-service.yaml.template ./${service}-service/
	cat ./${service}-service/kube-depoly-service.yaml.template | sed "s/%service%/$service/g" > ./${service}-service/kube-depoly-service.yaml
	cat ./${service}-service/kube-depoly-service.yaml
	
	echo ""
	echo "sudo kubectl delete -f ./${service}-service/kube-depoly-service.yaml"
	sudo kubectl delete -f ./${service}-service/kube-depoly-service.yaml
	
	echo ""
	echo "sudo kubectl apply -f ./${service}-service/kube-depoly-service.yaml"
	sudo kubectl apply -f ./${service}-service/kube-depoly-service.yaml
	
	echo "sudo kubectl rollout restart deployment/${service}-service"
	#sudo kubectl rollout restart deployment/${service}-service
	
	sudo kubectl get deployment ${service}-service
	
done

# end

