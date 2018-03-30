import hudson.FilePath
import jenkins.model.Jenkins
import hudson.model.*
import hudson.AbortException
import hudson.console.HyperlinkNote
import java.util.concurrent.CancellationException
import groovy.json.JsonSlurper

def thr = Thread.currentThread()
def build = thr?.executable

def BUILD_NUMBER = build.environment.get("BUILD_NUMBER")
def WORKSPACE = build.environment.get("WORKSPACE")
//def thread = Thread.currentThread()
//def build = thread.executable
/*
def thread = Thread.currentThread()
def build = thread.executable
// Get build parameters
def buildVariablesMap = build.buildVariables 
// Get all environment variables for the build
def buildEnvVarsMap = build.envVars
String jobName = buildEnvVarsMap?.JOB_NAME
if(build.workspace.isRemote()){channel = build.workspace.channel}
        String fp = build.workspace.toString()
        println fp*/

node("${SLAVE}") {
    //git branch: 'achernak', url: 'https://github.com/MNT-Lab/mntlab-pipeline.git'
    stage('Git Checkout'){checkout scm}
    //checkout([$class: 'GitSCM', branches: [[name: '*/achernak']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/mntlab-pipeline']]])}
    
    stage ('Build') {
        notifyStarted()
        tool name: 'gradle4.6', type: 'gradle'
        tool name: 'java8', type: 'jdk'
        withEnv(["JAVA_HOME=${ tool 'java8' }", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]){
        sh 'gradle build'}}
    stage('Test') {
    withEnv(["JAVA_HOME=${ tool 'java8' }", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]){
        parallel (
                'Unit Tests': {sh 'gradle test' },
                'Jacoco Tests': {sh 'gradle jacocoTestReport' },
                'Cucumber Tests': {sh 'gradle cucumber' }, )}}

    stage('Trigger job') {build job: 'MNTLAB-achernak-child1-build-job'
//mail bcc: '', body: '', cc: '', from: '', replyTo: '', subject: '$BUILD_TAG', to: 'ip.chernak@gmail.com'}
        sh 'rm -rf *.tar.gz'
        step([
            $class: 'CopyArtifact',
            filter: '*',
            projectName: 'MNTLAB-achernak-child1-build-job',])}
                        
    stage ('Package artifact'){
        sh 'tar xvf *.tar.gz' 	
        sh 'tar -czf pipeline-achernak-${BUILD_NUMBER}.tar.gz jobs.groovy Jenkinsfile -C build/libs/ mntlab-ci-pipeline.jar'
        archiveArtifacts 'pipeline-achernak-${BUILD_NUMBER}.tar.gz'}
    
    
 /*   
 if (build.workspace.isRemote()){channel = build.workspace.channel}
            build_properties_file_content=""
            fp = new hudson.FilePath(channel, build.workspace.toString() + "*.jar")

        if (fp != null) {build_properties_file_content = fp.readToString()}     
 */   

//newFile = new hudson.FilePath(channel, fp)
//newFile.write("xyz", null)

/*
manager.listener.logger.println manager.build.project.getWorkspace()
manager.listener.logger.println manager.build.workspace

if (manager.build.workspace.isRemote()){
    channel = manager.build.workspace.channel
    manager.listener.logger.println  "I AM REMOTE!!"
}

fp = manager.build.workspace.toString()
newFile = new hudson.FilePath(channel, fp)

if (newFile.exists()) {
        manager.listener.logger.println "FILE EXISTS!!!"
        def perfData = newFile.read().getText('UTF-8')
        manager.listener.logger.println perfData
}*/
    
    
    
    
    stage ('Push to Nexus'){
        def cred = "amVua2luczpqZW5raW5z"
        sh 'pwd'
        sh 'ls -la'
        //def ARTIFACT_NAME = "ls -t1 ${WORKSPACE}/".execute().text.split()[0]
        //def PROJECT_NAME = ARTIFACT_NAME.split("-",3)[0]
        //def ARTIFACT_SUFFIX = ARTIFACT_NAME.split("-",3)[1]
        //findFiles(glob: '**/TEST-*.xml')
        
        
        /*
        def File = new File(glob: "pipeline-achernak-${BUILD_NUMBER}.tar.gz")//.getBytes()
        def connection = new URL( "http://EPBYMINW6122.minsk.epam.com:8081/repository/tomcat/appbackup/pipeline-achernak/${BUILD_NUMBER}/pipeline-achernak-${BUILD_NUMBER}.tar.gz").openConnection()
        connection.setRequestMethod("PUT")
        connection.doOutput = true
        connection.setRequestProperty("Authorization" , "Basic ${cred}")
        def writer = new DataOutputStream(connection.outputStream)
        writer.write (File)
        writer.close()
        println connection.responseCode
        */}
         
    stage('Approval')
        {timeout(time: 120, unit: 'SECONDS')input message: 'Pull and deploy?', ok: 'pull and deploy'}   
    
    stage ('Pull from Nexus'){
        def cred = "amVua2luczpqZW5raW5z"
        def ARTIFACT_NAME = "ls -t1 ${WORKSPACE}/".execute().text.split()[0]
        def PROJECT_NAME = ARTIFACT_NAME.split("-",3)[0]
        def ARTIFACT_SUFFIX = ARTIFACT_NAME.split("-",3)[1]
        def url = new URL( "http://EPBYMINW6122.minsk.epam.com:8081/repository/tomcat/appbackup/${PROJECT_NAME}-${ARTIFACT_SUFFIX}/${BUILD_NUMBER}/${ARTIFACT_NAME}").openConnection()
        url.setRequestProperty("Authorization" , "Basic ${cred}")
        def file = new File("${WORKSPACE}/${ARTIFACT_NAME}")
        file << url.inputStream
        println url.responseCode}
        
    stage ('Deploy'){
        sh 'tar xvf *${BUILD_NUMBER}.tar.gz'
        sh 'java -jar $JOB_NAME.jar'}
        
        notifySuccessful()
}


def notifyStarted() {
    //emailext attachLog: true, body: 'Alarm', subject: '$env.BUILD_NUMBER'
    /*emailext (
      subject: "STARTED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
      body: """<p>STARTED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
        <p>Check console output at "<a href="${env.BUILD_URL}">${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>"</p>""",
      to: 'ip@chernak@gmail.com'
    )*/
}

def notifySuccessful() {
    //emailext attachLog: true, body: 'Alarm', subject: '$env.BUILD_NUMBER'
    /*emailext (
      subject: "SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
      body: """<p>SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
        <p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>&QUOT;</p>""",
      to: 'ip@chernak@gmail.com'
    )*/
}

def notifyFailed() {
    //emailext attachLog: true, body: 'Alarm', subject: '$env.BUILD_NUMBER'
   /* emailext (
      subject: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
      body: """<p>FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
        <p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>&QUOT;</p>""",
      to: 'ip@chernak@gmail.com'
    )*/
}
