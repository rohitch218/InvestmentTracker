pipeline {
    agent any
    
    parameters {
        choice(name: 'SERVICE_TO_DEPLOY', 
        choices: ['all', 'frontend', 'backend', 'gateway', 'auth-service', 'tenant-service', 'portfolio-service', 'transaction-service', 'audit-service'], 
        description: 'Select the service to deploy')
    }

    environment {
        AWS_REGION = "ap-south-1"
        PROJECT_NAME = "investment-tracker"
        AWS_ACCOUNT_ID = "924236436665"
        ECR_REGISTRY = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
        ALL_SERVICES = "auth-service tenant-service portfolio-service transaction-service audit-service gateway backend frontend"
    }
    
    stages {
        
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Login to ECR') {
            steps {
                sh """
                aws ecr get-login-password --region ${AWS_REGION} | \
                docker login --username AWS --password-stdin ${ECR_REGISTRY}
                """
            }
        }
        
        stage('Build and Push') {
            steps {
                script {
                    def servicesToProcess = params.SERVICE_TO_DEPLOY == 'all' ? 
                        env.ALL_SERVICES.split(' ') : 
                        [params.SERVICE_TO_DEPLOY]
                    
                    servicesToProcess.each { service ->
                        
                        def servicePath = service
                        
                        dir(servicePath) {
                            
                            def repoName = "${PROJECT_NAME}-${service}"
                            def imageTag = "${ECR_REGISTRY}/${repoName}:latest"
                            
                            echo "Building ${service}..."
                            
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
                    def servicesToProcess = params.SERVICE_TO_DEPLOY == 'all' ? 
                        env.ALL_SERVICES.split(' ') : 
                        [params.SERVICE_TO_DEPLOY]
                    
                    servicesToProcess.each { service ->
                        
                        def clusterName = "${PROJECT_NAME}-cluster"
                        
                        sh """
                        aws ecs update-service \
                        --cluster ${clusterName} \
                        --service ${PROJECT_NAME}-${service} \
                        --force-new-deployment
                        """
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
