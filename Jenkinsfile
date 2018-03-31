def stage_list = ['Hello', 'Prep', 'Building', 'Testing', 'Starting child job', 'Approval', 'Deployment']

def notifySuccessful() {
    emailext(
            attachLog: true,
            subject: "SUCCESSFUL: Job '${JOB_BASE_NAME} [${BUILD_NUMBER}]'",
            body: """SUCCESSFUL: Job '${JOB_BASE_NAME} [${BUILD_NUMBER}]':
        Check console output in the file attached""",
            to: "igarok.fil9@gmail.com"
    )
}


def notifyFailed(String stage_failed) {
    emailext (
            attachLog: true,
            subject: "FAILED Job ${JOB_BASE_NAME} [${BUILD_NUMBER}] on stage: ${stage_failed}",
            body: """FAILED: Job '${JOB_BASE_NAME} [${BUILD_NUMBER}]':
       Details on the following link: ${JOB_URL} """,
            to: "igarok.fil9@gmail.com"
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
        throw any
    }

    try {
        stage(stage_list.get(1)) {
            tool name: 'java8', type: 'jdk'
            tool name: 'gradle4.6', type: 'gradle'
            git branch: 'ifilimonau', url: 'https://github.com/MNT-Lab/mntlab-pipeline.git'
        }
    } catch (e) {
        notifyFailed(stage_list.get(1))
        throw any
    }

    try {
        stage(stage_list.get(2)) {
            withEnv(["JAVA_HOME=${tool 'java8'}", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]) {
                sh "gradle build"
            }
        }
    } catch (e) {
        notifyFailed(stage_list.get(2))
        throw any
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
        throw any
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
        notifyFailed(stage_list.get(4))
        throw any
    }

    try {
        stage(stage_list.get(5)) {
            input('Moving on?')
            sh "rm -f *tar.gz gradle-simple.jar"
        }
    } catch (e) {
        notifyFailed(stage_list.get(5))
        throw any
    }

    try {
        stage(stage_list.get(6)) {
            sh """groovy task_1.groovy pull pipeline-ifilimonau-\$BUILD_NUMBER.tar.gz
    tar xzvf *tar.gz
    java -jar gradle-simple.jar"""
        }
    } catch (e) {
        notifyFailed(stage_list.get(6))
        throw any
    } finally {
        notifySuccessful()
    }
}

