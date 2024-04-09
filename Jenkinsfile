def PORT

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
              author_name: 'GROOT',
              text: 'I am Groot!',
              color: 'good',
              fields: [
                [
                  title: 'Hello',
                  value: 'High',
                  short: false
                ]
              ],
              footer: "Message from Jenkins"
            ]
          ]
          slackSend(channel: "#alarm-test", attachments: attachments)
        }
      }
    }

    stage('Alert Message') {
      input {
          message "Approve Deploy"
          ok "Yes"
          parameters {
              string(name: 'Choose', defaultValue: 'Yes', description: 'You can say "Yes" or "No"')
          }
      }
      steps {
        echo "This is Your ${Choose}"
      }
    }

    stage('ssh to comm and execute war') {
      steps {
        sshagent(credentials: ['ubuntu']) {
          script {
            PORT = sh(script: '''
            ssh -o StrictHostKeyChecking=no -p ${PORT} ${TARGET_HOST}  '
            gcloud storage cp gs://ew1-dvs-dev-storage/communicator-$(date "+%Y-%m-%d")8080.tar.gz /appl/communicator-$(date "+%Y-%m-%d")8080.tar.gz
            tar -zxvf /appl/communicator-$(date "+%Y-%m-%d")8080.tar.gz -C /appl/ > dev/null
            mv /appl/penguin-0.0.1-SNAPSHOT.war /appl/communicator-$(date "+%Y-%m-%d")8080.war
            ./findport.sh > port.txt
            cat port.txt
            '
            ''', returnStdout:true).trim()
            // echo "PORT: ${PORT}"
          }
        }
      }
    }

    stage('http Request') {
      steps {
        script{
          def RESPONSE_CODE = httpRequest "http://${target}:80"
          FLAG="${RESPONSE_CODE.status}"
          echo "${FLAG}"
        }
      }
    }

    stage('application success') {
      when {
        expression { "${FLAG}"=="200" }
      }
      steps {
        script {
          echo "success"
        }
      }
    }
    
  }
}