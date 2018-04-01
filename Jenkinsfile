import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*

def namestage = ['Preparation (Checking out)','Building code','Testing code','Triggering job and fetching artefact after finishing','Packaging and Publishing results','Deployment']
def stageresults = []
def Log_of_node = currentBuild.rawBuild.getLog(20).join('\n')
def emailfailure (stageresults, namestage){
    emailext(
            to: 'vospitanarbyzami@gmail.com',
            attachLog: true,
            subject: "Jenkins Task11 - ${JOB_BASE_NAME}",
            body: """${currentBuild.fullDisplayName} 
Stage Name: ${namestage.join('\n')} Result status: ${stageresults.join('\n')}
Log: ${Log_of_node}"""
    )
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
    sh 'er!gradle cucumber'
    echo "Cucumber Test: Done"
}

node("${SLAVE}") {
    try {
        echo "Hello MNT-Lab"
        tool name: 'gradle4.6', type: 'gradle'
        tool name: 'java8', type: 'jdk'
        stage('Preparation (Checking out)') {
            cleanWs()
            //echo " Try git branch clone"
            //git branch: 'ayarmalovich', url: 'https://github.com/MNT-Lab/mntlab-pipeline.git'
            //echo "Branch Clone : Done"
            echo "Checkout scm"
            checkout scm
            stageresults.add('SUCCESS')
        }
        stage('Building code') {
            echo "Start Build"
            withEnv(["JAVA_HOME=${tool 'java8'}", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]) {
                sh "gradle build"
            }
            echo "Build: Done"
            stageresults.add('SUCCESS')
        }
        stage('Testing code') {
            echo "Start Tests"
            withEnv(["JAVA_HOME=${tool 'java8'}", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]) {
                parallel(tests)
            }
            echo "Tests: Done"
            stageresults.add('SUCCESS')
        }
        stage('Triggering job and fetching artefact after finishing') {
            echo "Start Triggering job"
            echo "Find ${NameJob(job_pattern)} and Trigger it"
            build job: "${NameJob(job_pattern)}"
            step([
                    $class     : 'CopyArtifact',
                    filter     : '*',
                    projectName: "${NameJob(job_pattern)}"
            ])
            echo "Triggering job: Done"
            stageresults.add('SUCCESS')
        }
        stage('Packaging and Publishing results') {
            echo "Start Packaging and Publishing"
            sh 'tar -xvf *.tar.gz'
            sh 'tar -czf pipeline-ayarmalovich-${BUILD_NUMBER}.tar.gz jobs.groovy Jenkinsfile -C build/libs/ ${JOB_BASE_NAME}.jar'
            archiveArtifacts 'pipeline-ayarmalovich-${BUILD_NUMBER}.tar.gz'
            sh 'groovy actions.groovy push pipeline-ayarmalovich-${BUILD_NUMBER}.tar.gz'
            sh 'rm -rf *tar.gz'
            echo "Packaging and Publishing: Done"
            stageresults.add('SUCCESS')
        }
        stage('Asking for manual approval') {
            input message: 'Do you want to deploy an artifact?', ok: 'Start Deploy'
        }
        stage('Deployment') {
            echo "Start Deployment"
            sh 'groovy actions.groovy pull pipeline-ayarmalovich-${BUILD_NUMBER}.tar.gz'
            sh 'tar -xvf *tar.gz'
            sh 'java -jar ${JOB_BASE_NAME}.jar'
            echo "Deployment: Done"
            stageresults.add('SUCCESS')
        }
    }
    catch (all) {
        stageresults.add('FAILURE')
        emailfailure(stageresults, namestage)
    }
    emailext(
            to: 'vospitanarbyzami@gmail.com',
            subject: "Jenkins Task11 - ${JOB_BASE_NAME}",
            body: """WELL DONE, COMRADES!
${JOB_BASE_NAME} - Finished: SUCCESS
BUILD_NUMBER: ${BUILD_NUMBER}
We pulled the artifact from nexus!
And deployed it!
We deployed ${JOB_BASE_NAME}.jar
Stage Name: ${namestage.join('\n')} Result status: ${stageresults.join('\n')}
Log: ${Log_of_node}"""
    )
}
