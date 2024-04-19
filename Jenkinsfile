def FLAG

pipeline {
  agent any
  parameters {
    string(name : 'TARGET_HOST', defaultValue : '0', description : '배포할 서버 ID@IP')
    string(name : 'PORT', defaultValue : '0', description : '배포할 서버 접속 PORT')
    }
  stages {
    stage('배포 시작') {
      parallel {
        stage ('Slack: 배포 승인 요청') {
          steps {
              slackSend (channel: '#alarm-test', color: 'good', failOnError: true, message: "[${env.BRANCH_NAME}] Communicator: Jenkins 배포 시작 승인 요청\n${env.RUN_DISPLAY_URL}")
          }
        }
        stage('service 실행') {
          input {
            message "배포 시작 승인"
            ok "예"
            parameters {
              string(name: 'Answer', defaultValue: '예', description: '새 서비스가 실행됩니다.')
            }
          }
          steps {
            echo "This is Your Answer: ${Answer}"
            sshagent(credentials: ['ubuntu']) {
              // script {
                FLAG = sh(script: '''
                ssh -o StrictHostKeyChecking=no -p ${params.PORT} ${params.TARGET_HOST} "
                echo "Hello~"
                "
                ''', returnStdout:true).trim()
                // echo "FLAG: ${FLAG}"
              // }
            }
          }
        }
      }
    }
    
    stage('어플리케이션 실행 실패') {
      when {
        expression { "${FLAG}"=="FAIL:8080" || "${FLAG}"=="FAIL:8081" || "${FLAG}"=="FAIL"}
        // 셋 다 되나 확인하기
        
        // anyOf { "${FLAG}" 'FAIL:8080'; "${FLAG}" 'FAIL:8081' }
      }
      steps {
        echo "Can I?"
      }
    }

    stage('트래픽 10% 전환') {
        parallel {
          stage ('Slack: 10% 전환 요청') {
            steps {
              slackSend (channel: '#alarm-test', color: 'good', failOnError: true, message: "${FLAG} 포트에 대한 어플리케이션 실행에 성공했습니다. Load Balancer 트래픽 분배 승인을 요청합니다.\n${env.RUN_DISPLAY_URL}")
            }
          } 
          stage ('LB 10% 전환'){
            input {
              message "트래픽 10% 전환 승인"
              ok "Yes"
              parameters {
                string(name: 'Answer', defaultValue: 'Yes', description: 'LoadBalancer 트래픽을 ${FLAG}로 10% 전환하시겠습니까?')
              }
            }
            steps {
              echo "This is Your Answer: ${Answer}"
              sh """sh /home/ubuntu/LB/${FLAG}-1.sh"""
            }
          }
        }
      }

      stage ('트래픽 100% 전환'){
        parallel {
          stage ('Slack: 100% 전환 요청') {
            steps {
              slackSend (channel: '#alarm-test', color: 'good', failOnError: true, message: "LB 트래픽이 안정적입니다. Load Balancer 트래픽 전환 승인을 요청합니다.\n${env.RUN_DISPLAY_URL}")
            }
          }
          stage ('LB 100% 전환'){
            input {
              message "트래픽 100% 전환 승인"
              ok "Yes"
              parameters {
                string(name: 'Answer', defaultValue: 'Yes', description: 'LoadBalancer 트래픽을 ${FLAG}로 100% 전환하시겠습니까?')
              }
            }
            steps {
              echo "This is Your Answer: ${Answer}"
              sh """sh /home/ubuntu/LB/${FLAG}-2.sh"""
              sshagent(credentials: ['ubuntu']) {
                script {
                  sh '''
                  ssh -o StrictHostKeyChecking=no -p ${params.PORT} ${params.TARGET_HOST} '
                  ./3-kill.sh
                  '
                  '''
                }
              }
            }
          }
        }
      }
    }
  post {
    success {
      slackSend (channel: '#alarm-test', color: 'good', message: "Jenkins 실행 완료\n${env.RUN_DISPLAY_URL}")
    }
    failure {
      slackSend (channel: '#alarm-test', color: 'danger', message: "Jenkins 실패\n${env.RUN_DISPLAY_URL}")
    }
  }
}
