def FLAG="FAIL"
def PORT

pipeline {
  agent any
  stages {
    stage('Message: Confirm to Deploy') {
      parallel {
        stage ('Slack: Confirm to Deploy') {
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
                      value: "${env.JENKINS_URL}blue/organizations/jenkins/penguin/detail/penguin/${env.BUILD_NUMBER}/pipeline"//"${env.BUILD_URL}",
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
      }
    }

    stage('ssh to comm and execute war') {
      steps {
        sshagent(credentials: ['ubuntu']) {
          script {
            FLAG = sh(script: '''
            ssh -o StrictHostKeyChecking=no -p ${PORT} ${TARGET_HOST}  '
            ./1-tardownload.sh
            ./2-findport.sh
            '
            ''', returnStdout:true).trim()
            echo "FLAG: ${FLAG}"
          }
        }
      }
    }
    
    stage('8081 port') {
      when {
        expression { "${FLAG}"=="8081" }
      }

      stages {
        stage ('90:10 approve request to slack') {
          steps {
            slackSend (channel: '#alarm-test', color: 'good', message: "8081 포트에 대한 어플리케이션 실행에 성공했습니다. Load Balancer 트래픽 분배 승인을 요청합니다.\n${env.JENKINS_URL}blue/organizations/jenkins/penguin/detail/penguin/${env.BUILD_NUMBER}/pipeline")
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
              sleep 30  // TODO 웨이팅 시간 맞추기
            }
          }
        }
        stage ('0:100 approve request to slack') {
          steps {
            slackSend (channel: '#alarm-test', color: 'good', message: "LB 트래픽이 안정적입니다. Load Balancer 트래픽 전환 승인을 요청합니다.\n${env.JENKINS_URL}blue/organizations/jenkins/penguin/detail/penguin/${env.BUILD_NUMBER}/pipeline")
          }
        }
        stage ('0:100 approve message'){
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
        stage('0:100') {
          steps {
            script {
              sh (script: 'sh /home/ubuntu/LB/alb-0-100.sh')
              sleep 30  // TODO 웨이팅 시간 맞추기
            }
          }
        }
      }
    }

    stage('8080 port') {
      // when {
      //   expression { "${FLAG}"=="SUCCESS:8080" }
      // }
      stages {
        stage ('10:90 approve request to slack') {
          steps {
            slackSend (channel: '#alarm-test', color: 'good', message: "8080 포트에 대한 어플리케이션 실행에 성공했습니다. Load Balancer 트래픽 분배 승인을 요청합니다.\n${env.JENKINS_URL}blue/organizations/jenkins/penguin/detail/penguin/${env.BUILD_NUMBER}/pipeline")
          }
        } 
        stage ('10:90 approve message'){
          input {
            message "Approve to Change traffic"
            ok "Yes"
            parameters {
              string(name: 'Answer', defaultValue: 'Yes', description: 'LoadBalancer 트래픽을 Backend1로 100% 전환하시겠습니까?')
            }
          }
          steps {
            echo "This is Your Answer: ${Answer}"
          }
        }
        stage('10:90') {
          steps {
            script {
              sh (script: 'sh /home/ubuntu/LB/${FLAG}-1.sh')
            }
          }
        }
        stage ('100:0 approve request to slack') {
          steps {
            slackSend (channel: '#alarm-test', color: 'good', message: "LB 트래픽이 안정적입니다. Load Balancer 트래픽 전환 승인을 요청합니다.\n${env.JENKINS_URL}blue/organizations/jenkins/penguin/detail/penguin/${env.BUILD_NUMBER}/pipeline")
          }
        }
        stage ('100:0 approve message'){
          input {
            message "Approve to Change traffic"
            ok "Yes"
            parameters {
              string(name: 'Answer', defaultValue: 'Yes', description: 'LoadBalancer 트래픽을 Backend2로 100% 전환하시겠습니까?')
            }
          }
          steps {
            echo "This is Your Answer: ${Answer}"
          }
        }
        stage('100:0') {
          steps {
            script {
              sh (script: 'sh /home/ubuntu/LB/${flag}-2.sh')
            }
          }
        }
      }
    }
  }
  post {
    success {
      slackSend (channel: '#alarm-test', color: 'good', message: "Jenkins Success\n${env.JENKINS_URL}blue/organizations/jenkins/penguin/detail/penguin/${env.BUILD_NUMBER}/pipeline")
    }
    failure {
      slackSend (channel: '#alarm-test', color: 'danger', message: "Jenkins FAILED\n${env.JENKINS_URL}blue/organizations/jenkins/penguin/detail/penguin/${env.BUILD_NUMBER}/pipeline")
    }
  }
}
