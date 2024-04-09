// def PORT
def FLAG="FAIL"

pipeline {
  agent any
  // environment {
  //   FLAG="FAIL"
  // }
  stages {
    stage('Slack: Confirm to Deploy') {
      steps {
        script {
          def attachments = [
            [
              title: 'Jenkins 배포 시작 승인 요청',
              text: 'URL 접속하여 승인 해주십시오.',
              color: '#45aaf2',
              fields: [
                [
                  title: 'URL',
                  value: "${env.BUILD_URL}",  // URL 변경 필요
                  // short: false
                ]
              ],
              footer: "Message from DEV"
            ]
          ]
          slackSend(channel: "#alarm-test", attachments: attachments)
        }
      }
    }

    stage('Jenkins Approve Message') {
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
            echo "FLAG: ${FLAG}"
          }
        }
      }
      post {
        success {
          slackSend (channel: '#alarm-test', color: 'good', message: "어플리케이션 실행에 성공했습니다. Load Balancer 트래픽 승인을 요청합니다.\n${env.BUILD_URL}")
        }
        failure {
          slackSend (channel: '#alarm-test', color: 'danger', message: "Jenkins Job FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'\n ${env.BUILD_URL}")
        }
      }
    }
    

    // slack 1:9 실행합니다
    stage('LB 10:90') {
      when {
        expression { "${FLAG}"=="SUCCESS:8081" }
      }
      steps {
        // sh (script: 'sh /home/ubuntu/LB/alb-90-10.sh')
        sh (script: 'echo "10:90"')
        sleep 300  // 300초 대기
      }
    }
    // slack 0:10 실행합니다
    stage('LB 0:100') {
      steps {
        // sh (script: 'sh /home/ubuntu/LB/alb-0-100.sh')
        sh (script: 'echo "0:100"')
      }
    }
    
  }
}