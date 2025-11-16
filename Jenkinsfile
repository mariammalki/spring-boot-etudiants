pipeline {
    agent any

    environment {
        IMAGE_NAME = "mariem507/spring-etudiants"
        TAG = "v1.1"
    }

    stages {

        stage('Checkout') {
            steps {
                echo "Récupération du code depuis GitHub via SSH"
                git branch: 'main',
                    credentialsId: 'github-ssh-rsa',
                    url: 'git@github.com:rouissinour464/app_act.git'
            }
        }

        stage('Docker Build') {
            steps {
                echo "Construction de l’image Docker"
                sh '''
                    docker build -t ${IMAGE_NAME}:${TAG} .
                    docker tag ${IMAGE_NAME}:${TAG} ${IMAGE_NAME}:latest
                '''
            }
        }

        stage('Docker Run') {
            steps {
                echo "Lancement du conteneur Docker"
                sh '''
                    docker rm -f etudiants || true

                    docker run -d --name etudiants \
                    -p 8081:8081 \
                    -e SERVER_PORT=8081 \
                    -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/gestion_etudiants \
                    -e SPRING_DATASOURCE_USERNAME=nour \
                    -e SPRING_DATASOURCE_PASSWORD=actpass \
                    ${IMAGE_NAME}:${TAG}
                '''
            }
        }

        stage('Docker Push') {
            steps {
                echo "Push de l’image Docker vers Docker Hub"

                withCredentials([usernamePassword(
                    credentialsId: 'docker-hub',
                    usernameVariable: 'USER',
                    passwordVariable: 'PASS'
                )]) {
                    sh '''
                        echo "$PASS" | docker login -u "$USER" --password-stdin
                        docker push ${IMAGE_NAME}:${TAG}
                        docker push ${IMAGE_NAME}:latest
                    '''
                }
            }
        }
    }
}
