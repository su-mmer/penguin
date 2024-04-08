def EXECUTE_PORT

pipeline {
  agent any
  environment {
    DEVBUCKET="${BUCKET}"
    FLAG="FAIL"
    // DATE=$(date "+%Y-%m-%d")
    SPRING_PORT=""
  }
  stages {
    stage('ssh to comm and execute war') {
      steps {
        sshagent(credentials: ['ubuntu']) {
          def EUNHO = sh(script: """
              echo 888
            """, returnStdout:true
          ).trim()
          echo "${EUNHO}"
        }
      }
    }

    
  }
}