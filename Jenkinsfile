def FLAG

pipeline {
  agent any
  stages {
    stage('배포 시작') {
      parallel {
        stage ('Slack: 배포 승인 요청') {
          steps {
            script {
              def attachments = [
                [
                  title: '[${env.BRANCH_NAME}] Communicator: Jenkins 배포 시작 승인 요청',
                  text: 'URL 접속하여 승인 해주십시오.',
                  color: '#45aaf2',
                  fields: [
                    [
                      title: 'JENKINS_URL',
                      value: "${env.JENKINS_URL}blue/organizations/jenkins/penguin/detail/penguin/${env.BUILD_NUMBER}/pipeline"
                      short: false
                    ],
                    [
                      title: 'RUN_DISPLAY_URL',
                      value: "${env.RUN_DISPLAY_URL}"
                      short: false
                    ],
                    [
                      title: 'RUN_CHANGES_DISPLAY_URL',
                      value: "${env.RUN_CHANGES_DISPLAY_URL}"
                      short: false
                    ],
                    [
                      title: 'JOB_DISPLAY_URL',
                      value: "${env.JOB_DISPLAY_URL}"
                      short: false
                    ]
                  ],
                  footer: "Message from DEV"
                ]
              ]
              slackSend(channel: "#alarm-test", attachments: attachments)
            }
          }
        }
        stage('service 실행') {
          input {
            message "Approve Deploy"
            ok "Yes"
            parameters {
              string(name: 'Answer', defaultValue: 'Yes', description: 'If you want to Deploy, say Yes')
            }
          }
          steps {
            echo "This is Your Answer: ${Answer}"
            sshagent(credentials: ['ubuntu']) {
            script {
              FLAG = sh(script: '''
              ssh -o StrictHostKeyChecking=no -p ${PORT} ${TARGET_HOST} '
              ./1-tardownload.sh
              ./2-findport.sh
              '
              ''', returnStdout:true).trim()
              echo "FLAG: ${FLAG}"
            }
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
              slackSend (channel: '#alarm-test', color: 'good', message: "${FLAG} 포트에 대한 어플리케이션 실행에 성공했습니다. Load Balancer 트래픽 분배 승인을 요청합니다.\n${env.JENKINS_URL}blue/organizations/jenkins/penguin/detail/penguin/${env.BUILD_NUMBER}/pipeline")
            }
          } 
          stage ('LB 10% 전환'){
            input {
              message "Approve to Change traffic"
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
              slackSend (channel: '#alarm-test', color: 'good', message: "LB 트래픽이 안정적입니다. Load Balancer 트래픽 전환 승인을 요청합니다.\n${env.JENKINS_URL}blue/organizations/jenkins/penguin/detail/penguin/${env.BUILD_NUMBER}/pipeline")
            }
          }
          stage ('LB 100% 전환'){
            input {
              message "Approve to Change traffic"
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
                  ssh -o StrictHostKeyChecking=no -p ${PORT} ${TARGET_HOST} '
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
      slackSend (channel: '#alarm-test', color: 'good', message: "Jenkins 실행 완료\n${env.JENKINS_URL}blue/organizations/jenkins/penguin/detail/penguin/${env.BUILD_NUMBER}/pipeline")
    }
    failure {
      slackSend (channel: '#alarm-test', color: 'danger', message: "Jenkins 실패\n${env.JENKINS_URL}blue/organizations/jenkins/penguin/detail/penguin/${env.BUILD_NUMBER}/pipeline")
    }
  }
}
