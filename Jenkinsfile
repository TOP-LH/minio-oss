def TARGET_PATH = '/www/server/wison-oss'
def SOURCE_PATH = './target'
def SHELL_NAME = 'wison-oss.sh'
def RELEASE_REMOTE_HOST = 'root@10.99.10.154'
def TEST_REMOTE_HOST = 'root@10.99.10.122'

pipeline {
    agent any

    tools {
        maven 'maven-3.6.3'
    }

    stages {
        stage('getGitlabBranchName') {
            steps {
                echo "current branch is: ${env.gitlabBranch}"
            }
        }

        stage('build-test') {
            when {
                environment name: 'gitlabBranch', value: 'test'
            }
            steps {
                withEnv(['JENKINS_NODE_COOKIE=dontkillme']) {
                    configFileProvider([configFile(fileId: 'maven-global-settings', variable: 'MAVEN_GLOBAL_ENV')]) {
                        sh "mvnd -s $MAVEN_GLOBAL_ENV clean package -U -Ptest -DskipTests"
                        sh "scp $SOURCE_PATH/*.jar $TEST_REMOTE_HOST:$TARGET_PATH"
                        sh "scp ./$SHELL_NAME $TEST_REMOTE_HOST:$TARGET_PATH"
                        sh "ssh $TEST_REMOTE_HOST \"sh $TARGET_PATH/$SHELL_NAME\" restart"
                    }
                }
            }
        }

        stage('build-release') {
            when {
                environment name: 'gitlabBranch', value: 'release'
            }
            steps {
                withEnv(['JENKINS_NODE_COOKIE=dontkillme']) {
                    configFileProvider([configFile(fileId: 'maven-global-settings', variable: 'MAVEN_GLOBAL_ENV')]) {
                        sh "mvnd -s $MAVEN_GLOBAL_ENV clean package -U -Pprod -DskipTests"
                        sh "scp $SOURCE_PATH/*.jar $RELEASE_REMOTE_HOST:$TARGET_PATH"
                        sh "scp ./$SHELL_NAME $RELEASE_REMOTE_HOST:$TARGET_PATH"
                        sh "ssh $RELEASE_REMOTE_HOST \"sh $TARGET_PATH/$SHELL_NAME\" restart"
                    }
                }
            }
        }
    }
}