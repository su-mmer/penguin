pipeline {
  agent any
  environment {
    DEVBUCKET="${BUCKET}"
  }
  stages {
    stage('test') {
      steps {
        echo 'Success Execute Jenkins'
      }
    }

    stage('ssh to comm') {
      steps {
        sshagent(credentials: ['ubuntu']) {
          sh '''
            ssh -o StrictHostKeyChecking=no -p ${PORT} ${TARGET_HOST}  "
              gcloud storage cp gs://${DEVBUCKET}/communicator-$(date "+%Y-%m-%d").tar.gz /appl/communicator-$(date "+%Y-%m-%d").tar.gz
              tar -zxvf /appl/communicator-$(date "+%Y-%m-%d").tar.gz -C /appl/communicator-$(date "+%Y-%m-%d").war
              "
          '''
        }
        
      }
    }

    // stage('') {
    //   steps {
    //     script {
    //       sh 
    //     }
    //   }
    // }
  }
}