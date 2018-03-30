def push_to_nexus() {
    nexusArtifactUploader artifacts: [[artifactId: 'pipeline', classifier: '', file: 'pipeline-uvalchkou-$BUILD_NUMBER.tar.gz', type: 'tar.gz']], credentialsId: 'nexus-creds', groupId: 'pipeline', nexusUrl: 'epbyminw2471.minsk.epam.com:8081', nexusVersion: 'nexus3', protocol: 'http', repository: 'uvalchkou', version: '$BUILD_NUMBER'
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
    
    stage('git') {
        checkout scm
        //git branch: 'uvalchkou', url: 'https://github.com/MNT-Lab/mntlab-pipeline/'
    }
    
    stage('build'){
        env("build")
        
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
        env.NODE_NAME
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
        sh 'ls $WORKSPACE'
        sh 'ls'
        pull_from_nexus()
    }
    
    stage('deploy'){
        sh 'ls'
        sh 'tar xzfv pipeline-uvalchkou-${BUILD_NUMBER}.tar.gz'
        sh 'ls'
        sh 'java -jar mntlab-ci-pipeline.jar'
    }
    
    /*stage('notification'){
        emailext attachLog: true, body: '', subject: 'STARTED', to: 'tarantino459@gmail.com'
    }*/
}    
