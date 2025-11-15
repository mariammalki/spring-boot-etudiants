pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-id')
        SLACK_WEBHOOK = credentials('slack-webhook-id')
        IMAGE_NAME = "mariam/spring-etudiants"
    }

    stages {
        stage('Checkout') {
            steps { git 'https://github.com/mariammalki/spring-boot-etudiants.git' }
        }

        stage('Build & Docker') {
            steps {
                sh 'mvn clean package -DskipTests'
                sh "docker build -t spring-etudiants:v1.1 ."
                sh "docker login -u mariem507 -p maryem123"
                sh "docker push spring-etudiants:v1.1"
            }
        }

        stage('Tests Unitaires') {
            steps { sh 'mvn test' }
        }

        stage('Security & Quality Scan') {
            steps {
                // SonarQube
                withSonarQubeEnv('SonarQubeServer') {
                    sh 'mvn sonar:sonar'
                }
                // Trivy scan code & Docker image
                sh 'trivy fs --exit-code 1 .'
                sh "trivy image --exit-code 1 ${IMAGE_NAME}:v1.0"
            }
        }

        stage('Slack Notification') {
            steps {
                slackSend (channel: '#ci-cd', color: 'good', message: "Pipeline succeeded for build ${env.BUILD_NUMBER}", webhookUrl: "${SLACK_WEBHOOK}")
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
            slackSend(channel: '#ci-cd', color: 'danger', message: "Pipeline FAILED for build ${env.BUILD_NUMBER}", webhookUrl: "${SLACK_WEBHOOK}")
        }
    }
}
