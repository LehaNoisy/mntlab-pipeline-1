import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*

def job_pattern = /EPBYMINW2473.*child*/
def NameJob(pattern) {
    def matchedJobs = Jenkins.instance.getAllItems(jenkins.model.ParameterizedJobMixIn.ParameterizedJob.class).findAll{
        job -> job =~ pattern
    }
    return matchedJobs[0].name
}

def tests = [:]
tests["Unit Tests"] = {
    sh 'gradle test'
}
tests["Jacoco Tests"] = {
    sh 'gradle jacocoTestReport'
}
tests["Cucumber Tests"] = {
    sh 'gradle cucumber'
}

node("${SLAVE}") {
    echo "Hello MNT-Lab"
    tool name: 'gradle4.6', type: 'gradle'
    tool name: 'java8', type: 'jdk'
    stage ('Preparation (Checking out)'){
        cleanWs()
        //echo " Try git branch clone"
        //git branch: 'ayarmalovich', url: 'https://github.com/MNT-Lab/mntlab-pipeline.git'
        //echo "Branch Clone : Done"
        echo "Checkout scm"
        checkout scm
    }
    stage ('Building code') {
        echo "Start Build"
        withEnv(["JAVA_HOME=${ tool 'java8' }", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]){
            sh "gradle build"
        }
        echo "Build: Done"
    }
    stage ('Testing code'){
        echo "Start Tests"
        withEnv(["JAVA_HOME=${tool 'java8'}", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]) {
            parallel(tests)
        }
        echo "Tests: Done"
    }
    stage ('Triggering job and fetching artefact after finishing'){
        echo "Start Triggering job"
        echo "Find ${NameJob(job_pattern)} and Trigger it"
        build job: "${NameJob(job_pattern)}"
        step([
                $class: 'CopyArtifact',
                filter: '*',
                projectName: "${NameJob(job_pattern)}"
        ])
        echo "Triggering job: Done"
    }
    stage ('Packaging and Publishing results'){
        echo "Start Packaging and Publishing"
        sh 'tar -xvf *.tar.gz'
        sh 'tar -czf pipeline-ayarmalovich-${BUILD_NUMBER}.tar.gz jobs.groovy Jenkinsfile -C build/libs/ ${JOB_BASE_NAME}.jar'
        archiveArtifacts 'pipeline-ayarmalovich-${BUILD_NUMBER}.tar.gz'
        sh 'groovy actions.groovy push pipeline-ayarmalovich-${BUILD_NUMBER}.tar.gz'
        sh 'rm -rf *tar.gz'
        echo "Packaging and Publishing: Done"
    }
    stage ('Asking for manual approval'){
        timeout(time: 60, unit: 'SECONDS'){
            input message: 'Do you want to deploy an artifact?', ok: 'Start Deploy'
        }
    }
    stage ('Deployment'){
        echo "Start Deployment"
        sh 'groovy actions.groovy pull pipeline-ayarmalovich-${BUILD_NUMBER}.tar.gz'
        sh 'tar -xvf *tar.gz'
        sh 'java -jar ${JOB_BASE_NAME}.jar'
        echo "Deployment: Done"
    }
}
