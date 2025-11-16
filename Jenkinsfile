pipeline {
    agent any

    environment {
        IMAGE_NAME = "mariem507/spring-etudiants"
        TAG = "v1.1"
        CONTAINER_NAME = "etudiants"
        LOCAL_PORT = "8082"
        CONTAINER_PORT = "8080"
    }

    stages {

        stage('Checkout') {
            steps {
                echo "Récupération du code depuis GitHub"
                git branch: 'main',
                    url: 'https://github.com/mariammalki/spring-boot-etudiants.git',
                    credentialsId: 'github-https-creds' // utiliser HTTPS si SSH ne marche pas
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
                echo "Lancement du conteneur Docker local"
                script {
                    // Si le conteneur existe déjà, on le supprime
                    def exists = sh(script: "docker ps -a --filter name=${CONTAINER_NAME} --format '{{.Names}}'", returnStdout: true).trim()
                    if (exists == "${CONTAINER_NAME}") {
                        sh "docker rm -f ${CONTAINER_NAME}"
                    }

                    // Lancement avec port dynamique pour éviter conflits
                    sh """
                        docker run -d \
                        -p ${LOCAL_PORT}:${CONTAINER_PORT} \
                        --name ${CONTAINER_NAME} \
                        -e SPRING_DATASOURCE_URL=jdbc:postgresql://pg-etudiants:5432/etudiantsdb \
                        -e SPRING_DATASOURCE_USERNAME=myuser \
                        -e SPRING_DATASOURCE_PASSWORD=pass123 \
                        ${IMAGE_NAME}:${TAG}
                    """
                }
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

                        for i in 1 2 3; do
                            docker push ${IMAGE_NAME}:${TAG} && break || echo "Retry $i..." && sleep 10
                        done

                        for i in 1 2 3; do
                            docker push ${IMAGE_NAME}:latest && break || echo "Retry $i..." && sleep 10
                        done

                        docker logout
                    '''
                }
            }
        }
    }

    post {
        always {
            echo "Nettoyage du conteneur local"
            sh 'docker rm -f ${CONTAINER_NAME} || true'
        }
        failure {
            echo "Le pipeline a échoué !"
        }
        success {
            echo "Pipeline terminé avec succès."
        }
    }
}
