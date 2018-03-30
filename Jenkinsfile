
node("${SLAVE}") {
    stage('Hello'){
        step([$class: 'WsCleanup'])
        echo 'Hello World'
    }

    stage('Prep'){
        tool name: 'java8', type: 'jdk'
        tool name: 'gradle4.6', type: 'gradle'
        git branch: 'ifilimonau', url: 'https://github.com/MNT-Lab/mntlab-pipeline.git'
    }

    stage('Building'){

        withEnv(["JAVA_HOME=${tool 'java8' }", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]){
            sh "gradle build"
        }

    }

    stage('Testing') {
        withEnv(["JAVA_HOME=${tool 'java8' }", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]){
            parallel(
                    cucumber: {
                        sh "gradle cucumber"
                    },
                    jacoco: {
                        sh "gradle jacocoTestReport"
                    },
                    unit: {
                        sh "gradle test"
                    }
            )
        }
    }
    stage ('Starting child job') {

        build job: 'MNTLAB-ifilimonau-child1-build-job', parameters: [[$class: 'StringParameterValue', name: 'BRANCH_NAME2', value: 'ifilimonau']]
        step ([$class: 'CopyArtifact',
               projectName: 'MNTLAB-ifilimonau-child1-build-job',
               filter: 'jobs.groovy']);
        sh """cp build/libs/mntlab-ci-pipeline.jar gradle-simple.jar
tar czvf pipeline-ifilimonau-\$BUILD_NUMBER.tar.gz jobs.groovy Jenkinsfile gradle-simple.jar
groovy task_1.groovy push pipeline-ifilimonau-\$BUILD_NUMBER.tar.gz"""
    }
    stage('Approval') {
        input('Moving on?')
    }
}

