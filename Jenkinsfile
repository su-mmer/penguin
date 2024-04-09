def EUNHO

pipeline {
  agent any
  environment {
    DEVBUCKET="${BUCKET}"
    FLAG="FAIL"
  }
  stages {
    stage('Confirm to Deploy') {
      steps {
        script {
          def attachments = [
            [
              fallback: 'Request Fail',
              title: 'This is Title',
              title_link: "https://github.com/${{ github.repository }}/actions/runs/${{ github.run_id }}",
              author_name: 'cloit',
              text: 'I am Groot!',
              color: 'good',
              fields: [
                {
                  "title": "Priority",
                  "value": "High",
                  "short": false
                }
              ],
              footer: "Slack API"
          ]
          slackSend(channel: "#alarm-test", attachments: attachments)
        }
      }
    }

    stage('ssh to comm and execute war') {
      steps {
        sshagent(credentials: ['ubuntu']) {
          script {
            EUNHO = sh(script: '''
            ssh -o StrictHostKeyChecking=no -p ${PORT} ${TARGET_HOST}  '
            gcloud storage cp gs://ew1-dvs-dev-storage/communicator-$(date "+%Y-%m-%d").tar.gz /appl/communicator-$(date "+%Y-%m-%d").tar.gz
            tar -zxvf /appl/communicator-$(date "+%Y-%m-%d").tar.gz -C /appl/ > dev/null
            mv /appl/penguin-0.0.1-SNAPSHOT.war /appl/communicator-$(date "+%Y-%m-%d").war
            ./findport.sh > port.txt
            cat port.txt
            '
            ''', returnStdout:true).trim()
            echo "EUNHO: ${EUNHO}"
          }
        }
      }
    }
    // stage('http Request') {
    //   steps {
    //     script{
    //       def RESPONSE_CODE = httpRequest "http://${target}:${EUNHO}"
    //       FLAG="${RESPONSE_CODE.status}"
    //       echo "${FLAG}"
    //     }
    //   }
    // }

    // stage('application success') {
    //   when {
    //     expression { "${FLAG}"=="200" }
    //   }
    //   steps {
    //     script {
    //       echo "success"
    //     }
    //   }
    // }
    
  }
}