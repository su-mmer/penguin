pipeline {
  agent any

  stages {
    stage('test') {
      steps {
        echo 'Success Execute Jenkins'
      }
    }

    stage('ssh to comm') {
      steps {
        sshagent(['ubuntu']) {
          sh '''
          ssh -o StrictHostKeyChecking=no ${user}@${target} -p {port} '
              gcloud storage cp gs://communicator-$(date "+%Y-%m-%d").tar.gz /appl/communicator-$(date "+%Y-%m-%d").tar.gz
              '
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