import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*

def job_pattern = /EPBYMINW2473.*child*/
def tests = [:]
def NameJob(pattern) {
    def matchedJobs = Jenkins.instance.getAllItems(jenkins.model.ParameterizedJobMixIn.ParameterizedJob.class).findAll{
        job -> job =~ pattern
    }
    return matchedJobs[0].name
}

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
        echo " Try git branch clone"
        //git branch: 'ayarmalovich', url: 'https://github.com/MNT-Lab/mntlab-pipeline.git'
        checkout scm
        echo "Branch Clone : Done"
    }
    stage ('Building code') {
        echo "Try Build"

        withEnv(["JAVA_HOME=${ tool 'java8' }", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]){
            sh "gradle build"
        }
        echo "End Build"
    }
    stage ('Testing code'){
        echo "Try Build"
        withEnv(["JAVA_HOME=${tool 'java8'}", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]) {
            parallel(tests)
        }
        echo "End Test"
    }
    stage ('Triggering job and fetching artefact after finishing'){
        echo "Find ${NameJob(job_pattern)} and Trigger it"
        build job: "${NameJob(job_pattern)}"
        step([
                $class: 'CopyArtifact',
                filter: '*',
                projectName: "${NameJob(job_pattern)}"
        ])
    }
    stage ('Packaging and Publishing results'){
        sh 'tar xvf *.tar.gz'
        sh 'tar -czf pipeline-ayarmalovich-${BUILD_NUMBER}.tar.gz jobs.groovy Jenkinsfile -C build/libs/ ${JOB_BASE_NAME}.jar'
        archiveArtifacts 'pipeline-ayarmalovich-${BUILD_NUMBER}.tar.gz'
        sh 'groovy actions.groovy push pipeline-ayarmalovich-${BUILD_NUMBER}.tar.gz'
    }
    stage ('Asking for manual approval'){
        timeout(time: 60, unit: 'SECONDS')
        input message: 'Do you want to deploy an artifact?', ok: 'Start Deploy'
    }
    stage ('Deployment'){
        sh 'groovy actions.groovy pull pipeline-ayarmalovich-${BUILD_NUMBER}.tar.gz'
        sh 'tar xzf *tar.gz'
        sh 'java -jar ${JOB_BASE_NAME}.jar'
    }
}
