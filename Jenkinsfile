// def PORT
def FLAG

pipeline {
  agent any
  // environment {
  //   FLAG="FAIL"
  // }
  stages {
    stage('Confirm to Deploy') {
      steps {
        script {
          def attachments = [
            [
              title: 'Jenkins 배포 승인 요청',
              text: 'URL에 접속 후 배포 승인',
              color: '#45aaf2',
              fields: [
                [
                  title: 'Commit',
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
              string(name: 'Answer', defaultValue: 'Yes', description: 'If you want to Deploy, say Yes')
          }
      }
      steps {
        echo "This is Your Answer: ${Answer}"
      }
    }

    stage('ssh to comm and execute war') {
      steps {
        sshagent(credentials: ['ubuntu']) {
          script {
            FLAG = sh(script: '''
            ssh -o StrictHostKeyChecking=no -p ${PORT} ${TARGET_HOST}  '
            gcloud storage cp gs://ew1-dvs-dev-storage/communicator-$(date "+%Y-%m-%d")8081.tar.gz /appl/communicator-$(date "+%Y-%m-%d")8081.tar.gz
            tar -zxvf /appl/communicator-$(date "+%Y-%m-%d")8081.tar.gz -C /appl/ > /dev/null 2>&1
            mv /appl/penguin-0.0.1-SNAPSHOT.war /appl/communicator-$(date "+%Y-%m-%d")8081.war
            ./findport.sh
            '
            ''', returnStdout:true).trim()
            // echo "PORT: ${PORT}"
          }
        }
      }
    }

    stage('application success') {
      // when {
      //   expression { "${FLAG}"=="200" }
      // }
      steps {
      //   script {
      //     sh /home/ubuntu/LB/war1-100.sh

      //   }
        sh (script: 'sh /home/ubuntu/LB/war1-100.sh')
      }
      // sh '/home/ubuntu/LB/war1-100.sh'
    }
    
  }
}