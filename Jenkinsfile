pipeline {
  agent any
  environment {
    DEVBUCKET="${BUCKET}"
    FLAG="FAIL"
    // DATE=$(date "+%Y-%m-%d")
    SPRING_PORT=""
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
              ${env.SPRING_PORT}=$(./findport.sh)
              "
          '''
        }

        
      }
    }

  }
}