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
                  title: 'Jenkins 배포 시작',
                  text: 'URL 접속하여 승인 해주십시오.',
                  color: '#45aaf2',
                  fields: [
                    // [
                      title: 'URL',
                      value: "${env.JENKINS_URL}blue/organizations/jenkins/penguin/detail/penguin/${env.BUILD_NUMBER}/pipeline"//"${env.BUILD_URL}",
                      // short: false
                    // ]
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
            parameters { string(name: 'Answer', defaultValue: 'Yes', description: '배포를 시작하시겠습니까?') }
          }
          steps {
            echo "This is Your Answer: ${Answer}"
            sshagent(credentials: ['ubuntu']) {
              script {
                FLAG = sh(script: '''
                ssh -o StrictHostKeyChecking=no -p ${PORT} ${TARGET_HOST} '
                  ./1-tardownload.sh
                  ./2-findport.sh '
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
        expression { "${FLAG}"=="FAIL:8080" || "${FLAG}"=="FAIL:8081" }
      }
      steps {
        echo "${FLAG}"  // 개발자는 어느 포트에서 실패했는지 알 필요 없음, 무조건 최신 버전 실패니까
        slackSend (channel: '#alarm-test', color: 'danger', message: "서비스 실행 실패\n${env.JENKINS_URL}blue/organizations/jenkins/penguin/detail/penguin/${env.BUILD_NUMBER}/pipeline")
        sh "exit 1"// 젠킨스 종료
      }
    }

    stage('트래픽 10% 전환') {
      parallel {
        stage ('Slack: 10% 전환 요청') {
          steps {
            slackSend (channel: '#alarm-test', color: 'good', message: "새로운 어플리케이션 실행에 성공했습니다. Load Balancer 트래픽 10% 분배 승인을 요청합니다.\n${env.JENKINS_URL}blue/organizations/jenkins/penguin/detail/penguin/${env.BUILD_NUMBER}/pipeline")
          }
        } 
        stage ('LB 10% 전환'){
          input {
            message "Approve to Change traffic"
            ok "Yes"
            parameters { string(name: 'Answer', defaultValue: 'Yes', description: 'LoadBalancer 트래픽을 10% 전환하시겠습니까?') }
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
            slackSend (channel: '#alarm-test', color: 'good', message: "LB 트래픽이 안정적입니다. Load Balancer 트래픽 100% 전환 승인을 요청합니다.\n${env.JENKINS_URL}blue/organizations/jenkins/penguin/detail/penguin/${env.BUILD_NUMBER}/pipeline")
          }
        }
        stage ('LB 100% 전환'){
          input {
            message "Approve to Change traffic"
            ok "Yes"
            parameters { string(name: 'Answer', defaultValue: 'Yes', description: 'LoadBalancer 트래픽을 100% 전환하시겠습니까?') }
          }
          steps {
            echo "This is Your Answer: ${Answer}"
            sh """sh /home/ubuntu/LB/${FLAG}-2.sh"""
          }
        }
      }
    }

    stage ('이전 버전 서비스 중단') {
      parallel {
        stage ('Slack: 이전 버전 서비스 중단 요청') {
          steps {
            slackSend (channel: '#alarm-test', color: 'good', message: "서비스가 정상적으로 실행되었습니다. 이전 버전에 대한 서비스 중단을 요청합니다.\n${env.JENKINS_URL}blue/organizations/jenkins/penguin/detail/penguin/${env.BUILD_NUMBER}/pipeline")
          }
        }
        stage ('이전 버전 서비스 중단'){
          input {
            message "Approve to stop the old version"
            ok "Yes"
            parameters { string(name: 'Answer', defaultValue: 'Yes', description: '이전 버전을 삭제하시겠습니까?') }
          }
          steps {
            echo "This is Your Answer: ${Answer}"
            sshagent(credentials: ['ubuntu']) {
              script {
                sh '''
                ssh -o StrictHostKeyChecking=no -p ${PORT} ${TARGET_HOST} '
                ./0-kill.sh
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
