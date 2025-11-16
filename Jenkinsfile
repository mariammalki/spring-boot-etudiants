pipeline {
    agent any

    environment {
        IMAGE_NAME = "mariam507/spring-etudiants"
        TAG = "v1.1"
    }

    stages {

        stage('Checkout') {
            steps {
                echo "Récupération du code depuis TON dépôt GitHub via SSH"
                git branch: 'main',
                    credentialsId: 'github-ssh-rsa',
                    url: 'git@github.com:mariammalki/spring-boot-etudiants.git'
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
                echo "Lancement du conteneur Docker (local)"
                sh '''
                    docker rm -f etudiants || true
                    docker run -d -p 8081:8080 --network etu-net \
                      -e SPRING_DATASOURCE_URL=jdbc:postgresql://pg-etudiants:5432/etudiantsdb \
                      -e SPRING_DATASOURCE_USERNAME=myuser \
                      -e SPRING_DATASOURCE_PASSWORD=pass123 \
                      ${IMAGE_NAME}:${TAG}
                '''
            }
        }

        stage('Docker Push') {
            steps {
                echo "Push de l’image Docker vers Docker Hub"
                withCredentials([usernamePassword(
                    credentialsId: 'docker-hub-credentials',
                    usernameVariable: 'USER',
                    passwordVariable: 'PASS'
                )]) {
                    sh '''
                        echo "$PASS" | docker login -u "$USER" --password-stdin
                        docker push ${IMAGE_NAME}:${TAG}
                        docker push ${IMAGE_NAME}:latest
                        docker logout
                    '''
                }
            }
        }
    }

    post {
        always {
            echo "Nettoyage du conteneur local"
            sh 'docker rm -f etudiants || true'
        }
        failure {
            echo "Le pipeline a échoué !"
        }
        success {
            echo "Pipeline terminé avec succès."
        }
    }
}
