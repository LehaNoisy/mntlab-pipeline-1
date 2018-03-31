def notifyStarted() {
    emailext attachLog: true, body: '$JOB_NAME $BUILD_NUMBER',
               subject: '$JOB_NAME $BUILD_NUMBER', to: 'ip.chernak@gmail.com'}

def notifySuccessful() {
   emailext attachLog: true, body: '$JOB_NAME $BUILD_NUMBER',
               subject: '$JOB_NAME $BUILD_NUMBER', to: 'ip.chernak@gmail.com'}


def notifyFailed() {
    //def log_20 = currentBuild.rawBuild.getLog(20).join('\n\t\t')
    emailext attachLog: true, 
             subject: "Failed Pipeline: ${env.JOB_NAME} ${env.BUILD_NUMBER}   ${currentBuild.fullDisplayName}", 
             body: """Job ${env.JOB_NAME} build № ${env.BUILD_NUMBER} on ${stagename} stage is down.
                  Something is wrong with ${env.BUILD_URL}"
                  See the job in address ${env.JOB_URL}
             
                  Last log: ${currentBuild.rawBuild.getLog(20).join('\n\t\t')}""",
             to: 'ip.chernak@gmail.com'}

stagename = ''

node("${SLAVE}") {
try { 
    stage('Git Checkout'){checkout scm
                         stagename = '$STAGE_NAME'//'Git Checkout'
                          println (stagename)
                         }
     
    stage ('Build') {
        stagename = 'Build'
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

    stage('Trigger job') {stagename = 'Trigger job'
        build job: 'MNTLAB-achernak-child1-build-job'
        sh 'rm -rf *.tar.gz'
        step([
            $class: 'CopyArtifact',
            filter: '*',
            projectName: 'MNTLAB-achernak-child1-build-job',])}
                        
    stage ('Package artifact'){stagename = 'Package artifact'
        sh 'tar xvf *.tar.gz' 	
        sh 'tar -czf pipeline-achernak-${BUILD_NUMBER}.tar.gz jobs.groovy Jenkinsfile -C build/libs/ mntlab-ci-pipeline.jar'
        archiveArtifacts 'pipeline-achernak-${BUILD_NUMBER}.tar.gz'}
   
    
    stage ('Push to Nexus'){
        stagename = 'Push to Nexus'
        sh 'groovy push.groovy $BUILD_NUMBER'}
        
    stage('Approval'){stagename = 'Approval'
         timeout(time: 40, unit: 'SECONDS')
         input message: 'Pull and deploy?', ok: 'pull and deploy'}   
    
    stage ('Pull from Nexus'){stagename = 'Pull from Nexus'
                              sh 'groovy pull.groovy $BUILD_NUMBER'}
        
    stage ('Deploy'){stagename = 'v'
        sh 'tar xvf *${BUILD_NUMBER}.tar.gz'
        sh 'java -jar mntlab-ci-pipeline.jar'}
        
        notifySuccessful()

}catch (all) {
    currentBuild.result = 'FAILED'
    notifyFailed()
}}
