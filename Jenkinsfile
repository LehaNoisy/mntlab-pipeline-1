def push_to_nexus() {
    nexusArtifactUploader artifacts: [[artifactId: 'pipeline', classifier: '', file: 'pipeline-uvalchkou-$BUILD_NUMBER.tar.gz', type: 'tar.gz']], credentialsId: 'nexus-creds', groupId: 'pipeline', nexusUrl: 'epbyminw2471.minsk.epam.com:8081', nexusVersion: 'nexus3', protocol: 'http', repository: 'uvalchkou', version: '$BUILD_NUMBER'
}

def email_notification(stage_name){
    emailext attachLog: true, body: "Job failed at ${stage_name} stage. You can find more information in attached log file.", subject: 'Job failed', to: 'tarantino459@gmail.com'
}
    
def pull_from_nexus() {
    withEnv(["PATH+GROOVY_HOME=${tool 'groovy4'}/bin"]){
        sh 'groovy pull.groovy $BUILD_NUMBER'
    }    
}

def env(String action){
    withEnv(["JAVA_HOME=${tool 'java8'}", "PATH+GRADLE=${tool 'gradle4.6'}/bin"]){
            sh 'gradle $action'
        }
}

node("${SLAVE}") {
    tool name: 'gradle4.6', type: 'gradle'
    tool name: 'java8', type: 'jdk'
    tool name: 'groovy4', type: 'hudson.plugins.groovy.GroovyInstallation'
    
    
    try {
        stage('git') {
            checkout sc2m
    }} catch (e) {
        email_notification('git')
        throw any
    }
    
    
    try {
        stage('build') {
            env('build')ew
    }} catch (e) {
        email_notification('build')
        throw any
    }
        
    
    stage('tests'){
        parallel(
            cucumber_test: {env('cucumber')},
            jacoco_test: {env('jacocoTestReport')},
            gradle_test: {env('test')}
        )
    }
    
    stage('trigger'){
        build job: 'MNTLAB-uvalchkou-child1-build-job'
    }
   
    stage('Packaging_and_Publishing'){
        sh 'rm -rf *.tar.gz'
        copyArtifacts filter: '*.tar.gz', projectName: 'MNTLAB-uvalchkou-child1-build-job'
        sh 'tar xzvf *.tar.gz'
        sh 'tar czvf pipeline-uvalchkou-$BUILD_NUMBER.tar.gz jobs.groovy Jenkinsfile -C build/libs/ mntlab-ci-pipeline.jar'
        archiveArtifacts 'pipeline-uvalchkou-$BUILD_NUMBER.tar.gz'
        push_to_nexus()
    }
    
    stage('aprooval'){
        input message: 'Process or Abort artifact pull and deploy?', ok: 'pull_and_deploy'
    }
    
    stage('pull'){
        pull_from_nexus()
    }
    
    stage('deploy'){
        sh 'tar xzfv pipeline-uvalchkou-$BUILD_NUMBER.tar.gz'
        sh 'java -jar mntlab-ci-pipeline.jar'
    }
    
    stage('notification'){
        emailext attachLog: true, body: '', subject: 'Build successful', to: 'tarantino459@gmail.com'

    }
}    
