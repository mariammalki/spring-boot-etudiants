pipeline {
    agent any

    environment {
        DOCKERHUB = credentials('dockerhub-id')
        IMAGE_NAME = "mariam/spring-etudiants"
    }

    stages {

        stage('Checkout') {
            steps {
                git 'https://github.com/mariammalki/spring-boot-etudiants.git'
            }
        }

        stage('Build & Docker') {
            steps {
                sh 'mvn clean package -DskipTests'

                sh "docker build -t ${IMAGE_NAME}:v1.1 ."

                sh "echo ${DOCKERHUB_PSW} | docker login -u ${DOCKERHUB_USR} --password-stdin"

                sh "docker push ${IMAGE_NAME}:v1.1"
            }
        }

        stage('Tests Unitaires') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Security & Quality Scan') {
            steps {
                withSonarQubeEnv('SonarQubeServer') {
                    sh 'mvn sonar:sonar'
                }
                sh 'trivy fs --exit-code 1 .'
                sh "trivy image --exit-code 1 ${IMAGE_NAME}:v1.1"
            }
        }

        stage('Slack Notification') {
            steps {
                slackSend(channel: '#ci-cd', color: 'good', message: "Pipeline succeeded for build ${env.BUILD_NUMBER}")
            }
        }

        stage('Deploy via ArgoCD') {
            steps {
                sh 'argocd app sync spring-etudiants'
            }
        }
    }

    post {
        failure {
            slackSend(channel: '#ci-cd', color: 'danger', message: "Pipeline FAILED for build ${env.BUILD_NUMBER}")
        }
    }
}
