import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*

def namestage = []
def type = "SUCCESS"
def user_t = ${BUILD_USER}
Date datestart = new Date() 
def emailfailure (namestage, type, datestart, user_t){
    Date date = new Date()
    Date datefail = new Date()
    def Log_of_node = currentBuild.rawBuild.getLog(20).join('\n')
    if (type == "FAILURE") {
    emailext(
            to: 'vospitanarbyzami@gmail.com',
            attachLog: true,
            subject: "Jenkins Task11 - ${JOB_BASE_NAME}",
            body: """${currentBuild.fullDisplayName} 
User ${user_t} triggered pipeline
Date start pipeline ---- ${datestart}
Date fail ---- ${datefail}
Stages  Name  -------------   Type  
${namestage.join(" --- SUCCESS \n")} --- ${type} \n
Log: ${Log_of_node}"""
    )
    }
    if (type == "SUCCESS") {
    emailext(
            to: 'vospitanarbyzami@gmail.com',
            subject: "Jenkins Task11 - ${JOB_BASE_NAME}",
            body: """WELL DONE, COMRADES!
User ${user_t} triggered pipeline
${JOB_BASE_NAME} - Finished: SUCCESS
BUILD_NUMBER: ${BUILD_NUMBER}
We pulled the artifact from nexus!
And deployed it!
We deployed ${JOB_BASE_NAME}.jar
Date start pipeline ---- ${datestart}
Date of deploy ---- ${date}
Stages  Name  -------------   Type
${namestage.join(" --- SUCCESS \n")} --- ${type} \n
Log: ${Log_of_node}"""
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
    try {
        echo "Hello MNT-Lab"
        tool name: 'gradle4.6', type: 'gradle'
        tool name: 'java8', type: 'jdk'
        stage('Preparation (Checking out)') {
            cleanWs()
            namestage.add('Preparation (Checking out)')
            //echo " Try git branch clone"
            //git branch: 'ayarmalovich', url: 'https://github.com/MNT-Lab/mntlab-pipeline.git'
            //echo "Branch Clone : Done"
            echo "Checkout scm"
            checkout scm
        }
        stage('Building code') {
            echo "Start Build"
            namestage.add('Building code')
            withEnv(["JAVA_HOME=${tool 'java8'}", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]) {
                sh "gradle build"
            }
            echo "Build: Done"
        }
        stage('Testing code') {
            echo "Start Tests"
            namestage.add('Testing code')
            withEnv(["JAVA_HOME=${tool 'java8'}", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]) {
                parallel(tests)
            }
            echo "Tests: Done"
        }
        stage('Triggering job and fetching artefact after finishing') {
            echo "Start Triggering job"
            namestage.add('Triggering job and fetching artefact after finishing')
            echo "Find ${NameJob(job_pattern)} and Trigger it"
            build job: "${NameJob(job_pattern)}"
            step([
                    $class     : 'CopyArtifact',
                    filter     : '*',
                    projectName: "${NameJob(job_pattern)}"
            ])
            echo "Triggering job: Done"
        }
        stage('Packaging and Publishing results') {
            echo "Start Packaging and Publishing"
            namestage.add('Packaging and Publishing results')
            sh 'tar -xvf *.tar.gz'
            sh 'tar -czf pipeline-ayarmalovich-${BUILD_NUMBER}.tar.gz jobs.groovy Jenkinsfile -C build/libs/ ${JOB_BASE_NAME}.jar'
            archiveArtifacts 'pipeline-ayarmalovich-${BUILD_NUMBER}.tar.gz'
            sh 'groovy actions.groovy push pipeline-ayarmalovich-${BUILD_NUMBER}.tar.gz'
            sh 'rm -rf *tar.gz'
            echo "Packaging and Publishing: Done"
        }
        stage('Asking for manual approval') {
            input message: 'Do you want to deploy an artifact?', ok: 'Start Deploy'
        }
        stage('Deployment') {
            echo "Start Deployment"
            namestage.add('Deployment')
            sh 'groovy actions.groovy pull pipeline-ayarmalovich-${BUILD_NUMBER}.tar.gz'
            sh 'tar -xvf *tar.gz'
            sh 'java -jar ${JOB_BASE_NAME}.jar'
            echo "Deployment: Done"
        }
    }
    catch (all) {
        type = "FAILURE"
        emailfailure(namestage,type, datestart)
        throw any
    }
    emailfailure(namestage,type, datestart) 
}
