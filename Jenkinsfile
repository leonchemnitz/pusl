pipeline {
    agent any
    tools {
        maven 'Maven 3.6.2'
        jdk 'jdk11'
    }
    stages {
        stage ('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                '''
            }
        }

        stage ('Build') {
            steps {
                sh 'mvn install' 
            }
            post {
                success {
                    junit 'target/surefire-reports/**/*.xml' 
                }
            }
        }

        stage ('Production Build') {
            steps {
                sh 'mvn clean package -Pproduction'
            }
        }

        stage ('Deploy') {
            steps {
                sh 'cp target/*.war /home/tomcat/apache-tomcat-9.0.29/webapps/ROOT.war'
            }
        }
    }
}