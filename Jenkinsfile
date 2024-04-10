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
    }
    
    stage('8081 port') {
      when {
        expression { "${FLAG}"=="SUCCESS:8081" }
      }
      stages {
        stage ('90:10 approve request to slack') {
          steps {
            slackSend (channel: '#alarm-test', color: 'good', message: "8081 포트에 대한 어플리케이션 실행에 성공했습니다. Load Balancer 트래픽 분배 승인을 요청합니다.\n${env.BUILD_URL}")
          }
        } 
        stage ('90:10 approve message'){
          input {
            message "Approve to Change traffic"
            ok "Yes"
            parameters {
              string(name: 'Answer', defaultValue: 'Yes', description: 'LoadBalancer 트래픽의 10%를 Backend2로 전환하시겠습니까?')
            }
          }
          steps {
            echo "This is Your Answer: ${Answer}"
          }
        }
        stage('90:10') {
          steps {
            script {
              sh (script: 'sh /home/ubuntu/LB/alb-90-10.sh')
              // sh (script: 'echo "90:10"')
              sleep 300
            }
          }
        }
        stage ('0:100 approve request to slack') {
          steps {
            slackSend (channel: '#alarm-test', color: 'good', message: "LB 트래픽이 안정적입니다. Load Balancer 트래픽 전환 승인을 요청합니다.\n${env.BUILD_URL}")
          }
        }
        stage('0:100') {
          steps {
            script {
              sh (script: 'sh /home/ubuntu/LB/alb-0-100.sh')
              // sh (script: 'echo "0:100"')
              sleep 300  // 300초 대기
            }
          }
        }
      }
    }

    stage('8080 port') {
      when {
        expression { "${FLAG}"=="SUCCESS:8080" }
      }
      stages {
        stage ('10:90 approve request to slack') {
          steps {
            slackSend (channel: '#alarm-test', color: 'good', message: "8080 포트에 대한 어플리케이션 실행에 성공했습니다. Load Balancer 트래픽 분배 승인을 요청합니다.\n${env.BUILD_URL}")
          }
        } 
        stage ('10:90 approve message'){
          input {
            message "Approve to Change traffic"
            ok "Yes"
            parameters {
              string(name: 'Answer', defaultValue: 'Yes', description: 'LoadBalancer 트래픽의 10%를 Backend1로 전환하시겠습니까?')
            }
          }
          steps {
            echo "This is Your Answer: ${Answer}"
          }
        }
        stage('10:90') {
          steps {
            script {
              // sh (script: 'echo "10:90"')
              sh (script: 'sh /home/ubuntu/LB/alb-10-90.sh')
              sleep 300
            }
          }
        }
        stage ('100:0 approve request to slack') {
          steps {
            slackSend (channel: '#alarm-test', color: 'good', message: "LB 트래픽이 안정적입니다. Load Balancer 트래픽 전환 승인을 요청합니다.\n${env.BUILD_URL}")
          }
        }
        stage('100:0') {
          steps {
            script {
              sh (script: 'sh /home/ubuntu/LB/alb-100-0.sh')
              // sh (script: 'echo "100:0"')
              sleep 300  // 300초(5분) 대기
            }
          }
        }
      }
    }
    
    // stage('LB 10:90 Message') {
    //   when {
    //     expression { "${FLAG}"=="SUCCESS:8081" }
    //     input {
    //       message "Approve Deploy"
    //       ok "Yes"
    //       parameters {
    //         string(name: 'Answer', defaultValue: 'Yes', description: 'If you want to Deploy, say Yes')
    //       }
    //     }
    //   }
    //   steps {
    //     echo "This is Your Answer: ${Answer}"
    //   }
    // }

    // stage('LB 10:90') {
    //   when {
    //     expression { "${FLAG}"=="SUCCESS:8081" }
    //   }
    //   steps {
    //     // sh (script: 'sh /home/ubuntu/LB/alb-90-10.sh')
    //     sh (script: 'echo "10:90"')
    //     sleep 300  // 300초 대기
    //   }
    // }
    // slack 0:10 실행합니다
    // stage('LB 0:100') {
    //   steps {
    //     // sh (script: 'sh /home/ubuntu/LB/alb-0-100.sh')
    //     sh (script: 'echo "0:100"')
    //   }
    // }
    
  }
}
