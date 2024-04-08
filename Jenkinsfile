pipeline {
  agent any
  environment {
    DEVBUCKET="${BUCKET}"
    FLAG="FAIL"
    SPRING_PORT="8080"
  }
  stages {
    stage('test') {
      steps {
        echo 'Success Execute Jenkins'
      }
    }

    stage('ssh to comm and execute war') {
      steps {
        sshagent(credentials: ['ubuntu']) {
          sh '''
            ssh -o StrictHostKeyChecking=no -p ${PORT} ${TARGET_HOST}  "
              gcloud storage cp gs://${DEVBUCKET}/communicator-$(date "+%Y-%m-%d").tar.gz /appl/communicator-$(date "+%Y-%m-%d").tar.gz
              tar -zxvf /appl/communicator-$(date "+%Y-%m-%d").tar.gz -C /appl/
              mv /appl/penguin-0.0.1-SNAPSHOT.war /appl/communicator-$(date "+%Y-%m-%d").war
              ./findport.sh
              ${SPRING_PORT}=$(cat port.txt)
              "
          '''
        }

        script{
          def RESPONSE_CODE = httpRequest "http://${target}:8080"
          FLAG="${RESPONSE_CODE.status}"
          echo "${FLAG}"
        }
      }
    }

  }
}