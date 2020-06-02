def SERVICE_NAME='frontend'
def DOCKER_REGISTRY='http://10.81.208.53:5000/'
def IMG_REPO='10.81.208.53:5000/jpetstore-' + SERVICE_NAME + '-service'
def BASE_IMG='lenasupport/lena-cluster:1.3.1.0_3-centos7-jdk8-openjdk'
def customImage

node {
    stage('Ready') {
        sh "echo 'Ready'"
        sh label: 'PWD', script: 'pwd'
        sh label: 'Whoami', script: 'whoami'
    }

    stage('Checkout') {
        sh "echo 'Checkout'"
        git 'https://github.com/BellaBita/jpetstore-6-dist.git'
    }

    stage('Build App') {
        sh label: 'Build War', script: 'mvn clean package'
    }

    stage('Build image') {
        sh "echo 'Image Build'"
        sh label: 'Pull Image', script:"docker pull $BASE_IMG"
        sh "cp -f ./${SERVICE_NAME}-service/Dockerfile_lena ./${SERVICE_NAME}-service/Dockerfile "
        sh label: 'Dockerfile', script: "cat ./${SERVICE_NAME}-service/Dockerfile"
        //customImage = docker.build("${IMG_REPO}:${env.BUILD_NUMBER}", "./${SERVICE_NAME}-service")
        customImage = docker.build("${IMG_REPO}", "./${SERVICE_NAME}-service")
        
        sh label: 'Image List', script: "sudo docker images | grep ${IMG_REPO}"
    }
    
     stage('Test image') {
         customImage.inside {
            //sh 'curl -f http://localhost:8180/jpetstore/'
            sh 'echo "test"'
         }
     }
    
    stage ('Regist image') {
        //docker.withRegistry(DOCKER_REGISTRY, 'dockerhub-lenasupport') {
        docker.withRegistry(DOCKER_REGISTRY) {
            customImage.push()
            //customImage.push('latest')
        }
    }

    stage('Deploy') {
        sh "echo 'Deploy with kubectrl'"
        //sh "cp  -f ./kube-depoly-service.yaml.template ./${SERVICE_NAME}-service/kube-depoly-service.yaml"
        //sh "sed -i 's/%service%/${SERVICE_NAME}/g\' ./${SERVICE_NAME}-service/kube-depoly-service.yaml"
        sh label: 'Kube Manifest', script: "cat ./${SERVICE_NAME}-service/kube-depoly-service.yaml"
        sh label: 'Kube Apply', script: "kubectl apply -f ./${SERVICE_NAME}-service/kube-depoly-service.yaml"
        sh label: 'Kube Update', script: "kubectl rollout restart deployment/${SERVICE_NAME}-service"
        sh label: 'Kube State', script: "kubectl get deployments -o wide | grep ${SERVICE_NAME}-service || true"
    }
    
    
    stage('Clean images') {
        sh label: 'Delete unused images', script:  'sudo docker images | grep jpetstore-frontend-service | awk \'{print "sudo docker rmi "$3" --force"}\' | sh || true'
    }
}