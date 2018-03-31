def stage_list = ['Hello', 'Prep', 'Building', 'Testing', 'Starting child job', 'Approval', 'Deployment']


def notifySuccessful(String stage_failed) {
    emailext(
            subject: "SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
            body: """<p>SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
        <p>Check console output at "<a href="${env.BUILD_URL}">${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>"</p>""",
            recipientProviders: [[$class: 'DevelopersRecipientProvider']]
    )
}


def notifyFailed(String stage_failed) {
    emailext (
            subject: "FAILED Pipeline on stage: '${stage_failed} [${BUILD_NUMBER}]'",
            body: """<p>FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
       <p>Check console output at "<a href="${env.BUILD_URL}">${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>"</p>""",
            recipientProviders: [[$class: 'DevelopersRecipientProvider']]
    )
}


node("${SLAVE}") {
    try {
        stage(stage_list.get(0)) {
            step([$class: 'WsCleanup'])
            echo 'Hello World'
        }
    } catch (e) {
        notifyFailed(stage_list.get(0))
    }

    try {
        stage(stage_list.get(1)) {
            tool name: 'java8', type: 'jdk'
            tool name: 'gradle4.6', type: 'gradle'
            git branch: 'ifilimonau', url: 'https://github.com/MNT-Lab/mntlab-pipeline.git'
        }
    } catch (e) {
        notifyFailed(stage_list.get(1))
    }

    try {
        stage(stage_list.get(2)) {
            withEnv(["JAVA_HOME=${tool 'java8'}", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]) {
                sh "gradle build"
            }
        }
    } catch (e) {
        notifyFailed(stage_list.get(2))
    }

    try {
        stage(stage_list.get(3)) {
            withEnv(["JAVA_HOME=${tool 'java8'}", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]) {
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
    } catch (e) {
        notifyFailed(stage_list.get(3))
    }

    try {
        stage(stage_list.get(4)) {
            build job: 'MNTLAB-ifilimonau-child1-build-job', parameters: [[$class: 'StringParameterValue', name: 'BRANCH_NAME2', value: 'ifilimonau']]
            step([$class     : 'CopyArtifact',
                  projectName: 'MNTLAB-ifilimonau-child1-build-job',
                  filter     : 'jobs.groovy']);
            sh """cp build/libs/mntlab-ci-pipeline.jar gradle-simple.jar
    tar czvf pipeline-ifilimonau-\$BUILD_NUMBER.tar.gz jobs.groovy Jenkinsfile gradle-simple.jar
    groovy task_1.groovy push pipeline-ifilimonau-\$BUILD_NUMBER.tar.gz"""
        }
    } catch (e) {
        notifyStarted(stage_list.get(4))
    }

    try {
        stage(stage_list.get(5)) {
            input('Moving on?')
            sh "rm -f *tar.gz gradle-simple.jar"
        }
    } catch (e) {
        notifyStarted(stage_list.get(5))
    }

    try {
        stage(stage_list.get(6)) {
            sh """groovy task_1.groovy pull pipeline-ifilimonau-\$BUILD_NUMBER.tar.gz
    tar xzvf *tar.gz
    java -jar gradle-simple.jar"""
        }
    } catch (e) {
        stage(notifyStarted(stage_list.get(6)))
    } finally {
        notifySuccessful()
    }
}

