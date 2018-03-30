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
def pull() {
sh """groovy 

    def Credles = "Yarm:Yarm"
    def Repository = "Pipeline"
    def nexus = "http://EPBYMINW2473.minsk.epam.com:8081"
    def Artifact_full_name = "ls -t1 ${WORKSPACE}/".execute().text.split()[0]
    def Parse = (Artifact_full_name.split("(?<=\\w)(?=[\\-\\.])|(?<=[\\-\\.])")).toList()
    Parse.removeAll('-')
    Parse.removeAll('.')
    def Group_Id = Parse[0]
    def Artifact_name = Parse[1]
    def Artifact = new File("${WORKSPACE}/${Artifact_full_name}").getBytes()
    def connection = new URL("${nexus}/repository/${Repository}/${Group_Id}/${Artifact_name}/${BUILD_NUMBER}/${Artifact_full_name}").openConnection()
    println(connection)
    connection.setRequestMethod("PUT")
    connection.doOutput = true
    connection.setRequestProperty("Authorization", "Basic ${Credles.getBytes().encodeBase64().toString()}")
    def writer = new DataOutputStream(connection.outputStream)
    writer.write(Artifact)
    writer.close()
    println connection.responseCode"""
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
    tool name: 'groovy4', type: 'hudson.plugins.groovy.GroovyInstallation'
    stage ('Preparation (Checking out)'){
        cleanWs()
        echo " Try git branch clone"
        git branch: 'ayarmalovich', url: 'https://github.com/MNT-Lab/mntlab-pipeline.git'
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
        withEnv(["JAVA_HOME=${ tool 'java8' }", "PATH+GRADLE=${tool 'gradle4.6'}/bin", "PATH+GROOVY_HOME=${tool 'groovy4'}/bin"]){
            pull()
        }
    }
    stage ('Asking for manual approval'){

    }
    stage ('Deployment'){

    }
}
