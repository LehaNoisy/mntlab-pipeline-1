import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*

def namestage = ""
def emailfailure (status, namestage){
    if(status=="FAILURE") {
        emailext(
                to: 'vospitanarbyzami@gmail.com',
                subject: "Jenkins Task11 - ${JOB_BASE_NAME}",
            body: """${currentBuild.fullDisplayName} 
            Stage Name - ${namestage} Result status - ${status}"""
        )
    }
}

def job_pattern = /EPBYMINW2473.*child*/
def NameJob(pattern) {
    def matchedJobs = Jenkins.instance.getAllItems(jenkins.model.ParameterizedJobMixIn.ParameterizedJob.class).findAll{
        job -> job =~ pattern
    }
    return matchedJobs[0].name
}
def tests = [:]
tests["Unit Tests"] = {
    echo "Start test"
    sh 'gradle test'
    echo "Test: Done"
}
tests["Jacoco Tests"] = {
    echo "Start Jacoco Test"
    sh 'gradle jacocoTestReport'
    echo "Jacoco Test: Done"
}
tests["Cucumber Tests"] = {
    echo "Start Cucumber Test"
    sh 'gradle cucumber'
    echo "Cucumber Test: Done"
}

node("${SLAVE}") {
    echo "Hello MNT-Lab"
    tool name: 'gradle4.6', type: 'gradle'
    tool name: 'java8', type: 'jdk'
    try {
        stage('Preparation (Checking out)') {
            cleanWs()
            namestage = "Preparation (Checking out)"
            //echo " Try git branch clone"
            //git branch: 'ayarmalovich', url: 'https://github.com/MNT-Lab/mntlab-pipeline.git'
            //echo "Branch Clone : Done"
            echo "Checkout scm"
            checkout scm666
        }
        currentBuild.result = "SUCCESS"
    }
    catch (all) {
        currentBuild.result = "FAILURE"
        emailfailure (currentBuild.result, namestage)
        throw any
    }
    try {
        stage('Building code') {
            echo "Start Build"
            namestage = "Testing code"
            withEnv(["JAVA_HOME=${tool 'java8'}", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]) {
                sh "gradle build"
            }
            echo "Build: Done"
        }
        currentBuild.result = "SUCCESS"
    }
    catch (all) {
        currentBuild.result = "FAILURE"
        emailfailure (currentBuild.result, namestage)
        throw any
    }
    try {
        stage('Testing code') {
            echo "Start Tests"
            namestage = "Testing code"
            withEnv(["JAVA_HOME=${tool 'java8'}", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]) {
                parallel(tests)
            }
            echo "Tests: Done"
        }
        currentBuild.result = "SUCCESS"
    }
    catch (all) {
        currentBuild.result = "FAILURE"
        emailfailure (currentBuild.result, namestage)
        throw any
    }
    try {
        stage('Triggering job and fetching artefact after finishing') {
            echo "Start Triggering job"
            namestage = "Triggering job and fetching artefact after finishing"
            echo "Find ${NameJob(job_pattern)} and Trigger it"
            build job: "${NameJob(job_pattern)}"
            step([
                    $class     : 'CopyArtifact',
                    filter     : '*',
                    projectName: "${NameJob(job_pattern)}"
            ])
            echo "Triggering job: Done"
            }
        currentBuild.result = "SUCCESS"
    }
    catch (all) {
        currentBuild.result = "FAILURE"
        emailfailure (currentBuild.result, namestage)
        throw any
    }
    try {
        stage ('Packaging and Publishing results'){
            echo "Start Packaging and Publishing"
            namestage = "Packaging and Publishing results"
            sh 'tar -xvf *.tar.gz'
            sh 'tar -czf pipeline-ayarmalovich-${BUILD_NUMBER}.tar.gz jobs.groovy Jenkinsfile -C build/libs/ ${JOB_BASE_NAME}.jar'
            archiveArtifacts 'pipeline-ayarmalovich-${BUILD_NUMBER}.tar.gz'
            sh 'groovy actions.groovy push pipeline-ayarmalovich-${BUILD_NUMBER}.tar.gz'
            sh 'rm -rf *tar.gz'
            echo "Packaging and Publishing: Done"
            currentBuild.result = "SUCCESS"
        }
    }
    catch (all) {
        currentBuild.result = "FAILURE"
        emailfailure (currentBuild.result, namestage)
        throw any
    }
    stage ('Asking for manual approval'){
        input message: 'Do you want to deploy an artifact?', ok: 'Start Deploy'
    }
    try {
        stage('Deployment') {
            echo "Start Deployment"
            namestage = "Deployment"
            sh 'groovy actions.groovy pull pipeline-ayarmalovich-${BUILD_NUMBER}.tar.gz'
            sh 'tar -xvf *tar.gz'
            sh 'java -jar ${JOB_BASE_NAME}.jar'
            echo "Deployment: Done"
            emailext(
                    to: 'vospitanarbyzami@gmail.com',
                    subject: "Jenkins Task11 - ${JOB_BASE_NAME}",
                    body: """WELL DONE, COMRADES!
            ${JOB_BASE_NAME} - Finished: SUCCESS
            BUILD_NUMBER: ${BUILD_NUMBER}
            We pulled the artifact from nexus!
            And deployed it!
            We deployed ${JOB_BASE_NAME}.jar"""
            )
        }
        currentBuild.result = "SUCCESS"
    }
    catch (all) {
        currentBuild.result = "FAILURE"
        emailfailure (currentBuild.result, namestage)
        throw any
    }
}
