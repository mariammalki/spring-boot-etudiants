pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-id')
        SLACK_WEBHOOK = credentials('slack-webhook-id')  // stocké dans Jenkins Credentials
        IMAGE_NAME = "mariem507/spring-etudiants"
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

                sh """
                    echo ${DOCKERHUB_CREDENTIALS_PSW} | docker login -u ${DOCKERHUB_CREDENTIALS_USR} --password-stdin
                """

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

                // Analyse SonarQube
                withSonarQubeEnv('SonarQubeServer') {
                    sh 'mvn sonar:sonar'
                }

                // Trivy Scan code source
                sh 'trivy fs --exit-code 1 .'

                // Trivy Scan image Docker
                sh "trivy image --exit-code 1 ${IMAGE_NAME}:v1.1"
            }
        }

        stage('Slack Notification') {
            steps {
                slackSend(
                    baseUrl: "${SLACK_WEBHOOK}",
                    message: "Pipeline SUCCESS ✔ - Build #${env.BUILD_NUMBER}",
                    color: "good"
                )
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
            slackSend(
                baseUrl: "${SLACK_WEBHOOK}",
                message: "❌ Pipeline FAILED - Build #${env.BUILD_NUMBER}",
                color: "danger"
            )
        }
    }
}
