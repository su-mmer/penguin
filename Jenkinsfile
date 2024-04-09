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
          blocks = [
          [
            "type": "section",
            "text": [
              "type": "mrkdwn",
              "text": "Hello, Assistant to the Regional Manager Dwight! *Michael Scott* wants to know where you'd like to take the Paper Company investors to dinner tonight.\n\n *Please select a restaurant:*"
            ]
          ],
            [
            "type": "divider"
          ],
          [
            "type": "section",
            "text": [
              "type": "mrkdwn",
              "text": "*Farmhouse Thai Cuisine*\n:star::star::star::star: 1528 reviews\n They do have some vegan options, like the roti and curry, plus they have a ton of salad stuff and noodles can be ordered without meat!! They have something for everyone here"
            ],
            "accessory": [
              "type": "image",
              "image_url": "https://s3-media3.fl.yelpcdn.com/bphoto/c7ed05m9lC2EmA3Aruue7A/o.jpg",
              "alt_text": "alt text for image"
            ]
          ]
        ]

          slackSend(channel: "#alarm-test", blocks: blocks)
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