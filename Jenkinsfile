pipeline {
    agent any

    environment {
        // Credentials ID exact depuis Jenkins
        DOCKERHUB_CREDENTIALS = credentials('docker-hub-credentials')
        SLACK_CREDENTIALS = credentials('slack-webhook-id')  // Slack Webhook ID Jenkins
        IMAGE_NAME = "mariem507/spring-etudiants"
        IMAGE_TAG = "v1.1"
    }

    stages {

        stage('Checkout') {
            steps { 
                echo "Récupération du code depuis GitHub..."
                git 'https://github.com/mariammalki/spring-boot-etudiants.git'
            }
        }

        stage('Build & Docker') {
            steps {
                echo "Compilation Maven et création du JAR..."
                sh 'mvn clean package -DskipTests'

                echo "Construction de l\'image Docker..."
                sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."

                echo "Login Docker Hub et push..."
                withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh "docker login -u $DOCKER_USER -p $DOCKER_PASS"
                }

                sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
            }
        }

        stage('Tests Unitaires') {
            steps {
                echo "Exécution des tests unitaires..."
                sh 'mvn test'
            }
        }

        stage('Security & Quality Scan') {
            steps {
                echo "Analyse qualité et sécurité..."

                // SonarQube
                withSonarQubeEnv('SonarQubeServer') {
                    sh 'mvn sonar:sonar'
                }

                // Trivy scan code
                sh 'trivy fs --exit-code 1 .'

                // Trivy scan Docker image
                sh "trivy image --exit-code 1 ${IMAGE_NAME}:${IMAGE_TAG}"
            }
        }

        stage('Deploy via ArgoCD') {
            steps {
                echo "Déploiement via ArgoCD..."
                sh 'argocd app sync spring-etudiants'
            }
        }

        stage('Slack Notification Success') {
            when {
                expression { currentBuild.currentResult == 'SUCCESS' }
            }
            steps {
                slackSend(channel: '#ci-cd', color: 'good', message: "Pipeline succeeded for build ${env.BUILD_NUMBER}", tokenCredentialId: 'slack-webhook-id')
            }
        }
    }

    post {
        failure {
            slackSend(channel: '#ci-cd', color: 'danger', message: "Pipeline FAILED for build ${env.BUILD_NUMBER}", tokenCredentialId: 'slack-webhook-id')
        }
    }
}
