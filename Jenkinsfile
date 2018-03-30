def notifyStarted() {
    //emailext attachLog: true, body: 'Alarm', subject: '$env.BUILD_NUMBER'
      emailext (
      subject: "STARTED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
      body: """<p>STARTED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
        <p>Check console output at "<a href="${env.BUILD_URL}">${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>"</p>""",
      to: 'ip@chernak@gmail.com')}

def notifySuccessful() {
    //emailext attachLog: true, body: 'Alarm', subject: '$env.BUILD_NUMBER'
      emailext (
      subject: "SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
      body: """<p>SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
        <p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>&QUOT;</p>""",
      to: 'ip@chernak@gmail.com')}

def notifyFailed() {
    //emailext attachLog: true, body: 'Alarm', subject: '$env.BUILD_NUMBER'
      emailext (
      subject: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
      body: """<p>FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
        <p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>&QUOT;</p>""",
      to: 'ip@chernak@gmail.com')}


node("${SLAVE}") {
try { 
    stage('Git Checkout'){checkout scm}
        
    stage ('Build') {
        notifyStarted()
        tool name: 'gradle4.6', type: 'gradle'
        tool name: 'java8', type: 'jdk'
        tool name: 'groovy4', type: 'hudson.plugins.groovy.GroovyInstallation'
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
   
    
    stage ('Push to Nexus'){withEnv(["PATH+GROOVY_HOME=${tool 'groovy4'}/bin"]){sh 'groovy push.groovy'}}
         
    stage('Approval')
        {timeout(time: 120, unit: 'SECONDS')input message: 'Pull and deploy?', ok: 'pull and deploy'}   
    
    stage ('Pull from Nexus'){withEnv(["PATH+GROOVY_HOME=${tool 'groovy4'}/bin"]){sh 'groovy pull.groovy'}}
        
    stage ('Deploy'){
        sh 'tar xvf *${BUILD_NUMBER}.tar.gz'
        sh 'java -jar $JOB_NAME.jar'}
        
        notifySuccessful()

}catch (e) {
    currentBuild.result = "FAILED"
    notifyFailed()
    throw e
    }}
