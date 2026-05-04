pipeline {
    agent any

    environment {
        DOCKER_REGISTRY = 'localhost:5000'
        KAFKA_BOOTSTRAP_SERVERS = 'kafka:9092'
    }

    tools {
        maven 'Maven 3.9'
        jdk 'JDK 21'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean verify -pl accounts-service,cash-service,transfer-service,notifications-service,front-app,auth-server,gateway-server --also-make'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                script {
                    def services = [
                        'auth-server',
                        'gateway-server',
                        'accounts-service',
                        'cash-service',
                        'transfer-service',
                        'notifications-service',
                        'front-app'
                    ]
                    for (svc in services) {
                        sh "docker build -t ${DOCKER_REGISTRY}/${svc}:${BUILD_NUMBER} ./${svc}"
                        sh "docker tag ${DOCKER_REGISTRY}/${svc}:${BUILD_NUMBER} ${DOCKER_REGISTRY}/${svc}:latest"
                        sh "docker push ${DOCKER_REGISTRY}/${svc}:${BUILD_NUMBER}"
                        sh "docker push ${DOCKER_REGISTRY}/${svc}:latest"
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                sh '''
                    kubectl create namespace bank --dry-run=client -o yaml | kubectl apply -f -
                    helm upgrade --install my-bank-app ./helm \
                        --namespace bank \
                        --set kafka.enabled=true \
                        --set global.imageTag=${BUILD_NUMBER} \
                        --wait --timeout 5m
                '''
            }
        }

        stage('Smoke Test') {
            steps {
                sh '''
                    kubectl wait --for=condition=ready pod \
                        -l app=gateway-server \
                        -n bank \
                        --timeout=120s
                    kubectl wait --for=condition=ready pod \
                        -l app=notifications-service \
                        -n bank \
                        --timeout=120s
                '''
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully'
        }
        failure {
            echo 'Pipeline failed'
        }
        always {
            cleanWs()
        }
    }
}