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
            ssh -o StrictHostKeyChecking=no -p ${PORT} ${TARGET_HOST}  "
            gcloud storage cp gs://${DEVBUCKET}/communicator-$(date "+%Y-%m-%d").tar.gz /appl/communicator-$(date "+%Y-%m-%d").tar.gz
            tar -zxvf /appl/communicator-$(date "+%Y-%m-%d").tar.gz -C /appl/
            mv /appl/penguin-0.0.1-SNAPSHOT.war /appl/communicator-$(date "+%Y-%m-%d").war
            ${SPRING_PORT}=cat port.txt
            echo ${SPRING_PORT}
            "
            """, returnStdout:true
          ).trim()
        }
      }
    }

    stage('get http request') {
      steps {
        script{
          // def RESPONSE_CODE = httpRequest "http://${TARGET}:8080"
          // FLAG="${RESPONSE_CODE.status}"
          // echo "${FLAG}"  // 200이면 8081로 실행
          echo ${SPRING_PORT}
        }
      }
    }
  }
}