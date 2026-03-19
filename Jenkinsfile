pipeline {
    agent any
    
    parameters {
        choice(name: 'SERVICE_TO_DEPLOY', choices: ['all', 'frontend', 'backend', 'gateway', 'auth-service', 'tenant-service', 'portfolio-service', 'transaction-service', 'audit-service'], description: 'Select the service to deploy')
    }

    environment {
        AWS_REGION = "ap-south-1"
        PROJECT_NAME = "investment-tracker"
        AWS_ACCOUNT_ID = credentials('AWS_ACCOUNT_ID')
        ECR_REGISTRY = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
        ALL_SERVICES = "auth-service tenant-service portfolio-service transaction-service audit-service gateway backend frontend"
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build and Push') {
            steps {
                script {
                    def servicesToProcess = []
                    if (params.SERVICE_TO_DEPLOY == 'all') {
                        servicesToProcess = env.ALL_SERVICES.split(' ')
                    } else {
                        servicesToProcess = [params.SERVICE_TO_DEPLOY]
                    }
                    
                    servicesToProcess.each { service ->
                        echo "Processing build for: ${service}"
                        dir(service) {
                            def repoName = "${PROJECT_NAME}-${service}"
                            def imageTag = "${ECR_REGISTRY}/${repoName}:latest"
                            
                            sh "aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}"
                            sh "docker build -t ${imageTag} ."
                            sh "docker push ${imageTag}"
                        }
                    }
                }
            }
        }
        
        stage('Deploy to ECS') {
            steps {
                script {
                    def servicesToProcess = []
                    if (params.SERVICE_TO_DEPLOY == 'all') {
                        servicesToProcess = env.ALL_SERVICES.split(' ')
                    } else {
                        servicesToProcess = [params.SERVICE_TO_DEPLOY]
                    }
                    
                    servicesToProcess.each { service ->
                        echo "Triggering deployment for: ${service}"
                        def clusterName = "${PROJECT_NAME}-${service}-cluster"
                        sh "aws ecs update-service --cluster ${clusterName} --service ${PROJECT_NAME}-${service} --force-new-deployment"
                    }
                }
            }
        }
    }
    
    post {
        always {
            sh "docker system prune -f"
        }
    }
}
